package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class MssgBox extends BaseMenu {

	private String message = "";
	private MainActivity.StateList prevState;

	public MssgBox(DynamicBitmap bmpBackground) {
		super(bmpBackground);
	}

	public void showMessage(String message, MainActivity.StateList prevState, MainActivity o) {
		o.captureTheScreen();
		o.getMainView().bringToFront();
		this.message = message;
		this.prevState = prevState;
		o.setState(MainActivity.StateList.MSSG_BOX);
	}

	public void Show(Canvas canvas) {
		super.Show(canvas);

		Paint paint = new Paint();
		paint.setTextSize(17);
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		Point point = new Point(Parameters.recMssgArea.left, Parameters.recMssgArea.top);
		Helper.drawTextWithMultipleLines(canvas, message, point, paint);
	}

	@Override
	public boolean Action(Point p, Object o) {
//		if (prevState == StateList.START_MENU) {
//		}
		
		((MainActivity) o).setState(prevState);
		((MainActivity) o).getMainView().invalidate();
		
		return true;
	}
}
