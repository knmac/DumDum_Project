package fr.eurecom.engine;

import java.util.LinkedList;
import java.util.List;

import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

public class Character {
    private Point position = new Point(0, 0);
    private double acceleration;
    private double initialVelocity;
    private Ray direction;
    private boolean state;
    private Physics physics;
    private List<Point> positionList;
    private int positionIndex;

    public Character(Point position)
    {
        this.position = position;
        this.acceleration = 0.0;
        this.initialVelocity = 0.0;
        this.direction = null;
        this.state = false;
        this.positionList = null;
        this.positionIndex = -1;
        
        Point[] arr = {new Point(0, 100)};
        this.physics = new Physics(arr);
    }
    
    public void init(double acceleration, double initialVelocity, Ray direction)
    {
        this.acceleration = acceleration;
        this.initialVelocity = initialVelocity;
        this.direction = direction;
        this.state = true;
        this.positionIndex = -1;
    }

    public Point getPosition() {
		return position;
	}
    public void setPosition(Point position) {
		this.position = position;
	}    

    public void show(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(position.x, position.y, Parameters.dBallRadius, paint);
    }

    public void show(Canvas canvas, Point offset)
    {
    	showShadow(canvas, offset, 255);
    }
    
    public void showShadow(Canvas canvas, Point offset, int alpha)
    {
        Point tmp = new Point(position.x + offset.x, position.y + offset.y);
        Paint paint = new Paint();
        paint.setStyle(Style.FILL_AND_STROKE);        
        paint.setColor(Color.WHITE);
        paint.setAlpha(alpha);
        canvas.drawCircle(tmp.x, tmp.y, Parameters.dBallRadius, paint);      
    }    

    public boolean update(double elapsedTime, double quantum) throws Exception
    {
        if (state == false)
            return false;

//        double displacement = 0.5 * this.acceleration * elapsedTime * elapsedTime + this.initialVelocity * elapsedTime;
//
//        if (displacement < 1)
//        {
//            if (displacement < 0)
//                this.exhaustTheball();
//            return false;
//        }
//
//        Circle c = new Circle(this.direction.getRoot(), displacement);
//        Point[] points = c.getCommonPointWith(this.direction.toLine());
//
//        if (this.direction.roughlyContains(points[0]))
//            this.position = points[0];
//        else
//            this.position = points[1];
//
//        if (elapsedTime + quantum > -this.initialVelocity / this.acceleration)
//        {
//            this.initialVelocity = 0.0;
//            this.direction = null;
//            this.state = false;
//        }
//
//        return true;
        
        if (this.positionList == null) 
        {
        	Point initVelocity = new Point(0, 0);
        	
        	initVelocity.x = this.direction.getSecondPoint().x - this.direction.getRoot().x;
        	initVelocity.y = this.direction.getSecondPoint().y - this.direction.getRoot().y;
        	
        	this.positionList =  physics.computeOrbit(position, initVelocity);
        }
                
      	this.position = this.positionList.get(++this.positionIndex);
             
        return true;
    }

    public Character getBallAtTime(double elapsedTime) throws Exception
    {
        if (state == false)
            return new Character(this.position);

        double displacement = 0.5 * this.acceleration * elapsedTime * elapsedTime + this.initialVelocity * elapsedTime;

        if (displacement < 1)
            return new Character(this.position);

        Circle c = new Circle(this.direction.getRoot(), displacement);
        Point[] points = c.getCommonPointWith(this.direction.toLine());

        if (this.direction.roughlyContains(points[0]))
            return new Character(points[0]);
        else
            return new Character(points[1]);
    }

    public double getInstantVelocity(double elapsedTime)
    {
        if (!this.isRunning())
            return 0.0;
        return this.initialVelocity + this.acceleration * elapsedTime;
    }
    public boolean isRunning()
    {
        return this.state;
    }
    public Ray getCurrentDirection()
    {
        return this.direction;
    }
    public double getCurrentAcceleration()
    {
        return this.acceleration;
    }
    public double getInitialVelocity()
    {
        return this.initialVelocity;
    }
    public boolean projectOn(Line line) throws Exception
    {
        if (this.isRunning())
            return false;

        Point newPosition = Helper.Point_GetProjectionOn(this.position, line);
        this.position.x = newPosition.x;
        this.position.y = newPosition.y;
        return true;
    }
    public void exhaustTheball()
    {
        this.initialVelocity = 0.0;
        this.direction = null;
        this.state = false;
    }
    public LinkedList<Segment> isOverWalls(LinkedList<Segment> wallList) throws Exception
    {	
    	LinkedList<Segment> resultList = new LinkedList<Segment>();
        int ballRad = Parameters.dBallRadius;

        for (int i = 0; i < wallList.size(); ++i)
        {
            Point projection = Helper.Point_GetProjectionOn(this.position, wallList.get(i).toLine());
            if (wallList.get(i).roughlyContains(projection))
            {
                double distance = Helper.Point_GetDistanceFrom(this.position, wallList.get(i).toLine());
                if (distance < ballRad)
                    resultList.add(wallList.get(i));
            }
            else
            {
                double distance1 = Helper.Point_GetDistanceFrom(wallList.get(i).getFirstPoint(), this.position);
                double distance2 = Helper.Point_GetDistanceFrom(wallList.get(i).getSecondPoint(), this.position);
                double distance = (distance1 < distance2) ? distance1 : distance2;
                if (distance < ballRad)
                    resultList.add(wallList.get(i));
            }
            if (resultList.size() == 2) {
            	break;
            }
        }

        return resultList;
    }
}