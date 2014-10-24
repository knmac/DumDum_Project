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
	private enum ButtonID {
		START, LOAD, USER, HIGHSCORE, HELP, EXIT
	};

	public MainMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);
		int btnWidth = Parameters.dBtnWidth;
		int btnHeight = Parameters.dBtnHeight;

		Button btnStart = new Button(ButtonID.START, Parameters.bmpBtnStart,
				Parameters.posBtnStartMainMenu, btnWidth, btnHeight);
		AddButton(btnStart);

		Button btnLoad = new Button(ButtonID.LOAD, Parameters.bmpBtnLoad,
				Parameters.posBtnLoadMainMenu, btnWidth, btnHeight);
		AddButton(btnLoad);

		Button btnUser = new Button(ButtonID.USER, Parameters.bmpBtnUser,
				Parameters.posBtnUserMainMenu, btnWidth, btnHeight);
		AddButton(btnUser);

		Button btnHighScore = new Button(ButtonID.HIGHSCORE,
				Parameters.bmpBtnHighScore,
				Parameters.posBtnHighScoreMainMenu, btnWidth, btnHeight);
		AddButton(btnHighScore);

		Button btnHelp = new Button(ButtonID.HELP, Parameters.bmpBtnHelp,
				Parameters.posBtnHelpMainMenu, btnWidth, btnHeight);
		AddButton(btnHelp);

		Button btnExit = new Button(ButtonID.EXIT, Parameters.bmpBtnExit,
				Parameters.posBtnExitMainMenu, btnWidth, btnHeight);
		AddButton(btnExit);
	}

	public void Show(Canvas canvas, String current_user, int current_level) {
		super.Show(canvas);

		// show current user
		if (current_user != null) {
			Paint paint = new Paint();
			paint.setTextSize(20);
			paint.setColor(Color.WHITE);
			paint.setAlpha(100);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			Helper.drawTextWithMultipleLines(canvas, "Welcome, "
					+ current_user + "\nYou were playing level "
					+ current_level, new Point(10,
					Parameters.dMaxHeight - 40), paint);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case START:
			CallStart(o);
			break;
		case LOAD:
			CallLoad(o);
			break;
		case USER:
			CallUser(o);
			break;
		case HIGHSCORE:
			CallHighscore(o);
			break;
		case HELP:
			CallHelp(o);
			break;
		case EXIT:
			CallExit(o);
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallStart(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.START_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallLoad(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.LOAD_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallUser(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.USER_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallHighscore(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.HIGH_SCORE_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallHelp(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.HELP_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallExit(Object o) {
		((MainActivity) o).shutdownApp();
	}
}