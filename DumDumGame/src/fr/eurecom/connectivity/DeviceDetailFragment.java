/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.eurecom.connectivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment implements ConnectionInfoListener {

	public static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;
	private Context activity;
	private int type;
	private int level;
	private int bet;
	public static ServerAsyncTask server = null;
	public static ClientAsyncTask client = null;

	public DeviceDetailFragment(Context activity2, int level, int bet) {
		// TODO Auto-generated constructor stub
		super();
		this.activity = activity2;
		this.level = level;
		this.bet = bet;
	}
	
	public void callClient() {
		new ClientAsyncTask("", 0);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		Uri uri = data.getData();
		Intent serviceIntent = new Intent(activity, FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
				uri.toString());
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				info.groupOwnerAddress.getHostAddress());
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
				8988);
		activity.startService(serviceIntent);
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		this.info = info;

		if (info.groupFormed && info.isGroupOwner) {
			/*
			 * Toast.makeText((App.getMyContext()), "Server",
			 * Toast.LENGTH_SHORT) .show(); server = (ServerAsyncTask) new
			 * ServerAsyncTask( App.getMyContext(), level).execute(); try {
			 * Thread.sleep(2000); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * createGame(level);
			 */
			//Toast.makeText((App.getMyContext()), "Client", Toast.LENGTH_SHORT)
			//		.show();
			server = (ServerAsyncTask) new ServerAsyncTask(App.getMyContext(),
					bet).execute();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			createGame(level, bet);
			
		} else if (info.groupFormed) {
			/*
			 * Toast.makeText((App.getMyContext()), "Client",
			 * Toast.LENGTH_SHORT) .show(); client = (ClientAsyncTask) new
			 * ClientAsyncTask("", level) .execute(); try { Thread.sleep(2000);
			 * } catch (InterruptedException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } if (level != -1) createGame(level);
			 */
			//Toast.makeText((App.getMyContext()), "Server", Toast.LENGTH_SHORT)
			//		.show();
			client = (ClientAsyncTask) new ClientAsyncTask("", bet).execute();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (level != -1)
				createGame(level, bet);
		}
	}

	private void createGame(int level, int bet) {
		// Do a down on the mutex
		try {
			Parameters.mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Critical Region--------------------------------------------
		GameManager.setCurrentState(GameManager.GameState.GAME);
		GameManager.chosenLevel = level;
		if (client != null) {
			GameManager.initGameDuoClient(bet);
			GameManager.mainView.invalidate();
			Parameters.mutex.release();
		} else {
			if (server != null) {
				GameManager.initGameDuoHost(bet);
				GameManager.mainView.invalidate();
				Parameters.mutex.release();
			}
		}

		// ------------------------------------------------------------
		// Do an up on the mutex

	}

	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		Toast.makeText((App.getMyContext()), this.device.deviceName,
				Toast.LENGTH_SHORT).show();
	}

	public static class ServerAsyncTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private TextView statusText;
		private int bet;
		private ServerSocket serverSocket;
		private Socket client;
		private String message = "";

		/**
		 * @param context
		 * @param statusText
		 */
		public ServerAsyncTask(Context context, int bet) {
			this.context = context;
			this.bet = bet;
		}

		public int getBet(){
			return this.bet;
		}
		
		public void sendMessage(String msg) {
			DataOutputStream dataOutputStream;
			try {
				if (client != null) {
					dataOutputStream = new DataOutputStream(
							client.getOutputStream());
					dataOutputStream.writeUTF(msg);
					Log.i("CONNECTIVITY", "sent to client: " + msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String getMessage() {
			return this.message;
		}

		@Override
		// receive
		protected Void doInBackground(Void... params) {
			try {

				try {
					serverSocket = new ServerSocket(8888);
					Log.i("CONNECTIVITY", "Serversocket: "
							+ serverSocket.getInetAddress().getHostAddress()
									.toString());
					client = serverSocket.accept();
					Log.i("CONNECTIVITY", "client accepted");
				} catch (Exception e) {
					Helper.onConnectionError();
				}

				DataInputStream dataInputStream = new DataInputStream(
						client.getInputStream());
				while (true) {
					String msg = dataInputStream.readUTF();
					this.message = msg;
					Log.i("CONNECTIVITY", "Server received: " + this.message);
				}

			} catch (IOException e) {
				Helper.onConnectionError();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			Log.i("CONNECTIVITY", "ServerPreExcute");
			// statusText.setText("Opening a server socket");
		}

		public void onClose() {
			try {
				this.serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static class ClientAsyncTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private WifiP2pInfo info;
		private String s = "";
		private String t = "";
		private int bet;
		private Socket socket = new Socket();
		private int port = 8888;
		private String message = "";

		public ClientAsyncTask(String t, int bet) {
			this.t = t;
			this.bet = bet;

		}

		public int getBet(){
			return this.bet;
		}
		
		// send function
		public void sendMessage(String msg) {
			DataOutputStream dataOutputStream;
			try {
				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				dataOutputStream.writeUTF(msg);
				Log.i("CONNECTIVITY", "sent to server: " + msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String getMessage() {
			return this.message;
		}

		@Override
		// receive function
		protected Void doInBackground(Void... params) {
			try {

				String host = "192.168.49.1";// info.groupOwnerAddress.getHostAddress();
				Log.i("CONNECTIVITY", "hostIP: " + host);
				try {
					socket.bind(null);
					socket.connect((new InetSocketAddress(host, port)));
				} catch (Exception e) {
					Helper.onConnectionError();
				}
				DataInputStream dataInputStream = new DataInputStream(
						socket.getInputStream());
				while (true) {
					// this.message = "";
					String msg = dataInputStream.readUTF();
					this.message = msg;
					Log.i("CONNECTIVITY", "client received: " + this.message);
				}
			} catch (IOException e) {
				Helper.onConnectionError();
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			Log.i("CONNECTIVITY", "ClientPreExcute");
		}

	}

}
