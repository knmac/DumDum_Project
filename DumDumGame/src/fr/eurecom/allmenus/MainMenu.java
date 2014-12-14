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
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class MainMenu extends BaseMenu {
	// variables
	private enum ButtonID {
		SINGLEPLAYER, MULTIPLAYER, SHOP, SETTING, EXIT
	};

	public MainMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Bitmap bmp;
		int w, h;
		Point pos;
		int smallSize = Parameters.dZoomParam * 3;

		// Single PLayer Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.single_player);
		h = Parameters.dMaxHeight / 5;
		w = h * bmp.getWidth() / bmp.getHeight();
		pos = new Point(Parameters.dMaxWidth / 4 - w / 2, Parameters.dMaxHeight
				* 3 / 5 - h / 2);
		Button btnSinglePlayer = new Button(ButtonID.SINGLEPLAYER, bmp, pos, w,
				h);
		AddButton(btnSinglePlayer);

		// MultipLayer Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.multi_player);
		pos = new Point(Parameters.dMaxWidth * 3 / 4 - w / 2,
				Parameters.dMaxHeight * 3 / 5 - h / 2);
		Button btnMultiPlayer = new Button(ButtonID.MULTIPLAYER, bmp, pos, w, h);
		AddButton(btnMultiPlayer);

		// Shop Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.shop);
		pos = new Point(smallSize / 2, Parameters.dMaxHeight - smallSize * 5
				/ 4);
		Button btnShop = new Button(ButtonID.SHOP, bmp, pos, smallSize,
				smallSize);
		AddButton(btnShop);

		// Setting Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.setting);
		pos = new Point((Parameters.dMaxWidth - smallSize) / 2,
				Parameters.dMaxHeight - smallSize * 5 / 4);
		Button btnSetting = new Button(ButtonID.SETTING, bmp, pos, smallSize,
				smallSize);
		AddButton(btnSetting);

		// Exit Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.exit);
		pos = new Point(Parameters.dMaxWidth - smallSize * 3 / 2,
				Parameters.dMaxHeight - smallSize * 5 / 4);
		Button btnExit = new Button(ButtonID.EXIT, bmp, pos, smallSize,
				smallSize);
		AddButton(btnExit);
	}

	// public void Show(Canvas canvas, String current_user, int current_level) {
	// super.Show(canvas);

	// show current user
	// if (current_user != null) {
	// Paint paint = new Paint();
	// paint.setTextSize(20);
	// paint.setColor(Color.WHITE);
	// paint.setAlpha(100);
	// paint.setTypeface(Typeface.DEFAULT_BOLD);
	// Helper.drawTextWithMultipleLines(canvas, "Welcome, " + current_user
	// + "\nYou were playing level " + current_level, new Point(
	// 10, Parameters.dMaxHeight - 40), paint);
	// }
	// }

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		// case START:
		// CallStart(o);
		// break;
		case SINGLEPLAYER:
			CallSinglePlayer(o);
			break;
		case MULTIPLAYER:
			CallMultiPlayer();
			break;
		// case USER:
		// CallUser(o);
		// break;
		// case HIGHSCORE:
		// CallHighscore(o);
		// break;
		// case HELP:
		// CallHelp(o);
		// break;
		case SETTING:
			CallSetting();
			break;
		case SHOP:
			CallShop();
			break;
		case EXIT:
			CallExit(o);
			break;
		default:
			return false;
		}

		return true;
	}

	// private void CallStart(Object o) {
	// ((MainActivity) o).setState(MainActivity.StateList.START_MENU);
	// ((MainActivity) o).getMainView().invalidate();
	// }

	private void CallSinglePlayer(Object o) {
		GameManager.setCurrentState(GameManager.GameState.LOAD_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallMultiPlayer() {
		GameManager.setCurrentState(GameManager.GameState.MULTIPLAYER_MENU);
		GameManager.mainView.invalidate();
	}

	// private void CallUser(Object o) {
	// ((MainActivity) o).setState(MainActivity.StateList.USER_MENU);
	// ((MainActivity) o).getMainView().invalidate();
	// }
	//
	// private void CallHighscore(Object o) {
	// ((MainActivity) o).setState(MainActivity.StateList.HIGH_SCORE_MENU);
	// ((MainActivity) o).getMainView().invalidate();
	// }
	//
	// private void CallHelp(Object o) {
	// ((MainActivity) o).setState(MainActivity.StateList.HELP_MENU);
	// ((MainActivity) o).getMainView().invalidate();
	// }

	private void CallShop() {
		GameManager.setCurrentState(GameManager.GameState.SHOP_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallSetting() {
		GameManager.setCurrentState(GameManager.GameState.SETTING_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallExit(Object o) {
		GameManager.flushSound();
		((MainActivity) o).shutdownApp(); // TODO!!
	}
}