package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.MainActivity;
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
	private Point[] posList = new Point[] { 
			new Point(150, 250), new Point(350, 250), new Point(550, 250), new Point(750, 250), new Point(950, 250), 
			new Point(150, 450), new Point(350, 450), new Point(550, 450), new Point(750, 450), new Point(950, 450) };

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
		paint.setTextSize(25);
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		for (int i = 0; i < 10; ++i) {
			canvas.drawText("Level " + (i + 1), posList[i].x+10, posList[i].y
					+ h + 20, paint);
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
			GameManager.getMssgBox().showMessage("You haven't unlocked this\n\n level yet!",
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
		//------------------------------------------------------------
		// Do an up on the mutex
		Parameters.mutex.release();
	}
}

