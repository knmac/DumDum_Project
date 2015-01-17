package fr.eurecom.dumdumgame;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import fr.eurecom.engine.Game;
import fr.eurecom.engine.Segment;
import fr.eurecom.utility.Parameters;

public class Platforms extends SupportiveObstacles {

	public LinkedList<Segment> reflectorList = new LinkedList<Segment>();
	
	public Platforms () {
	}
	
	private Platforms (Segment aWall) {
		reflectorList.add(aWall);
	}
	
	public Point getBottomRight() {
		
		Point mapBottomRight = new Point();
		
		if (reflectorList == null || reflectorList.size() < 1)
			return null;
		
		for (int i = 0; i < reflectorList.size(); i++) {
			mapBottomRight.x = reflectorList.get(i).getFirstPoint().x > mapBottomRight.x ? reflectorList
					.get(i).getFirstPoint().x : mapBottomRight.x;
			mapBottomRight.y = reflectorList.get(i).getFirstPoint().y > mapBottomRight.y ? reflectorList
					.get(i).getFirstPoint().y : mapBottomRight.y;
			mapBottomRight.x = reflectorList.get(i).getSecondPoint().x > mapBottomRight.x ? reflectorList
					.get(i).getSecondPoint().x : mapBottomRight.x;
			mapBottomRight.y = reflectorList.get(i).getSecondPoint().y > mapBottomRight.y ? reflectorList
					.get(i).getSecondPoint().y : mapBottomRight.y;
		}
		
		return mapBottomRight;
	}
	
	@Override
	public void addData(Object data, int zoomFactor, int shiftFactor) {
		super.addData(data, zoomFactor, shiftFactor);
		
		Collections.sort(reflectorList);
	}

	@Override
	protected void readData(Object data) {
		// TODO check data of exactly type LinkedList<Segment>. How?
		try {
			this.reflectorList = (LinkedList<Segment>) data;
		} catch (ClassCastException e) {
			Log.e("ERROR", "Type cast error in class Platform", e);
		}
	}

	@Override
	protected void shift(int shiftDisplacement) {

		for (int i = 0; i < reflectorList.size(); ++i) {

			reflectorList.get(i).setPoints(ShiftPoint(reflectorList.get(i).getFirstPoint(),shiftDisplacement),
							ShiftPoint(reflectorList.get(i).getSecondPoint(),shiftDisplacement));
		}
	}
	
	@Override
	protected void zoom(int zoomFactor) {
		for (int i = 0; i < reflectorList.size(); ++i) {
			Point zoomedFirst = ZoomPoint(reflectorList.get(i).getFirstPoint(),
					zoomFactor);
			Point zoomedSecond = ZoomPoint(reflectorList.get(i)
					.getSecondPoint(), zoomFactor);
			// reflectorList.get(i).setFirstPoint(ZoomPoint(reflectorList.get(i).getFirstPoint(),
			// zoomParam));
			// reflectorList.get(i).setSecondPoint(ZoomPoint(reflectorList.get(i).getSecondPoint(),
			// zoomParam));
			reflectorList.get(i).setPoints(zoomedFirst, zoomedSecond);
		}		
	}

	@Override
	public Point[] interact(Point initialVelocity, Point currentPosition, int currPosIdx) {
		return Game.getPhysics().bouncing(initialVelocity, currentPosition, currPosIdx, reflectorList.getFirst());
	}

	// Show reflective surfaces
	@Override
	public void show(Canvas canvas) {		
		for (int i = 0; i < this.reflectorList.size(); ++i) {
			Point first = this.reflectorList.get(i).getFirstPoint();
			Point second = this.reflectorList.get(i).getSecondPoint();
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(3);
			canvas.drawLine(first.x, first.y, second.x, second.y, paint);
		}		
	}

	// check if posToBeCheckd is within range of this obstacle within the limit of [rangeStart, rangeEnd]
	@Override 
	public Obstacles ballInRange(Point next, Point current, Point rangeStart, Point rangeEnd) {
		
		double minDistance = Double.MAX_VALUE;
		Segment returnedWall = null;
		int reflectorList_len = this.reflectorList.size();
		
		for (int i = 0; i < reflectorList_len; ++i) {
			Segment currentReflector = this.reflectorList.get(i);

			if (rangeStart.x < currentReflector.getFirstPoint().x
					&& rangeEnd.x < currentReflector.getFirstPoint().x)
				break;

			Point wall1 = new Point(currentReflector.getFirstPoint());
			Point wall2 = new Point(currentReflector.getSecondPoint());

			// equation of the wall
			double A = wall2.y - wall1.y;
			double B = -(wall2.x - wall1.x);
			double C = -wall1.x * (wall2.y - wall1.y) + wall1.y * (wall2.x - wall1.x);
			double vAB = Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));

			double distanceNextWall = Math.abs(A * next.x + B * next.y + C) / (vAB);
			double distanceCurrWall = Math.abs(A * current.x + B * current.y + C) / (vAB);

			double cPoint = -current.x * (next.y - current.y) + current.y * (next.x - current.x);
			double cPoint1 = -wall1.x * (wall2.y - wall1.y) + wall1.y * (wall2.x - wall1.x);

			double checkingInq1a = (next.y - current.y) * wall1.x - (next.x - current.x) * wall1.y + cPoint;
			double checkingInq1b = (next.y - current.y) * wall2.x - (next.x - current.x) * wall2.y + cPoint;

			double checkingIng2a = (wall2.y - wall1.y) * current.x - (wall2.x - wall1.x) * current.y + cPoint1;
			double checkingIng2b = (wall2.y - wall1.y) * next.x - (wall2.x - wall1.x) * next.y + cPoint1;

			if ((checkingInq1a * checkingInq1b <= 0 && checkingIng2a * checkingIng2b <= 0)) {
				double distancePointWall = Math.abs(A * current.x + B * current.y + C);

				if (distancePointWall < minDistance) {
					minDistance = distancePointWall;
					returnedWall = currentReflector;
				}
				
			} else if (distanceNextWall < Parameters.dBallRadius && distanceCurrWall > distanceNextWall) {
				double distNextWall1 = Math.sqrt(Math.pow(next.x - wall1.x, 2) + Math.pow(next.y - wall1.y, 2));
				double distNextWall2 = Math.sqrt(Math.pow(next.x - wall2.x, 2) + Math.pow(next.y - wall2.y, 2));
				double distWall1Wall2 = Math.sqrt(Math.pow(wall1.x - wall2.x, 2) + Math.pow(wall1.y - wall2.y, 2));

				double condition1 = Math.sqrt(Math.pow(distNextWall1, 2) - Math.pow(distanceNextWall, 2));
				double condition2 = Math.sqrt(Math.pow(distNextWall2, 2) - Math.pow(distanceNextWall, 2));

				double epsilon = 1;
				if (condition1 + condition2 > distWall1Wall2 - epsilon 
						&& condition1 + condition2 < distWall1Wall2 + epsilon) {
					if (distanceNextWall < minDistance) {
						minDistance = distanceNextWall;
						returnedWall = currentReflector;
					}
				}
			}
		}

		if (returnedWall == null)		
			return null;
		else
			return new Platforms(returnedWall);
	}
}
