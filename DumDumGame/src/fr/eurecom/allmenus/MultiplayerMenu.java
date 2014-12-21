package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Align;

public class MultiplayerMenu extends BaseMenu {

	private enum ButtonID {
		HOST_GAME, SCAN_GAME, RETURN
	};

	private String[] btnName = new String[] { "Create a new room",
			"Connect to a room" };

	public MultiplayerMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Button btn;
		int w, h;
		Point pos;
		Bitmap bmp;
		int dist;

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.host_btn);
		w = Parameters.dMaxWidth / 2;
		h = bmp.getHeight() * w / bmp.getWidth();
		dist = h / 8;
		pos = new Point((Parameters.dMaxWidth - w) / 2, Parameters.dMaxHeight
				/ 2 - h - dist);
		btn = new Button(ButtonID.HOST_GAME, bmp, pos, w, h);
		AddButton(btn);

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.findavail_btn);
		pos = new Point((Parameters.dMaxWidth - w) / 2, Parameters.dMaxHeight
				/ 2 + dist);
		btn = new Button(ButtonID.SCAN_GAME, bmp, pos, w, h);
		AddButton(btn);

		Button btnReturn = new Button(ButtonID.RETURN, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);
	}

	@Override
	public void Show(Canvas canvas) {
		super.Show(canvas);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case HOST_GAME:
			CallHost();
			break;
		case SCAN_GAME:
			CallScan();
			break;
		case RETURN:
			CallReturn();
			break;
		default:
			return false;
		}

		return true;
	}
	
	private void CallHost() {
		GameManager.setCurrentState(GameManager.GameState.HOST_MENU);
		GameManager.mainView.invalidate();
	}
	
	private void CallScan() {
		GameManager.setCurrentState(GameManager.GameState.CLIENT_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallReturn() {
		GameManager.setCurrentState(GameManager.GameState.MAIN_MENU);
		GameManager.mainView.invalidate();
	}

}
