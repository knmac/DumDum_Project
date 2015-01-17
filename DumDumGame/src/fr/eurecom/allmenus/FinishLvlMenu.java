package fr.eurecom.allmenus;

import com.facebook.widget.FacebookDialog;

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
import android.widget.Toast;

public class FinishLvlMenu extends BaseMenu {

	private enum ButtonID {
		RESTART, CHOOSE_LVL, FACEBOOK
	};

	public FinishLvlMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Bitmap bmp;
		Button btn;
		Point pos;
		int w = Parameters.dMaxWidth;
		int h = Parameters.dMaxHeight;
		int btnSize = Parameters.dZoomParam * 2;
		int dist = Parameters.dZoomParam / 2;

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.restart);
		pos = new Point(w / 4 + dist, h * 3 / 4 - btnSize - dist);
		btn = new Button(ButtonID.RESTART, bmp, pos, btnSize, btnSize);
		AddButton(btn);

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.play);
		pos = new Point((w - btnSize) / 2, h * 3 / 4 - btnSize - dist);
		btn = new Button(ButtonID.CHOOSE_LVL, bmp, pos, btnSize, btnSize);
		AddButton(btn);

		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.fb_btn);
		pos = new Point(w * 3 / 4 - btnSize - dist, h * 3 / 4 - btnSize - dist);
		btn = new Button(ButtonID.FACEBOOK, bmp, pos, btnSize, btnSize);
		AddButton(btn);
	}

	public void Show(Canvas canvas) {
		if (GameManager.screenShot != null) {
			canvas.drawBitmap(GameManager.screenShot, 0, 0, new Paint());
			canvas.drawARGB(80, 0, 0, 0);
		}

		int w = Parameters.dMaxWidth;
		int h = Parameters.dMaxHeight;

		RectF rect = new RectF(w / 4, h / 4, w * 3 / 4, h * 3 / 4);

		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.DKGRAY);
		rectPaint.setAlpha(125);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(Parameters.dZoomParam);

		canvas.drawRoundRect(rect, Parameters.dZoomParam / 2,
				Parameters.dZoomParam / 2, rectPaint);
		Helper.drawTextWithMultipleLines(canvas,
				"Level Completed\n\nCandies: ", new Point(w / 2,
						h * 2 / 5), textPaint);

		// draw buttons
		for (Button btn : buttonList) {
			btn.show(canvas);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;
		
		switch (ResultButtonID) {
		case RESTART:
			CallRestart();
			break;
		case CHOOSE_LVL:
			CallChooseLevel();
			break;
		case FACEBOOK:
			CallFacebook();
			break;
		default:
			break;
		}
		return true;
	}

	private void CallRestart() {
		GameManager.setCurrentState(GameManager.GameState.GAME);
		GameManager.game.restart();
		GameManager.mainView.invalidate();
	}
	
	private void CallChooseLevel() {
		GameManager.setCurrentState(GameManager.GameState.LOAD_MENU);
		GameManager.mainView.invalidate();
	}
	
	private void CallFacebook() {
		if (FacebookDialog.canPresentShareDialog((MainActivity)App.getMyContext(), 
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder((MainActivity)App.getMyContext())
            .setLink("http://www.eurecom.fr/")
            .setApplicationName("DumDum")
            .setDescription("My DumDum has passed stage *** and collected yyy candies. My score is now zzz. Join me in DumDum's Great Adventure")
            .setPicture("https://dl.dropboxusercontent.com/u/11067028/dumdumicon.png")
            .build();
            ((MainActivity)App.getMyContext()).getUiHelper().trackPendingDialogCall(shareDialog.present());
     } else {
         Toast.makeText(App.getMyContext(), "Please install Facebook App!", Toast.LENGTH_SHORT).show();
     }
	}
}
