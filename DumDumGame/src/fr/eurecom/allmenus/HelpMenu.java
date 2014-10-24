package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class HelpMenu extends BaseMenu {

	private String str;

	private enum ButtonID {
		RETURN
	};

	public HelpMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Button btnReturn = new Button(ButtonID.RETURN,
				Parameters.bmpBtnReturn, Parameters.posBtnReturn,
				Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);

		str = "- Click the ball and drag your\n mouse to choose the power.\n\n";
		str += "- Black Holes teleport the\n ball randomly.\n\n";
		str += "- Conveyors pull the ball.\n\n";
		str += "- Sand slows the ball down\n while water speeds it up.\n\n";
		str += "                               Have Fun!";
	}

	@Override
	public void Show(Canvas canvas) {
		super.Show(canvas);
		Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		Point p = new Point(Parameters.recTextArea.left,
				Parameters.recTextArea.top);
		Helper.drawTextWithMultipleLines(canvas, this.str, p, paint);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;

		switch (ResultButtonID) {
		case RETURN:
			CallReturn(o);
			break;
		default:
			return false;
		}

		return true;
	}

	// CHECK AGAIN IF IT'S NECESSARY TO PASS ON THE ACTIVITY LIKE THIS
	private void CallReturn(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.MAIN_MENU);
		((MainActivity) o).getMainView().invalidate();
	}
}
