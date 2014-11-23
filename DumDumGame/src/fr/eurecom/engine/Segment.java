package fr.eurecom.engine;

import java.util.Comparator;

import fr.eurecom.utility.Helper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Segment {
	
	// with great assumption that first point must have smaller x component than second point
	private Point firstPoint = new Point(0, 0);
    private Point secondPoint = new Point(0, 0);

    public Segment(Point firstPoint, Point secondPoint)
    {
    	// only use property function (getter/setter) to set points
        setFirstPoint(firstPoint);
        setSecondPoint(secondPoint);
    }
    public void setPoints(Point point1, Point point2) {
    	if (point1.x > point2.x)
    	{    		
    		this.firstPoint = point2;
    		this.secondPoint = point1;
    	}
    	else 
    	{
    		this.firstPoint = point1;
    		this.secondPoint = point2;
    	}
    }
    
    public Point getFirstPoint() {
		return firstPoint;
	}
    private void setFirstPoint(Point firstPoint) {
    	
    	if (this.secondPoint.x > firstPoint.x)
    	{    		
    		this.firstPoint = this.secondPoint;
    		this.secondPoint = firstPoint;
    	}
    	else
    		this.firstPoint = firstPoint;
	}
    public Point getSecondPoint() {
		return secondPoint;
	}
    private void setSecondPoint(Point secondPoint) {
    	if (this.firstPoint.x < secondPoint.x)
    	{    		
    		this.secondPoint = this.firstPoint;
    		this.firstPoint = secondPoint;
    	}
    	else
    		this.secondPoint = secondPoint;
	}

    public Line toLine()
    {
        return new Line(this.firstPoint, this.secondPoint);
    }

    public boolean strictlyContains(Point p)
    {
        double distance1 = Helper.Point_GetDistanceFrom(p, this.firstPoint);
        double distance2 = Helper.Point_GetDistanceFrom(p, this.secondPoint);
        if (this.toLine().StrictlyContains(p) && distance1 + distance2 == this.getLength())
        	return true;                        
        return false;
    }
    public boolean roughlyContains(Point p) throws Exception
    {
        Line line = this.toLine();

        if (line.RoughlyContains(p))
        {
            double distance1 = Helper.Point_GetDistanceFrom(p, this.firstPoint);
            double distance2 = Helper.Point_GetDistanceFrom(p, this.secondPoint);

            if (distance1 + distance2 <= this.getLength() + 2)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public double getLength()
    {
        return Helper.Point_GetDistanceFrom(this.firstPoint, this.secondPoint);
    }
    public boolean isDifferentFrom(Segment anotherSegment)
    {
        if (this.firstPoint.x != anotherSegment.firstPoint.x ||
            this.firstPoint.y != anotherSegment.firstPoint.y ||
            this.secondPoint.x != anotherSegment.secondPoint.x ||
            this.secondPoint.y != anotherSegment.secondPoint.y)
            return true;
        return false;
    }
    public Line getAbreastLine(Point point) throws Exception
    { 
        Point normalVector = (this.toLine()).GetNormalVector();
        return new Line(point, normalVector.x, normalVector.y);
    }
    public Rect getBoundRect()
    {
        int minX, maxX, minY, maxY;
        if (this.firstPoint.x > this.secondPoint.x)
        {
            maxX = this.firstPoint.x;
            minX = this.secondPoint.x;
        }
        else
        {
            maxX = this.secondPoint.x;
            minX = this.firstPoint.x;
        }

        if (this.firstPoint.y > this.secondPoint.y)
        {
            maxY = this.firstPoint.y;
            minY = this.secondPoint.y;
        }
        else
        {
            maxY = this.secondPoint.y;
            minY = this.firstPoint.y;
        }

        return new Rect(minX, minY, maxX, maxY);
    }
    
    public void show(Canvas canvas, Point offset)
    {       
        Point first = new Point(firstPoint.x + offset.x, firstPoint.y + offset.y);
        Point second = new Point(secondPoint.x + offset.x, secondPoint.y + offset.y);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(3);
        canvas.drawLine(first.x, first.y, second.x, second.y, paint);
    }
    
    
}
