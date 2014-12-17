package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserWriter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class PauseMenu extends BaseMenu {
	private enum ButtonID {
		RESUME, RESTART, SOUND, HOME
	};

	public PauseMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);
		int w, h;
		w = h = Parameters.dZoomParam * 4;
		Button btn;
		Bitmap bmp;
		Bitmap[] bmpArr;
		Point pos;

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.play);
		pos = new Point(Parameters.dMaxWidth / 2 - 2 * w,
				(Parameters.dMaxHeight - h) / 2);
		btn = new Button(ButtonID.RESUME, bmp, pos, w, h);
		AddButton(btn);

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.restart);
		pos = new Point(Parameters.dMaxWidth / 2 - w,
				(Parameters.dMaxHeight - h) / 2);
		btn = new Button(ButtonID.RESTART, bmp, pos, w, h);
		AddButton(btn);

		bmpArr = new Bitmap[] {
				BitmapFactory.decodeResource(App.getMyContext().getResources(),
						R.drawable.sound_on),
				BitmapFactory.decodeResource(App.getMyContext().getResources(),
						R.drawable.sound_off) };
		pos = new Point(Parameters.dMaxWidth / 2,
				(Parameters.dMaxHeight - h) / 2);
		btn = new Button(ButtonID.SOUND, bmpArr, pos, w, h);
		AddButton(btn);

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.home);
		pos = new Point(Parameters.dMaxWidth / 2 + w,
				(Parameters.dMaxHeight - h) / 2);
		btn = new Button(ButtonID.HOME, bmp, pos, w, h);
		AddButton(btn);
	}

	public void Show(Canvas canvas) {
		if (GameManager.screenShot != null) {
			canvas.drawBitmap(GameManager.screenShot, 0, 0, new Paint());
			canvas.drawARGB(80, 0, 0, 0);
		}

		int w = Parameters.dMaxWidth;
		int h = Parameters.dMaxHeight;

		RectF rect = new RectF(w / 6, h / 4, w * 5 / 6, h * 3 / 4);

		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.DKGRAY);
		rectPaint.setAlpha(100);
		canvas.drawRoundRect(rect, Parameters.dZoomParam / 2,
				Parameters.dZoomParam / 2, rectPaint);
		
		super.Show(canvas);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case RESUME:
			CallResume();
			break;
		case RESTART:
			CallRestart();
			break;
		case SOUND:
			CallSound();
			break;
		case HOME:
			try {
				CallHome();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallResume() {
		GameManager.setCurrentState(GameManager.GameState.GAME);
		GameManager.game.resume();
		GameManager.mainView.invalidate();
	}

	private void CallRestart() {
		GameManager.setCurrentState(GameManager.GameState.GAME);
		GameManager.game.restart();
		GameManager.mainView.invalidate();
	}

	private void CallSound() {
		for (int i = 0; i < buttonList.size(); ++i)
			if ((ButtonID) buttonList.get(i).getID() == ButtonID.SOUND) {
				buttonList.get(i).updateToTheNextImage();
				GameManager.mainView.invalidate();
				GameManager.switchSound();
				break;
			}
	}

	private void CallHome() throws Exception {
		GameManager.setCurrentState(GameManager.GameState.MAIN_MENU);
		UserWriter.writeUserData(GameManager.user, Parameters.pthUserData);
		GameManager.updateContent();

		GameManager.game.flushData();
		GameManager.mainView.invalidate();
	}
}