package fr.eurecom.utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.App;

public class UserWriter {
	static public void writeUserData(User user, String dataPath) {
		String newLine = System.getProperty("line.separator");

		try {
			FileOutputStream fos = App.getMyContext().openFileOutput(dataPath,
					Context.MODE_PRIVATE);

			// name
			fos.write(user.getName().getBytes());
			fos.write(newLine.getBytes());

			// max lives
			fos.write(Integer.toString(user.getMaxLives()).getBytes());
			fos.write(newLine.getBytes());

			// lives
			fos.write(Integer.toString(user.getCurrentLives()).getBytes());
			fos.write(newLine.getBytes());

			// refill time
			fos.write(Integer.toString(user.getRefillTime()).getBytes());
			fos.write(newLine.getBytes());

			// last time
			// SimpleDateFormat sdf = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String lastTime = sdf.format(user.getLastTime());
			fos.write(user.getLastTime().getBytes());
			fos.write(newLine.getBytes());

			// unlocked level
			fos.write(Integer.toString(user.getUnlockedLevel()).getBytes());
			fos.write(newLine.getBytes());

			// level score
			LinkedList<Integer> intList = user.getLevelScore();
			String aLine = "";
			for (int i = 0; i < intList.size(); i++) {
				aLine = aLine + Integer.toString(intList.get(i)) + " ";
			}
			fos.write(aLine.getBytes());
			fos.write(newLine.getBytes());

			// current money
			fos.write(Integer.toString(user.getCurrentMoney()).getBytes());
			fos.write(newLine.getBytes());
			
			// gear amount
			intList = user.getGearAmount();
			aLine = "";
			for (int i = 0; i < intList.size(); i++) {
				aLine = aLine + Integer.toString(intList.get(i)) + " ";
			}
			fos.write(aLine.getBytes());
			fos.write(newLine.getBytes());
		} catch (FileNotFoundException e) {
			Log.e("Data Writer", e.getMessage());
		} catch (IOException e) {
			Log.e("Data Writer", e.getMessage());
		}
	}
}