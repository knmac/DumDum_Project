package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Parameters;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

public class InfoMenu extends BaseMenu {

	private enum ButtonID {
		PREV, NEXT, RETURN
	};

	private int infoNumber = 3; // TODO: to change
	private int[] infoID;
	private int infoIdx;
	private DynamicBitmap currentImg;
	private Point imgPos, imgSize;

	public InfoMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		// info images
		infoID = new int[infoNumber];
		infoID[0] = R.drawable.h1;
		infoID[1] = R.drawable.h2;
		infoID[2] = R.drawable.h3;
		infoIdx = 0;
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), infoID[infoIdx]);
		imgSize = new Point();
		imgSize.y = Parameters.dMaxHeight * 3 / 4;
		imgSize.x = bmp.getWidth() * imgSize.y / bmp.getHeight();
		imgPos = new Point((Parameters.dMaxWidth - imgSize.x) / 2,
				(Parameters.dMaxHeight - imgSize.y) / 2);
		currentImg = new DynamicBitmap(bmp, imgPos, imgSize.x, imgSize.y);

		// buttons
		Button btn;
		Point pos;
		int w, h;

		// next
		w = Parameters.bmpBtnReturn.getWidth();
		h = Parameters.bmpBtnReturn.getHeight();
		pos = new Point(Parameters.dMaxWidth - w, Parameters.dMaxHeight / 2 - h
				/ 2);
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.arrow_right);
		btn = new Button(ButtonID.NEXT, bmp, pos, w, h);
		AddButton(btn);

		// previous
		w = Parameters.bmpBtnReturn.getWidth();
		h = Parameters.bmpBtnReturn.getHeight();

		pos = new Point(0, Parameters.dMaxHeight / 2 - h / 2);
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.arrow_left);
		btn = new Button(ButtonID.PREV, bmp, pos, w, h);
		AddButton(btn);

		// return
		btn = new Button(ButtonID.RETURN, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btn);
	}

	@Override
	public void Show(Canvas canvas) {
		super.Show(canvas);

		currentImg.show(canvas);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case PREV:
			CallPrev();
			break;
		case NEXT:
			CallNext();
			break;
		case RETURN:
			CallReturn();
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallPrev() {
		if (infoIdx == 0)
			infoIdx = infoNumber - 1;
		else
			infoIdx--;
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), infoID[infoIdx]);
		currentImg.recycle();
		currentImg = new DynamicBitmap(bmp, imgPos, imgSize.x, imgSize.y);
		GameManager.mainView.invalidate();
	}

	private void CallNext() {
		if (infoIdx == infoNumber - 1)
			infoIdx = 0;
		else
			infoIdx++;
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), infoID[infoIdx]);
		currentImg.recycle();
		currentImg = new DynamicBitmap(bmp, imgPos, imgSize.x, imgSize.y);
		GameManager.mainView.invalidate();
	}

	private void CallReturn() {
		GameManager.setCurrentState(GameManager.GameState.MAIN_MENU);
		GameManager.mainView.invalidate();
	}

}
