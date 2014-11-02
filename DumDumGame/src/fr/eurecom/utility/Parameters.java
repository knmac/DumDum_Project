package fr.eurecom.utility;

import java.util.concurrent.Semaphore;

import fr.eurecom.dumdumgame.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

public class Parameters {
	// sprite number
	static public int numSprtActiveLevel = 6;

	// Button Image
	static public Bitmap bmpBtnStart;
	static public Bitmap bmpBtnSinglePlayer;
	static public Bitmap bmpBtnMultiPlayer;
	static public Bitmap bmpBtnUser;
	static public Bitmap bmpBtnHighScore;
	static public Bitmap bmpBtnHelp;
	static public Bitmap bmpBtnShop;
	static public Bitmap bmpBtnSetting;
	static public Bitmap bmpBtnExit;
	static public Bitmap bmpBtnReturn;
	static public Bitmap bmpBtnTransparent;
	static public Bitmap bmpBtnDelete;
	static public Bitmap bmpBtnAdd;
	static public Bitmap bmpBtnActiveLevel;
	static public Bitmap bmpBtnInactiveLevel;

	// Button Position
	static public Point posBtnStartMainMenu;
	static public Point posBtnSinglePlayerMenu;
	static public Point posBtnMultiPlayerMenu;
	static public Point posBtnUserMainMenu;
	static public Point posBtnHighScoreMainMenu;
	static public Point posBtnHelpMainMenu;
	static public Point posBtnShopMenu;
	static public Point posBtnSettingMenu;
	static public Point posBtnExitMainMenu;

	static public Point posBtnReturn;
	static public Point posBtnAdd;

	// Button Size
	static public int dBtnWidth = 120;
	static public int dBtnHeight = 40;

	static public int dBtnSinglePlayerWidth = 295;
	static public int dBtnSinglePlayerHeight = 150;
	static public int dBtnMultiPlayerWidth = 295;
	static public int dBtnMultiPlayerHeight = 150;
	static public int dBtnShopWidth = 120;
	static public int dBtnShopHeight = 120;
	static public int dBtnSettingWidth = 120;
	static public int dBtnSettingHeight = 120;
	static public int dBtnExitWidth = 120;
	static public int dBtnExitHeight = 120;

	// Background Image
	static public Bitmap bmpBkMainMenu;
	static public Bitmap bmpBkSubMenu;
	static public Bitmap bmpMssgBox;
	static public Bitmap bmpCongrat;
	static public Point[] posScoreList;
	static public Rect recMssgArea = new Rect(55, 170, 55 + 160, 170 + 120);

	// Game Image
	static public Bitmap bmpTextureWall;
	static public Bitmap bmpTextureGrass;
	static public Bitmap bmpSand;
	static public Bitmap bmpWater;
	static public Bitmap bmpTextureWallpaper;
	static public Bitmap[] bmpTeleporter;
	static public Bitmap[] bmpConveyorLeft;
	static public Bitmap[] bmpConveyorRight;
	static public Bitmap[] bmpConveyorUp;
	static public Bitmap[] bmpConveyorDown;
	static public Bitmap[] bmpRain;

	// Pause Game Menu
	static public Bitmap bmpMetalDisc;
	static public Bitmap bmpBtnPlay;
	static public Bitmap bmpBtnRestart;
	static public Bitmap bmpBtnHome;
	static public Bitmap[] bmpBtnZoom;
	static public Bitmap[] bmpBtnSound;
	static public Point posMetalDisc = new Point(10, 90);
	static public Point posBtnPlay = new Point(110, 190);
	static public Point posBtnRestart = new Point(6, 190);
	static public Point posBtnHome = new Point(110, 290);
	static public Point posBtnZoom = new Point(214, 190);
	static public Point posBtnSound = new Point(110, 90);
	static public int dPauseBtnSize = 100;
	static public int dMetalDiscSize = 300;

	// Data Path
	// static public String programPath;
	// static public String pthHighScore;
	// static public String pthData;

	// Text Area
	static public Rect recTextArea;

	// Screen
	static public int dMaxWidth;
	static public int dMaxHeight;

	// Ball properties
	static public int dBallRadius = 18;
	static public int dMaxNumOfCollisions = 20;

	// Conveyor properties
	static public int dConveyorWidth;
	static public int dConveyorSpeed = 3;

	// Teleporter properties
	static public int dTeleRadius;

	// Zoom param
	static public int dZoomParam = 50;
	static public int dShiftParam = 200;

	// Extra informations
	static public double grassFrictionAcceleration = -100;
	static public double sandFrictionAcceleration = -390;
	static public double waterFrictionAcceleration = -22;
	static public double forceCoefficient = 1.5;

	// Time
	static public int timer = 40;
	static public int updatePeriod = 5;

	// // SoundTrack
	// static public String pthMenuSoundtrack;
	// static public String pthBackgroundSoundtrack;
	// static public String pthVictorySoundtrack;
	// static public String pthBloibSound;

	// Resource
	static public Resources resource;
	static public int dDataID = R.raw.data;
	static public String pthData = "UserData.txt";
	static public int[] dMapID = new int[] { R.raw.map1, R.raw.map2,
			R.raw.map3, R.raw.map4, R.raw.map5, R.raw.map6, R.raw.map7,
			R.raw.map8, R.raw.map9, R.raw.map10 };
	static public int dMenuSoundtrack = R.raw.menu;
	static public int dBackgroundSoundtrack = R.raw.background;
	static public int dVictorySoundtrack = R.raw.victory;
	static public int dBloibSound = R.raw.bloib;

	// Mutex
	static public Semaphore mutex = new Semaphore(1);

	// Init Macro
	static public void initParameters(Rect screen, int timeInterval)
			throws Exception {
		Resources res = App.getMyContext().getResources();

		// Step
		// 1--------------------------------------------------------------------------
		bmpBtnStart = BitmapFactory.decodeResource(res, R.drawable.start_game);
		bmpBtnSinglePlayer = BitmapFactory.decodeResource(res,
				R.drawable.single_player);
		bmpBtnMultiPlayer = BitmapFactory.decodeResource(res,
				R.drawable.multi_player);
		bmpBtnUser = BitmapFactory.decodeResource(res, R.drawable.user);
		bmpBtnHighScore = BitmapFactory.decodeResource(res,
				R.drawable.high_score);
		bmpBtnHelp = BitmapFactory.decodeResource(res, R.drawable.help);
		bmpBtnShop = BitmapFactory.decodeResource(res, R.drawable.shop);
		bmpBtnSetting = BitmapFactory.decodeResource(res, R.drawable.setting);
		bmpBtnExit = BitmapFactory.decodeResource(res, R.drawable.exit);
		bmpBtnReturn = BitmapFactory
				.decodeResource(res, R.drawable.return_back);
		bmpBtnTransparent = BitmapFactory.decodeResource(res,
				R.drawable.transparent);
		bmpBtnDelete = BitmapFactory.decodeResource(res, R.drawable.delete);
		bmpBtnAdd = BitmapFactory.decodeResource(res, R.drawable.add);
		bmpBtnActiveLevel = BitmapFactory.decodeResource(res,
				R.drawable.active_level);
		bmpBtnInactiveLevel = BitmapFactory.decodeResource(res,
				R.drawable.inactive_level);

		bmpBkMainMenu = BitmapFactory.decodeResource(res, R.drawable.main_menu);
		bmpBkSubMenu = BitmapFactory.decodeResource(res, R.drawable.sub_menu);
		bmpMssgBox = BitmapFactory.decodeResource(res, R.drawable.message_box);
		bmpCongrat = BitmapFactory.decodeResource(res,
				R.drawable.congratulation);

		posScoreList = new Point[10];
		posScoreList[0] = new Point(65, 155);
		posScoreList[1] = new Point(110, 155);
		posScoreList[2] = new Point(160, 155);
		posScoreList[3] = new Point(206, 155);
		posScoreList[4] = new Point(252, 155);
		posScoreList[5] = new Point(65, 200);
		posScoreList[6] = new Point(110, 200);
		posScoreList[7] = new Point(160, 200);
		posScoreList[8] = new Point(206, 200);
		posScoreList[9] = new Point(252, 200);

		bmpTextureWall = BitmapFactory.decodeResource(res,
				R.drawable.wall_texture);
		bmpTextureGrass = BitmapFactory.decodeResource(res,
				R.drawable.grass_texture);
		bmpSand = BitmapFactory.decodeResource(res, R.drawable.sand);
		bmpWater = BitmapFactory.decodeResource(res, R.drawable.water);
		bmpTextureWallpaper = BitmapFactory.decodeResource(res,
				R.drawable.wallpaper);

		bmpMetalDisc = BitmapFactory.decodeResource(res, R.drawable.metal_disc);
		bmpBtnPlay = BitmapFactory.decodeResource(res, R.drawable.play);
		bmpBtnRestart = BitmapFactory.decodeResource(res, R.drawable.restart);
		bmpBtnHome = BitmapFactory.decodeResource(res, R.drawable.home);
		bmpBtnZoom = new Bitmap[2];
		bmpBtnZoom[0] = BitmapFactory.decodeResource(res, R.drawable.zoom_in);
		bmpBtnZoom[1] = BitmapFactory.decodeResource(res, R.drawable.zoom_out);
		bmpBtnSound = new Bitmap[2];
		bmpBtnSound[0] = BitmapFactory.decodeResource(res, R.drawable.sound_on);
		bmpBtnSound[1] = BitmapFactory
				.decodeResource(res, R.drawable.sound_off);

		resource = res;

		// Step
		// 2---------------------------------------------------------------------------
		dMaxWidth = screen.width();
		dMaxHeight = screen.height();
		// int tempX = (screen.width() - dBtnWidth) / 2;
		// int tempY = screen.height() / 3 + 30;
		// tempY -= 10;

		// posBtnStartMainMenu = new Point(tempX, tempY);
		// posBtnLoadMainMenu = new Point(tempX, tempY + 1 * dBtnHeight);
		// posBtnUserMainMenu = new Point(tempX, tempY + 2 * dBtnHeight);
		// posBtnHighScoreMainMenu = new Point(tempX, tempY + 3 * dBtnHeight);
		// posBtnHelpMainMenu = new Point(tempX, tempY + 4 * dBtnHeight);
		// posBtnExitMainMenu = new Point(tempX, tempY + 5 * dBtnHeight);

		int tempX = dMaxWidth - dBtnWidth;
		int tempY = 0;
		posBtnStartMainMenu = new Point(tempX, tempY);
		posBtnUserMainMenu = new Point(tempX, tempY + 1 * dBtnHeight);
		posBtnHighScoreMainMenu = new Point(tempX, tempY + 2 * dBtnHeight);
		posBtnHelpMainMenu = new Point(tempX, tempY + 3 * dBtnHeight);

		posBtnSinglePlayerMenu = new Point(
				(dMaxWidth - dBtnSinglePlayerWidth) * 1 / 5,
				(dMaxHeight - dBtnSinglePlayerHeight) * 3 / 5);
		posBtnMultiPlayerMenu = new Point(
				(dMaxWidth - dBtnMultiPlayerWidth) * 4 / 5,
				(dMaxHeight - dBtnMultiPlayerHeight) * 3 / 5);
		posBtnShopMenu = new Point(dMaxWidth / 30, dMaxHeight - dBtnShopHeight
				- screen.height() / 30);
		posBtnSettingMenu = new Point((dMaxWidth - dBtnSettingWidth) / 2,
				screen.height() - dBtnSettingHeight - dMaxHeight / 30);
		posBtnExitMainMenu = new Point(dMaxWidth - dBtnExitWidth - dMaxWidth
				/ 30, dMaxHeight - dBtnExitHeight - dMaxHeight / 30);

		posBtnReturn = new Point(screen.width() - bmpBtnReturn.getWidth(),
				screen.height() - bmpBtnReturn.getHeight());
		posBtnAdd = new Point((screen.width() - bmpBtnAdd.getWidth()) / 2,
				screen.height() / 2);

		recTextArea = new Rect(20, screen.height() / 3 + 30, screen.width(),
				screen.height() - 30);

		bmpTeleporter = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.black_hole), 5,
				Cutter.CutStyle.VERTICAL);

		bmpConveyorLeft = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.conveyor_left), 4,
				Cutter.CutStyle.VERTICAL);
		bmpConveyorRight = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.conveyor_right),
				4, Cutter.CutStyle.VERTICAL);
		bmpConveyorUp = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.conveyor_up), 4,
				Cutter.CutStyle.VERTICAL);
		bmpConveyorDown = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.conveyor_down), 4,
				Cutter.CutStyle.VERTICAL);

		bmpRain = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.rain), 4,
				Cutter.CutStyle.VERTICAL);

		dConveyorWidth = dZoomParam;
		dTeleRadius = 2 * dBallRadius;
		timer = timeInterval;
	}

	static public void resetMacro(int zoomParam, int ballRadius) {
		dZoomParam = zoomParam;
		dBallRadius = ballRadius;
		dConveyorWidth = dZoomParam;
		dTeleRadius = 2 * dBallRadius;
	}
}
