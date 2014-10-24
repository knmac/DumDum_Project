package fr.eurecom.allmenus;

import java.util.LinkedList;

import fr.eurecom.data.User;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.MainActivity;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

public class HighScoreMenu extends BaseMenu {

	private class Score {
		public Score(String userName, int score) {
			this.userName = userName;
			this.levelScore = score;
		}

		public String userName;
		public int levelScore;
	}

	private enum ButtonID {
		RETURN
	};

	private LinkedList<Score> scoreList = new LinkedList<Score>();

	public HighScoreMenu(DynamicBitmap bmpBackground, LinkedList<User> userList) {
		super(bmpBackground);
		Button btnReturn = new Button(ButtonID.RETURN,
				Parameters.bmpBtnReturn, Parameters.posBtnReturn,
				Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);

		FindTotalScore(userList);
	}

	public void FindTotalScore(LinkedList<User> userList) {
		scoreList = new LinkedList<Score>();

		// get name and total score
		for (int i = 0; i < userList.size(); ++i) {
			int total_score = 0;
			for (int j = 0; j < userList.get(i).getLevelScore().size(); ++j)
				total_score += userList.get(i).getLevelScore().get(j);
			scoreList.add(new Score(userList.get(i).getName(), total_score));
		}

		// sort by total score
		for (int i = 0; i < scoreList.size() - 1; ++i)
			for (int j = i + 1; j < scoreList.size(); ++j)
				if (scoreList.get(i).levelScore > scoreList.get(j).levelScore) {
					Score score = scoreList.get(i);
					scoreList.set(i, scoreList.get(j));
					scoreList.set(j, score);
				}
	}

	public void Show(Canvas canvas) {
		super.Show(canvas);

		Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.MONOSPACE);
		String data = "";

		for (int i = 0; i < scoreList.size(); ++i) {
			String tmp = "";
			
			for (int j = 0; j < 18 - scoreList.get(i).userName.length(); ++j)
				tmp += "-";
			
			data += "  " + scoreList.get(i).userName + tmp
					+ scoreList.get(i).levelScore + "\n";
		}

//		canvas.drawText(data, Parameters.recTextArea.left,
//				Parameters.recTextArea.top, paint);
		Point point = new Point(Parameters.recTextArea.left,
				Parameters.recTextArea.top);
		Helper.drawTextWithMultipleLines(canvas, data, point, paint);
	}

	@Override
	public boolean Action(Point p, Object o) {
		ButtonID ResultButtonID = (ButtonID) ClickedButton(p);
		
		if (ResultButtonID == null)
			return false;
		
		switch (ResultButtonID) {
		case RETURN:
			CallReturn(o);
			break;
		default:
			return false;
		}

		return true;
	}

	private void CallReturn(Object o) {
		((MainActivity) o).setState(MainActivity.StateList.MAIN_MENU);
		((MainActivity) o).getMainView().invalidate();
	}
}
