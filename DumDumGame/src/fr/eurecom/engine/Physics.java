package fr.eurecom.engine;

import java.util.LinkedList;

import fr.eurecom.utility.Parameters;
import android.graphics.Point;

// Assume that every objects has the same mass so that all forces will cause
// the same effect. In this case, forces are treated as acceleration. Otherwise, we must
// indicate clearly the mass of each object and then compute the acceleration based on that.


public class Physics {

	private Point[] forceArr;
	private Point netForce;
	static private double interval = Parameters.timer / 500.0;
	private Point mapBottomRight = null; 
	private double coeff;
	
	public Physics(Environment environment, Point mapBottomRight) {
		this.forceArr = environment.getForceList();
		this.coeff = environment.getDepletingCoef();
		computeNetForce();
		this.mapBottomRight = mapBottomRight; 
	}
	
	public void computeNetForce() {
		
		netForce = new Point(0, 0);
		
		for (int i = 0; i < forceArr.length; ++i) {
			netForce.x += forceArr[i].x;
			netForce.y += forceArr[i].y;
		}
	}
	
	public LinkedList<Point> computeTrajectory(Point initPosition, Point initVelocity) {
		// assume we have no environmental (air, water) resistance
		
		LinkedList<Point> positionList = new LinkedList<Point>();
		double t = 0;
		
		Point new_pos = new Point(initPosition);
		positionList.add(new_pos);
		
		// remember that y-axis pointing downward
		while(new_pos.y <= this.mapBottomRight.y) {
			
			new_pos = new Point(initPosition);
			
			// projectile motion
			new_pos.x = (int) Math.ceil(netForce.x * t * t / 2 + initVelocity.x * t + initPosition.x);
			new_pos.y = (int) Math.ceil(netForce.y * t * t / 2 + initVelocity.y * t + initPosition.y);
			
			t += interval;
			
			positionList.add(new_pos);
		}
		
		// Remove exessive duplication
		int i = 1;
		while (i < positionList.size()) {
			if (positionList.get(i).x == positionList.get(i-1).x &&
					positionList.get(i).y == positionList.get(i-1).y) {
				positionList.remove(i);
			} else {
				i++;
			}
		}
		
		return positionList;		
	}
	
	public Point[] bouncing (Point initVelocity, Point currentPosition, int currPosIdx, Segment wall) {
		
		// assume we have no environmental (air, water) resistance
		
		Point currentVelocity = new Point();
		double currTimePnt;

		// get velocity at current position
		currTimePnt = currPosIdx * interval;

		currentVelocity.x = (int) Math.ceil(netForce.x * currTimePnt
				+ initVelocity.x);
		currentVelocity.y = (int) Math.ceil(netForce.y * currTimePnt
				+ initVelocity.y);

		// find intersection point of ball trajectory with the wall
		double a1 = currentVelocity.y;
		double b1 = -currentVelocity.x;
		double c1 = -currentPosition.x * currentVelocity.y + currentPosition.y
				* currentVelocity.x;

		double a2 = wall.getSecondPoint().y - wall.getFirstPoint().y;
		double b2 = -(wall.getSecondPoint().x - wall.getFirstPoint().x);
		double c2 = -(wall.getFirstPoint().x
				* (wall.getSecondPoint().y - wall.getFirstPoint().y) - wall
				.getFirstPoint().y
				* (wall.getSecondPoint().x - wall.getFirstPoint().x));

		Point interPoint = SolveEquation(a1, b1, c1, a2, b2, c2);
		
		Point[] temp = new Point[2];
		if (interPoint == null) {
			temp[0] = currentPosition;
			temp[1] = currentVelocity;
			
			return temp;
		}
		
		// get the new velocity by reflection rule and compute new trajectory
		Point X1 = new Point(interPoint.x - currentVelocity.x, interPoint.y
				- currentVelocity.y);
		
		if ((X1.x - interPoint.x) * (currentPosition.x - interPoint.x) + (X1.y - interPoint.y) * (currentPosition.y - interPoint.y) < 0)
		{
			temp[0] = currentPosition;
			temp[1] = currentVelocity;
			
			return temp;
		}
		
		Point dirVecWall = new Point(wall.getSecondPoint().x
				- wall.getFirstPoint().x, wall.getSecondPoint().y
				- wall.getFirstPoint().y);

		a1 = dirVecWall.x;
		b1 = dirVecWall.y;
		c1 = -dirVecWall.x * interPoint.x - dirVecWall.y * interPoint.y;

		a2 = dirVecWall.y;
		b2 = -dirVecWall.x;
		c2 = -X1.x * dirVecWall.y + X1.y * dirVecWall.x;

		Point centerPoint = SolveEquation(a1, b1, c1, a2, b2, c2);
		Point X2 = new Point(2 * centerPoint.x - X1.x, 2 * centerPoint.y - X1.y);

		Point newVelocity = new Point(X2.x - interPoint.x, X2.y - interPoint.y);

		temp[0] = interPoint;
		temp[1] = newVelocity;

		// backoff the ball along the incident vector from the intersection
		// point (temp[0])
		// so that the distance from its center to the wall equals radius
		Point dirWall = new Point(wall.getSecondPoint().x
				- wall.getFirstPoint().x, wall.getSecondPoint().y
				- wall.getFirstPoint().y);
		double dotProd = dirWall.x * currentVelocity.x + dirWall.y
				* currentVelocity.y;
		double lenProd = Math.sqrt((Math.pow(currentVelocity.x, 2) + Math.pow(
				currentVelocity.y, 2))
				* (Math.pow(dirWall.x, 2) + Math.pow(dirWall.y, 2)));
		double cosInAngle = Math.abs(dotProd / lenProd);

		double displacement = Parameters.dBallRadius
				/ Math.sqrt(1 - Math.pow(cosInAngle, 2));

		double lenIniVel = Math.sqrt(Math.pow(currentVelocity.x, 2)
				+ Math.pow(currentVelocity.y, 2));
		temp[0].x = (int) (temp[0].x - currentVelocity.x / lenIniVel
				* displacement); // move back along the incident vector a length
									// of displacement
		temp[0].y = (int) (temp[0].y - currentVelocity.y / lenIniVel
				* displacement); // move back along the incident vector a length
									// of displacement

		temp[1].x *= (1 - this.coeff);
		temp[1].y *= (1 - this.coeff);

		return temp;
	}
	
	// this function is obsolette
	public LinkedList<Point> computeTrajectory(Point initVelocity, Point currentPosition, int currPosIdx, Segment wall) {
		// assume we have no environmental (air, water) resistance
		
		LinkedList<Point> positionList = new LinkedList<Point>();
		double t = 0;
		Point new_pos;
		
		Point currentVelocity = new Point();
		double currTimePnt;
		
		// get velocity at current position
		currTimePnt = currPosIdx * interval;
		
		currentVelocity.x = (int) Math.ceil(netForce.x * currTimePnt + initVelocity.x);
		currentVelocity.y = (int) Math.ceil(netForce.y * currTimePnt + initVelocity.y);
		
		// find intersection point of ball trajectory with the wall
		double a1 = currentVelocity.y;
		double b1 = - currentVelocity.x;
		double c1 = - currentPosition.x * currentVelocity.y + currentPosition.y * currentVelocity.x;
		
		double a2 = wall.getSecondPoint().y - wall.getFirstPoint().y;
		double b2 = - (wall.getSecondPoint().x - wall.getFirstPoint().x);
		double c2 = - (wall.getFirstPoint().x * (wall.getSecondPoint().y - wall.getFirstPoint().y) 
				- wall.getFirstPoint().y * (wall.getSecondPoint().x - wall.getFirstPoint().x));
		
		Point interPoint = SolveEquation(a1, b1, c1, a2, b2, c2); // check when return NULL!!!
		
		// get the new velocity by reflection rule and compute new trajectory
		Point X1 = new Point (interPoint.x - currentVelocity.x, interPoint.y - currentVelocity.y);
		Point dirVecWall = new Point (wall.getSecondPoint().x - wall.getFirstPoint().x, 
				wall.getSecondPoint().y - wall.getFirstPoint().y);
		 
		a1 = dirVecWall.x;
		b1 = dirVecWall.y;
		c1 = - dirVecWall.x * interPoint.x - dirVecWall.y * interPoint.y;
		
		a2 = dirVecWall.y;
		b2 = - dirVecWall.x;
		c2 = -X1.x * dirVecWall.y + X1.y * dirVecWall.x;
		
		Point centerPoint = SolveEquation(a1, b1, c1, a2, b2, c2);
		Point X2 = new Point (2 * centerPoint.x - X1.x, 2 * centerPoint.y - X1.y);
		
		Point newVelocity = new Point (X2.x - interPoint.x, X2.y - interPoint.y);
		

		new_pos = new Point(interPoint);
		positionList.add(new_pos);
		
		// remember that y-axis pointing downward
		while(new_pos.y <= this.mapBottomRight.y) {	// change back to screen height!!!!!!!
			
			new_pos = new Point(interPoint);
			
			// projectile motion
			new_pos.x = (int) Math.ceil(netForce.x * t * t / 2 + newVelocity.x * t + interPoint.x);
			new_pos.y = (int) Math.ceil(netForce.y * t * t / 2 + newVelocity.y * t + interPoint.y);
			
			t += interval;
			
			positionList.add(new_pos);
		}
		
		return positionList;		
	}
	
	public LinkedList<Point> computeTrajectory(Point initPosition, Point initVelocity, double resistanceCoef) {
		//http://vatlyvietnam.org/forum/showthread.php?t=10637
	
		LinkedList<Point> positionList = new LinkedList<Point>();
		double t = 0;
		double mass = 1;
		double g = 9.8;
		
		Point new_pos = new Point(initPosition);
		positionList.add(new_pos);
		
		// remember that y-axis pointing downward
		while(new_pos.y <= this.mapBottomRight.y) {
			
			new_pos.x = (int)Math.ceil(mass * initVelocity.x / resistanceCoef * (1 - Math.exp(-(resistanceCoef / mass * t))));
			new_pos.y = (int)Math.ceil(mass * g / resistanceCoef * t + mass*mass*g / (resistanceCoef * resistanceCoef) * (Math.exp(-resistanceCoef / mass * t) - 1));
		}
		
		return positionList;
	}
	
	
	// Solve the equations: A1x + B1y + C1 = 0 and A2x + B2y + C2 = 0
	public Point SolveEquation (double A1, double B1, double C1, double A2, double B2, double C2) {
		C1 = -C1;
		C2 = -C2;
		double D = A1 * B2 - A2 * B1;
		double Dx = C1 * B2 - B1 * C2;
		double Dy = A1 * C2 - A2 * C1;
		
		if (D == 0.0)
			return null;

		return new Point((int)(Dx / D), (int)(Dy / D));
		
	}
}