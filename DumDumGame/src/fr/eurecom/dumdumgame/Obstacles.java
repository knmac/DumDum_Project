package fr.eurecom.dumdumgame;

import android.graphics.Canvas;
import android.graphics.Point;

public abstract class Obstacles {
	
	
	public void addData(Object data, int zoomFactor, int shiftFactor){		

		// read data into appropriate data structure (of a subclass)
		readData(data);
		
		// Shift map downward 1 to nullify negative number
		shift(1);

		// zoom
		zoom(zoomFactor);

		// Shift map downward to move the map to the center
		shift(shiftFactor);	
	}
	
	protected final Point ShiftPoint(Point p, int shiftParam) {
		return new Point(p.x + shiftParam, p.y + shiftParam);
	}
	
	protected final Point ZoomPoint(Point p, int zoomParam) {
		return new Point(p.x * zoomParam, p.y * zoomParam);
	}
	
	protected abstract void readData(Object data);
	protected abstract void zoom(int zoomFactor);
	protected abstract void shift(int shiftDisplacement);
	
	public abstract Point[] interact(Point initVelocity, Point currentPosition, int currPosIdx);
	public abstract void show(Canvas canvas);
	
	// check if posToBeCheckd is within range of this obstacle within the limit of [rangeStart, rangeEnd]
	public abstract Obstacles ballInRange(Point posToBeChecked, Point pastPosition, Point rangeStart, Point rangeEnd);
	public Obstacles ballInRange(Point posToBeChecked, Point pastPosition)
	{
		return ballInRange(posToBeChecked, pastPosition, null, null);
	}
	public Obstacles ballInRange(Point posToBeChecked)
	{
		return ballInRange(posToBeChecked, null);
	}
	

}
