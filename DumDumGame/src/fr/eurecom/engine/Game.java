package fr.eurecom.engine;


import java.util.LinkedList;
import java.util.Random;

import fr.eurecom.data.Map;
import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Conveyor;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.dumdumgame.Obstacles;
import fr.eurecom.utility.DataWriter;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.MapReader;
import fr.eurecom.utility.Parameters;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.widget.Toast;

public class Game {
    private MapReader gameData;
    private Map background;
    private Character ball;
    private double elapsedTime;
    private DynamicBitmap[] teleporters;
    private Conveyor[] conveyors;
    private DynamicBitmap rain;
    private int score;
    private MainActivity mainForm;
    private MediaPlayer[] bloibs;
    private int bloibIndex;
    public enum MouseState { MOUSE_UP, MOUSE_DOWN, MOUSE_MOVE }

    public Game(Object o)
    {
        // Load game data from a matrix map
        int chosenLevel = ((MainActivity)o).getChosenLevel();
        gameData = new MapReader(Parameters.dMapID[chosenLevel - 1]);

        // Create a background from those data
        Bitmap tmpBitmap = Bitmap.createBitmap(1300, 1300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tmpBitmap);        
        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        BitmapShader shader = new BitmapShader(Parameters.bmpTextureWallpaper, TileMode.REPEAT, TileMode.REPEAT);
        paint.setShader(shader);
        
        canvas.drawRect(0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), paint);        
        gameData.Show(canvas);

        background = new Map(tmpBitmap, new Point(0, 0),
                     new Rect(0, 0, Parameters.dMaxWidth, Parameters.dMaxHeight));
        
        // Create a ball
        User currentUser = ((MainActivity)o).getCurrentUser();
        if (chosenLevel == currentUser.getCurrentLevel())
        {
            ball = new Character(currentUser.getCurrentPos());
            score = currentUser.getCurrentScore();
        }
        else
        {
            ball = new Character(gameData.getStartPos());
            score = 0;
        }
        elapsedTime = 0.0;

        // Create teleporters
        teleporters = new DynamicBitmap[gameData.getTeleporterList().size()];
        int teleRad = Parameters.dTeleRadius;
        for (int i = 0; i < gameData.getTeleporterList().size(); ++i)
        {
            Point position = new Point(gameData.getTeleporterList().get(i).x - teleRad, gameData.getTeleporterList().get(i).y - teleRad);
            teleporters[i] = new DynamicBitmap(Parameters.bmpTeleporter, position, 0, 2 * teleRad, 2 * teleRad);
        }

        // Create conveyors
        conveyors = new Conveyor[gameData.getConveyorList().size()];
        for (int i = 0; i < gameData.getConveyorList().size(); ++i)
            conveyors[i] = new Conveyor(gameData.getConveyorList().get(i));

        // Create rain
        if (gameData.isRain())
            rain = new DynamicBitmap(Parameters.bmpRain, new Point(0, 0), 0,
            						 Parameters.dMaxWidth, Parameters.dMaxHeight);
        else
            rain = null;

        // Load Sound
        bloibs = new MediaPlayer[3];
        for (int i = 0; i < bloibs.length; ++i)
        {
        	bloibs[i] = MediaPlayer.create((MainActivity)o, Parameters.dBloibSound);
            bloibs[i].setLooping(false);
        }
        bloibIndex = 0;

        mainForm = (MainActivity)o;
        updateView();
    }

    private boolean isBallClicked = false;
    private boolean isDragging = false; // for ball (ruler)
    private Point junction = new Point(0, 0); // for ruler
    private boolean amulet = false;    // for teleporters
    private int numOfCollisions = 0;// for multi-reflection
    private int updateCounter = 1;  // for conveyors, rain
    private Point clickedPoint = new Point(0, 0);  // for dragging background
    private boolean isBackgroundClicked = false;
    private boolean isUpdateView = false;   // 3 cases: ball out of view, ball stop, drag background
    private boolean firstTimeShow = true;
    private boolean isPreviouslyDragging = false;  // dragging the ball
    private boolean isPreviouslyBackgroundDragging = false;
    private LinkedList<Segment> previousObstacles = null;  // for highlighting obstacles
    private int highlightCounter = 0;   // for highlighting obstacles

    public void Action(Point mousePos, Object o, MouseState mouseState)
    {
        if (ball.isRunning())
            return;

        mousePos.x -= background.getPosition().x;
        mousePos.y -= background.getPosition().y;

        if (mouseState == MouseState.MOUSE_DOWN)
        {
            if (Helper.Point_GetDistanceFrom(mousePos, ball.getPosition()) <= Parameters.dBallRadius)
            {
                isBallClicked = true;
            }
            else
            {
                isBackgroundClicked = true;
                clickedPoint = mousePos;
            }
        }
        else if (mouseState == MouseState.MOUSE_UP)
        {
            if (isBallClicked)
            {
                double acceleration = getAccelerationUnderTheBall();
                double initialVelocity = Helper.Point_GetDistanceFrom(ball.getPosition(), mousePos) * Parameters.forceCoefficient;                                    
                Ray direction = new Ray(ball.getPosition(), Helper.Point_GetMirrorFrom(mousePos, ball.getPosition()));
                initBall(initialVelocity, acceleration, direction);
                isBallClicked = false;
                isDragging = false;
                isPreviouslyDragging = true;
                score++;
            }
            if (isBackgroundClicked)
            {
                isBackgroundClicked = false;
                isPreviouslyBackgroundDragging = true;
                isUpdateView = false;
            }
        }
        else // MOUSE_MOVE event
        {
            if (isBallClicked)
            {
                junction.x = mousePos.x + background.getPosition().x;
                junction.y = mousePos.y + background.getPosition().y;
                isDragging = true;
            }
            if (isBackgroundClicked)
            {
            	Point newPosition = new Point(background.getPosition().x + mousePos.x - clickedPoint.x, background.getPosition().y + mousePos.y - clickedPoint.y);
            	background.setPosition(newPosition);
                isUpdateView = true;
                ((MainActivity)o).getMainView().invalidate();
            }
        }
    }
    
    private Segment isNextPostAvailable() {
    	// TODO with great assumption that wall list is sorted according to the x position of first point 
    	// and that first point of wall is always smaller than second point in term of x position
    	
    	int reflectorList_len = gameData.getReflectorList().size();
    	
    	for (int i = 0; i < reflectorList_len; ++i)
    	{
    		Segment currentReflector = gameData.getReflectorList().get(i);
    		
    		if (ball.getTrajectoryList().getFirst().x < currentReflector.getFirstPoint().x &&
    				ball.getTrajectoryList().getLast().x < currentReflector.getFirstPoint().x)
    			break;    		
    		
			Point current = new Point(ball.getCurrentPosition());
			Point next = new Point(ball.getNextPosition());
			Point wall1 = new Point(currentReflector.getFirstPoint());
			Point wall2 = new Point(currentReflector.getSecondPoint());
			
			double cPoint = -current.x * (next.y - current.y) + current.y
					* (next.x - current.x);
			double cPoint1 = -wall1.x * (wall2.y - wall1.y) + wall1.y
					* (wall2.x - wall1.x);

			double checkingInq1a = (next.y - current.y) * wall1.x
					- (next.x - current.x) * wall1.y + cPoint;
			double checkingInq1b = (next.y - current.y) * wall2.x
					- (next.x - current.x) * wall2.y + cPoint;

			double checkingIng2a = (wall2.y - wall1.y) * current.x
					- (wall2.x - wall1.x) * current.y + cPoint1;
			double checkingIng2b = (wall2.y - wall1.y) * next.x
					- (wall2.x - wall1.x) * next.y + cPoint1;

			if (checkingInq1a * checkingInq1b < 0
					&& checkingIng2a * checkingIng2b < 0) {
				return currentReflector;
			}
				
  		}
						
    	  	
    	return null;
    }
    
    public void show(Canvas canvas) throws Exception
    {
        // Show background
        showBackground(canvas);

        if (--updateCounter <= -1)
            updateCounter = Parameters.updatePeriod;

        // Highlight previous obstacles
        if (highlightCounter < 3 && previousObstacles != null)
        {
            if (ball.isRunning())
            {
                for (int i = 0; i < previousObstacles.size(); ++i)
                    previousObstacles.get(i).show(canvas, background.getPosition());
                highlightCounter++;
            }
        }

        // Show rain, if any
        if (rain != null)
        {
            rain.show(canvas);
            if (updateCounter == 0 || updateCounter == 3)
                rain.updateToTheNextImage();
        }

        // Show teleporters, if any
        for (int i = 0; i < gameData.getTeleporterList().size(); ++i)
        {
            teleporters[i].show(canvas, background.getPosition());
            teleporters[i].updateToTheNextImage();
        }
        
        // Show conveyors, if any
        boolean conveyorInEffect = false;
        for (int i = 0; i < gameData.getConveyorList().size(); ++i)
        {
            conveyors[i].show(canvas, background.getPosition());
            if (updateCounter == 0)
                conveyors[i].updateToTheNextImage();
            if (conveyors[i].contains(ball.getPosition()) && !conveyorInEffect)
            {
                conveyorInEffect = true;
                Point increment = conveyors[i].getIncrement();
                if (!ball.isRunning())
                {
                    ball.projectOn(conveyors[i].getCenterLine());
                    Point newPosition = new Point(ball.getPosition().x + increment.x, ball.getPosition().y + increment.y);
                    ball.setPosition(newPosition);
                }
                else
                    ball.exhaustTheball();
            }
        }

        // Show ruler, if any
        if (isDragging)
        {
        	Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(2);            
            canvas.drawLine(ball.getPosition().x + background.getPosition().x, 
            				ball.getPosition().y + background.getPosition().y, 
            				junction.x, junction.y, paint);
        }

        // Show ball
        if (ball.isRunning())
        {
            Point shadowPos1 = new Point(ball.getPosition());        	
            double quantum = Parameters.timer / 1000.0;
            elapsedTime += quantum;

            // If the ball hits the wall
//			if (!mainForm.isWallThroughAllowed()) {
//				Character aBall = ball.getBallAtTime(elapsedTime);
//				LinkedList<Segment> nextObstacles = null;
//				nextObstacles = aBall.isOverWalls(gameData.getReflectorList());
//				if (nextObstacles.size() > 0) {
//					this.bloibs[bloibIndex].start();
//					bloibIndex = (bloibIndex + 1) % bloibs.length;
//					previousObstacles = nextObstacles;
//					highlightCounter = 0;
//					reflectTheBall(nextObstacles);
//
//					
////					 * // Remove this fragment of code for performance purposes
////					 * // Test again! aBall = ball.getBallAtTime(elapsedTime);
////					 * nextObstacles =
////					 * aBall.isOverWalls(gameData.getReflectorList()); if
////					 * (nextObstacles.size() > 0) ball.exhaustTheball();
////					 
//				}
//			}
			
			// TODO: this object must be of type Obstacle and then be type-casted into Segment
			Segment obstacle = isNextPostAvailable();

			if (obstacle == null)
				ball.update(elapsedTime, quantum);		// these parameters are only for placeholder purpose, they have no meaning till this moment!!!
			else
			{				
				ball.bounce(obstacle);
				ball.update(elapsedTime, quantum);
			}
			
			
//            if (ball.update(elapsedTime, quantum))
//            {
//                double acc = getAccelerationUnderTheBall();
//                if (ball.getCurrentAcceleration() != acc)
//                    changeTheBallAcceleration(acc);
//            }
            
            // Show ball's shadow
            Point temp = new Point(ball.getPosition());
            Point shadowPos2 = Helper.Point_GetMirrorFrom(temp, shadowPos1);
            (new Character(shadowPos2)).showShadow(canvas, background.getPosition(), 50);
            (new Character(shadowPos1)).showShadow(canvas, background.getPosition(), 100);

            // Show ball
            ball.show(canvas, background.getPosition());

            // If the ball hits the teleporter, if any
            boolean isBallSucked = false;
            double min = (Parameters.dTeleRadius + Parameters.dBallRadius) / 2;
            for (int i = 0; i < gameData.getTeleporterList().size(); ++i)
            {
                double distance = Helper.Point_GetDistanceFrom(ball.getPosition(), gameData.getTeleporterList().get(i)); 
                if (distance <= min)
                {
                    isBallSucked = true;
                    if (!amulet)
                    {
                        teleportTheBall(i);
                        amulet = true;
                    }
                    break;
                }
             }
            if (!isBallSucked)
                amulet = false;

            // If the ball stops running
            if (!ball.isRunning())
            {
                elapsedTime = 0.0;
                updateView();
                numOfCollisions = 0;
                //
                Paint paint = new Paint();
                paint.setStyle(Style.FILL);
                BitmapShader shader = new BitmapShader(Parameters.bmpTextureWallpaper, TileMode.REPEAT, TileMode.REPEAT);
                paint.setShader(shader);                
                canvas.drawRect(0, 0, Parameters.dMaxWidth, Parameters.dMaxHeight, paint);                
                this.show(canvas);
            }

            // If the ball is outside the visible region
            if (isBallOutOfView())
                updateView();
        }
        else
            ball.show(canvas, background.getPosition());

        if (Helper.Point_GetDistanceFrom(ball.getPosition(), gameData.getHolePos()) < 0.65 * Parameters.dBallRadius) {
        	// Do a down on the mutex
        	Parameters.mutex.acquire();
        	// Critical region
            levelUp();
            // ------------------
            // Do an up on the mutex
            Parameters.mutex.release();
        }
    }

    public boolean isRunning()
    {
        if (gameData.getTeleporterList().size() > 0)
            return true;
        if (gameData.getConveyorList().size() > 0)
            return true;
        if (gameData.isRain())
            return true;
        if (this.ball.isRunning() || isDragging)
            return true;
        return false;
    }
    public boolean isBallRunning()
    {
        return this.ball.isRunning();
    }
    public void resume()
    {
        this.firstTimeShow = true;
    }
    public void flushData()
    {
        for (int i = 0; i < bloibs.length; ++i)
        {
        	if (bloibs[i] != null)
        	{
        		if (bloibs[i].isPlaying())
        			bloibs[i].stop();
        		bloibs[i].release();
        	}
        }
        background.recycle();
        background = null;
        gameData = null;
        ball = null;
        teleporters = null;
        conveyors = null;
        rain = null;
    }
    public void restart()
    {
        this.firstTimeShow = true;
        ball = new Character(gameData.getStartPos());
        updateView();
        score = 0;
    }
    public int getScore()
    {
        return this.score;
    }
    public Point getBallPos()
    {
        return this.ball.getPosition();
    }
    private void updateView()
    {
    	Point point = new Point(ball.getPosition().x + background.getPosition().x, ball.getPosition().y + background.getPosition().y);
        background.updateView(point);
        isUpdateView = true;
    }
    private boolean isBallOutOfView()
    {
        Point point = new Point(ball.getPosition().x + background.getPosition().x, ball.getPosition().y + background.getPosition().y);
        if (point.x < 0 || point.x > Parameters.dMaxWidth ||
            point.y < 0 || point.y > Parameters.dMaxHeight)
            return true;
        return false;
    }
    private void initBall(double initialVelocity, double acceleration, Ray direction)
    {
        ball.init(acceleration, initialVelocity, direction);
        elapsedTime = 0.0;
    }
    private void reflectTheBall(LinkedList<Segment> nextObstacles) throws Exception
    {
        if (nextObstacles == null || nextObstacles.size() == 0)
            return;

        boolean engineError = false;
        double acceleration = ball.getCurrentAcceleration();
        double velocity = ball.getInstantVelocity(elapsedTime);
        Ray direction = null;
        if (nextObstacles.size() == 1)
        {
            // Need more code here +.+!
            Line tmpLine = nextObstacles.get(0).getAbreastLine(ball.getPosition());
            Ray tmpRay = new Ray(ball.getCurrentDirection().getRoot(), ball.getCurrentDirection().getSecondPoint());
            if (tmpLine.RoughlyContains(tmpRay.getRoot()))
            {
                Point p = Helper.Point_GetMirrorFrom(tmpRay.getSecondPoint(), tmpRay.getRoot());
                tmpRay.setRoot(Helper.Point_GetMirrorFrom(tmpRay.getRoot(), p));
            }
            try
            {
                direction = tmpRay.fastGenerateReflectedRayFrom(tmpLine);
            }
            catch (Exception ex)
            {
                engineError = true;
            }
        }
        else
        {
            // THE VERY BIG BUG IS HERE
            direction = new Ray(ball.getPosition(), ball.getCurrentDirection().getRoot());
        }

        if (!engineError)
            initBall(velocity, acceleration, direction);
        else
            ball.exhaustTheball();

        numOfCollisions++;
        if (numOfCollisions > Parameters.dMaxNumOfCollisions)
            ball.exhaustTheball();
    }
    private void teleportTheBall(int currentTeleporter)
    {
        Random generator = new Random();
        int index = 0;                        
        while ((index = generator.nextInt(gameData.getTeleporterList().size())) == currentTeleporter);
        Point newPosition = gameData.getTeleporterList().get(index);

        double acceleration = Parameters.grassFrictionAcceleration;
        double velocity = ball.getInstantVelocity(elapsedTime) + 12;
        ball.setPosition(newPosition);

        int incrementX = 0;
        int incrementY = 0;        
        while ((incrementX = generator.nextInt(41)) == 20);
        while ((incrementY = generator.nextInt(41)) == 20);
        incrementX -= 20;
        incrementY -= 20;
        
        Ray direction = new Ray(ball.getPosition(), new Point(newPosition.x + incrementX, newPosition.y + incrementY));
        initBall(velocity, acceleration, direction);
    }
    private void changeTheBallAcceleration(double acceleration)
    {
        if (!ball.isRunning())
            return;
        double initialVelocity = ball.getInstantVelocity(elapsedTime);
        Ray direction = new Ray(ball.getPosition(), Helper.Point_GetMirrorFrom(ball.getCurrentDirection().getRoot(), ball.getPosition()));
        initBall(initialVelocity, acceleration, direction);
    }
//    private void showReflectors(Canvas canvas)
//    {
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(3);
//        
//        for (int i = 0; i < gameData.getReflectorList().size(); ++i)
//        {
//            Point first = new Point(gameData.getReflectorList().get(i).getFirstPoint());
//            Point second = new Point(gameData.getReflectorList().get(i).getSecondPoint());          
//            first.x += background.getPosition().x;
//            first.y += background.getPosition().y;
//            second.x += background.getPosition().x;
//            second.y += background.getPosition().y;                        
//            canvas.drawLine(first.x, first.y, second.x, second.y, paint);
//        }            
//    }
    private void showBackground(Canvas canvas)
    {
//        int rad = Parameters.dBallRadius;

        if (isDragging || isPreviouslyDragging || isUpdateView || firstTimeShow || isPreviouslyBackgroundDragging)
        {
            showFullBackground(canvas);
            isUpdateView = false;
            firstTimeShow = false;
            isPreviouslyDragging = false;
            isPreviouslyBackgroundDragging = false;
        }
        else if (gameData.isRain())
            showFullBackground(canvas);
        else if (ball.isRunning())
        {
//            background.showEx(canvas, ball.getPosition().x - rad - rad, ball.getPosition().y - rad - rad, 4 * rad, 4 * rad);
//            showReflectors(canvas);
        	showFullBackground(canvas);

//            // Show background under the teleporters
//            int teleRad = Parameters.dTeleRadius;
//            for (int i = 0; i < gameData.getTeleporterList().size(); ++i)
//            {
//                Point p = gameData.getTeleporterList().get(i);
//                background.showEx(canvas, p.x - teleRad - 3, p.y - teleRad - 3, 2 * teleRad + 6, 2 * teleRad + 6);
//            }
        }
        else // Show background under the stay-still ball
        {
//            background.showEx(canvas, ball.getPosition().x - rad - 2, ball.getPosition().y - rad - 2, 2 * rad + 4, 2 * rad + 4);
//            showReflectors(canvas);
        	showFullBackground(canvas);
        }
       
        // Show score        
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(20);
        paint.setColor(Color.BLUE);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Score: " + score, Parameters.dMaxWidth / 2 - 40, 20, paint);
    }
    private double getAccelerationUnderTheBall()
    {
        // If the ball is in water puddle
        for (int i = 0; i < gameData.getWaterList().size(); ++i)
        {
            if (gameData.getWaterList().get(i).getBoundRect().contains(ball.getPosition().x, ball.getPosition().y))
            {
                return Parameters.waterFrictionAcceleration;
            }
        }
        
        // If the ball is in sand region
        for (int i = 0; i < gameData.getSandList().size(); ++i)
        {
            if (gameData.getSandList().get(i).getBoundRect().contains(ball.getPosition().x, ball.getPosition().y))
            {
                return Parameters.sandFrictionAcceleration;
            }
        }

        // Otherwise
        return Parameters.grassFrictionAcceleration;
    }
    public void levelUp() throws Exception
    {
        User user = mainForm.getCurrentUser();

        // save progress
        int level = mainForm.getChosenLevel(); 
        if (user.getLevelScore().get(level - 1) == 0 ||
            (user.getLevelScore().get(level - 1) > score && user.getLevelScore().get(level - 1) != 0)) // save the better result
            user.getLevelScore().set(level - 1, score);

        // go to next level
        if (mainForm.getChosenLevel() == 10)
        {
            mainForm.setChosenLevel(1);
            mainForm.setState(StateList.CONGRAT_BOX);
            mainForm.getMainView().invalidate();
            return;
        }
        else
        {
            mainForm.setChosenLevel(level + 1);
        }

        // reset data for new level
        user.setCurrentLevel(mainForm.getChosenLevel());
        user.setCurrentScore(0);
        gameData = null;
        gameData = new MapReader(Parameters.dMapID[mainForm.getChosenLevel() - 1]);        
        user.setCurrentPos(gameData.getStartPos());                
        if (user.getCurrentLevel() > user.getUnlockedLevel())
            user.setUnlockedLevel(user.getCurrentLevel());

        // write to disk         
        DataWriter.WriteData(mainForm.getUserList(), Parameters.pthData, user.getName());
        mainForm.updateContent();

        mainForm.getGame().flushData();
        mainForm.initGame();
        mainForm.getMainView().invalidate();          
    }
    private void showFullBackground(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        BitmapShader shader = new BitmapShader(Parameters.bmpTextureWallpaper, TileMode.REPEAT, TileMode.REPEAT);
        paint.setShader(shader);                
        canvas.drawRect(0, 0, Parameters.dMaxWidth, background.getPosition().y, paint);
        canvas.drawRect(0, 0, background.getPosition().x, Parameters.dMaxHeight, paint);
        canvas.drawRect(background.getWidth() + background.getPosition().x, 0, Parameters.dMaxWidth, Parameters.dMaxHeight, paint);
        canvas.drawRect(0, background.getHeight() + background.getPosition().y, Parameters.dMaxWidth, Parameters.dMaxHeight, paint);
        
        background.showEx(canvas, -background.getPosition().x, -background.getPosition().y, 
        				  Parameters.dMaxWidth, Parameters.dMaxHeight);       
    }
}