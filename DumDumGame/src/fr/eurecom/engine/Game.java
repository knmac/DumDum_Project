package fr.eurecom.engine;

import java.util.Calendar;
import java.util.LinkedList;

import fr.eurecom.data.Map;
import fr.eurecom.data.MapTexture;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Candies;
import fr.eurecom.dumdumgame.Conveyor;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.Obstacles;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserWriter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;

public class Game {
	private MapTexture gameData;
	private Map background;
	private Character ball;
	private double elapsedTime;
	private DynamicBitmap[] teleporters;
	private Conveyor[] conveyors;
	private DynamicBitmap rain;
	private int turn;
	private MediaPlayer[] bloibs;
	private int bloibIndex;
	private static Physics physics;
	private DynamicBitmap[] heartRedArr;
	private DynamicBitmap[] heartBlackArr;
	private DynamicBitmap microwave;
	private DynamicBitmap pauseButton;
	private DynamicBitmap gearUpButton;

	public Obstacles[] obstacleList;
	private Environment myEnvironment;

	public static Physics getPhysics() {
		return physics;
	}

	public enum MouseState {
		MOUSE_UP, MOUSE_DOWN, MOUSE_MOVE
	}

	public enum ObstacleIdx {
		Platform(0), Candy(1), Blackhole(2), Spike(3), Conveyor(4), Bat(5);

		// return number of obstacle types
		public static int numObstacleType() {
			return ObstacleIdx.values().length;
		}

		private final int value;

		private ObstacleIdx(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	// game objects compose of 3 types:
	// 1. fix-screen objects: icons, logos, scores
	// 2. map-texture: stable objects that stick to the map structures
	// and has no interaction with game characters
	// 3. game-character: main character, enemies, and others moving objects
	// that can be interacted (directly like main character or indirectly like
	// blackholes, etc.)

	public Game() {
		// Load game data from a matrix map
		int chosenLevel = GameManager.chosenLevel;
		gameData = new MapTexture(Parameters.dMapID[chosenLevel - 1]);
		// Initialize the obstacle list
		obstacleList = gameData.readMapData(Parameters.dMapID[chosenLevel - 1]); // TODO
																					// TEST!!

		// Create a background from those data
		Point boundary = gameData.getMapBottomRight();
		Bitmap tmpBitmap = Bitmap.createBitmap(boundary.x
				+ Parameters.dZoomParam, boundary.y, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(tmpBitmap);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		BitmapShader shader = new BitmapShader(Parameters.bmpTextureWallpaper,
				TileMode.REPEAT, TileMode.REPEAT);
		paint.setShader(shader);

		canvas.drawRect(0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(),
				paint);
		gameData.Show(canvas);
		// obstacleList[ObstacleIdx.Platform.getValue()].show(canvas); // TODO
		// Originally here, but moved to Game.Show()

		background = new Map(tmpBitmap, new Point(0, 0), new Rect(0, 0,
				Parameters.dMaxWidth, Parameters.dMaxHeight));

		ball = new Character(gameData.getStartPos());
		turn = 0;
		elapsedTime = 0.0;

		// Create teleporters
		// teleporters = new DynamicBitmap[gameData.getTeleporterList().size()];
		// int teleRad = Parameters.dTeleRadius;
		// for (int i = 0; i < gameData.getTeleporterList().size(); ++i) {
		// Point position = new Point(gameData.getTeleporterList().get(i).x -
		// teleRad, gameData.getTeleporterList().get(i).y - teleRad);
		// teleporters[i] = new DynamicBitmap(Parameters.bmpTeleporter,
		// position, 0, 2 * teleRad, 2 * teleRad);
		// }

		// Create conveyers
		conveyors = new Conveyor[gameData.getConveyorList().size()];
		for (int i = 0; i < gameData.getConveyorList().size(); ++i)
			conveyors[i] = new Conveyor(gameData.getConveyorList().get(i));

		// Create rain
		if (gameData.isRain())
			rain = new DynamicBitmap(Parameters.bmpRain, new Point(0, 0), 0,
					Parameters.dMaxWidth, Parameters.dMaxHeight);
		else
			rain = null;

		// TODO: BOUNCING SOUND
		bloibs = new MediaPlayer[3];
		for (int i = 0; i < bloibs.length; ++i) {
			bloibs[i] = MediaPlayer.create(App.getMyContext(),
					Parameters.dBloibSound);
			bloibs[i].setLooping(false);
		}
		bloibIndex = 0;

		// Create hearts
		int maxLives = GameManager.user.getMaxLives();
		heartRedArr = new DynamicBitmap[maxLives];
		heartBlackArr = new DynamicBitmap[maxLives];
		for (int i = 0; i < maxLives; i++) {
			int size = Parameters.dBallRadius;
			Point position = new Point(size * (i + 2), size / 2);
			heartRedArr[i] = new DynamicBitmap(Parameters.bmpHeartRed,
					position, size, size);
			heartBlackArr[i] = new DynamicBitmap(Parameters.bmpHeartBlack,
					position, size, size);
		}

		// create gear-up button
		int h = Parameters.dBallRadius;
		int w = h * Parameters.bmpDumDumNormal.getWidth()
				/ Parameters.bmpDumDumNormal.getHeight();
		Point pos = new Point(Parameters.dBallRadius * 2 - w,
				Parameters.dBallRadius / 2);
		gearUpButton = new DynamicBitmap(Parameters.bmpDumDumNormal, pos, w, h);

		// create microwave
		w = 4 * Parameters.dBallRadius;
		h = w * Parameters.bmpMicrowave.getHeight()
				/ Parameters.bmpMicrowave.getWidth();
		pos = new Point(gameData.getHolePos().x - w / 2,
				gameData.getHolePos().y - h / 2);
		microwave = new DynamicBitmap(Parameters.bmpMicrowave, pos, w, h);

		// create pause buttons
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.pause_btn);
		pauseButton = new DynamicBitmap(bmp, new Point(Parameters.dMaxWidth - 2
				* Parameters.dBallRadius, Parameters.dBallRadius / 2),
				Parameters.dBallRadius, Parameters.dBallRadius);

		updateView();

		myEnvironment = new Earth();
		this.physics = new Physics(myEnvironment, gameData.getMapBottomRight());
	}

	private boolean isBallClicked = false;
	private boolean isDragging = false; // for ball (ruler)
	private Point junction = new Point(0, 0); // for ruler
	private boolean amulet = false; // for teleporters
	private int numOfCollisions = 0;// for multi-reflection
	private int updateCounter = 1; // for conveyors, rain
	private Point clickedPoint = new Point(0, 0); // for dragging background
	private boolean isBackgroundClicked = false;
	private boolean isUpdateView = false; // 3 cases: ball out of view, ball
											// stop, drag background
	private boolean firstTimeShow = true;
	private boolean isPreviouslyDragging = false; // dragging the ball
	private boolean isPreviouslyBackgroundDragging = false;
	private LinkedList<Segment> previousObstacles = null; // for highlighting
															// obstacles
	private int highlightCounter = 0; // for highlighting obstacles

	public void Action(Point mousePos, Object o, MouseState mouseState) {
		// click pause button
		if (mouseState == MouseState.MOUSE_DOWN
				&& pauseButton.isClicked(mousePos)) {
			GameManager.captureScreen();
			GameManager.setCurrentState(GameManager.GameState.PAUSE_MENU);
			GameManager.redrawScreen();
			return;
		}

		// click gear-up button
		if (mouseState == MouseState.MOUSE_DOWN
				&& gearUpButton.isClicked(mousePos)) {
			updateView();
			GameManager.captureScreen();
			GameManager.setCurrentState(GameManager.GameState.GEAR_UP_MENU);
			GameManager.redrawScreen();
			return;
		}

		// =========================================================================
		if (ball.isRunning()) // no interaction with DumDum while running
			return;

		mousePos.x -= background.getPosition().x;
		mousePos.y -= background.getPosition().y;

		if (mouseState == MouseState.MOUSE_DOWN) {
			if (Helper.Point_GetDistanceFrom(mousePos, ball.getPosition()) <= Parameters.dBallRadius) {
				isBallClicked = true;
			} else {
				isBackgroundClicked = true;
				clickedPoint = mousePos;
			}
		} else if (mouseState == MouseState.MOUSE_UP) {
			if (isBallClicked) {
				// double acceleration = getAccelerationUnderTheBall();
				// double initialVelocity = Helper.Point_GetDistanceFrom(
				// ball.getPosition(), mousePos)
				// * Parameters.forceCoefficient;
				// Ray direction = new Ray(
				// ball.getPosition(),
				// Helper.Point_GetMirrorFrom(mousePos, ball.getPosition()));
				// initBall(initialVelocity, acceleration, direction);

				Point direction = new Point(ball.getPosition().x - mousePos.x,
						ball.getPosition().y - mousePos.y);
				initBall(direction);
				isBallClicked = false;
				isDragging = false;
				isPreviouslyDragging = true;
				turn++;
			}
			if (isBackgroundClicked) {
				isBackgroundClicked = false;
				isPreviouslyBackgroundDragging = true;
				isUpdateView = false;
			}
		} else // MOUSE_MOVE event
		{
			if (isBallClicked) {
				junction.x = mousePos.x + background.getPosition().x;
				junction.y = mousePos.y + background.getPosition().y;
				isDragging = true;
			}
			if (isBackgroundClicked) {
				Point newPosition = new Point(background.getPosition().x
						+ mousePos.x - clickedPoint.x,
						background.getPosition().y + mousePos.y
								- clickedPoint.y);
				background.setPosition(newPosition);
				isUpdateView = true;
				GameManager.mainView.invalidate();
			}
		}
	}

	int endGame = 0;

	private LinkedList<Obstacles> isNextPostAvailable() {
		// TODO with great assumption that wall list is sorted according to the
		// x position of first point
		// and that first point of wall is always smaller than second point in
		// term of x position

		/*
		 * int reflectorList_len = gameData.getReflectorList().size();
		 * 
		 * double minDistance = Double.MAX_VALUE; Segment returnedWall = null;
		 */

		Point current = ball.getCurrentPosition();

		Point next = ball.getNextPosition();

		if (current == null)
			return null;

		// ball gets out of scence
		// TODO
		if (next == null) {
			++endGame;
			return null;
		}

		Point rangeStart = ball.getTrajectoryList().getFirst();
		Point rangeEnd = ball.getTrajectoryList().getLast();

		LinkedList<Obstacles> resultObstacles = new LinkedList<Obstacles>();
		Obstacles tmpObstacle;

		// check candy
		if (obstacleList[ObstacleIdx.Candy.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Candy.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// check platform
		if (obstacleList[ObstacleIdx.Platform.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Platform.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// check blackhole, make this one the LAST!!!!
		if (obstacleList[ObstacleIdx.Blackhole.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Blackhole.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null) { 
//				resultObstacles = new LinkedList<Obstacles>(); // overwrite everything else
				resultObstacles.add(tmpObstacle);
			}
		}

		// return null if there is no obstacles
		if (resultObstacles.size() == 0)
			return null;

		return resultObstacles;

		/*
		 * for (int i = 0; i < reflectorList_len; ++i) { Segment
		 * currentReflector = gameData.getReflectorList().get(i);
		 * 
		 * if (ball.getTrajectoryList().getFirst().x < currentReflector
		 * .getFirstPoint().x && ball.getTrajectoryList().getLast().x <
		 * currentReflector .getFirstPoint().x) break;
		 * 
		 * Point wall1 = new Point(currentReflector.getFirstPoint()); Point
		 * wall2 = new Point(currentReflector.getSecondPoint());
		 * 
		 * // equation of the wall double A = wall2.y - wall1.y; double B =
		 * -(wall2.x - wall1.x); double C = -wall1.x * (wall2.y - wall1.y) +
		 * wall1.y (wall2.x - wall1.x); double vAB = Math.sqrt(Math.pow(A, 2) +
		 * Math.pow(B, 2));
		 * 
		 * double distanceNextWall = Math.abs(A * next.x + B * next.y + C) /
		 * (vAB);
		 * 
		 * double cPoint = -current.x * (next.y - current.y) + current.y (next.x
		 * - current.x); double cPoint1 = -wall1.x * (wall2.y - wall1.y) +
		 * wall1.y (wall2.x - wall1.x);
		 * 
		 * double checkingInq1a = (next.y - current.y) * wall1.x - (next.x -
		 * current.x) * wall1.y + cPoint; double checkingInq1b = (next.y -
		 * current.y) * wall2.x - (next.x - current.x) * wall2.y + cPoint;
		 * 
		 * double checkingIng2a = (wall2.y - wall1.y) * current.x - (wall2.x -
		 * wall1.x) * current.y + cPoint1; double checkingIng2b = (wall2.y -
		 * wall1.y) * next.x - (wall2.x - wall1.x) * next.y + cPoint1;
		 * 
		 * if ((checkingInq1a * checkingInq1b <= 0 && checkingIng2a
		 * checkingIng2b <= 0)) { double distancePointWall = Math.abs(A *
		 * current.x + B current.y + C);
		 * 
		 * if (distancePointWall < minDistance) { minDistance =
		 * distancePointWall; returnedWall = currentReflector; } } else if
		 * (distanceNextWall < Parameters.dBallRadius) { double distNextWall1 =
		 * Math.sqrt(Math.pow(next.x - wall1.x, 2) + Math.pow(next.y - wall1.y,
		 * 2)); double distNextWall2 = Math.sqrt(Math.pow(next.x - wall2.x, 2) +
		 * Math.pow(next.y - wall2.y, 2)); double distWall1Wall2 =
		 * Math.sqrt(Math .pow(wall1.x - wall2.x, 2) + Math.pow(wall1.y -
		 * wall2.y, 2));
		 * 
		 * double condition1 = Math.sqrt(Math.pow(distNextWall1, 2) -
		 * Math.pow(distanceNextWall, 2)); double condition2 =
		 * Math.sqrt(Math.pow(distNextWall2, 2) - Math.pow(distanceNextWall,
		 * 2));
		 * 
		 * double epsilon = 1; if (condition1 + condition2 > distWall1Wall2 -
		 * epsilon && condition1 + condition2 < distWall1Wall2 + epsilon) { if
		 * (distanceNextWall < minDistance) { minDistance = distanceNextWall;
		 * returnedWall = currentReflector; } } } }
		 * 
		 * return returnedWall;
		 */
	}

	public void show(Canvas canvas) throws Exception {
		// Show background
		showBackground(canvas);
		if (obstacleList[ObstacleIdx.Platform.getValue()] != null)
			obstacleList[ObstacleIdx.Platform.getValue()].show(canvas,
					background.getPosition());
		if (obstacleList[ObstacleIdx.Candy.getValue()] != null)
			obstacleList[ObstacleIdx.Candy.getValue()].show(canvas,
					background.getPosition());
		if (obstacleList[ObstacleIdx.Blackhole.getValue()] != null)
			obstacleList[ObstacleIdx.Blackhole.getValue()].show(canvas,
					background.getPosition());

		if (--updateCounter <= -1)
			updateCounter = Parameters.updatePeriod;

		// Highlight previous obstacles
		if (highlightCounter < 3 && previousObstacles != null) {
			if (ball.isRunning()) {
				for (int i = 0; i < previousObstacles.size(); ++i)
					previousObstacles.get(i).show(canvas,
							background.getPosition());
				highlightCounter++;
			}
		}

		// Show rain, if any
		if (rain != null) {
			rain.show(canvas);
			if (updateCounter == 0 || updateCounter == 3)
				rain.updateToTheNextImage();
		}

		// Show teleporters, if any
		// for (int i = 0; i < gameData.getTeleporterList().size(); ++i) {
		// teleporters[i].show(canvas, background.getPosition());
		// teleporters[i].updateToTheNextImage();
		// }

		// Show conveyors, if any
		boolean conveyorInEffect = false;
		for (int i = 0; i < gameData.getConveyorList().size(); ++i) {
			conveyors[i].show(canvas, background.getPosition());
			if (updateCounter == 0)
				conveyors[i].updateToTheNextImage();
			if (conveyors[i].contains(ball.getPosition()) && !conveyorInEffect) {
				conveyorInEffect = true;
				Point increment = conveyors[i].getIncrement();
				if (!ball.isRunning()) {
					ball.projectOn(conveyors[i].getCenterLine());
					Point newPosition = new Point(ball.getPosition().x
							+ increment.x, ball.getPosition().y + increment.y);
					ball.setPosition(newPosition);
				} else
					ball.exhaustTheball();
			}
		}

		// Show pulling line, if any
		if (isDragging) {
			Paint paint = new Paint();
			paint.setColor(Color.GRAY);
			paint.setStrokeWidth(5);
			// paint.setAlpha(200);
			canvas.drawLine(ball.getPosition().x + background.getPosition().x,
					ball.getPosition().y + background.getPosition().y,
					junction.x, junction.y, paint);
		}

		// Show ball
		// TODO: polish cho nay lai nhaaaaaaa!!!!
		if (ball.isRunning() || ball.getState() == Character.motionState.DEATH) {
			Point shadowPos1 = new Point(ball.getPosition());
			double quantum = Parameters.timer / 1000.0;
			elapsedTime += quantum;

			// TODO: this object must be of type Obstacle and then be
			// type-casted into Segment
			LinkedList<Obstacles> resultObstacles = isNextPostAvailable();

			// TODO: DOUBLE CHECK THESE CONDITIONS, change the method or at
			// least the name
			if (endGame == 1) {
				ball.setState(Character.motionState.DEATH);
				ball.update(elapsedTime, quantum);
				endGame++;
			} else if (endGame == 2) {
				ball.update(elapsedTime, quantum);
			} else if (endGame == 3) { // MUST BE 3 (not 2 nhe)
				endGame = 0;

				int lives = GameManager.user.getCurrentLives();
				if (lives > 0) { // TODO: set lives
					GameManager.user.setCurrentLives(lives - 1);
				} else {
					GameManager
							.setCurrentState(GameManager.GameState.MAIN_MENU);
				}

				Calendar currentTime = Calendar.getInstance();

				int ss = currentTime.get(Calendar.SECOND);
				int mm = currentTime.get(Calendar.MINUTE);
				int hh = currentTime.get(Calendar.HOUR_OF_DAY);
				int yyyy = currentTime.get(Calendar.YEAR);
				int MM = currentTime.get(Calendar.MONTH) + 1;
				int dd = currentTime.get(Calendar.DAY_OF_MONTH);

				String strCurrentTime = String.format("%04d", yyyy) + "-"
						+ String.format("%02d", MM) + "-"
						+ String.format("%02d", dd) + " "
						+ String.format("%02d", hh) + ":"
						+ String.format("%02d", mm) + ":"
						+ String.format("%02d", ss);
				Log.i("DATETIME", strCurrentTime);

				GameManager.user.setLastTime(strCurrentTime);
				UserWriter.writeUserData(GameManager.user,
						Parameters.pthUserData);

				this.restart();
				updateView();
				GameManager.mainView.invalidate();
				return;
			}

			else if (resultObstacles == null)
				ball.update(elapsedTime, quantum); // these parameters are only
													// for placeholder purpose,
													// they have no meaning till
													// this moment!!!
			else if (resultObstacles != null && endGame == 0) {
				bloibs[bloibIndex].start();
				bloibIndex = bloibIndex == bloibs.length - 1 ? 0
						: bloibIndex + 1;
				// ball.bounce(obstacle);

				for (Obstacles obstacle : resultObstacles) {
					obstacle.interact(ball);
				}

				ball.update(elapsedTime, quantum);
			}

			// if (ball.update(elapsedTime, quantum))
			// {
			// double acc = getAccelerationUnderTheBall();
			// if (ball.getCurrentAcceleration() != acc)
			// changeTheBallAcceleration(acc);
			// }

			// Show ball's shadow
			if (ball.getState() == Character.motionState.MOVING) {
				Point temp = new Point(ball.getPosition());
				Point shadowPos2 = Helper.Point_GetMirrorFrom(temp, shadowPos1);
				(new Character(shadowPos2)).showShadow(canvas,
						background.getPosition(), 50);
				(new Character(shadowPos1)).showShadow(canvas,
						background.getPosition(), 100);
			}

			// Show ball
			ball.show(canvas, background.getPosition());

			// If the ball hits the teleporter, if any
			// TODO: teleporter
			// boolean isBallSucked = false;
			// double min = (Parameters.dTeleRadius + Parameters.dBallRadius) /
			// 2;
			// for (int i = 0; i < gameData.getTeleporterList().size(); ++i) {
			// double distance = Helper
			// .Point_GetDistanceFrom(ball.getPosition(), gameData
			// .getTeleporterList().get(i));
			// if (distance <= min) {
			// isBallSucked = true;
			// if (!amulet) {
			// teleportTheBall(i);
			// amulet = true;
			// }
			// break;
			// }
			// }
			// if (!isBallSucked)
			// amulet = false;

			// If the ball stops running
			// TODO: polish: original is !ball.isRunning()
			if (ball.getState() == Character.motionState.STANDING) {
				elapsedTime = 0.0;
				updateView();
				numOfCollisions = 0;
				//
				Paint paint = new Paint();
				paint.setStyle(Style.FILL);
				BitmapShader shader = new BitmapShader(
						Parameters.bmpTextureWallpaper, TileMode.REPEAT,
						TileMode.REPEAT);
				paint.setShader(shader);
				canvas.drawRect(0, 0, Parameters.dMaxWidth,
						Parameters.dMaxHeight, paint);
				this.show(canvas);
			}

			// If the ball is outside the visible region
			if (isBallOutOfView())
				updateView();
		} else
			// ball is not running
			ball.show(canvas, background.getPosition());

		// draw microwave
		microwave.show(canvas, background.getPosition());

		// draw hearts
		int lives = GameManager.user.getCurrentLives();
		for (int i = 0; i < lives; i++) {
			heartRedArr[i].show(canvas);
		}
		for (int i = lives; i < heartBlackArr.length; i++) {
			heartBlackArr[i].show(canvas);
		}

		// draw gear-up button
		gearUpButton.show(canvas);

		// draw pause button
		pauseButton.show(canvas);

		// check level up
		if (Helper.Point_GetDistanceFrom(ball.getPosition(),
				gameData.getHolePos()) < 0.65 * Parameters.dBallRadius) {

			// capture background screenshot
			GameManager.captureScreen();
			GameManager.setCurrentState(GameManager.GameState.FINISH_LVL_MENU);
			GameManager.redrawScreen();

			// Do a down on the mutex
			Parameters.mutex.acquire();
			// Critical region
			GameManager.levelUp(turn);
			// ------------------
			// Do an up on the mutex
			Parameters.mutex.release();
		}
	}

	public boolean isRunning() {
		// if (gameData.getTeleporterList().size() > 0)
		// return true;
		if (obstacleList[ObstacleIdx.Blackhole.getValue()] != null)
			return true;
		if (gameData.getConveyorList().size() > 0)
			return true;
		if (gameData.isRain())
			return true;
		if (this.ball.isRunning() || isDragging
				|| ball.getState() == Character.motionState.DEATH)
			return true;
		return false;
	}

	public boolean isBallRunning() {
		return this.ball.isRunning();
	}

	public void resume() {
		this.firstTimeShow = true;
	}

	public void flushData() {
		for (int i = 0; i < bloibs.length; ++i) {
			if (bloibs[i] != null) {
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

	public void restart() {
		this.firstTimeShow = true;
		ball = new Character(gameData.getStartPos());
		updateView();
		turn = 0;
	}

	public int getScore() {
		return this.turn;
	}

	public Point getBallPos() {
		return this.ball.getPosition();
	}

	private void updateView() {
		Point point = new Point(ball.getPosition().x
				+ background.getPosition().x, ball.getPosition().y
				+ background.getPosition().y);
		background.updateView(point);
		isUpdateView = true;
	}

	private boolean isBallOutOfView() {
		Point point = new Point(ball.getPosition().x
				+ background.getPosition().x, ball.getPosition().y
				+ background.getPosition().y);
		if (point.x < 0 || point.x > Parameters.dMaxWidth || point.y < 0
				|| point.y > Parameters.dMaxHeight)
			return true;
		return false;
	}

	private void initBall(Point direction) {
		ball.init(direction);
		elapsedTime = 0.0;
	}

	// private void teleportTheBall(int currentTeleporter) {
	// Random generator = new Random();
	// int index = 0;
	// while ((index = generator.nextInt(gameData.getTeleporterList().size()))
	// == currentTeleporter)
	// ;
	// Point newPosition = gameData.getTeleporterList().get(index);
	//
	// double acceleration = Parameters.grassFrictionAcceleration;
	// double velocity = ball.getInstantVelocity(elapsedTime) + 12;
	// ball.setPosition(newPosition);
	//
	// int incrementX = 0;
	// int incrementY = 0;
	// while ((incrementX = generator.nextInt(41)) == 20)
	// ;
	// while ((incrementY = generator.nextInt(41)) == 20)
	// ;
	// incrementX -= 20;
	// incrementY -= 20;
	//
	// Ray direction = new Ray(ball.getPosition(), new Point(newPosition.x
	// + incrementX, newPosition.y + incrementY));
	// initBall(velocity, acceleration, direction);
	// }

	private void showBackground(Canvas canvas) {
		// int rad = Parameters.dBallRadius;

		if (isDragging || isPreviouslyDragging || isUpdateView || firstTimeShow
				|| isPreviouslyBackgroundDragging) {
			showFullBackground(canvas);
			isUpdateView = false;
			firstTimeShow = false;
			isPreviouslyDragging = false;
			isPreviouslyBackgroundDragging = false;
		} else if (gameData.isRain())
			showFullBackground(canvas);
		else if (ball.isRunning()) {
			// background.showEx(canvas, ball.getPosition().x - rad - rad,
			// ball.getPosition().y - rad - rad, 4 * rad, 4 * rad);
			// showReflectors(canvas);
			showFullBackground(canvas);

			// // Show background under the teleporters
			// int teleRad = Parameters.dTeleRadius;
			// for (int i = 0; i < gameData.getTeleporterList().size(); ++i)
			// {
			// Point p = gameData.getTeleporterList().get(i);
			// background.showEx(canvas, p.x - teleRad - 3, p.y - teleRad - 3, 2
			// * teleRad + 6, 2 * teleRad + 6);
			// }
		} else // Show background under the stay-still ball
		{
			// background.showEx(canvas, ball.getPosition().x - rad - 2,
			// ball.getPosition().y - rad - 2, 2 * rad + 4, 2 * rad + 4);
			// showReflectors(canvas);
			showFullBackground(canvas);
		}

		// Show turn and score
		Paint paint = new Paint();
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(Parameters.dZoomParam / 2);
		paint.setColor(Color.LTGRAY);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);

		int score = ((Candies) obstacleList[ObstacleIdx.Candy.getValue()])
				.computeScore();

		canvas.drawText(
				"Turn: " + turn + "     Candies: " + Integer.toString(score),
				Parameters.dMaxWidth / 2, Parameters.dBallRadius, paint);
	}

	/*
	 * private double getAccelerationUnderTheBall() { // If the ball is in water
	 * puddle for (int i = 0; i < gameData.getWaterList().size(); ++i) { if
	 * (gameData.getWaterList().get(i).getBoundRect()
	 * .contains(ball.getPosition().x, ball.getPosition().y)) { return
	 * Parameters.waterFrictionAcceleration; } }
	 * 
	 * // If the ball is in sand region for (int i = 0; i <
	 * gameData.getSandList().size(); ++i) { if
	 * (gameData.getSandList().get(i).getBoundRect()
	 * .contains(ball.getPosition().x, ball.getPosition().y)) { return
	 * Parameters.sandFrictionAcceleration; } }
	 * 
	 * // Otherwise return Parameters.grassFrictionAcceleration; }
	 */
	private void showFullBackground(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		// BitmapShader shader = new
		// BitmapShader(Parameters.bmpTextureWallpaper,
		// TileMode.REPEAT, TileMode.REPEAT);
		BitmapShader shader = new BitmapShader(Parameters.bmpTextureWallpaper,
				TileMode.MIRROR, TileMode.MIRROR);
		paint.setShader(shader);
		canvas.drawRect(0, 0, Parameters.dMaxWidth, background.getPosition().y,
				paint);
		canvas.drawRect(0, 0, background.getPosition().x,
				Parameters.dMaxHeight, paint);
		canvas.drawRect(background.getWidth() + background.getPosition().x, 0,
				Parameters.dMaxWidth, Parameters.dMaxHeight, paint);
		canvas.drawRect(0, background.getHeight() + background.getPosition().y,
				Parameters.dMaxWidth, Parameters.dMaxHeight, paint);

		background.showEx(canvas, -background.getPosition().x,
				-background.getPosition().y, Parameters.dMaxWidth,
				Parameters.dMaxHeight);
	}

	public void changeGear(Character.gearState gear) {
		this.ball.resetGear(gear);
	}
}