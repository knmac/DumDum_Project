package fr.eurecom.dumdumgame;

import java.util.LinkedList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import fr.eurecom.data.Bee;
import fr.eurecom.data.Blackhole;
import fr.eurecom.data.Candy;
import fr.eurecom.data.Spike;
import fr.eurecom.engine.Character;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.Parameters;

public class Bees extends SupportiveObstacles {

	private LinkedList<Bee> beeList = new LinkedList<Bee>();
	Random rand = new Random(System.currentTimeMillis());

	public Bees() {
	}

	private Bees(Bee aBee) {
		beeList.add(aBee);
	}

	@Override
	protected void readData(Object data) {
		try {
			this.beeList = (LinkedList<Bee>) data;
		} catch (ClassCastException e) {
			Log.e("ERROR", "Type cast error in class Blackholes", e);
		}
	}

	@Override
	protected void zoom(int zoomFactor) {
		for (int i = 0; i < beeList.size(); ++i) {
			LinkedList<Point> orbit = beeList.get(i).getOrbitVertices();
			for (int j = 0; j < orbit.size(); j++) {
				orbit.set(j, ZoomPoint(orbit.get(j), zoomFactor));
			}
			beeList.get(i).setOrbitVertices(orbit);
		}
	}

	@Override
	protected void shift(int shiftDisplacement) {
		for (int i = 0; i < beeList.size(); ++i) {
			LinkedList<Point> orbit = beeList.get(i).getOrbitVertices();
			for (int j = 0; j < orbit.size(); j++) {
				orbit.set(j, ShiftPoint(orbit.get(j), shiftDisplacement));
			}
			beeList.get(i).setOrbitVertices(orbit);
		}
	}

	@Override
	protected void refineData() {
		for (int i = 0; i < beeList.size(); ++i) {
			beeList.get(i).generateOrbit();
		}
	}

	@Override
	public void interact(Character ball) {
		if (Character.gear != Character.gearState.FEEDER)
			ball.setState(Character.motionState.DEATH);
	}

	@Override
	public void show(Canvas canvas, Point offset) {
		for (Bee bee : beeList) {
			bee.show(canvas, offset);
		}
	}

	@Override
	public Obstacles ballInRange(Point posToBeChecked, Point pastPosition,
			Point rangeStart, Point rangeEnd) {
		for (Bee bee : beeList) {
			if (bee.isOverlapped(pastPosition, Parameters.dBallRadius))
				return new Bees(bee);
		}

		return null;
	}

}
