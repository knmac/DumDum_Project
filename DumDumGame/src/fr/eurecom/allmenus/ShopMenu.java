package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
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
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.Toast;

public class ShopMenu extends BaseMenu {

	private enum ButtonID {
		NEXT_GEAR, PREV_GEAR, BUY_GEAR, NEXT_PKG, PREV_PKG, BUY_PKG, RETURN
	};

	private String[] gearNames = new String[] { "DumDum", "Helmet DumDum",
			"Drill DumDum", "Scholar DumDum", "Time-Delay DumDum",
			"Hungry-Feeder DumDum", "Ninja DumDum", "Angel DumDum" };
	private String[] pkgNames = new String[] { "Small Jar of Candies",
			"Jar Half-Full of Candies", "Jar Full of Candies" };

	// TODO: change values
	private int[] gearPrices = new int[] { 100, 1000, 2000, 3000, 4000, 5000,
			6000, 7000 };
	private double[] pkgPrices = new double[] { 0.99, 4.99, 9.99 };
	// TODO: get values from user data
	private int[] gearAvail = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };

	private String[] gearDes = new String[] { "Give you another life",
			"Invulnerable against spikes", "Destroy walls",
			"Make the trajectory available", "Slow down enemies",
			"Attack enemies", "Stick to walls",
			"Tap while moving to float" };
	private int[] pkgDes = new int[] { 1000, 7000, 15000 };

	private DynamicBitmap allDumDumGears;
	private DynamicBitmap allPackages;
	private Point gearSize;
	private Point pkgSize;
	private Point gearPos;
	private Point pkgPos;
	private int arrowSize;

	public ShopMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		/*****************************************************************/
		// load resource
		Bitmap[] bmpArr;
		Resources res = App.getMyContext().getResources();
		bmpArr = new Bitmap[] {
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

		// create gears
		Point orgSize = new Point(bmpArr[0].getWidth(), bmpArr[0].getHeight());
		Point screenSize = new Point(Parameters.dMaxWidth,
				Parameters.dMaxHeight);
		float tmpW = (float) screenSize.x * (1.0f / 5.0f);
		float tmpH = (float) orgSize.y * tmpW / (float) orgSize.x;
		gearSize = new Point(Math.round(tmpW), Math.round(tmpH));
		gearPos = new Point(screenSize.x / 4 - gearSize.x / 2,
				(screenSize.y - gearSize.y) / 2);

		allDumDumGears = new DynamicBitmap(bmpArr, gearPos, 0, gearSize.x,
				gearSize.y);

		/*****************************************************************/
		// load resource
		// TODO: change resources
		bmpArr = new Bitmap[] {
				BitmapFactory.decodeResource(res, R.drawable.jar1),
				BitmapFactory.decodeResource(res, R.drawable.jar2),
				BitmapFactory.decodeResource(res, R.drawable.jar3) };

		// create packages
		orgSize = new Point(bmpArr[0].getWidth(), bmpArr[0].getHeight());
		tmpW = (float) screenSize.x * (1.0f / 5.0f);
		tmpH = (float) orgSize.y * tmpW / (float) orgSize.x;
		pkgSize = new Point(Math.round(tmpW), Math.round(tmpH));
		pkgPos = new Point(screenSize.x * 3 / 4 - gearSize.x / 2,
				(screenSize.y - gearSize.y) / 2);

		allPackages = new DynamicBitmap(bmpArr, pkgPos, 0, pkgSize.x, pkgSize.y);

		/*****************************************************************/
		// create buttons
		arrowSize = gearSize.y / 2;
		Bitmap bmp;
		Button btn;

		// prev gear
		bmp = BitmapFactory.decodeResource(res, R.drawable.arrow_left);
		btn = new Button(ButtonID.PREV_GEAR, bmp, new Point(gearPos.x
				- arrowSize, gearPos.y + gearSize.y - arrowSize), arrowSize,
				arrowSize);
		AddButton(btn);

		// next gear
		bmp = BitmapFactory.decodeResource(res, R.drawable.arrow_right);
		btn = new Button(ButtonID.NEXT_GEAR, bmp, new Point(gearPos.x
				+ gearSize.x, gearPos.y + gearSize.y - arrowSize), arrowSize,
				arrowSize);
		AddButton(btn);

		// buy gear
		bmp = BitmapFactory.decodeResource(res, R.drawable.buy_btn);
		btn = new Button(ButtonID.BUY_GEAR, bmp, new Point(screenSize.x / 4
				- arrowSize / 2, Parameters.dMaxHeight - arrowSize * 5 / 2),
				arrowSize, arrowSize);
		AddButton(btn);

		// prev pkg
		bmp = BitmapFactory.decodeResource(res, R.drawable.arrow_left);
		btn = new Button(ButtonID.PREV_PKG, bmp, new Point(
				pkgPos.x - arrowSize, pkgPos.y + pkgSize.y - arrowSize),
				arrowSize, arrowSize);
		AddButton(btn);

		// next pkg
		bmp = BitmapFactory.decodeResource(res, R.drawable.arrow_right);
		btn = new Button(ButtonID.NEXT_PKG, bmp, new Point(
				pkgPos.x + pkgSize.x, pkgPos.y + pkgSize.y - arrowSize),
				arrowSize, arrowSize);
		AddButton(btn);

		// buy pkg
		bmp = BitmapFactory.decodeResource(res, R.drawable.buy_btn);
		btn = new Button(ButtonID.BUY_PKG, bmp, new Point(screenSize.x * 3 / 4
				- arrowSize / 2, Parameters.dMaxHeight - arrowSize * 5 / 2),
				arrowSize, arrowSize);
		AddButton(btn);

		// return
		btn = new Button(ButtonID.RETURN, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btn);
	}

	@Override
	public void Show(Canvas canvas) {
		bmpBackground.show(canvas);

		/*****************************************************************/
		// draw round rect
		int dist = Parameters.dZoomParam;

		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.DKGRAY);
		rectPaint.setAlpha(125);

		RectF rect = new RectF(dist, 3 * dist, Parameters.dMaxWidth / 2 - dist,
				Parameters.dMaxHeight - 3 * dist);
		canvas.drawRoundRect(rect, dist / 2, dist / 2, rectPaint);

		rect = new RectF(Parameters.dMaxWidth / 2 + dist, 3 * dist,
				Parameters.dMaxWidth - dist, Parameters.dMaxHeight - 3 * dist);
		canvas.drawRoundRect(rect, dist / 2, dist / 2, rectPaint);

		/*****************************************************************/
		// draw gear names
		String name = gearNames[allDumDumGears.getCurrentIndex()];
		Point p = new Point(Parameters.dMaxWidth / 4,
				allDumDumGears.getPosition().y - 2 * Parameters.dZoomParam);
		TextPaint paint = new TextPaint();

		paint.setTextSize(Parameters.dZoomParam);
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

		// draw pkg names
		name = pkgNames[allPackages.getCurrentIndex()];
		p = new Point(Parameters.dMaxWidth * 3 / 4, allPackages.getPosition().y
				- 2 * Parameters.dZoomParam);
		paint = new TextPaint();

		paint.setTextSize(Parameters.dZoomParam);
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

		/*****************************************************************/
		paint = new TextPaint();
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.WHITE);
		paint.setTextSize(Parameters.dZoomParam * 3 / 4);

		// draw gear price
		String price = Integer.toString(gearPrices[allDumDumGears
				.getCurrentIndex()]) + " candies";
		p = new Point(Parameters.dMaxWidth / 4, allDumDumGears.getPosition().y
				- Parameters.dZoomParam);
		Helper.drawTextWithMultipleLines(canvas, price, p, paint);

		// draw gear description
		String description = gearDes[allDumDumGears.getCurrentIndex()];
		p = new Point(Parameters.dMaxWidth / 4, allDumDumGears.getPosition().y
				+ gearSize.y + Parameters.dZoomParam);
		Helper.drawTextWithMultipleLines(canvas, description, p, paint);

		// draw availability
		String avail = "x "
				+ Integer.toString(gearAvail[allDumDumGears.getCurrentIndex()]);
		p = new Point(gearPos.x + gearSize.x, allDumDumGears.getPosition().y
				+ gearSize.y - Parameters.dZoomParam / 2);
		Helper.drawTextWithMultipleLines(canvas, avail, p, paint);

		// draw pkg price
		price = Double.toString(pkgPrices[allPackages.getCurrentIndex()])
				+ " Euros";
		p = new Point(Parameters.dMaxWidth * 3 / 4, allPackages.getPosition().y
				- Parameters.dZoomParam);
		Helper.drawTextWithMultipleLines(canvas, price, p, paint);

		// draw pkg description
		description = "Give "
				+ Integer.toString(pkgDes[allPackages.getCurrentIndex()])
				+ " candies";
		p = new Point(Parameters.dMaxWidth * 3 / 4, allPackages.getPosition().y
				+ pkgSize.y + Parameters.dZoomParam);
		Helper.drawTextWithMultipleLines(canvas, description, p, paint);

		// draw total candies
		paint.setTextSize(Parameters.dZoomParam);
		String str = "Total candies: "
				+ Integer.toString(GameManager.user.getCurrentMoney());
		p = new Point(Parameters.dMaxWidth / 2, 2 * Parameters.dZoomParam);
		Helper.drawTextWithMultipleLines(canvas, str, p, paint);

		/*****************************************************************/
		// draw gears
		allDumDumGears.show(canvas);

		// draw packages
		allPackages.show(canvas);

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
		case PREV_GEAR:
			CallPrevGear();
			break;
		case NEXT_GEAR:
			CallNextGear();
			break;
		case BUY_GEAR:
			CallBuyGear();
			break;
		case PREV_PKG:
			CallPrevPkg();
			break;
		case NEXT_PKG:
			CallNextPkg();
			break;
		case BUY_PKG:
			CallBuyPkg();
			break;
		case RETURN:
			CallReturn();
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallPrevGear() {
		allDumDumGears.updateToThePrevImage();
		GameManager.mainView.invalidate();
	}

	private void CallNextGear() {
		allDumDumGears.updateToTheNextImage();
		GameManager.mainView.invalidate();
	}

	private void CallBuyGear() {
		int index = allDumDumGears.getCurrentIndex();
		int currCandies = GameManager.user.getCurrentMoney();
		int gearPrice = gearPrices[index];

		if (currCandies - gearPrice >= 0) {
			gearAvail[index]++;
			currCandies -= gearPrice;
			GameManager.user.setCurrentMoney(currCandies);

			// TODO: write user data

			GameManager.mainView.invalidate();
		} else {
			Toast.makeText(App.getMyContext(),
					"Sorry, you don't have enough candies.", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void CallPrevPkg() {
		allPackages.updateToThePrevImage();
		GameManager.mainView.invalidate();
	}

	private void CallNextPkg() {
		allPackages.updateToTheNextImage();
		GameManager.mainView.invalidate();
	}

	private void CallBuyPkg() {
		int index = allPackages.getCurrentIndex();
		int currCandies = GameManager.user.getCurrentMoney();
		int boughtCandies = pkgDes[index];

		// TODO: online transaction
		currCandies += boughtCandies;
		GameManager.user.setCurrentMoney(currCandies);

		// TODO: write user data

		GameManager.mainView.invalidate();
	}

	private void CallReturn() {
		GameManager.setCurrentState(GameManager.GameState.MAIN_MENU);
		GameManager.mainView.invalidate();
	}

}
