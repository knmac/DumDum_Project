package fr.eurecom.dumdumgame;

import java.util.LinkedList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import fr.eurecom.data.Blackhole;
import fr.eurecom.data.Candy;
import fr.eurecom.data.Spike;
import fr.eurecom.engine.Character;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.Parameters;

public class Spikes extends SupportiveObstacles {

	private LinkedList<Spike> spikeList = new LinkedList<Spike>();
	Random rand = new Random(System.currentTimeMillis());

	public Spikes() {
	}

	private Spikes(Spike aSpike) {
		spikeList.add(aSpike);
	}
	
	@Override
	protected void readData(Object data) {
		try {
			LinkedList<Point> tmpPoint = (LinkedList<Point>) data;
			for (int i = 0; i < tmpPoint.size(); i++) {
				spikeList.add(new Spike(tmpPoint.get(i)));
			}
		} catch (ClassCastException e) {
			Log.e("ERROR", "Type cast error in class Blackholes", e);
		}
	}

	@Override
	protected void zoom(int zoomFactor) {
		for (int i = 0; i < spikeList.size(); ++i) {
			Point point = ZoomPoint(spikeList.get(i).getCenter(),
					zoomFactor);
			spikeList.get(i).setCenter(point.x, point.y);
		}
	}

	@Override
	protected void shift(int shiftDisplacement) {
		for (int i = 0; i < spikeList.size(); ++i) {
			Point point = ShiftPoint(spikeList.get(i).getCenter(),
					shiftDisplacement);
			spikeList.get(i).setCenter(point.x, point.y);
		}
	}

	@Override
	public void interact(Character ball) {
		
	}

	@Override
	public void show(Canvas canvas, Point offset) {
		for (Spike spike : spikeList) {
			spike.show(canvas, offset);
		}
	}

	@Override
	public Obstacles ballInRange(Point posToBeChecked, Point pastPosition,
			Point rangeStart, Point rangeEnd) {
		for (Spike spike : spikeList) {
			if (spike.isOverlapped(pastPosition, Parameters.dBallRadius))
				return new Spikes(spike);
		}

		return null;
	}

}
