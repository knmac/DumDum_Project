package fr.eurecom.dumdumgame;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import fr.eurecom.allmenus.*;
import fr.eurecom.data.User;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class GameManager {

	// --------------------------------------------------------------------------
	// System variables
	public static GameView mainView;

	// --------------------------------------------------------------------------
	// Game variables

	public enum GameState {
		MAIN_MENU(0), LOAD_MENU(1), MULTIPLAYER_MENU(2), SHOP_MENU(3), INFO_MENU(
				4), PAUSE_MENU(5), MSSG_BOX(6), CONGRAT_BOX(7), FINISH_LVL_MENU(
				8), GEAR_UP_MENU(9), HOST_MENU(10), CLIENT_MENU(11), GAME(10);
		private final int value;

		private GameState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static int numMenu() {
			// return 8;
			return GameState.values().length - 1;
		}
	}

	public enum SoundState {
		MENU(0), GAME(1), VIC(3);
		private final int value;

		private SoundState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private static GameState currentState = GameState.MAIN_MENU;
	// private static GameState currentState = GameState.FINISH_LVL_MENU;

	public static BaseMenu menuList[];

	public static MediaPlayer spMenu;
	public static MediaPlayer spBackground;
	public static MediaPlayer spVictory;
	public static boolean hasSound = true;

	public static int chosenLevel;

	public static User user;
	public static Game game;

	public static Point screenSize;

	public static Bitmap screenShot = null;

	public static void createCorrespondingMenu(GameState menuState) {

		if (menuList[menuState.getValue()] == null) {
			switch (menuState) {
			case MAIN_MENU:
				menuList[menuState.getValue()] = new MainMenu(
						new DynamicBitmap(Parameters.bmpBkMainMenu, new Point(
								0, 0), 0, screenSize.x, screenSize.y));
				break;
			case LOAD_MENU:
				try {
					menuList[menuState.getValue()] = new LoadMenu(
							new DynamicBitmap(Parameters.bmpBkSubMenu,
									new Point(0, 0), 0, screenSize.x,
									screenSize.y), user.getUnlockedLevel());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case MULTIPLAYER_MENU:
				menuList[menuState.getValue()] = new MultiplayerMenu(
						new DynamicBitmap(Parameters.bmpBkSubMenu, new Point(0,
								0), 0, screenSize.x, screenSize.y));
				break;
			case SHOP_MENU:
				menuList[menuState.getValue()] = new ShopMenu(
						new DynamicBitmap(Parameters.bmpBkSubMenu, new Point(0,
								0), 0, screenSize.x, screenSize.y));
				break;
			case INFO_MENU:
				menuList[menuState.getValue()] = new InfoMenu(
						new DynamicBitmap(Parameters.bmpBkSubMenu, new Point(0,
								0), 0, screenSize.x, screenSize.y));
				break;
			case PAUSE_MENU:
				menuList[menuState.getValue()] = new PauseMenu(
						new DynamicBitmap(Parameters.bmpHalfTransparent,
								new Point(0, 0), 0, screenSize.x, screenSize.y));
				break;
			case MSSG_BOX:
				int tmp;
				tmp = (screenSize.y - Parameters.bmpMssgBox.getHeight()) / 2;
				menuList[menuState.getValue()] = new MssgBox(new DynamicBitmap(
						Parameters.bmpMssgBox, new Point(0, tmp)));
				break;
			case CONGRAT_BOX:
				menuList[menuState.getValue()] = new CongratBox(
						new DynamicBitmap(Parameters.bmpCongrat,
								new Point(0, 0), 0, screenSize.x, screenSize.y),
						(MainActivity) App.getMyContext());
				break;
			case FINISH_LVL_MENU:
				menuList[menuState.getValue()] = new FinishLvlMenu(
						new DynamicBitmap(Parameters.bmpHalfTransparent,
								new Point(0, 0), 0, screenSize.x, screenSize.y));
				break;
			case GEAR_UP_MENU:
				menuList[menuState.getValue()] = new GearUpMenu(
						new DynamicBitmap(Parameters.bmpHalfTransparent,
								new Point(0, 0), 0, screenSize.x, screenSize.y));
				break;
			case HOST_MENU:
				menuList[menuState.getValue()] = new HostMenu(
						new DynamicBitmap(Parameters.bmpBkSubMenu, new Point(0,
								0), 0, screenSize.x, screenSize.y));
				break;
			case CLIENT_MENU:
				menuList[menuState.getValue()] = new ClientMenu(
						new DynamicBitmap(Parameters.bmpBkSubMenu, new Point(0,
								0), 0, screenSize.x, screenSize.y));
				break;
			default:
				break;
			}
		}
	}

	// --------------------------------------------------------------------------
	// Public methods

	// TODO: double check these 3 functions
	public static MssgBox getMssgBox() {
		return (MssgBox) GameManager.menuList[GameState.MSSG_BOX.getValue()];
	}

	public PauseMenu getPauseMenu() {
		return (PauseMenu) GameManager.menuList[GameState.PAUSE_MENU.getValue()];
	}

	public void setPauseMenu(PauseMenu pauseMenu) {
		GameManager.menuList[GameState.PAUSE_MENU.getValue()] = pauseMenu;
	}

	public static void captureScreen() {
		screenShot = mainView.getScreenShot();
	}

	public static void initGame() {
		game = new Game();
	}

	public static void initSound() {
		spMenu = MediaPlayer.create(App.getMyContext(),
				Parameters.dMenuSoundtrack);
		spBackground = MediaPlayer.create(App.getMyContext(),
				Parameters.dBackgroundSoundtrack);
		spVictory = MediaPlayer.create(App.getMyContext(),
				Parameters.dVictorySoundtrack);

		spMenu.setLooping(true);
		spBackground.setLooping(true);
		spVictory.setLooping(false);
	}

	public static void switchSound() {
		hasSound = !hasSound;
		checkSound();
	}

	public static void flushSound() {
		if (spMenu != null) {
			if (spMenu.isPlaying())
				spMenu.stop();
			spMenu.release();
			spMenu = null;
		}

		if (spBackground != null) {
			if (spBackground.isPlaying())
				spBackground.stop();
			spBackground.release();
			spBackground = null;
		}

		if (spVictory != null) {
			if (spVictory.isPlaying())
				spVictory.stop();
			spVictory.release();
			spVictory = null;
		}
	}

	// TODO: loadmenu problem
	public static void updateContent() throws Exception {
		((LoadMenu) (GameManager.menuList[GameState.LOAD_MENU.getValue()]))
				.SpawnLevel(user.getUnlockedLevel());
	}

	// --------------------------------------------------------------------------

	public GameManager() {

	}

	public static void levelUp(int score) throws Exception {
		User user = GameManager.user;

		// save progress
		int level = GameManager.chosenLevel;
		if (user.getLevelScore().get(level - 1) == 0
				|| (user.getLevelScore().get(level - 1) > score && user
						.getLevelScore().get(level - 1) != 0)) // save the
																// better result
			user.getLevelScore().set(level - 1, score);

		/*
		 * // go to next level if (GameManager.chosenLevel == 10) {
		 * GameManager.chosenLevel = 1;
		 * GameManager.setCurrentState(GameManager.GameState.CONGRAT_BOX);
		 * GameManager.mainView.invalidate(); return; } else {
		 * GameManager.chosenLevel = level + 1; }
		 */

		// reset data for new level
		// user.setCurrentLevel(mainForm.getChosenLevel());
		// user.setCurrentScore(0);

		// TODO: this is replication with the one in the Game constructor, isnt
		// it?
		// gameData = null;
		// gameData = new MapReader(Parameters.dMapID[GameModel.chosenLevel -
		// 1]);

		// user.setCurrentPos(gameData.getStartPos());
		// if (user.getCurrentLevel() > user.getUnlockedLevel())
		// user.setUnlockedLevel(user.getCurrentLevel());

		// write to disk
		// DataWriter.WriteData(mainForm.getUserList(), Parameters.pthUserData,
		// user.getName());
		// TODO: what is this function for?
		updateContent();

		GameManager.game.flushData();
		initGame(); // TODO: this should be just the constructor
		mainView.invalidate();
	}

	public static void redrawScreen() {
		mainView.invalidate();
	}

	public static void prepareGame(GameView gameView, Point screenSize,
			User user) {

		// prepare view
		// GameManager.mainView = gameView;
		// GameManager.mainView.bringToFront();
		//
		// GameManager.screenSize = screenSize;
		// GameManager.user = user;

		GameManager.menuList = new BaseMenu[GameManager.GameState.numMenu()];
		for (int i = 0; i < GameManager.GameState.numMenu(); ++i)
			GameManager.menuList[i] = null;
	}

	public static GameState getCurrentState() {
		return currentState;
	}

	public static void setCurrentState(GameState currentState) {
		GameManager.currentState = currentState;
		checkSound();
	}
	
	public static void soundOn() {
		hasSound = true;
		checkSound();
	}
	
	public static void soundOff() {
		hasSound = false;
		checkSound();
	}

	public static void checkSound() {
		if (hasSound == false) {
			GameManager.spBackground = Helper.stopMediaPlayer(
					GameManager.spBackground, Parameters.dBackgroundSoundtrack);
			GameManager.spVictory = Helper.stopMediaPlayer(
					GameManager.spVictory, Parameters.dVictorySoundtrack);
			GameManager.spMenu = Helper.stopMediaPlayer(GameManager.spMenu,
					Parameters.dMenuSoundtrack);
		} else {
			switch (GameManager.getCurrentState()) {
			case MAIN_MENU:
			case MULTIPLAYER_MENU:
			case SHOP_MENU:
			case INFO_MENU:
			case HOST_MENU:
			case CLIENT_MENU:
			case LOAD_MENU:
				GameManager.spBackground = Helper.stopMediaPlayer(
						GameManager.spBackground,
						Parameters.dBackgroundSoundtrack);
				GameManager.spVictory = Helper.stopMediaPlayer(
						GameManager.spVictory, Parameters.dVictorySoundtrack);
				GameManager.spMenu.start();
				break;
			case PAUSE_MENU:
			case GEAR_UP_MENU:
			case GAME:
				GameManager.spVictory = Helper.stopMediaPlayer(
						GameManager.spVictory, Parameters.dVictorySoundtrack);
				GameManager.spMenu = Helper.stopMediaPlayer(GameManager.spMenu,
						Parameters.dMenuSoundtrack);
				GameManager.spBackground.start();
				break;
			case CONGRAT_BOX:
			case FINISH_LVL_MENU:
				GameManager.spBackground = Helper.stopMediaPlayer(
						GameManager.spBackground,
						Parameters.dBackgroundSoundtrack);
				GameManager.spMenu = Helper.stopMediaPlayer(GameManager.spMenu,
						Parameters.dMenuSoundtrack);
				GameManager.spVictory.start();
				break;
			default:
				break;
			}
		}
	}
}
