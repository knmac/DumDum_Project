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
	
	private Boolean canPlay = true;
	private int bet = 0;
	
	public GameDuoHost(int bet) {
		super();
		try {
			this.bet = bet;
			String startPos = tagConnect.STARTPOS + this.gameData.getStartPos().x + "," + this.gameData.getStartPos().y;
			//Send its startPos
			DeviceDetailFragment.server.sendMessage(startPos);
			
				Thread.sleep(Parameters.sleepPeriod);
			//Receive opponent's startPos
			String msg = DeviceDetailFragment.server.getMessage();
			Log.i("CONNECTIVITY", msg);
			String tokens[] = msg.split(",");
			Point oppStartPoint = new Point(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
			
			calFactorX = (double)this.gameData.getStartPos().x / (double)oppStartPoint.x;
			calFactorY = (double)this.gameData.getStartPos().y / (double)oppStartPoint.y;
			
			Toast.makeText(App.getMyContext(), "You are Player 1", Toast.LENGTH_SHORT).show();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Helper.onConnectionError();
		}
	}
	
	@Override
	public void Action(Point mousePos, Object o, MouseState mouseState) {
		if (canPlay) {
			super.Action(mousePos, o, mouseState);
			
			if (mouseState == MouseState.MOUSE_UP && ball.isRunning()) { // finish an launch trigger
				canPlay = false;
			}
		} else {
			String msg = DeviceDetailFragment.server.getMessage();
			if (msg.equals(tagConnect.FINMOVE)) {
				canPlay = true;
			}
		}
	}
	
	@Override
	protected LinkedList<Obstacles> isNextPostAvailable() {
		// TODO Auto-generated method stub
		return super.isNextPostAvailable();
	}
	
	@Override
	protected void decreaseLives() {
		GameManager.loseDuo(bet);
	}
	
	@Override
	public void show(Canvas canvas) throws Exception {
		// TODO Auto-generated method stub
		super.show(canvas);
		String msg = DeviceDetailFragment.client.getMessage();
		if (msg.equals(tagConnect.WINDUO)) {
			GameManager.loseDuo(bet);
		} else {
			if(msg.equals(tagConnect.LOSEDUO)) {
				int candies = ((Candies) obstacleList[ObstacleIdx.Candy.getValue()])
						.computeScore();
				GameManager.winDuo(candies + bet);
			}
		}
	}
	
	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return super.isRunning();
	}
	
	@Override
	public boolean isBallRunning() {
		// TODO Auto-generated method stub
		return super.isBallRunning();
	}
	
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		super.resume();
	}
	
	@Override
	public void flushData() {
		// TODO Auto-generated method stub
		super.flushData();
	}
	
	@Override
	public void restart() {
		// TODO Auto-generated method stub
		super.restart();
	}
	
	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return super.getScore();
	}
	
	@Override
	public Point getBallPos() {
		// TODO Auto-generated method stub
		return super.getBallPos();
	}
	
	@Override
	protected void updateView() {
		// TODO Auto-generated method stub
		super.updateView();
	}
	
	@Override
	protected boolean isBallOutOfView() {
		// TODO Auto-generated method stub
		return super.isBallOutOfView();
	}
	
	@Override
	protected void initBall(Point direction) {
		// TODO Auto-generated method stub
		super.initBall(direction);
	}
	
	@Override
	protected void showBackground(Canvas canvas) {
		// TODO Auto-generated method stub
		super.showBackground(canvas);
	}
	
	@Override
	protected void showFullBackground(Canvas canvas) {
		// TODO Auto-generated method stub
		super.showFullBackground(canvas);
	}
	
	@Override
	public void changeGear(gearState gear) {
		// TODO Auto-generated method stub
		super.changeGear(gear);
	}
}
