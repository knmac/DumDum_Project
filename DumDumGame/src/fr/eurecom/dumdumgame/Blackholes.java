package fr.eurecom.dumdumgame;

import java.util.LinkedList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import fr.eurecom.data.Blackhole;
import fr.eurecom.data.Candy;
import fr.eurecom.engine.Character;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.Parameters;

public class Blackholes extends SupportiveObstacles {

	private LinkedList<Blackhole> teleporterList = new LinkedList<Blackhole>();
	Random rand = new Random(System.currentTimeMillis());

	@Override
	protected void readData(Object data) {
		// TODO check data of exactly type LinkedList<Segment>. How?
		try {
			LinkedList<Point> tmpPoint = (LinkedList<Point>) data;
			for (int i = 0; i < tmpPoint.size(); i++) {
				teleporterList.add(new Blackhole(tmpPoint.get(i)));
			}
		} catch (ClassCastException e) {
			Log.e("ERROR", "Type cast error in class Blackholes", e);
		}
	}

	@Override
	protected void zoom(int zoomFactor) {
		for (int i = 0; i < teleporterList.size(); ++i) {
			Point point = ZoomPoint(teleporterList.get(i).getCenter(),
					zoomFactor);
			teleporterList.get(i).setCenter(point.x, point.y);
		}
	}

	@Override
	protected void shift(int shiftDisplacement) {
		for (int i = 0; i < teleporterList.size(); ++i) {
			Point point = ShiftPoint(teleporterList.get(i).getCenter(),
					shiftDisplacement);
			teleporterList.get(i).setCenter(point.x, point.y);
		}
	}

	@Override
	public void interact(Character ball) {
		int exitBlackHole = 0;
		while (exitBlackHole <= 0 || exitBlackHole >= teleporterList.size()) {
			exitBlackHole = rand.nextInt(teleporterList.size());
		}
		Point exitPoint = teleporterList.get(exitBlackHole).getCenter();

		// Point[] temp = Game.getPhysics().bouncing(ball.getInitialVelocity(),
		// ball.getCurrentPosition(), ball.getPositionIndex(),
		// reflectorList.getFirst());

		int range = Math.max(Math.abs(ball.getInitialVelocity().x),
				Math.abs(ball.getInitialVelocity().y));
		Point initVelocity = new Point(); 
		while (Math.abs(initVelocity.x) <= Parameters.dTeleRadius || 
				Math.abs(initVelocity.y) <= Parameters.dTeleRadius) {
		initVelocity = new Point(rand.nextInt(2 * range) - range,
				rand.nextInt(2 * range) - range);
		}
		LinkedList<Point> newTraject = Game.getPhysics().computeTrajectory(
				exitPoint, initVelocity);

		// remove starting points of the trajectory until the ball is actually
		// out of the black hole
		while (teleporterList.get(exitBlackHole).isOverlapped(
				newTraject.getFirst(), Parameters.dBallRadius)) {
			newTraject.removeFirst();

			if (newTraject.size() == 0) {
				// generate new trajectory
				initVelocity = new Point(rand.nextInt(2 * range) - range,
						rand.nextInt(2 * range) - range);
				newTraject = Game.getPhysics().computeTrajectory(exitPoint,
						initVelocity);
			}
		}

		ball.createNewMovement(newTraject, initVelocity);
	}

	@Override
	public void show(Canvas canvas, Point offset) {
		for (Blackhole blackhole : teleporterList) {
			blackhole.show(canvas, offset);
		}
	}

	@Override
	public Obstacles ballInRange(Point posToBeChecked, Point pastPosition,
			Point rangeStart, Point rangeEnd) {
		int entranceBlackhole = -1;

		for (int i = 0; i < teleporterList.size(); i++) {
			if (teleporterList.get(i).isOverlapped(posToBeChecked,
					Parameters.dBallRadius)) {
				entranceBlackhole = i;
				break;
			}
		}

		if (entranceBlackhole == -1) {
			return null;
		}

		// make the first blackhole of the list be the one the the ball gets in
		Blackhole tmp = teleporterList.get(entranceBlackhole);
		teleporterList.remove(entranceBlackhole);
		teleporterList.addFirst(tmp);

		return this;
	}

}
