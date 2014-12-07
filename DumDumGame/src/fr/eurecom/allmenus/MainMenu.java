package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class MainMenu extends BaseMenu {
	// variables
//	private enum ButtonID {
//		START, SINGLEPLAYER, MULTIPLAYER, USER, HIGHSCORE, HELP, SHOP, SETTING, EXIT
//	};
	
	private enum ButtonID {
		SINGLEPLAYER, MULTIPLAYER, SHOP, SETTING, EXIT
	};

	public MainMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		int btnWidth = Parameters.dBtnWidth;
		int btnHeight = Parameters.dBtnHeight;

//		// Start Button
//		Button btnStart = new Button(ButtonID.START, Parameters.bmpBtnStart,
//				Parameters.posBtnStartMainMenu, btnWidth, btnHeight);
//		AddButton(btnStart);

		// Single PLayer Button
		Button btnSinglePlayer = new Button(ButtonID.SINGLEPLAYER,
				Parameters.bmpBtnSinglePlayer,
				Parameters.posBtnSinglePlayerMenu,
				Parameters.dBtnSinglePlayerWidth,
				Parameters.dBtnSinglePlayerHeight);
		AddButton(btnSinglePlayer);

		// Single PLayer Button
		Button btnMultiPlayer = new Button(ButtonID.MULTIPLAYER,
				Parameters.bmpBtnMultiPlayer,
				Parameters.posBtnMultiPlayerMenu,
				Parameters.dBtnMultiPlayerWidth,
				Parameters.dBtnMultiPlayerHeight);
		AddButton(btnMultiPlayer);

//		// User Button
//		Button btnUser = new Button(ButtonID.USER, Parameters.bmpBtnUser,
//				Parameters.posBtnUserMainMenu, btnWidth, btnHeight);
//		AddButton(btnUser);
//
//		// Highscore Button
//		Button btnHighScore = new Button(ButtonID.HIGHSCORE,
//				Parameters.bmpBtnHighScore, Parameters.posBtnHighScoreMainMenu,
//				btnWidth, btnHeight);
//		AddButton(btnHighScore);
//
//		// Help Button
//		Button btnHelp = new Button(ButtonID.HELP, Parameters.bmpBtnHelp,
//				Parameters.posBtnHelpMainMenu, btnWidth, btnHeight);
//		AddButton(btnHelp);

		// Shop Button
		Button btnShop = new Button(ButtonID.SHOP, Parameters.bmpBtnShop,
				Parameters.posBtnShopMenu, Parameters.dBtnShopWidth,
				Parameters.dBtnShopHeight);
		AddButton(btnShop);

		// Setting Button
		Button btnSetting = new Button(ButtonID.SETTING,
				Parameters.bmpBtnSetting, Parameters.posBtnSettingMenu,
				Parameters.dBtnSettingWidth, Parameters.dBtnSettingHeight);
		AddButton(btnSetting);

		// Exit Button
		Button btnExit = new Button(ButtonID.EXIT, Parameters.bmpBtnExit,
				Parameters.posBtnExitMainMenu, Parameters.dBtnExitWidth,
				Parameters.dBtnExitWidth);
		AddButton(btnExit);
	}

//	public void Show(Canvas canvas, String current_user, int current_level) {
//		super.Show(canvas);

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
//	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
//		case START:
//			CallStart(o);
//			break;
		case SINGLEPLAYER:
			CallSinglePlayer(o);
			break;
		case MULTIPLAYER:
			CallMultiPlayer();
			break;
//		case USER:
//			CallUser(o);
//			break;
//		case HIGHSCORE:
//			CallHighscore(o);
//			break;
//		case HELP:
//			CallHelp(o);
//			break;
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

//	private void CallStart(Object o) {
//		((MainActivity) o).setState(MainActivity.StateList.START_MENU);
//		((MainActivity) o).getMainView().invalidate();
//	}

	private void CallSinglePlayer(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.LOAD_MENU);
		((MainActivity) o).getMainView().invalidate();
	}
	
	private void CallMultiPlayer() {
		// TODO
	}

//	private void CallUser(Object o) {
//		((MainActivity) o).setState(MainActivity.StateList.USER_MENU);
//		((MainActivity) o).getMainView().invalidate();
//	}
//
//	private void CallHighscore(Object o) {
//		((MainActivity) o).setState(MainActivity.StateList.HIGH_SCORE_MENU);
//		((MainActivity) o).getMainView().invalidate();
//	}
//
//	private void CallHelp(Object o) {
//		((MainActivity) o).setState(MainActivity.StateList.HELP_MENU);
//		((MainActivity) o).getMainView().invalidate();
//	}

	private void CallShop() {
		// TODO
	}

	private void CallSetting() {
		// TODO
	}

	private void CallExit(Object o) {
		((MainActivity) o).flushSound();
		((MainActivity) o).shutdownApp();
	}
}