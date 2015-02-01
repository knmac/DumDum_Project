package fr.eurecom.engine;

import java.util.LinkedList;

import fr.eurecom.connectivity.DeviceDetailFragment;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Candies;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.Obstacles;
import fr.eurecom.engine.Character.gearState;
import fr.eurecom.engine.Game.MouseState;
import fr.eurecom.engine.Game.ObstacleIdx;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.Parameters.tagConnect;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

public class GameDuoHost extends Game {

	private double calFactorX = 0;
	private double calFactorY = 0;

	// private Boolean canPlay = true;
	private int bet = 0;
	private double oppElapsedTime = 0.0;

	private Character oppChar;

	public GameDuoHost(int bet) {
		super();
		try {
			this.bet = bet;
			String startPos = tagConnect.STARTPOS
					+ this.gameData.getStartPos().x + ","
					+ this.gameData.getStartPos().y;
			// Send its startPos
			DeviceDetailFragment.server.sendMessage(startPos);

			Thread.sleep(Parameters.sleepPeriod);
			// Receive opponent's startPos
			String msg = DeviceDetailFragment.server.getMessage();
			Log.i("CONNECTIVITY", msg);
			String tokens[] = msg.split(",");
			Point oppStartPoint = new Point(Integer.parseInt(tokens[1]),
					Integer.parseInt(tokens[2]));
			oppChar = new Character(oppStartPoint);

			// compute calibration factor
			calFactorX = (double) this.gameData.getStartPos().x
					/ (double) oppStartPoint.x;
			calFactorY = (double) this.gameData.getStartPos().y
					/ (double) oppStartPoint.y;

			Toast.makeText(App.getMyContext(), "You are Player 1",
					Toast.LENGTH_SHORT).show();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Helper.onConnectionError();
		}
	}

	@Override
	protected void decreaseLives() {
		GameManager.loseDuo(bet);
	}

	@Override
	public void show(Canvas canvas) throws Exception {
		super.show(canvas);

		animateOpp(canvas);

		String msg = DeviceDetailFragment.server.getMessage();

		if (msg == null || msg.equals(""))
			return;

		String tokens[] = msg.split(",");
		tokens[0] += ",";
		if (tokens[0].equals(tagConnect.WINDUO)) {
			GameManager.loseDuo(bet);
		} else if (tokens[0].equals(tagConnect.LOSEDUO)) {
			int candies = ((Candies) obstacleList[ObstacleIdx.Candy.getValue()])
					.computeScore();
			GameManager.winDuo(candies + bet);
		} else if (tokens[0].equals(tagConnect.STARTMOVE)) {
			double tmpX = (double) (Integer.parseInt(tokens[1])) * calFactorX;
			double tmpY = (double) (Integer.parseInt(tokens[2])) * calFactorY;
			Point p = new Point((int) tmpX, (int) tmpY);
			oppChar.setPosition(p);

			tmpX = (double) (Integer.parseInt(tokens[3])) * calFactorX;
			tmpY = (double) (Integer.parseInt(tokens[4])) * calFactorY;
			p = new Point((int) tmpX, (int) tmpY);
			oppChar.init(p);
			oppElapsedTime = 0.0;
		} else if (tokens[0].equals(tagConnect.FINMOVE)) {
			double tmpX = (double) (Integer.parseInt(tokens[1])) * calFactorX;
			double tmpY = (double) (Integer.parseInt(tokens[2])) * calFactorY;
			Point oppNewPos = new Point((int) tmpX, (int) tmpY);
			// oppChar.setPosition(oppNewPos);
		}
		// else if (tokens[0].equals(tagConnect.CHANGEGEAR)) {
		// String gearName = tokens[1];
		//
		// Character.gearState gear = gearState.NORMAL;
		// if (gearName.equals("NORMAL")) {
		// gear = gearState.NORMAL;
		// } else if (gearName.equals("ANGEL")) {
		// gear = gearState.ANGEL;
		// } else if (gearName.equals("DRILL")) {
		// gear = gearState.DRILL;
		// } else if (gearName.equals("FEEDER")) {
		// gear = gearState.FEEDER;
		// } else if (gearName.equals("HELMET")) {
		// gear = gearState.HELMET;
		// } else if (gearName.equals("NINJA")) {
		// gear = gearState.NINJA;
		// } else if (gearName.equals("SCHOLAR")) {
		// gear = gearState.SCHOLAR;
		// } else if (gearName.equals("TIME")) {
		// gear = gearState.TIME;
		// }
		//
		// oppChar.resetGear(gear);
		// }
	}

	private void animateOpp(Canvas canvas) throws Exception {
		if (oppChar.isRunning()) {
			double quantum = Parameters.timer / 1000.0;
			oppElapsedTime += quantum;

			LinkedList<Obstacles> resultObstacles = isNextPostAvailableDuo();

			// TODO: DOUBLE CHECK THESE CONDITIONS, change the method or at
			// least the name
			if (resultObstacles == null)
				oppChar.update(oppElapsedTime, quantum);
			else if (resultObstacles != null) {
				for (Obstacles obstacle : resultObstacles) {
					obstacle.interact(oppChar);
				}
				oppChar.update(oppElapsedTime, quantum);
			}

			// Show ball
			oppChar.showWithAlpha(canvas, background.getPosition(), 125);

			// If the ball stops running
			// TODO: polish: original is !ball.isRunning()
			/*
			 * if (ball.getState() == Character.motionState.STANDING) {
			 * oppElapsedTime = 0.0; updateView();
			 * 
			 * Paint paint = new Paint(); paint.setStyle(Style.FILL);
			 * BitmapShader shader = new BitmapShader(
			 * Parameters.bmpTextureWallpaper, TileMode.REPEAT,
			 * TileMode.REPEAT); paint.setShader(shader); canvas.drawRect(0, 0,
			 * Parameters.dMaxWidth, Parameters.dMaxHeight, paint);
			 * this.show(canvas); }
			 */
		} else
			// ball is not running
			oppChar.showWithAlpha(canvas, background.getPosition(), 125);
	}

	private LinkedList<Obstacles> isNextPostAvailableDuo() {
		Point current = oppChar.getCurrentPosition();
		Point next = oppChar.getNextPosition();

		if (current == null)
			return null;

		// ball gets out of scence
		// TODO
		// if (next == null) {
		//
		// if (oppChar.getState() != Character.motionState.DEATH)
		// oppChar.setState(Character.motionState.DEATH);
		// else {
		// decreaseLives();
		// updateView();
		// GameManager.mainView.invalidate();
		// }
		// // ++endGame;
		// return null;
		// } else if (oppChar.getState() == Character.motionState.DEATH)
		// return null;

		Point rangeStart = oppChar.getTrajectoryList().getFirst();
		Point rangeEnd = oppChar.getTrajectoryList().getLast();

		LinkedList<Obstacles> resultObstacles = new LinkedList<Obstacles>();
		Obstacles tmpObstacle;

		// check candy
		if (obstacleList[ObstacleIdx.Candy.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Candy.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// check platform
		if (obstacleList[ObstacleIdx.Platform.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Platform.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// check spikes
		if (obstacleList[ObstacleIdx.Spike.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Spike.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// check bees
		if (obstacleList[ObstacleIdx.Bee.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Bee.getValue()].ballInRange(
					next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// check blackhole, make this one the LAST!!!!
		if (obstacleList[ObstacleIdx.Blackhole.getValue()] != null) {
			tmpObstacle = obstacleList[ObstacleIdx.Blackhole.getValue()]
					.ballInRange(next, current, rangeStart, rangeEnd);
			if (tmpObstacle != null)
				resultObstacles.add(tmpObstacle);
		}

		// return null if there is no obstacles
		if (resultObstacles.size() == 0)
			return null;

		return resultObstacles;
	}
}
