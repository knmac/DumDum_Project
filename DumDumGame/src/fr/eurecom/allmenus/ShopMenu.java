package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextPaint;

public class ShopMenu extends BaseMenu {

	private enum ButtonID {
		CHOOSE_NEXT_SKIN, CHOOSE_PREV_SKIN, BUY, RETURN
	};

	private String[] names = new String[] { "DumDum", "Helmet DumDum",
			"Drill DumDum", "Scholar DumDum", "Time-Delay DumDum",
			"Hungry-Feeder DumDum", "Ninja DumDum", "Angel DumDum" };

	// TODO: get from user data
	private int[] availableNumber = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };

	private String[] descriptions = new String[] { "Give you another life",
			"Invulnerable against spikes", "Destroy walls",
			"Make the trajectory available", "Slow down everything",
			"Destroy all enemies", "Stick to walls",
			"Nothing much, just make you feel less guilty :))" };

	private DynamicBitmap allDumDumSkin;
	private Point skinSize;
	private Point skinPos;
	private int arrowSize;

	public ShopMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		// load resource
		Bitmap[] bmpDumDumAllSkinBig;
		Resources res = App.getMyContext().getResources();
		bmpDumDumAllSkinBig = new Bitmap[] {
				BitmapFactory.decodeResource(res, R.drawable.dumdum_normal_big),
				BitmapFactory.decodeResource(res, R.drawable.dumdum_helmet_big),
				BitmapFactory.decodeResource(res, R.drawable.dumdum_drill_big),
				BitmapFactory.decodeResource(res,
						R.drawable.dumdum_tracingray_big),
				BitmapFactory.decodeResource(res,
						R.drawable.dumdum_timedelay_big),
				BitmapFactory.decodeResource(res,
						R.drawable.dumdum_hungryfeeder_big),
				BitmapFactory.decodeResource(res, R.drawable.dumdum_ninja_big),
				BitmapFactory.decodeResource(res, R.drawable.dumdum_angel_big), };

		// create skins
		Point orgSize = new Point(bmpDumDumAllSkinBig[0].getWidth(),
				bmpDumDumAllSkinBig[0].getHeight());
		Point screenSize = new Point(Parameters.dMaxWidth,
				Parameters.dMaxHeight);
		float tmpW = (float) screenSize.x * (2.0f / 5.0f);
		float tmpH = (float) orgSize.y * (2.0f / 5.0f) * (float) screenSize.x
				/ (float) orgSize.x;
		skinSize = new Point(Math.round(tmpW), Math.round(tmpH));
		skinPos = new Point((screenSize.x - skinSize.x) / 2,
				(screenSize.y - skinSize.y) / 2);

		allDumDumSkin = new DynamicBitmap(bmpDumDumAllSkinBig, skinPos, 0,
				skinSize.x, skinSize.y);

		// create buttons
		arrowSize = skinSize.y / 2;
		Button btnChoosePrevSkin = new Button(ButtonID.CHOOSE_PREV_SKIN,
				Parameters.bmpBtnArrowLeft, new Point(skinPos.x - arrowSize,
						skinPos.y + skinSize.y - arrowSize), arrowSize,
				arrowSize);
		AddButton(btnChoosePrevSkin);

		Button btnChooseNextSkin = new Button(ButtonID.CHOOSE_NEXT_SKIN,
				Parameters.bmpBtnArrowRight, new Point(skinPos.x + skinSize.x,
						skinPos.y + skinSize.y - arrowSize), arrowSize,
				arrowSize);
		AddButton(btnChooseNextSkin);

		Button btnBuy = new Button(ButtonID.BUY, Parameters.bmpBtnBuy,
				new Point((screenSize.x - arrowSize * 3 / 4) / 2, skinPos.y
						+ skinSize.y + arrowSize / 2), arrowSize / 2,
				arrowSize / 2);
		AddButton(btnBuy);

		Button btnReturn = new Button(ButtonID.RETURN, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);
	}

	@Override
	public void Show(Canvas canvas) {
		super.Show(canvas);
		allDumDumSkin.show(canvas);

		// draw name
		String name = names[allDumDumSkin.getCurrentIndex()];
		Point p = new Point(Parameters.dMaxWidth / 2,
				allDumDumSkin.getPosition().y - Parameters.dZoomParam);
		TextPaint paint = new TextPaint();
		paint.setTextSize(Parameters.dZoomParam * 3 / 2);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);
		paint.setStyle(Style.STROKE);
		paint.setStrokeJoin(Join.ROUND);
		paint.setColor(Color.DKGRAY);
		paint.setStrokeMiter(10.0f);
		paint.setStrokeWidth(12f);
		Helper.drawTextWithMultipleLines(canvas, name, p, paint);

		paint.setStrokeWidth(4f);
		paint.setColor(Color.WHITE);
		Helper.drawTextWithMultipleLines(canvas, name, p, paint);

		// draw description
		String description = descriptions[allDumDumSkin.getCurrentIndex()];
		p = new Point(Parameters.dMaxWidth / 2, allDumDumSkin.getPosition().y
				+ skinSize.y + Parameters.dZoomParam);
		paint = new TextPaint();
		paint.setTextSize(Parameters.dZoomParam);
		paint.setColor(Color.DKGRAY);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);
		paint.setStyle(Style.STROKE);
		Helper.drawTextWithMultipleLines(canvas, description, p, paint);

		// draw availability
		String avail = "x "
				+ Integer.toString(availableNumber[allDumDumSkin
						.getCurrentIndex()]);
		p = new Point(skinPos.x + skinSize.x, allDumDumSkin.getPosition().y
				+ skinSize.y - Parameters.dZoomParam / 2);
		paint.setTextSize(Parameters.dZoomParam);
		Helper.drawTextWithMultipleLines(canvas, avail, p, paint);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case CHOOSE_PREV_SKIN:
			CallChoosePrevSkin();
			break;
		case CHOOSE_NEXT_SKIN:
			CallChooseNextSkin();
			break;
		case BUY:
			CallBuy();
			break;
		case RETURN:
			CallReturn();
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallChoosePrevSkin() {
		allDumDumSkin.updateToThePrevImage();
		((MainActivity) App.getMyContext()).getMainView().invalidate();
	}

	private void CallChooseNextSkin() {
		allDumDumSkin.updateToTheNextImage();
		((MainActivity) App.getMyContext()).getMainView().invalidate();
	}

	private void CallBuy() {
		int index = allDumDumSkin.getCurrentIndex();
		availableNumber[index]++;
		((MainActivity) App.getMyContext()).getMainView().invalidate();
	}

	private void CallReturn() {
		((MainActivity) App.getMyContext())
				.setState(MainActivity.StateList.MAIN_MENU);
		((MainActivity) App.getMyContext()).getMainView().invalidate();
	}

}
