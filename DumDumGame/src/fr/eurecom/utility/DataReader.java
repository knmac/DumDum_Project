package fr.eurecom.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.App;

public class DataReader {
	
	static public String ReadRawData(LinkedList<User> userList, int dataID) {
		String[] arr;
		int numberOfUser;
		User user;

		String currentUser = "";

		try {
			InputStream inputStream = Parameters.resource.openRawResource(dataID);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			numberOfUser = Integer.parseInt(reader.readLine());
//			currentUser.append(reader.readLine());
			currentUser = reader.readLine();

			for (int i = 0; i < numberOfUser; ++i) {
				user = new User();

				// 1st line: <user name>
				user.setName(reader.readLine());

				// 2nd line: <current level><current score><current x><current
				// y><unlocked level>
				arr = reader.readLine().split(" ");
				user.setCurrentLevel(Integer.parseInt(arr[0]));
				user.setCurrentScore(Integer.parseInt(arr[1]));
				user.setCurrentPos(new Point(Integer.parseInt(arr[2]), Integer
						.parseInt(arr[3])));
				user.setUnlockedLevel(Integer.parseInt(arr[4]));

				// 3rd line: score of each level
				arr = reader.readLine().split(" ");
				LinkedList<Integer> levelScore = new LinkedList<Integer>();
				for (int j = 0; j < 10; ++j)
					levelScore.add(Integer.parseInt(arr[j]));
				user.setLevelScore(levelScore);

				// add that user
				userList.add(user);
			}
			
			// write raw data to new file
			DataWriter.WriteData(userList, Parameters.pthData, currentUser);

			reader.close();
			inputStream.close();
		} catch (Exception e) {
			Log.e("Data Reader", e.getMessage());
		}
		
		return currentUser;
	}
	
	static public String ReadData(LinkedList<User> userList, String dataPath) {
		String[] arr;
		int numberOfUser;
		User user;

//		currentUser = new StringBuilder();
		String currentUser = "";

		try {
//			InputStream inputStream = new FileInputStream(dataPath);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			
			FileInputStream fin = App.getMyContext().openFileInput(dataPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

			numberOfUser = Integer.parseInt(reader.readLine());
//			currentUser.append(reader.readLine());
			currentUser = reader.readLine();

			for (int i = 0; i < numberOfUser; ++i) {
				user = new User();

				// 1st line: <user name>
				user.setName(reader.readLine());

				// 2nd line: <current level><current score><current x><current
				// y><unlocked level>
				arr = reader.readLine().split(" ");
				user.setCurrentLevel(Integer.parseInt(arr[0]));
				user.setCurrentScore(Integer.parseInt(arr[1]));
				user.setCurrentPos(new Point(Integer.parseInt(arr[2]), Integer
						.parseInt(arr[3])));
				user.setUnlockedLevel(Integer.parseInt(arr[4]));

				// 3rd line: score of each level
				arr = reader.readLine().split(" ");
				LinkedList<Integer> levelScore = new LinkedList<Integer>();
				for (int j = 0; j < 10; ++j)
					levelScore.add(Integer.parseInt(arr[j]));
				user.setLevelScore(levelScore);

				// add that user
				userList.add(user);
			}

			reader.close();
			fin.close();
		} catch (Exception e) {
			Log.e("Data Reader", e.getMessage());
		}
		
		return currentUser;
	}
}
