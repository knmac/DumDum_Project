package fr.eurecom.engine;

import fr.eurecom.utility.Helper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Segment {
	private Point firstPoint = new Point(0, 0);
    private Point secondPoint = new Point(0, 0);

    public Segment(Point firstPoint, Point secondPoint)
    {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    public Point getFirstPoint() {
		return firstPoint;
	}
    public void setFirstPoint(Point firstPoint) {
		this.firstPoint = firstPoint;
	}
    public Point getSecondPoint() {
		return secondPoint;
	}
    public void setSecondPoint(Point secondPoint) {
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
