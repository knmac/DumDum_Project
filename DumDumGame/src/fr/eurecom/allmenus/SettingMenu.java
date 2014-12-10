package fr.eurecom.allmenus;

import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Point;

public class SettingMenu extends BaseMenu {

	private enum ButtonID {
		RETURN
	};

	public SettingMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		Button btnReturn = new Button(ButtonID.RETURN,
				Parameters.bmpBtnReturn, Parameters.posBtnReturn,
				Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);
	}

	@Override
	public void Show(Canvas canvas) {
		super.Show(canvas);
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

	private void CallReturn(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.MAIN_MENU);
		((MainActivity) o).getMainView().invalidate();
	}
	
}
