package fr.eurecom.dumdumgame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import fr.eurecom.dumdumgame.GameManager.GameState;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserReader;
import fr.eurecom.utility.UserWriter;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	

	private boolean timerOn;
	
	//for facebook
	private UiLifecycleHelper uiHelper;

	// Override methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//for facebook
		try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "fr.eurecom.dumdumgame", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
		
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
		GameManager.initSound();
		GameManager.checkSound();
		GameManager.prepareGame(new GameView(this), GameManager.screenSize, GameManager.user);
		
		//------------------------------------------------------------------------
		
		//for facebook
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
		
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
		
		GameManager.soundOff();
		uiHelper.onPause();//for facebook
	}
	
	@Override
	protected void onResume() {
		// TODO Implement onResume function
		super.onResume();
		
		GameManager.soundOn();		
		uiHelper.onResume(); //for facebook
	}

	//for facebook
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);

	     uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	         @Override
	         public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	             Log.e("Activity", String.format("Error: %s", error.toString()));
	         }

	         @Override
	         public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	             Log.i("Activity", "Success!");
	         }
	     });
	 }
	 
	 public UiLifecycleHelper getUiHelper() {
	     return this.uiHelper;
	 }
	 
	 public void publishFeedDialog() {
	     Bundle params = new Bundle();
	     params.putString("name", "Facebook SDK for Android");
	     params.putString("caption", "Build great social apps and get more installs.");
	     params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	     params.putString("link", "https://developers.facebook.com/android");
	     params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	     WebDialog feedDialog = (
	         new WebDialog.FeedDialogBuilder(this,
	             Session.getActiveSession(),
	             params))
	         .setOnCompleteListener(new OnCompleteListener() {

	             @Override
	             public void onComplete(Bundle values,
	                 FacebookException error) {
	                 if (error == null) {
	                     // When the story is posted, echo the success
	                     // and the post Id.
	                     final String postId = values.getString("post_id");
	                     if (postId != null) {
	                         Toast.makeText(getApplicationContext(),
	                             "Posted story, id: "+postId,
	                             Toast.LENGTH_SHORT).show();
	                     } else {
	                         // User clicked the Cancel button
	                         Toast.makeText(getApplicationContext(), 
	                             "Publish cancelled", 
	                             Toast.LENGTH_SHORT).show();
	                     }
	                 } else if (error instanceof FacebookOperationCanceledException) {
	                     // User clicked the "x" button
	                     Toast.makeText(getApplicationContext(), 
	                         "Publish cancelled", 
	                         Toast.LENGTH_SHORT).show();
	                 } else {
	                     // Generic, ex: network error
	                     Toast.makeText(getApplicationContext(), 
	                         "Error posting story", 
	                         Toast.LENGTH_SHORT).show();
	                 }
	             }

	         })
	         .build();
	     feedDialog.show();
	 }

	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	     super.onSaveInstanceState(outState);
	     uiHelper.onSaveInstanceState(outState);
	 }

	 @Override
	 public void onDestroy() {
	     super.onDestroy();
	     uiHelper.onDestroy();
	 }
	// ///////////////////////////////////////////////////////////////////////////////////
}
