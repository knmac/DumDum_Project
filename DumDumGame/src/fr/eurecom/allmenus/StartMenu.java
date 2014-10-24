package fr.eurecom.allmenus;

import android.graphics.Point;

import java.util.LinkedList;


import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.utility.MapReader;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Point;
import android.widget.EditText;

public class StartMenu extends BaseMenu {
	private enum ButtonID {
		ADD_USER, RETURN
	};

//	private EditText txtAddUser;

	public StartMenu(DynamicBitmap bmpBackground, Object o) {
		super(bmpBackground);
		Button btnReturn = new Button(ButtonID.RETURN,
				Parameters.bmpBtnReturn, Parameters.posBtnReturn,
				Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);

		Button btnAdd = new Button(ButtonID.ADD_USER, Parameters.bmpBtnAdd,
				Parameters.posBtnAdd, Parameters.bmpBtnAdd.getWidth(),
				Parameters.bmpBtnAdd.getHeight());
		AddButton(btnAdd);

		// set properties for new text box but not show it
		//txtAddUser = ((MainActivity) o).getTxtAddUser();
	}

	private void hideTextBox(Object o) {
		((MainActivity) o).getMainView().bringToFront();
//		txtAddUser.setText("");
	}

	public void Show(Canvas canvas) {
		super.Show(canvas);

		// show text box
//		txtAddUser.bringToFront();
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

		if (ResultButtonID == null)
			return false;
		
		switch (ResultButtonID) {
		case ADD_USER:
			try {
				// Do a down on the mutex
				Parameters.mutex.acquire();
				// Critical region
				CallAddUser(o);
				// ----------------------
				// Do an up on the mutex
				Parameters.mutex.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case RETURN:
			CallReturn(o);
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallAddUser(Object o) throws Exception {
//		String new_user_name = txtAddUser.getText().toString();
		String new_user_name = "start menu 88";
		this.hideTextBox(o);

		if (new_user_name.length() == 0) {
			((MainActivity) o).getMainView().invalidate();
			((MainActivity) o).getMssgBox().showMessage(
					"User name is be at least 1\n\n character long!",
					StateList.START_MENU, ((MainActivity) o));
			return;
		}
		if (new_user_name.length() >= 15) {			
			((MainActivity) o).getMainView().invalidate();
			((MainActivity) o).getMssgBox().showMessage(
					"User name is at most 15\n\n characters long!",
					StateList.START_MENU, ((MainActivity) o));
			return;
		}

		if (new_user_name.trim().contentEquals("")) {		
			((MainActivity) o).getMainView().invalidate();
			((MainActivity) o).getMssgBox().showMessage(
					"User name cannot contain\n\n only white spaces!",
					StateList.START_MENU, ((MainActivity) o));
			return;
		}		
		
		if (((MainActivity) o).getUserList().size() >= 10) {			
			((MainActivity) o).getMainView().invalidate();
			((MainActivity) o).getMssgBox().showMessage(
					"There is at most 10 users.\n\nPlease delete a user in\n\n order to add the new one!",
					StateList.START_MENU, ((MainActivity) o));
			return;
		}
		for (int i = 0; i < ((MainActivity) o).getUserList().size(); ++i) {
			if (((MainActivity) o).getUserList().get(i).getName().contentEquals(new_user_name)) {
				((MainActivity) o).getMainView().invalidate();
				((MainActivity) o).getMssgBox().showMessage(
								"That user's already existed.\n\nPlease choose another\n\n name!",
								StateList.START_MENU, ((MainActivity) o));
				return;
			}
		}

		// add new user
		User new_user = new User();
		new_user.setCurrentLevel(1);
		new_user.setUnlockedLevel(1);
		new_user.setCurrentScore(0);
		new_user.setName(new_user_name);
		new_user.setLevelScore(new LinkedList<Integer>());

		for (int i = 0; i < 10; ++i)
			new_user.getLevelScore().add(0);
		MapReader gameData = new MapReader(Parameters.dMapID[0]);
		new_user.setCurrentPos(gameData.getStartPos());

		((MainActivity) o).getUserList().add(new_user);
		((MainActivity) o).setCurrentUserName(new_user.getName());
		((MainActivity) o).setChosenLevel(1);
		((MainActivity) o).updateContent();

		// start game
		((MainActivity) o).initGame();
		((MainActivity) o).setState(StateList.GAME);
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallReturn(Object o) {
		// hide text box
		this.hideTextBox(o);

		// return
		((MainActivity) o).setState(StateList.MAIN_MENU);
		((MainActivity) o).getMainView().invalidate();
	}
}
