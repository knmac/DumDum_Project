package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.utility.Cutter;
import fr.eurecom.utility.Parameters;
import android.graphics.Point;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class LoadMenu extends BaseMenu {
	private Point[] posList = new Point[] { new Point(11, 340),
			new Point(91, 340), new Point(171, 340), new Point(251, 340),
			new Point(51, 260), new Point(131, 260), new Point(211, 260),
			new Point(91, 180), new Point(171, 180), new Point(131, 100) };

	private final int returnID = 1000;
	private int w;
	private int h;
	private int unlockedLevel;

	public LoadMenu(DynamicBitmap bmpBackground, int unlocked) throws Exception {
		super(bmpBackground);
		SpawnLevel(unlocked);
	}

	public void SpawnLevel(int unlocked) throws Exception {
		this.buttonList.clear();
		unlockedLevel = unlocked;

		Button btnReturn = new Button(returnID,
				Parameters.bmpBtnReturn, Parameters.posBtnReturn,
				Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);

		Bitmap[] activeBmp = Cutter.cutBitmap(Parameters.bmpBtnActiveLevel,
				Parameters.numSprtActiveLevel, Cutter.CutStyle.VERTICAL);
		w = Parameters.bmpBtnActiveLevel.getWidth()
				/ Parameters.numSprtActiveLevel;
		h = Parameters.bmpBtnActiveLevel.getHeight();
		
		// active levels
		for (int i = 0; i < unlocked; ++i) {
			Button btn = new Button((Integer) i, activeBmp, posList[i], w, h);
			AddButton(btn);
		}

		// inactive levels
		for (int i = unlocked; i < 10; ++i) {
			Button btn = new Button((Integer) i,
					Parameters.bmpBtnInactiveLevel, posList[i], w, h);
			AddButton(btn);
		}
	}

	public void Show(Canvas canvas) {
		super.Show(canvas);
		for (Button btn : buttonList) {
			btn.updateToTheNextImage();
		}
		Paint paint = new Paint();
		paint.setTextSize(15);
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		for (int i = 0; i < 10; ++i) {
			canvas.drawText("Level " + (i + 1), posList[i].x, posList[i].y
					+ h + 12, paint);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		Integer ResultButtonID = (Integer) ClickedButton(p);

		if (ResultButtonID == null)
			return false;
		
//		if (ResultButtonID == returnID) {
		if (ResultButtonID.intValue() == returnID) {
			CallReturn(o);
			return true;
		}

		if (ResultButtonID.intValue() >= unlockedLevel) {		
			((MainActivity) o).getMssgBox().showMessage("You haven't unlocked this\n\n level yet!",
					StateList.LOAD_MENU, (MainActivity) o);
			return true;
		}

		int chosenLevel = ResultButtonID.intValue() + 1;
		CallGame(o, chosenLevel);
		return true;
	}

	private void CallReturn(Object o) {
		((MainActivity) o).setState(StateList.MAIN_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallGame(Object o, int chosenLevel) {
		// Do a down on the mutex
		try {
			Parameters.mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Critical Region--------------------------------------------
		((MainActivity) o).setState(StateList.GAME);
		((MainActivity) o).setChosenLevel(chosenLevel);
		((MainActivity) o).initGame();
		((MainActivity) o).getMainView().invalidate();
		//------------------------------------------------------------
		// Do an up on the mutex
		Parameters.mutex.release();
	}
}

