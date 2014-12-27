package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.dumdumgame.YoutubeActivity;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.content.Context;
import android.content.Intent;
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
		SINGLEPLAYER, MULTIPLAYER, SHOP, STORY, INFO, EXIT
	};

	public MainMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Bitmap bmp;
		int w, h;
		Point pos;
		int btnSize = Parameters.dZoomParam * 2;
		Button btn;

		// Single PLayer Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.single_player);
		h = Parameters.dMaxHeight / 5;
		w = h * bmp.getWidth() / bmp.getHeight();
		pos = new Point(Parameters.dMaxWidth / 4 - w / 2, Parameters.dMaxHeight
				* 3 / 5 - h / 2);
		btn = new Button(ButtonID.SINGLEPLAYER, bmp, pos, w, h);
		AddButton(btn);

		// MultipLayer Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.multi_player);
		pos = new Point(Parameters.dMaxWidth * 3 / 4 - w / 2,
				Parameters.dMaxHeight * 3 / 5 - h / 2);
		btn = new Button(ButtonID.MULTIPLAYER, bmp, pos, w, h);
		AddButton(btn);

		// Shop Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.shop);
		pos = new Point(btnSize / 2, Parameters.dMaxHeight - btnSize * 5 / 4);
		btn = new Button(ButtonID.SHOP, bmp, pos, btnSize, btnSize);
		AddButton(btn);

		// Story Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.book);
		pos = new Point(Parameters.dMaxWidth / 3 - btnSize / 6,
				Parameters.dMaxHeight - btnSize * 5 / 4);
		btn = new Button(ButtonID.STORY, bmp, pos, btnSize, btnSize);
		AddButton(btn);

		// Info Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.info);
		pos = new Point(Parameters.dMaxWidth * 2 / 3 - btnSize * 5 / 6,
				Parameters.dMaxHeight - btnSize * 5 / 4);
		btn = new Button(ButtonID.INFO, bmp, pos, btnSize, btnSize);
		AddButton(btn);

		// Exit Button
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.exit);
		pos = new Point(Parameters.dMaxWidth - btnSize * 3 / 2,
				Parameters.dMaxHeight - btnSize * 5 / 4);
		btn = new Button(ButtonID.EXIT, bmp, pos, btnSize, btnSize);
		AddButton(btn);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case SINGLEPLAYER:
			CallSinglePlayer();
			break;
		case MULTIPLAYER:
			CallMultiPlayer();
			break;
		case STORY:
			CallStory();
			break;
		case INFO:
			CallInfo();
			break;
		case SHOP:
			CallShop();
			break;
		case EXIT:
			CallExit();
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallSinglePlayer() {
		GameManager.setCurrentState(GameManager.GameState.LOAD_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallMultiPlayer() {
		GameManager.setCurrentState(GameManager.GameState.MULTIPLAYER_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallShop() {
		GameManager.setCurrentState(GameManager.GameState.SHOP_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallStory() {
		Intent intent = new Intent(App.getMyContext(), YoutubeActivity.class);
		App.getMyContext().startActivity(intent);
	}

	private void CallInfo() {
		GameManager.setCurrentState(GameManager.GameState.INFO_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallExit() {
		GameManager.flushSound();
		((MainActivity) App.getMyContext()).shutdownApp();
	}
}