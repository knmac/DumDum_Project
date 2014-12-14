package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Cutter;
import fr.eurecom.utility.Parameters;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;

public class LoadMenu extends BaseMenu {
	private Point[] posList = new Point[] { new Point(150, 250),
			new Point(350, 250), new Point(550, 250), new Point(750, 250),
			new Point(950, 250), new Point(150, 450), new Point(350, 450),
			new Point(550, 450), new Point(750, 450), new Point(950, 450) };

	private final int returnID = 1000;
	private int w;
	private int h;
	private int unlockedLevel;
	private int maxLevel = 8; // TODO

	public LoadMenu(DynamicBitmap bmpBackground, int unlocked) throws Exception {
		super(bmpBackground);
		SpawnLevel(unlocked);
	}

	public void SpawnLevel(int unlocked) throws Exception {
		this.buttonList.clear();
		unlockedLevel = unlocked;

		// return button
		Button btnReturn = new Button(returnID, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);

		// level buttons
		Bitmap activeBmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.active_level);
		Bitmap inactiveBmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.inactive_level);
		h = 4 * Parameters.dZoomParam;
		w = activeBmp.getHeight() * h / activeBmp.getWidth();

		// level position
		int screenW = Parameters.dMaxWidth;
		int screenH = Parameters.dMaxHeight;
		int dist = Parameters.dZoomParam;
		posList = new Point[maxLevel];
		posList[0] = new Point(dist, screenH - h - dist);
		posList[1] = new Point(screenW / 6, screenH * 2 / 3 - h);
		posList[2] = new Point(dist * 2, dist);
		posList[3] = new Point((screenW - w) / 2, screenH * 3 / 5);
		posList[4] = new Point(screenW * 2 / 5, screenH / 3 - h);
		posList[5] = new Point(screenW * 3 / 5, screenH / 2 - h);
		posList[6] = new Point(screenW - w - 2 * dist, screenH - h - 4 * dist);
		posList[7] = new Point(screenW - w - dist, dist);

		// active levels
		for (int i = 0; i < unlocked; ++i) {
			Button btn = new Button((Integer) i, activeBmp, posList[i], w, h);
			AddButton(btn);
		}

		// inactive levels
		for (int i = unlocked; i < maxLevel; ++i) {
			Button btn = new Button((Integer) i, inactiveBmp, posList[i], w, h);
			AddButton(btn);
		}
	}

	private void drawGlowLine(Canvas canvas, Point p1, Point p2) {
		Paint _paintSimple = new Paint();
		_paintSimple.setAntiAlias(true);
		_paintSimple.setDither(true);
		_paintSimple.setColor(Color.argb(248, 255, 255, 255));
		_paintSimple.setStrokeWidth(5f);
		_paintSimple.setStyle(Paint.Style.STROKE);
		_paintSimple.setStrokeJoin(Paint.Join.ROUND);
		_paintSimple.setStrokeCap(Paint.Cap.ROUND);

		Paint _paintBlur = new Paint();
		_paintBlur.set(_paintSimple);
		_paintBlur.setColor(Color.argb(235, 74, 138, 255));
		_paintBlur.setStrokeWidth(10f);
		_paintBlur.setMaskFilter(new BlurMaskFilter(15,
				BlurMaskFilter.Blur.NORMAL));

		Path path = new Path();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		canvas.drawPath(path, _paintBlur);
		canvas.drawPath(path, _paintSimple);
	}

	private void drawGlowLine(Canvas canvas, Point p1, Point p2, Point offset) {
		drawGlowLine(canvas, new Point(p1.x + offset.x, p1.y + offset.y),
				new Point(p2.x + offset.x, p2.y + offset.y));
	}

	public void Show(Canvas canvas) {
		// super.Show(canvas);
		// for (Button btn : buttonList) {
		// btn.updateToTheNextImage();
		// }
		
		this.bmpBackground.show(canvas);

		// draw glow lines
		Point offset = new Point(w / 2, h / 2);
		drawGlowLine(canvas, posList[0], posList[1], offset);
		drawGlowLine(canvas, posList[1], posList[2], offset);
		drawGlowLine(canvas, posList[1], posList[3], offset);
		drawGlowLine(canvas, posList[1], posList[4], offset);
		drawGlowLine(canvas, posList[4], posList[5], offset);
		drawGlowLine(canvas, posList[5], posList[6], offset);
		drawGlowLine(canvas, posList[5], posList[7], offset);
		
		// draw level
		for (Button btn : buttonList) {
			btn.show(canvas);
		}

		// draw level name
		Paint paint = new Paint();
		paint.setTextSize(Parameters.dZoomParam / 2);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextAlign(Align.CENTER);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		for (int i = 0; i < maxLevel; ++i) {
			canvas.drawText("Level " + (i + 1), posList[i].x + w / 2,
					posList[i].y + h + Parameters.dZoomParam / 2, paint);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		Integer ResultButtonID = (Integer) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		// if (ResultButtonID == returnID) {
		if (ResultButtonID.intValue() == returnID) {
			CallReturn(o);
			return true;
		}

		if (ResultButtonID.intValue() >= unlockedLevel) {
			GameManager.getMssgBox().showMessage(
					"You haven't unlocked this\n\n level yet!",
					GameManager.GameState.LOAD_MENU, (MainActivity) o);
			return true;
		}

		int chosenLevel = ResultButtonID.intValue() + 1;
		CallGame(o, chosenLevel);
		return true;
	}

	private void CallReturn(Object o) {
		GameManager.setCurrentState(GameManager.GameState.MAIN_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallGame(Object o, int chosenLevel) {
		// Do a down on the mutex
		try {
			Parameters.mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Critical Region--------------------------------------------
		GameManager.setCurrentState(GameManager.GameState.GAME);
		GameManager.chosenLevel = chosenLevel;
		GameManager.initGame();
		GameManager.mainView.invalidate();
		// ------------------------------------------------------------
		// Do an up on the mutex
		Parameters.mutex.release();
	}
}
