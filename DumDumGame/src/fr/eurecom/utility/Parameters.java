package fr.eurecom.utility;

import java.util.concurrent.Semaphore;

import fr.eurecom.dumdumgame.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

public class Parameters {
	// sprite number
	static public int numSprtActiveLevel = 6;

	// Button Image
	static public Bitmap bmpBtnReturn;
	static public Bitmap bmpBtnTransparent;
	static public Bitmap bmpBtnHalfTransparent;
	static public Bitmap bmpBtnActiveLevel;
	static public Bitmap bmpBtnInactiveLevel;

	static public Bitmap bmpBtnArrowRight;
	static public Bitmap bmpBtnArrowLeft;
	static public Bitmap bmpBtnBuy;

	// Button Position
	static public Point posBtnSinglePlayerMenu;
	static public Point posBtnMultiPlayerMenu;
	static public Point posBtnShopMenu;
	static public Point posBtnSettingMenu;
	static public Point posBtnExitMainMenu;

	static public Point posBtnReturn;

	// Button Size
	static public int dBtnWidth = 120;
	static public int dBtnHeight = 40;

	// TODO: change the button size according to the screen
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
	static public Bitmap bmpTextureScenery;
	static public Bitmap bmpSand;
	static public Bitmap bmpWater;
	static public Bitmap bmpTextureWallpaper;
	static public Bitmap[] bmpTeleporter;
	static public Bitmap[] bmpConveyorLeft;
	static public Bitmap[] bmpConveyorRight;
	static public Bitmap[] bmpConveyorUp;
	static public Bitmap[] bmpConveyorDown;
	static public Bitmap[] bmpRain;
	static public Bitmap[] bmpRoll;

	static public Bitmap bmpHeartRed;
	static public Bitmap bmpHeartBlack;

	static public Bitmap bmpDumDumNormal;
	static public Bitmap bmpDumDumAngel;
	static public Point posDumDumPivot = new Point(-3, -40); // TODO

	// Pause Game Menu
	// TODO
	static public Point posBtnPlay = new Point(400, 350);
	static public Point posBtnRestart = new Point(500, 350);
	static public Point posBtnHome = new Point(600, 350);
	static public Point posBtnSound = new Point(700, 350);
	static public int dPauseBtnSize = 100;

	// Screen
	static public int dMaxWidth;
	static public int dMaxHeight;

	// Ball properties
	static public int dBallRadius;
	static public int dMaxNumOfCollisions = 20;

	// Conveyor properties
	static public int dConveyorWidth;
	static public int dConveyorSpeed = 3;

	// Teleporter properties
	static public int dTeleRadius;

	// Zoom param
	static public int dZoomParam;
	static public int dShiftParam = 0; // 200

	// Extra informations
	static public double grassFrictionAcceleration = -100;
	static public double sandFrictionAcceleration = -390;
	static public double waterFrictionAcceleration = -22;
	static public double forceCoefficient = 1.5;

	// Time
	static public int timer = 40;
	static public int updatePeriod = 5;

	// Resource
	static public Resources resource;
	static public String pthUserData = "user_data.txt";
	static public int dUserData = R.raw.user_data;
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
		bmpBtnReturn = BitmapFactory.decodeResource(res, R.drawable.back_btn);
		bmpBtnTransparent = BitmapFactory.decodeResource(res,
				R.drawable.transparent);
		bmpBtnHalfTransparent = BitmapFactory.decodeResource(res,
				R.drawable.half_transparent);
		bmpBtnActiveLevel = BitmapFactory.decodeResource(res,
				R.drawable.active_level);
		bmpBtnInactiveLevel = BitmapFactory.decodeResource(res,
				R.drawable.inactive_level);
		bmpBtnArrowRight = BitmapFactory.decodeResource(res,
				R.drawable.arrow_right);
		bmpBtnArrowLeft = BitmapFactory.decodeResource(res,
				R.drawable.arrow_left);
		bmpBtnBuy = BitmapFactory.decodeResource(res, R.drawable.buy_btn);

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

		bmpTextureWall = BitmapFactory.decodeResource(res, R.drawable.block);
		bmpTextureScenery = BitmapFactory.decodeResource(res,
				R.drawable.background);
		bmpSand = BitmapFactory.decodeResource(res, R.drawable.sand);
		bmpWater = BitmapFactory.decodeResource(res, R.drawable.water);
		bmpTextureWallpaper = BitmapFactory.decodeResource(res,
				R.drawable.space_pattern);

		resource = res;

		// Step
		// 2---------------------------------------------------------------------------
		dMaxWidth = screen.width();
		dMaxHeight = screen.height();

		dZoomParam = screen.height() / 18;
		dBallRadius = dZoomParam * 5 / 4;

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

		posBtnReturn = new Point(screen.width() - bmpBtnReturn.getWidth()
				- dZoomParam / 2, screen.height() - bmpBtnReturn.getHeight()
				- dZoomParam / 2);

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

		bmpHeartRed = BitmapFactory.decodeResource(res, R.drawable.hear_red);
		bmpHeartBlack = BitmapFactory
				.decodeResource(res, R.drawable.hear_black);

		bmpRoll = Cutter.cutBitmap(
				BitmapFactory.decodeResource(res, R.drawable.roll_sprite), 8,
				Cutter.CutStyle.VERTICAL);
		bmpDumDumNormal = BitmapFactory.decodeResource(res,
				R.drawable.dumdum_normal);
		bmpDumDumAngel = BitmapFactory.decodeResource(res,
				R.drawable.dumdum_angel);

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
