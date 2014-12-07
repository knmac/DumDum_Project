package fr.eurecom.allmenus;

import android.graphics.Point;

import java.util.LinkedList;

import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class UserMenu extends BaseMenu {
	private LinkedList<User> userList;
	private final int RETURN_ID = 1000;

	public UserMenu(DynamicBitmap bmpBackground, LinkedList<User> list) {
		super(bmpBackground);
		Button btnReturn = new Button((Integer) RETURN_ID,
				Parameters.bmpBtnReturn, Parameters.posBtnReturn,
				Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);

		userList = list;
		SpawnUserButton();
	}

	public void SpawnUserButton() {
		int tmp = Parameters.dMaxHeight / 3 + 30;
		for (int i = 0; i < userList.size(); ++i) {
			// user button id is now the same as its index
			Button btn = new Button((Integer) i,
					Parameters.bmpBtnTransparent,
					new Point(50, tmp + (i - 1) * 25 + 5), 150, 25);
			AddButton(btn);

			// delete button id = 100 + id
			btn = new Button((Integer) (100 + i), Parameters.bmpBtnDelete,
					new Point(250, tmp + (i - 1) * 25 + 5), 25, 25);
			AddButton(btn);
		}
	}

	public void Show(Canvas canvas) {
		super.Show(canvas);

		// draw user name on transparent button
		int tmp = Parameters.dMaxHeight / 3 + 30;

		Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.DEFAULT_BOLD);

		for (int i = 0; i < userList.size(); ++i) {
			canvas.drawText(userList.get(i).getName(), 50, tmp + i * 25, paint);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		Integer ResultButtonID = (Integer) ClickedButton(p);

		if (ResultButtonID == null) // no meaning
		{
			return false;
		}

		// button id
//		if (ResultButtonID == RETURN_ID) // return case
		if (ResultButtonID.intValue() == RETURN_ID) { // return case
			CallReturn(o);
		} else // user button id case
		if (ResultButtonID.intValue() <= 10) {
			try {
				CallSetCurrentUser(ResultButtonID, o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else // delete user
		{
			try {
				CallDeleteUser(ResultButtonID - 100, o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private void CallReturn(Object o) {
		((MainActivity) o).setState(StateList.MAIN_MENU);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallSetCurrentUser(int userIndex, Object o) throws Exception {
		// TODO
//		((MainActivity) o).setCurrentUserName(userList.get(userIndex).getName());
//		((MainActivity) o).updateContent();	
//		((MainActivity) o).getMssgBox().showMessage(
//				"Current user is now: \n\n" + userList.get(userIndex).getName(),
//				StateList.USER_MENU, (MainActivity) o);
//		((MainActivity) o).getMainView().invalidate();
	}

	private void CallDeleteUser(int userIndex, Object o) throws Exception {
		// TODO
//		if (userList.get(userIndex).getName().contentEquals(((MainActivity) o).getCurrentUserName())) {
//			((MainActivity) o).getMssgBox().showMessage(
//					"Can't remove current user.\n\n Please select another one.",
//					StateList.USER_MENU, (MainActivity) o);
//			((MainActivity) o).getMainView().invalidate();
//			return;
//		}
//
//		userList.remove(userIndex); // delete that user
//		((MainActivity) o).setUserList(userList); // update new list to database
//		((MainActivity) o).updateContent(); // update highscore since the user
//											// has changed
//
//		buttonList.clear(); // clear all button
//		
//		// add return button
//		Button btnReturn = new Button(RETURN_ID, Parameters.bmpBtnReturn,
//				Parameters.posBtnReturn,
//				Parameters.bmpBtnReturn.getWidth(),
//				Parameters.bmpBtnReturn.getHeight());
//		AddButton(btnReturn);
//		SpawnUserButton(); // reset user button, based on the new user list
//
//		((MainActivity) o).getMainView().invalidate();
	}
}
