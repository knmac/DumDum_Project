package fr.eurecom.allmenus;

import java.util.LinkedList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.widget.Toast;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class ClientMenu extends BaseMenu {

	private class HostData {
		String id;
		int bet;
		int level;
	}

	private LinkedList<HostData> hostList;

	private final Integer RETURN = 1000;
	private final Integer REFRESH = 2000;

	public ClientMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		// Get the list of all hosts
		hostList = ScanWifiDirect();

		// add button for each item
		Button btn;
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.connect);
		int w, h;
		Point pos;

		w = h = Parameters.dZoomParam;
		for (int i = 0; i < hostList.size(); i++) {
			pos = new Point(Parameters.dMaxWidth * 5 / 7 - w,
					Parameters.dMaxHeight / 4 + (i + 1) * (h * 3 / 2) + h / 3);
			btn = new Button((Integer) i, bmp, pos, w, h);
			AddButton(btn);
		}

		// return buttons
		bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(),
				R.drawable.restart);
		pos = new Point(Parameters.dZoomParam / 2, Parameters.dMaxHeight
				- Parameters.bmpBtnReturn.getHeight() - Parameters.dZoomParam
				/ 2);
		btn = new Button(REFRESH, bmp, pos, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btn);

		// return buttons
		Button btnReturn = new Button(RETURN, Parameters.bmpBtnReturn,
				Parameters.posBtnReturn, Parameters.bmpBtnReturn.getWidth(),
				Parameters.bmpBtnReturn.getHeight());
		AddButton(btnReturn);
	}

	private LinkedList<HostData> ScanWifiDirect() {
		// TODO: add data

		// dummy list
		LinkedList<HostData> list = new LinkedList<HostData>();
		Random rand = new Random();
		for (int i = 0; i < 5; i++) {
			HostData data = new HostData();
			data.id = "aaskjasfhsfjgfjfa";
			data.bet = Math.abs(rand.nextInt() % 10000) + 1;
			data.level = Math.abs(rand.nextInt() % 8) + 1;

			list.add(data);
		}
		return list;
	}

	@Override
	public void Show(Canvas canvas) {
		bmpBackground.show(canvas);

		int w = Parameters.dMaxWidth;
		int h = Parameters.dMaxHeight;

		RectF rect = new RectF(w / 6, h / 6, w * 5 / 6, h * 5 / 6);

		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.DKGRAY);
		rectPaint.setAlpha(125);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setTextSize(Parameters.dZoomParam * 3 / 4);

		canvas.drawRoundRect(rect, Parameters.dZoomParam / 2,
				Parameters.dZoomParam / 2, rectPaint);

		// write table header
		Helper.drawTextWithMultipleLines(canvas, "Host", new Point(w / 4,
				Parameters.dMaxHeight / 6 + 2 * Parameters.dZoomParam),
				textPaint);
		Helper.drawTextWithMultipleLines(canvas, "Bet", new Point(w / 2,
				Parameters.dMaxHeight / 6 + 2 * Parameters.dZoomParam),
				textPaint);
		Helper.drawTextWithMultipleLines(canvas, "Stage", new Point(w * 3 / 5,
				Parameters.dMaxHeight / 6 + 2 * Parameters.dZoomParam),
				textPaint);

		canvas.drawLine(w / 5, Parameters.dMaxHeight / 4
				+ Parameters.dZoomParam, w * 4 / 5, Parameters.dMaxHeight / 4
				+ Parameters.dZoomParam, textPaint);

		// write content of each host
		Point pos;
		for (int i = 0; i < hostList.size(); i++) {
			pos = new Point(w / 4, Parameters.dMaxHeight / 4 + (i + 1)
					* (Parameters.dZoomParam * 3 / 2) + Parameters.dZoomParam);
			Helper.drawTextWithMultipleLines(canvas, hostList.get(i).id, pos,
					textPaint);

			pos = new Point(w / 2, Parameters.dMaxHeight / 4 + (i + 1)
					* (Parameters.dZoomParam * 3 / 2) + Parameters.dZoomParam);
			Helper.drawTextWithMultipleLines(canvas,
					Integer.toString(hostList.get(i).bet), pos, textPaint);

			pos = new Point(w * 3 / 5, Parameters.dMaxHeight / 4 + (i + 1)
					* (Parameters.dZoomParam * 3 / 2) + Parameters.dZoomParam);
			Helper.drawTextWithMultipleLines(canvas,
					Integer.toString(hostList.get(i).level), pos, textPaint);
		}

		// draw buttons
		for (Button btn : buttonList) {
			btn.show(canvas);
		}
	}

	@Override
	public boolean Action(Point p, Object o) {
		Integer ResultButtonID = (Integer) ClickedButton(p);

		if (ResultButtonID == null)
			return false;
		
		if (ResultButtonID.intValue() == REFRESH) {
			CallRefresh();
			return true;
		}

		if (ResultButtonID.intValue() == RETURN) {
			CallReturn();
			return true;
		}

		HostData host = hostList.get(ResultButtonID.intValue());
		CallConnect(host);
		return true;
	}
	
	private void CallRefresh() {
		this.hostList = ScanWifiDirect();
		GameManager.mainView.invalidate();
	}

	private void CallReturn() {
		GameManager.setCurrentState(GameManager.GameState.MULTIPLAYER_MENU);
		GameManager.mainView.invalidate();
	}

	private void CallConnect(HostData host) {
		String str = host.id + "-" + host.bet + "-" + host.level;
		Toast.makeText(App.getMyContext(), str, Toast.LENGTH_SHORT).show();
	}

}
