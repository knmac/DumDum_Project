package fr.eurecom.dumdumgame;

import java.util.LinkedList;

import fr.eurecom.engine.Candy;
import fr.eurecom.engine.Character;
import fr.eurecom.engine.Segment;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

public class Candies extends SupportiveObstacles {
	LinkedList<Candy> candyList = new LinkedList<Candy>();

	public Candies() {
	}

	private Candies(Candy aCandy) {
		candyList.add(aCandy);
	}

	@Override
	protected void readData(Object data) {
		// TODO check data of exactly type LinkedList<Segment>. How?
		try {
			this.candyList = (LinkedList<Candy>) data;
		} catch (ClassCastException e) {
			Log.e("ERROR", "Type cast error in class Platform", e);
		}
	}

	@Override
	protected void zoom(int zoomFactor) {
		for (int i = 0; i < candyList.size(); ++i) {
			Point point = ZoomPoint(candyList.get(i).getPos(), zoomFactor);
			candyList.get(i).getPos().set(point.x, point.y);
		}
	}

	@Override
	protected void shift(int shiftDisplacement) {
		for (int i = 0; i < candyList.size(); ++i) {
			Point point = ShiftPoint(candyList.get(i).getPos(),
					shiftDisplacement);
			candyList.get(i).getPos().set(point.x, point.y);
		}
	}

	@Override
	public void interact(Character ball) {
		this.candyList.getFirst().getEaten();
	}

	@Override
	public void show(Canvas canvas, Point offset) {
		for (int i = 0; i < this.candyList.size(); ++i) {
			this.candyList.get(i).show(canvas, offset);
		}
	}

	@Override
	public Obstacles ballInRange(Point posToBeChecked, Point pastPosition,
			Point rangeStart, Point rangeEnd) {
		for (Candy candy : candyList) {
			if (candy.isOverlapped(pastPosition, Parameters.dBallRadius))
				return new Candies(candy); // only 1 candy at a time
		}

		return null;
	}

	public int computeScore() {
		int score = 0;
		for (Candy candy : candyList) {
			if (!candy.isAvailable())
				score += candy.getValue();
		}

		return score;
	}

}
