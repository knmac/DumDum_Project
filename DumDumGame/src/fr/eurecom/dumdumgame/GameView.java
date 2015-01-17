package fr.eurecom.dumdumgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import fr.eurecom.allmenus.ShopMenu;
import fr.eurecom.dumdumgame.GameManager.GameState;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class GameView extends View {
	
	
	public GameView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Canvas myCanvas = canvas;
		
		if (GameManager.getCurrentState() == GameState.GAME) {
			try {
				GameManager.game.show(myCanvas);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			GameManager.createCorrespondingMenu(GameManager.getCurrentState());
			GameManager.menuList[GameManager.getCurrentState().getValue()].Show(myCanvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Depend on current state, the mouse position invokes different
		// actions
		Point mousePos = new Point((int) event.getX(), (int) event.getY());

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			
			if (GameManager.getCurrentState() == GameState.GAME) {
				GameManager.game.Action(mousePos, App.getMyContext(), Game.MouseState.MOUSE_UP);
			}
			else 
			{
				GameManager.createCorrespondingMenu(GameManager.getCurrentState());
				GameManager.menuList[GameManager.getCurrentState().getValue()].Action(mousePos, App.getMyContext());
			}
			break;
		case MotionEvent.ACTION_DOWN:
			switch (GameManager.getCurrentState()) {
			case GAME:
				GameManager.game.Action(mousePos, App.getMyContext(),
						Game.MouseState.MOUSE_DOWN);
				break;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			switch (GameManager.getCurrentState()) {
			case GAME:
				GameManager.game.Action(mousePos, App.getMyContext(),
						Game.MouseState.MOUSE_MOVE);
				break;
			}
			break;
		default:
			break;
		}

		return true;
	}
	

	public Bitmap getScreenShot() {

		this.setDrawingCacheEnabled(true);
		Bitmap result = Bitmap.createBitmap(this.getDrawingCache());
		this.setDrawingCacheEnabled(false);
		return result;
	}
}
