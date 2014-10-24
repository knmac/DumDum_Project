package fr.eurecom.utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.App;

public class DataWriter {
	
	
	static public void WriteData(LinkedList<User> userList, String dataPath, String currentUser) {
		String newLine = System.getProperty("line.separator");
		
		try {
			FileOutputStream fos = App.getMyContext().openFileOutput(dataPath, Context.MODE_PRIVATE);
			
			fos.write((userList.size() + "").getBytes());
			fos.write(newLine.getBytes());

			fos.write(currentUser.getBytes());
			fos.write(newLine.getBytes());

			for (int i = 0; i < userList.size(); ++i) // for each user
			{
				User user = userList.get(i);
				
				fos.write(user.getName().getBytes());
				fos.write(newLine.getBytes());
				
				fos.write((user.getCurrentLevel() + " "
						+ user.getCurrentScore() + " " + user.getCurrentPos().x
						+ " " + user.getCurrentPos().y + " "
						+ user.getUnlockedLevel()).getBytes());
				fos.write(newLine.getBytes());
				
				for (int j = 0; j < 10; ++j)
					fos.write((user.getLevelScore().get(j) + " ").getBytes());
				fos.write(newLine.getBytes());
			}
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("Data Writer", e.getMessage());
		} catch (IOException e) {
			Log.e("Data Writer", e.getMessage());
		}
	}
	
}
