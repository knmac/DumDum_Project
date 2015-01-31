package fr.eurecom.allmenus;

import java.util.LinkedList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.util.Log;
import android.widget.Toast;
import fr.eurecom.connectivity.DeviceListFragment.DeviceActionListener;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import fr.eurecom.connectivity.*;

public class ClientMenu extends BaseMenu implements ChannelListener,
		DeviceActionListener {

	private class HostData {
		String id;
		int bet;
		int level;
	}

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;

	private LinkedList<HostData> hostList = null;

	private final Integer RETURN = 1000;
	private final Integer REFRESH = 2000;

	public static final String TAG = "wifidirectdemo";
	private WifiP2pManager manager;
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;

	private DeviceListFragment dlf = new DeviceListFragment();
	private DeviceDetailFragment ddf;

	/**
	 * @param isWifiP2pEnabled
	 *            the isWifiP2pEnabled to set
	 */
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	public DeviceListFragment getDLF() {
		return this.dlf;
	}

	public ClientMenu(DynamicBitmap bmpBackground) {
		super(bmpBackground);

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) (App.getMyContext())
				.getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(App.getMyContext(),
				(App.getMyContext()).getMainLooper(), null);

		// App.getMyContext().startActivity(new
		// Intent(Settings.ACTION_WIRELESS_SETTINGS));

		receiver = new WiFiDirectBroadcastReceiver(manager, channel,
				App.getMyContext(), this);
		(App.getMyContext()).registerReceiver(receiver, intentFilter);

		// setDeviceName("HOANG XUAN QUANG NHAT");

		// Get the list of all hosts
		// hostList = ScanWifiDirect();

		// add button for each item
		Button btn;
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.connect);
		int w, h;
		Point pos;

		w = h = Parameters.dZoomParam;

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

	/**
	 * Remove all peers and clear all fields. This is called on
	 * BroadcastReceiver receiving a state change event.
	 */
	public void resetData() {
		dlf.clearPeers();
	}

	public int returnLevel() {
		if(this.hostList!=null)
			return this.hostList.getFirst().level;
		return -1;
	}

	public int returnBet() {
		return this.hostList.getFirst().bet;
	}
	
	public LinkedList<HostData> getHostList() {
		return this.hostList;
	}
	
	private LinkedList<HostData> ScanWifiDirect() {
		// TODO: add data
		// scan
		// dummy list
		LinkedList<HostData> list = new LinkedList<HostData>();
		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(App.getMyContext(), "Discovery Initiated",
						Toast.LENGTH_SHORT).show();
				if ( dlf.peers.size() == 0)
				Toast.makeText(App.getMyContext(), "No games found. Please try again!",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast.makeText(App.getMyContext(),
						"Discovery Failed : " + reasonCode, Toast.LENGTH_SHORT)
						.show();
			}
		});

		for (int i = 0; i < dlf.peers.size(); i++) {
			HostData data = new HostData();
			String info = dlf.peers.get(i).deviceName;
			String splitted[] = info.split("_");
			if (splitted.length > 1) {
				data.id = splitted[0];
				data.bet = Integer.parseInt(splitted[1]);
				data.level = Integer.parseInt(splitted[2]);
			} else
				data.id = dlf.peers.get(i).deviceName;
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
		if (hostList != null)
			for (int i = 0; i < hostList.size(); i++) {
				pos = new Point(w / 4, Parameters.dMaxHeight / 4 + (i + 1)
						* (Parameters.dZoomParam * 3 / 2)
						+ Parameters.dZoomParam);
				Helper.drawTextWithMultipleLines(canvas, hostList.get(i).id,
						pos, textPaint);

				pos = new Point(w / 2, Parameters.dMaxHeight / 4 + (i + 1)
						* (Parameters.dZoomParam * 3 / 2)
						+ Parameters.dZoomParam);
				Helper.drawTextWithMultipleLines(canvas,
						Integer.toString(hostList.get(i).bet), pos, textPaint);

				pos = new Point(w * 3 / 5, Parameters.dMaxHeight / 4 + (i + 1)
						* (Parameters.dZoomParam * 3 / 2)
						+ Parameters.dZoomParam);
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
		Button btn;
		Bitmap bmp = BitmapFactory.decodeResource(App.getMyContext()
				.getResources(), R.drawable.connect);
		int w, h;
		Point pos;
		w = h = Parameters.dZoomParam;
		if (hostList.size() != 0) {
			for (int i = 0; i < hostList.size(); i++) {
				pos = new Point(Parameters.dMaxWidth * 5 / 7 - w,
						Parameters.dMaxHeight / 4 + (i + 1) * (h * 3 / 2) + h
								/ 3);
				btn = new Button((Integer) i, bmp, pos, w, h);
				AddButton(btn);
			}
		}
	}

	public BroadcastReceiver getReceiver() {
		return this.receiver;
	}

	private void CallReturn() {
		if (DeviceDetailFragment.client !=null)
			DeviceDetailFragment.client = null;
		GameManager.setCurrentState(GameManager.GameState.MULTIPLAYER_MENU);
		// (App.getMyContext()).unregisterReceiver(receiver);
		GameManager.mainView.invalidate();
	}

	private void CallConnect(HostData host) {
		// Connect to master
		final WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = dlf.peers.get(0).deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		// ).connect(config);
		manager.connect(channel, config, new ActionListener() {
			@Override
			public void onSuccess() {
				Toast.makeText((App.getMyContext()), "Loading...",
						Toast.LENGTH_LONG).show();
				// DeviceDetailFragment fragment = new
				// DeviceDetailFragment(App.getMyContext());
				// manager.requestConnectionInfo(channel, fragment);
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText((App.getMyContext()), "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void connect(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText((App.getMyContext()), "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void disconnect() {
		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onFailure(int reasonCode) {
				Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
			}

			@Override
			public void onSuccess() {

			}

		});
	}

	@Override
	public void onChannelDisconnected() {
		// we will try once more
		if (manager != null && !retryChannel) {
			Toast.makeText(App.getMyContext(), "Channel lost. Trying again",
					Toast.LENGTH_LONG).show();
			resetData();
			retryChannel = true;
			manager.initialize(App.getMyContext(), App.getMyContext()
					.getMainLooper(), this);
		} else {
			Toast.makeText(
					App.getMyContext(),
					"Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void showDetails(WifiP2pDevice device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelDisconnect() {
		// TODO Auto-generated method stub

	}
}
