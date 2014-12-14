package fr.eurecom.engine;

import java.util.LinkedList;
import java.util.List;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Cutter;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.Log;

public class Character {
	private Point position = new Point(0, 0);
	private double acceleration;
	private double initialVelocity;
	private Ray direction;
	private motionState state; // isRunningState
	// private Physics physics;
	private LinkedList<Point> trajectoryList;
	private int positionIndex;
	// private DynamicBitmap charImgRoll;
	// private DynamicBitmap charImgStand;
	// private DynamicBitmap charImgDead;

	private DynamicBitmap[] allImg;

	public static enum motionState {
		STANDING(0), MOVING(1), DEATH(2);

		private final int value;

		private motionState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	public static enum gearState {
		NORMAL, HELMET, DRILL, SCHOLAR, TIME, FEEDER, NINJA, ANGEL
	};

	Point LastPost = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	int count = 0;
	int delta = 3;
	public static gearState gear;

	public Character(Point position) {
		this.position = position;
		this.acceleration = 0.0;
		this.initialVelocity = 0.0;
		this.direction = null;
		this.setState(motionState.STANDING);
		this.setTrajectoryList(null);
		this.positionIndex = -1;

		// Point[] arr = {new Point(0, 100)};
		// this.physics = new Physics(arr);

		// create image for character
		gear = gearState.NORMAL;
		this.allImg = new DynamicBitmap[3];
		this.allImg[motionState.MOVING.getValue()] = new DynamicBitmap(
				Parameters.bmpRoll, this.position, 0,
				Parameters.dBallRadius * 2, Parameters.dBallRadius * 2);
		// this.allImg[motionState.STANDING.getValue()] = new DynamicBitmap(
		// Parameters.bmpDumDumNormal, this.position);
		this.allImg[motionState.DEATH.getValue()] = new DynamicBitmap(
				Parameters.bmpDumDumAngel, this.position);

		resetGear(this.gear);
	}

	public void resetGear(gearState newGear) {
		this.gear = newGear;

		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.dumdum_normal);

		switch (gear) {
		case NORMAL:
			break;
		case HELMET:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_helmet);
			break;
		case DRILL:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_drill);
			break;
		case SCHOLAR:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_tracingray);
			break;
		case TIME:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_timedelay);
			break;
		case FEEDER:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_hungryfeeder);
			break;
		case NINJA:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_ninja);
			break;
		case ANGEL:
			bmp = BitmapFactory.decodeResource(App.getMyContext()
					.getResources(), R.drawable.dumdum_angel);
			break;
		}

		this.allImg[motionState.STANDING.getValue()] = new DynamicBitmap(bmp,
				this.position);
	}

	// TODO: change the Ray + initialVelocity into 1 variable presenting
	// velocity
	public void init(double acceleration, double initialVelocity, Ray direction) {
		this.acceleration = acceleration;
		this.initialVelocity = initialVelocity;
		this.direction = direction;
		this.setState(motionState.MOVING);
		this.positionIndex = 0;

		Point initVelocity = new Point(0, 0);

		initVelocity.x = this.direction.getSecondPoint().x
				- this.direction.getRoot().x;
		initVelocity.y = this.direction.getSecondPoint().y
				- this.direction.getRoot().y;

		this.setTrajectoryList(Game.getPhysics().computeTrajectory(position,
				initVelocity));
	}

	// TODO: the name is not really appropriate, find another one!
	public void bounce(Segment wall) {

		Point initVelocity = new Point(0, 0);

		initVelocity.x = this.direction.getSecondPoint().x
				- this.direction.getRoot().x;
		initVelocity.y = this.direction.getSecondPoint().y
				- this.direction.getRoot().y;

		// this.setTrajectoryList(physics.computeTrajectory(initVelocity,
		// getCurrentPosition(), this.positionIndex, wall));
		Point[] temp = Game.getPhysics().bouncing(initVelocity,
				getCurrentPosition(), this.positionIndex, wall);

		temp[1] = new Point((int) (temp[1].x), (int) (temp[1].y));

		this.setTrajectoryList(Game.getPhysics().computeTrajectory(temp[0],
				temp[1]));

		// exhaust the ball
		if (Math.abs(LastPost.x - temp[0].x) < delta
				&& Math.abs(LastPost.y - temp[0].y) < delta)
			count++;
		else
			count = 0;

		LastPost.x = temp[0].x;
		LastPost.y = temp[0].y;

		if (count == 10 || temp[1].y == 0) {
			setState(motionState.STANDING);
			position = temp[0];
			count = 0;
		}

		this.positionIndex = 0;
		this.direction = new Ray(new Point(0, 0), temp[1]);
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Point getFirstPosition() {
		if (this.getTrajectoryList() != null)
			return this.getTrajectoryList().get(0);
		return null;
	}

	public Point getLastPosition() {
		if (this.getTrajectoryList() != null)
			return this.getTrajectoryList().get(
					this.getTrajectoryList().size() - 1);
		return null;
	}

	public Point getCurrentPosition() {
		if (this.getTrajectoryList() != null && this.positionIndex > -1
				&& this.positionIndex < this.getTrajectoryList().size())
			return this.getTrajectoryList().get(this.positionIndex);
		return null;
	}

	public Point getNextPosition() {
		if (this.getTrajectoryList() != null
				&& this.positionIndex < this.getTrajectoryList().size() - 1)
			return this.getTrajectoryList().get(this.positionIndex + 1);
		return null;
	}

	public void show(Canvas canvas) {
		// Paint paint = new Paint();
		// paint.setStyle(Style.FILL_AND_STROKE);
		// paint.setColor(Color.WHITE);
		// canvas.drawCircle(position.x, position.y, Parameters.dBallRadius,
		// paint);

		this.allImg[motionState.MOVING.getValue()].show(canvas);
		this.allImg[motionState.MOVING.getValue()].updateToTheNextImage();
	}

	public void show(Canvas canvas, Point offset) { // offset of the background
		showShadow(canvas, offset, 100);

		switch (getState()) {
		case MOVING:
			this.allImg[motionState.MOVING.getValue()].setPosition(new Point(
					this.position.x - Parameters.dBallRadius, this.position.y
							- Parameters.dBallRadius));
			this.allImg[motionState.MOVING.getValue()].show(canvas, offset);
			this.allImg[motionState.MOVING.getValue()].updateToTheNextImage();
			break;
		case STANDING:
			this.allImg[motionState.STANDING.getValue()].setPosition(new Point(
					this.position.x - Parameters.posDumDumPivot.x - 2
							* Parameters.dBallRadius, this.position.y
							- Parameters.posDumDumPivot.y - 2
							* Parameters.dBallRadius));
			this.allImg[motionState.STANDING.getValue()].show(canvas, offset);
			break;
		case DEATH:
			this.allImg[motionState.DEATH.getValue()].setPosition(new Point(
					this.position.x - Parameters.posDumDumPivot.x - 2
							* Parameters.dBallRadius, this.position.y
							- Parameters.posDumDumPivot.y - 2
							* Parameters.dBallRadius));
			this.allImg[motionState.DEATH.getValue()].show(canvas, offset);
			break;
		}
	}

	public void showShadow(Canvas canvas, Point offset, int alpha) {
		Point tmp = new Point(position.x + offset.x, position.y + offset.y);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setAlpha(alpha);
		canvas.drawCircle(tmp.x, tmp.y, Parameters.dBallRadius, paint);
	}

	public boolean update(double elapsedTime, double quantum) throws Exception {

		switch (getState()) {
		case STANDING:
			return false;
		case MOVING:
			if (this.positionIndex >= this.getTrajectoryList().size())
				return false;
			else
				this.position = this.getTrajectoryList().get(
						++this.positionIndex);
			return true;
		case DEATH:
			if (this.positionIndex >= this.getTrajectoryList().size())
				return false;
			else
				this.position = this.getTrajectoryList().get(
						++this.positionIndex);
			return true;
		}

		return true;

		// if (getState() == motionState.STANDING)
		// return false;

		// double displacement = 0.5 * this.acceleration * elapsedTime *
		// elapsedTime + this.initialVelocity * elapsedTime;
		//
		// if (displacement < 1)
		// {
		// if (displacement < 0)
		// this.exhaustTheball();
		// return false;
		// }
		//
		// Circle c = new Circle(this.direction.getRoot(), displacement);
		// Point[] points = c.getCommonPointWith(this.direction.toLine());
		//
		// if (this.direction.roughlyContains(points[0]))
		// this.position = points[0];
		// else
		// this.position = points[1];
		//
		// if (elapsedTime + quantum > -this.initialVelocity /
		// this.acceleration)
		// {
		// this.initialVelocity = 0.0;
		// this.direction = null;
		// this.state = false;
		// }
		//
		// return true;

	}

	// public Character getBallAtTime(double elapsedTime) throws Exception {
	// if (state == false)
	// return new Character(this.position);
	//
	// double displacement = 0.5 * this.acceleration * elapsedTime
	// * elapsedTime + this.initialVelocity * elapsedTime;
	//
	// if (displacement < 1)
	// return new Character(this.position);
	//
	// Circle c = new Circle(this.direction.getRoot(), displacement);
	// Point[] points = c.getCommonPointWith(this.direction.toLine());
	//
	// if (this.direction.roughlyContains(points[0]))
	// return new Character(points[0]);
	// else
	// return new Character(points[1]);
	// }

	public double getInstantVelocity(double elapsedTime) {
		if (!this.isRunning())
			return 0.0;
		return this.initialVelocity + this.acceleration * elapsedTime;
	}

	public boolean isRunning() {
		return (this.getState() == motionState.MOVING);
	}

	public Ray getCurrentDirection() {
		return this.direction;
	}

	public double getCurrentAcceleration() {
		return this.acceleration;
	}

	public double getInitialVelocity() {
		return this.initialVelocity;
	}

	public boolean projectOn(Line line) throws Exception {
		if (this.isRunning())
			return false;

		Point newPosition = Helper.Point_GetProjectionOn(this.position, line);
		this.position.x = newPosition.x;
		this.position.y = newPosition.y;
		return true;
	}

	public void exhaustTheball() {
		this.initialVelocity = 0.0;
		this.direction = null;
		this.setState(motionState.STANDING);
	}

	public LinkedList<Segment> isOverWalls(LinkedList<Segment> wallList)
			throws Exception {
		LinkedList<Segment> resultList = new LinkedList<Segment>();
		int ballRad = Parameters.dBallRadius;

		for (int i = 0; i < wallList.size(); ++i) {
			Point projection = Helper.Point_GetProjectionOn(this.position,
					wallList.get(i).toLine());
			if (wallList.get(i).roughlyContains(projection)) {
				double distance = Helper.Point_GetDistanceFrom(this.position,
						wallList.get(i).toLine());
				if (distance < ballRad)
					resultList.add(wallList.get(i));
			} else {
				double distance1 = Helper.Point_GetDistanceFrom(wallList.get(i)
						.getFirstPoint(), this.position);
				double distance2 = Helper.Point_GetDistanceFrom(wallList.get(i)
						.getSecondPoint(), this.position);
				double distance = (distance1 < distance2) ? distance1
						: distance2;
				if (distance < ballRad)
					resultList.add(wallList.get(i));
			}
			if (resultList.size() == 2) {
				break;
			}
		}

		return resultList;
	}

	public LinkedList<Point> getTrajectoryList() {
		return trajectoryList;
	}

	private void setTrajectoryList(LinkedList<Point> trajectoryList) {
		this.trajectoryList = trajectoryList;
	}

	public motionState getState() {
		return state;
	}

	public void setState(motionState state) {
		this.state = state;

		if (this.state == motionState.DEATH) {
			this.trajectoryList = new LinkedList<Point>();
			int disp = Parameters.dBallRadius / 2;

			for (int y = position.y; y >= 0; y -= disp)
				this.trajectoryList.add(new Point(position.x, y));

			positionIndex = 0;

		}
	}
}