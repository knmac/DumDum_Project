package fr.eurecom.engine;

import fr.eurecom.utility.Helper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Circle {
    private Point center = new Point(0, 0);
    private double radius;
    public enum RelationBetweenLineAndCircle {OUTSIDE, TANGENT, SECANT}

    public Circle(Point center, double radius) throws Exception
    {
        if (radius < 1)
            throw new Exception("The radius cannot be less than 1");
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
		return center;
	}
    public void setCenter(Point center) {
		this.center = center;
	}
    public double getRadius() {
		return radius;
	}
    public void setRadius(double radius) throws Exception {
    	if (radius < 1)
    		throw new Exception("The radius cannot be less than 1");    
    	
		this.radius = radius;
	}
    

    public boolean strictlyContains(Point p)
    {
        return (p.x - center.x) * (p.x - center.x) + (p.y - center.y) * (p.y - center.y) == radius * radius;
    }
    public boolean roughlyContains(Point p) throws Exception
    {
        if (this.strictlyContains(p))
            return true;
        else if ((new Circle(this.center, this.radius + 1)).strictlyContains(p))
            return true;
        else
        {
            if (this.radius == 1)
            {
                if (this.center.equals(p.x, p.y))
                    return true;
                else
                    return false;
            }
            else
            {
                if ((new Circle(this.center, this.radius - 1)).strictlyContains(p))
                    return true;
                else
                    return false;
            }
        }
    }
    public void show(Canvas canvas)
    {
        canvas.drawCircle(this.center.x, this.center.y, (float)this.radius, new Paint());
    }

    public RelationBetweenLineAndCircle getRelationWith(Line line) throws Exception
    {
        double distance = Helper.Point_GetDistanceFrom(this.center, line);
        if (distance > this.radius + 1)
            return RelationBetweenLineAndCircle.OUTSIDE;
        else if (distance < this.radius - 1)
            return RelationBetweenLineAndCircle.SECANT;
        else
            return RelationBetweenLineAndCircle.TANGENT;
    }
   
    public Point[] getCommonPointWith(Line line) throws Exception
    {
        RelationBetweenLineAndCircle relation = this.getRelationWith(line);

        if (relation == RelationBetweenLineAndCircle.OUTSIDE)
            throw new Exception("There's no common points between the circle and the line");

        Point first = this.getMappingFrom(line.getFirstPoint(), new Point(-center.x, -center.y));
        Point second = this.getMappingFrom(line.getSecondPoint(), new Point(-center.x, -center.y));
        Line fakeLine = new Line(first, second);

        double dX = fakeLine.getSecondPoint().x - fakeLine.getFirstPoint().x;
        double dY = fakeLine.getSecondPoint().y - fakeLine.getFirstPoint().y;
        double dRSquare = dX * dX + dY * dY;
        double D = fakeLine.getFirstPoint().x * fakeLine.getSecondPoint().y - fakeLine.getSecondPoint().x * fakeLine.getFirstPoint().y;
        int sign = (dY < 0) ? -1 : 1;

        if (relation == RelationBetweenLineAndCircle.SECANT)
        {
            int x1 = (int)((D * dY + sign * dX * Math.sqrt(this.radius * this.radius * dRSquare - D * D)) / dRSquare);
            int y1 = (int)((-D * dX + Math.abs(dY) * Math.sqrt(this.radius * this.radius * dRSquare - D * D)) / dRSquare);
            int x2 = (int)((D * dY - sign * dX * Math.sqrt(this.radius * this.radius * dRSquare - D * D)) / dRSquare);
            int y2 = (int)((-D * dX - Math.abs(dY) * Math.sqrt(this.radius * this.radius * dRSquare - D * D)) / dRSquare);

            Point[] result = new Point[2];
            result[0] = getMappingFrom(new Point(x1, y1), this.center);
            result[1] = getMappingFrom(new Point(x2, y2), this.center);
            return result;
        }
        else // relation == RelationBetweenLineAndCircle.TANGENT
        {
            this.radius = Helper.Point_GetDistanceFrom(this.center, fakeLine);
            int x = (int)((D * dY ) / dRSquare);
            int y = (int)((-D * dX) / dRSquare);

            Point[] result = new Point[1];
            result[0] = getMappingFrom(new Point(x, y), this.center);
            return result;
        }
    }
    private Point getMappingFrom(Point point, Point movingVector)
    {
        return new Point(movingVector.x + point.x, movingVector.y + point.y);
    }
}
