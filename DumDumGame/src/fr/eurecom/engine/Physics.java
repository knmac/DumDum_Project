package fr.eurecom.engine;

import java.util.ArrayList;
import java.util.List;

import fr.eurecom.utility.Parameters;
import android.graphics.Point;

// Assume that every objects has the same mass so that all forces will cause
// the same effect. In this case, forces are treated as acceleration. Otherwise, we must
// indicate clearly the mass of each object and then compute the acceleration based on that.


public class Physics {

	private Point[] forceArr;
	private Point netForce;
	static private double interval = Parameters.timer / 1000.0;
	
	public Physics(Point[] forces) {
		this.forceArr = forces;
		computeNetForce();
	}
	
	public void computeNetForce() {
		
		netForce = new Point(0, 0);
		
		for (int i = 0; i < forceArr.length; ++i) {
			netForce.x += forceArr[i].x;
			netForce.y += forceArr[i].y;
		}
	}
	
	public List<Point> computeTrajectory(Point initPosition, Point initVelocity) {
		// assume we have no environmental (air, water) resistance
		
		List<Point> positionList = new ArrayList<Point>();
		double t = 0;
		
		Point new_pos = new Point(initPosition);
		
		// remember that y-axis pointing downward
		while(new_pos.y <= Parameters.dMaxHeight) {	// change back to screen height!!!!!!!
			
			// projectile motion
			new_pos = new Point(initPosition);
			new_pos.x = (int) Math.ceil(netForce.x * t * t / 2 + initVelocity.x * t + initPosition.x);
			new_pos.y = (int) Math.ceil(netForce.y * t * t / 2 + initVelocity.y * t + initPosition.y);
			
			t += interval;
			
			positionList.add(new_pos);
		}
		
		return positionList;		
	}
	
	public List<Point> computeTrajectory(Point initPosition, Point initVelocity, Point currentPosition, int currPosIdx, Segment wall) {
		// assume we have no environmental (air, water) resistance
		
		List<Point> positionList = new ArrayList<Point>();
		double t = 0;
		Point new_pos = new Point(initPosition);
		
		Point currentVelocity = new Point();
		double currTimePnt;
		
		currTimePnt = currPosIdx * interval;
		
		currentVelocity.x = (int) Math.ceil(netForce.x * currTimePnt + initVelocity.x);
		currentVelocity.y = (int) Math.ceil(netForce.y * currTimePnt + initVelocity.y);
		
		// TODO: find intersection point of ball trajectory with the ball, get the new velocity and compute new trajectory
		
		
		// remember that y-axis pointing downward
		while(new_pos.y <= Parameters.dMaxHeight) {	// change back to screen height!!!!!!!
			
			// projectile motion
			new_pos = new Point(initPosition);
			new_pos.x = (int) Math.ceil(netForce.x * t * t / 2 + initVelocity.x * t + initPosition.x);
			new_pos.y = (int) Math.ceil(netForce.y * t * t / 2 + initVelocity.y * t + initPosition.y);
			
			t += interval;
			
			positionList.add(new_pos);
		}
		
		return positionList;		
	}
	
	public Point[] computeInstantly(Point[] tempForce){
		return tempForce;
	}
	
}
