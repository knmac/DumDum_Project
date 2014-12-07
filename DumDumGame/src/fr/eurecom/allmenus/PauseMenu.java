package fr.eurecom.allmenus;


import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.dumdumgame.MainActivity.StateList;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.DataWriter;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserWriter;
import android.graphics.Canvas;
import android.graphics.Point;

public class PauseMenu extends BaseMenu {
	private enum ButtonID {
		PLAY, RESTART, ZOOM, SOUND, HOME
	};
	DynamicBitmap metalDisc;

	private boolean isZoomed = false;

	public PauseMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);
		int w, h;
		w = h = Parameters.dPauseBtnSize;
		Button btn;

		btn = new Button(ButtonID.PLAY, Parameters.bmpBtnPlay,
				Parameters.posBtnPlay, w, h);
		AddButton(btn);
		btn = new Button(ButtonID.RESTART, Parameters.bmpBtnRestart,
				Parameters.posBtnRestart, w, h);
		AddButton(btn);
		btn = new Button(ButtonID.ZOOM, Parameters.bmpBtnZoom,
				Parameters.posBtnZoom, w, h);
		AddButton(btn);
		btn = new Button(ButtonID.SOUND, Parameters.bmpBtnSound,
				Parameters.posBtnSound, w, h);
		AddButton(btn);
		btn = new Button(ButtonID.HOME, Parameters.bmpBtnHome,
				Parameters.posBtnHome, w, h);
		AddButton(btn);
		
		int s = Parameters.dMetalDiscSize;
		metalDisc = new DynamicBitmap(Parameters.bmpMetalDisc,
				Parameters.posMetalDisc, 0, s, s);		
	}

	public void Show(Canvas canvas) {
		metalDisc.show(canvas);
		super.Show(canvas);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);
		
		if (ResultButtonID == null)
			return false;
		
		switch (ResultButtonID) {
		case PLAY:
			CallPlay(o);
			break;
		case RESTART:
			CallRestart(o);
			break;
		case ZOOM:
			CallZoom(o);
			break;
		case SOUND:
			CallSound(o);
			break;
		case HOME:
			try {
				CallHome(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallPlay(Object o) {
		((MainActivity) o).setState(StateList.GAME);
		((MainActivity) o).getGame().resume();
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallRestart(Object o) {
		((MainActivity) o).setState(StateList.GAME);
		((MainActivity) o).getGame().restart();
		((MainActivity) o).getMainView().invalidate();
	}

	private void CallZoom(Object o) {
		for (int i = 0; i < buttonList.size(); ++i)
			if ((ButtonID)buttonList.get(i).getID() == ButtonID.ZOOM) {
				buttonList.get(i).updateToTheNextImage();

				Game game = ((MainActivity) o).getGame();
				User user = ((MainActivity) o).getUser();

				// write to buffer
//				user.setCurrentLevel(((MainActivity) o).getChosenLevel());
//				user.setCurrentScore(((MainActivity) o).getGame().getScore());
				Point oldBallPos = ((MainActivity) o).getGame().getBallPos();

				// unshift, unzoom ball position
				double deltaX, deltaY;
				deltaX = oldBallPos.x;
				deltaY = oldBallPos.y;
				deltaX -= Parameters.dShiftParam;
				deltaX /= Parameters.dZoomParam;
				deltaY -= Parameters.dShiftParam;
				deltaY /= Parameters.dZoomParam;

				if (!isZoomed) {
					Parameters.resetMacro(Parameters.dZoomParam * 2,
							Parameters.dBallRadius * 2);
				} else {
					Parameters.resetMacro(Parameters.dZoomParam / 2,
							Parameters.dBallRadius / 2);
				}

				// zoom and shift the ball position again
				int newZoomParam = Parameters.dZoomParam;
				deltaX = deltaX * newZoomParam + Parameters.dShiftParam;
				deltaY = deltaY * newZoomParam + Parameters.dShiftParam;
//				user.setCurrentPos(new Point((int) deltaX, (int) deltaY));

				game.flushData();

				isZoomed = !isZoomed;
				((MainActivity) o).setState(StateList.GAME);
				((MainActivity) o).getMainView().invalidate();
				break;
			}
	}

	private void CallSound(Object o) {
		for (int i = 0; i < buttonList.size(); ++i)
			if ((ButtonID)buttonList.get(i).getID() == ButtonID.SOUND) {
				buttonList.get(i).updateToTheNextImage();
				((MainActivity) o).getMainView().invalidate();
				((MainActivity) o).switchSoundOnOff();
				break;
			}
	}

	public void Unzoom(Object o) {
		User user = ((MainActivity) o).getUser();

		// unzoom the ball position before writing
		if (isZoomed) {
			// unzoom current zoom param
			Point oldBallPos = ((MainActivity) o).getGame().getBallPos();
			double deltaX, deltaY;
			deltaX = oldBallPos.x;
			deltaY = oldBallPos.y;
			deltaX -= Parameters.dShiftParam;
			deltaX /= Parameters.dZoomParam;
			deltaY -= Parameters.dShiftParam;
			deltaY /= Parameters.dZoomParam;

			// zoom with default zoom param
			int defaultZoomParam = Parameters.dZoomParam / 2;
			deltaX = deltaX * defaultZoomParam + Parameters.dShiftParam;
			deltaY = deltaY * defaultZoomParam + Parameters.dShiftParam;
//			user.setCurrentPos(new Point((int) deltaX, (int) deltaY));

			Parameters.resetMacro(Parameters.dZoomParam / 2,
					Parameters.dBallRadius / 2);

			for (int i = 0; i < buttonList.size(); ++i)
				if ((ButtonID)buttonList.get(i).getID() == ButtonID.ZOOM) {
					buttonList.get(i).updateToTheNextImage();
					break;
				}
		} else {
//			user.setCurrentPos(((MainActivity) o).getGame().getBallPos());
		}
		
		// reset zoom state
		isZoomed = false;
	}

	private void CallHome(Object o) throws Exception {
		User user = ((MainActivity) o).getUser();

		((MainActivity) o).setState(StateList.MAIN_MENU);

		Unzoom(o);

		// write to buffer
//		user.setCurrentLevel(((MainActivity) o).getChosenLevel());
//		user.setCurrentScore(((MainActivity) o).getGame().getScore());
//		if (user.getCurrentLevel() > user.getUnlockedLevel())
//			user.setUnlockedLevel(user.getCurrentLevel());

		// write to disk
		// TODO
//		DataWriter.WriteData(((MainActivity) o).getUser(), Parameters.pthUserData,
//				user.getName());
		UserWriter.writeUserData(((MainActivity) o).getUser(),
				Parameters.pthUserData);
		((MainActivity) o).updateContent();

		((MainActivity) o).getGame().flushData();
		((MainActivity) o).getMainView().invalidate();
	}
}
