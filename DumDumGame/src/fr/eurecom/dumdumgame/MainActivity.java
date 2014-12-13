package fr.eurecom.dumdumgame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.eurecom.allmenus.*;
import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.GameManager.GameState;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserReader;
import fr.eurecom.utility.UserWriter;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends ActionBarActivity {
	

	private boolean timerOn;
	

	// Override methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make activity full-screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// fixed rotation: landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		
		// ----------------
		// create a new view class to be in charge of the app view
		
		
		GameManager.mainView = new GameView(this);
		GameManager.mainView.bringToFront();
		
		// add the new view to the app layout
		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		mainLayout.addView(GameManager.mainView);		
				
		
		// Initialize system
		
		// TODO: !! GameModel: size, user, mainLayout, mainView, spMenu, spBackground, spVictory
		
		App.setMyContext(this);
		Display mainDisplay = getWindowManager().getDefaultDisplay();
		GameManager.screenSize = new Point();
		mainDisplay.getSize(GameManager.screenSize);
		try {
			Parameters.initParameters(new Rect(0, 0, GameManager.screenSize.x, GameManager.screenSize.y));
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// If internally stored file exists, read that file
//		try { // try if the file exists
//			FileInputStream fin = openFileInput(Parameters.pthUserData);
//			fin.close();
//			GameManager.user = UserReader.readUserData(Parameters.pthUserData);
//		} catch (FileNotFoundException e2) {
//			GameManager.user = UserReader.readUserData(Parameters.dUserData);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// If internally stored file exists, read that file
		try { // try if the file exists
			FileInputStream fin = openFileInput(Parameters.pthUserData);
			fin.close();
			GameManager.user = UserReader.readUserData(Parameters.pthUserData);
		} catch (FileNotFoundException e2) { // try if the file does not exist -> read dummy data
			GameManager.user = UserReader.readUserData(Parameters.dUserData);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) { // if there is ANY kind of errors (pretty risky)
			//e.printStackTrace();
			GameManager.user = UserReader.readUserData(Parameters.dUserData); // read dummy data
			UserWriter.writeUserData(GameManager.user, Parameters.pthUserData); // overwrite the existing data by the dummy data
		}

		// Load sound track
		GameManager.spMenu = MediaPlayer.create(this, Parameters.dMenuSoundtrack);
		GameManager.spBackground = MediaPlayer.create(this, Parameters.dBackgroundSoundtrack);
		GameManager.spVictory = MediaPlayer.create(this, Parameters.dVictorySoundtrack);

		GameManager.spMenu.setLooping(true);
		GameManager.spBackground.setLooping(true);
		GameManager.spVictory.setLooping(false);
		
		
		GameManager.prepareGame(new GameView(this), GameManager.screenSize, GameManager.user);
		
		//------------------------------------------------------------------------
		
		
		// Timer for the game
		timerOn = true;
		Thread timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (timerOn) {
					// Do a down on the mutex
					try {
						Parameters.mutex.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					// Critical region---------------------------------
					if ( (GameManager.getCurrentState() == GameManager.GameState.LOAD_MENU) ||
							(GameManager.getCurrentState() == GameManager.GameState.GAME && GameManager.game.isRunning()) )
						GameManager.mainView.postInvalidate();
					// ----------------------------------------------

					// Do an up on the mutex
					Parameters.mutex.release();

					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		timerThread.setPriority(Thread.MAX_PRIORITY);
		timerThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return true;
	}
	
	public void shutdownApp() {
		UserWriter.writeUserData(GameManager.user, Parameters.pthUserData);
		GameManager.flushSound();
		timerOn = false;
		if (GameManager.screenShot != null) {
			GameManager.screenShot.recycle();
		}

		// System.runFinalizersOnExit(true);
		System.exit(0);
		this.finish();
	}
	
	@Override
	protected void onPause() {
		// TODO Implement onPause functions
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Implement onResume function
		super.onResume();
	}

	// ///////////////////////////////////////////////////////////////////////////////////
}
