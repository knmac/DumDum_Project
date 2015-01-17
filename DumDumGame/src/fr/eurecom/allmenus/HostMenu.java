package fr.eurecom.allmenus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.widget.Toast;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class HostMenu extends BaseMenu {

	private enum ButtonID {
		PLUS_BET, MINUS_BET, PREV_STAGE, NEXT_STAGE, HOST, RETURN
	};

	private int maxStage = 8; // TODO: change according to the number of stages
	private int stage = 1;
	private int maxCandies;
	private int bet = 10;
	private int betQuantum = 10;

	public HostMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		maxCandies = GameManager.user.getCurrentMoney();

		Button btn;
		int w, h;
		Point pos;
		Bitmap bmp;

		// minus bet
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.minus);
		w = Parameters.dZoomParam * 2;
		h = bmp.getHeight() * w / bmp.getWidth();
		pos = new Point(Parameters.dMaxWidth / 4 - w / 2, Parameters.dMaxHeight
				/ 2 - h);
		btn = new Button(ButtonID.MINUS_BET, bmp, pos, w, h);
		AddButton(btn);

		// plus bet
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.plus);
		pos = new Point(Parameters.dMaxWidth * 3 / 4 - w / 2,
				Parameters.dMaxHeight / 2 - h);
		btn = new Button(ButtonID.PLUS_BET, bmp, pos, w, h);
		AddButton(btn);

		// prev stage
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.arrow_left);
		pos = new Point(Parameters.dMaxWidth / 4 - w / 2,
				Parameters.dMaxHeight / 2);
		btn = new Button(ButtonID.PREV_STAGE, bmp, pos, w, h);
		AddButton(btn);

		// next stage
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.arrow_right);
		pos = new Point(Parameters.dMaxWidth * 3 / 4 - w / 2,
				Parameters.dMaxHeight / 2);
		btn = new Button(ButtonID.NEXT_STAGE, bmp, pos, w, h);
		AddButton(btn);

		// connect
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.connect);
		w = Parameters.dZoomParam * 3;
		h = bmp.getHeight() * w / bmp.getWidth();
		pos = new Point(Parameters.dMaxWidth / 2 - w / 2, Parameters.dMaxHeight
				* 5 / 6 - h);
		btn = new Button(ButtonID.HOST, bmp, pos, w, h);
		AddButton(btn);

		// return
		Button btnReturn = new Button(ButtonID.RETURN, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);
	}

	@Override
	public void Show(Canvas canvas) {
		bmpBackground.show(canvas);

		int w = Parameters.dMaxWidth;
		int h = Parameters.dMaxHeight;

		RectF rect = new RectF(w / 6, h / 6, w * 5 / 6, h * 5 / 6);

		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.DKGRAY);
		rectPaint.setAlpha(125);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(Parameters.dZoomParam * 3 / 4);

		canvas.drawRoundRect(rect, Parameters.dZoomParam / 2,
				Parameters.dZoomParam / 2, rectPaint);
		Helper.drawTextWithMultipleLines(canvas,
				"Total candies: " + Integer.toString(this.maxCandies),
				new Point(w / 2, h / 5 + Parameters.dZoomParam), textPaint);
		textPaint.setTextSize(Parameters.dZoomParam * 4 / 3);
		Helper.drawTextWithMultipleLines(canvas,
				"Bet candies: " + Integer.toString(this.bet), new Point(w / 2,
						h / 2 - Parameters.dZoomParam * 2
								+ Parameters.dZoomParam), textPaint);
		Helper.drawTextWithMultipleLines(canvas,
				"Stage: " + Integer.toString(this.stage), new Point(w / 2, h
						/ 2 + Parameters.dZoomParam), textPaint);

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
		case MINUS_BET:
			CallMinusBet();
			break;
		case PLUS_BET:
			CallPlusBet();
			break;
		case PREV_STAGE:
			CallPrevStage();
			break;
		case NEXT_STAGE:
			CallNextStage();
			break;
		case HOST:
			CallConnect();
			break;
		case RETURN:
			CallReturn();
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallMinusBet() {
		if (this.bet - this.betQuantum > 0) {
			this.bet -= this.betQuantum;
			GameManager.mainView.invalidate();
		}
	}

	private void CallPlusBet() {
		if (this.bet + this.betQuantum <= this.maxCandies) {
			this.bet += this.betQuantum;
			GameManager.mainView.invalidate();
		}
	}

	private void CallPrevStage() {
		if (this.stage > 1) {
			this.stage--;
			GameManager.mainView.invalidate();
		}
	}

	private void CallNextStage() {
		if (this.stage < maxStage) {
			this.stage++;
			GameManager.mainView.invalidate();
		}
	}

	private void CallConnect() {
		// TODO
		Toast.makeText(App.getMyContext(), "Waiting for opponent...",
				Toast.LENGTH_LONG).show();
	}

	private void CallReturn() {
		GameManager.setCurrentState(GameManager.GameState.MULTIPLAYER_MENU);
		GameManager.mainView.invalidate();
	}

}
