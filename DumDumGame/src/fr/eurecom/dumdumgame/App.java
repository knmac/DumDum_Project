package fr.eurecom.dumdumgame;

import android.app.Application;
import android.content.Context;
import android.speech.tts.TextToSpeech.OnInitListener;

public class App extends Application implements OnInitListener {
	private static Context myContext;

	public static Context getMyContext() {
		return myContext;
	}

	public static void setMyContext(Context myContext) {
		App.myContext = myContext;
	}

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
