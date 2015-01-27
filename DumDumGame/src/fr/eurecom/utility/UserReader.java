package fr.eurecom.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedList;

import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.App;

public class UserReader {
	static public User readUserData(int dataID) {
		User user = null;
		try {
			InputStream inputStream = Parameters.resource
					.openRawResource(dataID);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

			user = reading(reader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return user;
	}

	static public User readUserData(String dataPath) {
		User user = null;
		try {
			FileInputStream fin = App.getMyContext().openFileInput(dataPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fin));

			user = reading(reader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return user;
	}

	static private User reading(BufferedReader reader) throws IOException,
			ParseException {
		User user = new User();
		String aLine;
		String[] tokens;

		// name
		aLine = reader.readLine();
		user.setName(aLine);

		// max lives
		aLine = reader.readLine();
		user.setMaxLives(Integer.parseInt(aLine));

		// lives
		aLine = reader.readLine();
		user.setCurrentLives(Integer.parseInt(aLine));

		// refill time
		aLine = reader.readLine();
		user.setRefillTime(Integer.parseInt(aLine));

		// last time
		aLine = reader.readLine();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(sdf.parse(aLine));
		user.setLastTime(aLine);

		// unlocked level
		aLine = reader.readLine();
		user.setUnlockedLevel(Integer.parseInt(aLine));

		// level score
		tokens = reader.readLine().split(" ");
		LinkedList<Integer> intList = new LinkedList<Integer>();
		for (int i = 0; i < tokens.length; i++) {
			intList.add(Integer.parseInt(tokens[i]));
		}
		user.setLevelScore(intList);

		// current money
		aLine = reader.readLine();
		user.setCurrentMoney(Integer.parseInt(aLine));
		
		// gear amount
		tokens = reader.readLine().split(" ");
		intList = new LinkedList<Integer>();
		for (int i = 0; i < tokens.length; i++) {
			intList.add(Integer.parseInt(tokens[i]));
		}
		user.setGearAmount(intList);

		return user;
	}
}
