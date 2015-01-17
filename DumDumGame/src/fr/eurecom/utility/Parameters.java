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
	static public Bitmap bmpTransparent;
	static public Bitmap bmpHalfTransparent;

	// Button Position
	static public Point posBtnReturn;

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
	static public Bitmap bmpMicrowave;
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
	static public Point pivotDumDum = new Point();
	static public Point sizeDumDum = new Point();

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
	static public void initParameters(Rect screen) throws Exception {
		Resources res = App.getMyContext().getResources();

		// Step 1-------------------------------------------------------------
		bmpBtnReturn = BitmapFactory.decodeResource(res, R.drawable.back_btn);
		bmpTransparent = BitmapFactory.decodeResource(res,
				R.drawable.transparent);
		bmpHalfTransparent = BitmapFactory.decodeResource(res,
				R.drawable.half_transparent);

		bmpBkMainMenu = BitmapFactory.decodeResource(res, R.drawable.main_menu);
		bmpBkSubMenu = BitmapFactory.decodeResource(res, R.drawable.sub_menu);
		bmpMssgBox = BitmapFactory.decodeResource(res, R.drawable.message_box);
		bmpCongrat = BitmapFactory.decodeResource(res,
				R.drawable.congratulation);

		bmpTextureWall = BitmapFactory.decodeResource(res, R.drawable.block);
		bmpTextureScenery = BitmapFactory.decodeResource(res,
				R.drawable.background);
		bmpSand = BitmapFactory.decodeResource(res, R.drawable.sand);
		bmpWater = BitmapFactory.decodeResource(res, R.drawable.water);
		bmpTextureWallpaper = BitmapFactory.decodeResource(res,
				R.drawable.space_pattern);
		bmpMicrowave = BitmapFactory.decodeResource(res, R.drawable.microwave);
		resource = res;

		// Step 2--------------------------------------------------------
		dMaxWidth = screen.width();
		dMaxHeight = screen.height();

		dZoomParam = screen.height() / 18;
		dBallRadius = dZoomParam * 5 / 4;

		// resize texture
		bmpTextureWall = Bitmap.createScaledBitmap(bmpTextureWall, dZoomParam,
				dZoomParam, false);
		bmpTextureScenery = Bitmap.createScaledBitmap(bmpTextureScenery,
				dMaxWidth, dMaxHeight, false);

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
				R.drawable.dumdum_normal_big);
		bmpDumDumAngel = BitmapFactory.decodeResource(res,
				R.drawable.dumdum_angel_big);

		// resize DumDum
		sizeDumDum.y = dBallRadius * 5 / 2;
		sizeDumDum.x = sizeDumDum.y * bmpDumDumNormal.getWidth()
				/ bmpDumDumNormal.getHeight();
		pivotDumDum.x = sizeDumDum.x / 60;
		pivotDumDum.y = sizeDumDum.y * 3 / 10;

		dConveyorWidth = dZoomParam;
		dTeleRadius = 2 * dBallRadius;
	}
}
