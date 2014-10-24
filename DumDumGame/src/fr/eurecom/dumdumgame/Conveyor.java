package fr.eurecom.dumdumgame;


import fr.eurecom.engine.Line;
import fr.eurecom.engine.Segment;
import fr.eurecom.utility.Parameters;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public class Conveyor extends DynamicBitmap{
    ConveyorType conveyorType;
    Point firstPoint = new Point(0, 0);
    Point secondPoint = new Point(0, 0);
    Point increment = new Point(0, 0);

    public enum ConveyorType { TOP_DOWN, BOTTOM_UP, LEFT_RIGHT, RIGHT_LEFT }

    public Conveyor(Segment s)
    {
    	super();
        Point position;
        Bitmap[] bmpConveyor = null;
        ConveyorType conveyorType;
        int width, height;
        this.increment = new Point(0, 0);

        if (s.getFirstPoint().x == s.getSecondPoint().x) // vertical conveyor
        {
            if (s.getFirstPoint().y < s.getSecondPoint().y) // top-down conveyor
            {
                position = s.getFirstPoint();
                bmpConveyor = Parameters.bmpConveyorDown;
                conveyorType = ConveyorType.TOP_DOWN;
                increment.y += Parameters.dConveyorSpeed;
            }
            else // bottom-up conveyor
            {
                position = s.getSecondPoint();
                bmpConveyor = Parameters.bmpConveyorUp;
                conveyorType = ConveyorType.BOTTOM_UP;
                increment.y -= Parameters.dConveyorSpeed;
                
                //them cai nay nha :vasdsadsadsadsasdsds
            }
            width = Parameters.dConveyorWidth;
            height = (int)s.getLength();
        }
        else // horizontal conveyor
        {
            if (s.getFirstPoint().x < s.getSecondPoint().x) // left-right conveyor
            {
                position = s.getFirstPoint();
                bmpConveyor = Parameters.bmpConveyorRight;
                conveyorType = ConveyorType.LEFT_RIGHT;
                increment.x += Parameters.dConveyorSpeed;
            }
            else // right-left conveyor
            {
                position = s.getSecondPoint();
                bmpConveyor = Parameters.bmpConveyorLeft;
                conveyorType = ConveyorType.RIGHT_LEFT;
                increment.x -= Parameters.dConveyorSpeed;
            }
            width = (int)s.getLength();
            height = Parameters.dConveyorWidth;
        }

        super.constructor(bmpConveyor, position, 0, width, height);
        
        this.conveyorType = conveyorType;
        this.firstPoint = s.getFirstPoint();
        this.secondPoint = s.getSecondPoint();      
    }

    public ConveyorType getType()
    {
        return this.conveyorType;
    }

    public Point getIncrement()
    {
        return this.increment;
    }    
    
    public boolean contains(Point point)
    {
        Rect boundRect = new Rect();

        switch (this.conveyorType)
        {
            case LEFT_RIGHT:
                boundRect = new Rect(firstPoint.x, firstPoint.y, firstPoint.x + this.getWidth(), firstPoint.y + this.getHeight());
                break;
            case RIGHT_LEFT:
                boundRect = new Rect(secondPoint.x, secondPoint.y, secondPoint.x + this.getWidth(), secondPoint.y + this.getHeight());
                break;
            case TOP_DOWN:
                boundRect = new Rect(firstPoint.x, firstPoint.y, firstPoint.x + this.getWidth(), firstPoint.y + this.getHeight());
                break;
            case BOTTOM_UP:
                boundRect = new Rect(secondPoint.x, secondPoint.y, secondPoint.x + this.getWidth(), secondPoint.y + this.getHeight());
                break;
        }

        boundRect.left -= (int)(0.3 * Parameters.dBallRadius);
        boundRect.top -= (int)(0.3 * Parameters.dBallRadius);
        boundRect.right += (int)(0.3 * Parameters.dBallRadius);
        boundRect.bottom += (int)(0.3 * Parameters.dBallRadius);        
        
        return boundRect.contains(point.x, point.y);
    }

    public Line getCenterLine()
    {
        int r = Parameters.dConveyorWidth / 2;

        if (this.conveyorType == ConveyorType.TOP_DOWN || this.conveyorType == ConveyorType.BOTTOM_UP)
            return new Line(new Point(firstPoint.x + r, firstPoint.y), new Point(secondPoint.x + r, secondPoint.y));
        else
            return new Line(new Point(firstPoint.x, firstPoint.y + r), new Point(secondPoint.x, secondPoint.y + r));
    }
}