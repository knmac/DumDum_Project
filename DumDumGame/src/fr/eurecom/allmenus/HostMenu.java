package fr.eurecom.allmenus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

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
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import fr.eurecom.connectivity.DeviceListFragment;
import fr.eurecom.connectivity.WiFiDirectBroadcastReceiver;
import fr.eurecom.connectivity.DeviceListFragment.DeviceActionListener;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.dumdumgame.R;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class HostMenu extends BaseMenu implements ChannelListener, DeviceActionListener {

    private class HostData {
        String id;
        int bet;
        int level;
    }
    
    private enum ButtonID {
        PLUS_BET, MINUS_BET, PREV_STAGE, NEXT_STAGE, HOST, RETURN
    };
    
    private int maxStage = 8; // TODO: change according to the number of stages
    private int stage = 1;
    private int maxCandies;
    private int bet = 10;
    private int betQuantum = 10;

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private ServerSocket server;
    
    private DeviceListFragment dlf = new DeviceListFragment();

    public DeviceListFragment getDLF(){
        return this.dlf;
    }
    
    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void resetData() {
        dlf.clearPeers();
    }
    
    public HostMenu(DynamicBitmap bmpBackground) {
        super(bmpBackground);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) (App.getMyContext()).getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(App.getMyContext(), (App.getMyContext()).getMainLooper(), null);
        
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, App.getMyContext(),this);
        (App.getMyContext()).registerReceiver(receiver, intentFilter);
        
        String betStr = "" + bet;
        String levelStr = "" + stage;
        String deviceName = getDeviceName() + "\t" + betStr + "\t" + levelStr;
        
        setDeviceName(deviceName);
        
        maxCandies = GameManager.user.getCurrentMoney();

        Button btn;
        int w, h;
        Point pos;
        Bitmap bmp;

        // minus bet
        bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(), R.drawable.minus);
        w = Parameters.dZoomParam * 2;
        h = bmp.getHeight() * w / bmp.getWidth();
        pos = new Point(Parameters.dMaxWidth / 4 - w / 2, Parameters.dMaxHeight / 2 - h);
        btn = new Button(ButtonID.MINUS_BET, bmp, pos, w, h);
        AddButton(btn);

        // plus bet
        bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(), R.drawable.plus);
        pos = new Point(Parameters.dMaxWidth * 3 / 4 - w / 2, Parameters.dMaxHeight / 2 - h);
        btn = new Button(ButtonID.PLUS_BET, bmp, pos, w, h);
        AddButton(btn);

        // prev stage
        bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(), R.drawable.arrow_left);
        pos = new Point(Parameters.dMaxWidth / 4 - w / 2, Parameters.dMaxHeight / 2);
        btn = new Button(ButtonID.PREV_STAGE, bmp, pos, w, h);
        AddButton(btn);

        // next stage
        bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(), R.drawable.arrow_right);
        pos = new Point(Parameters.dMaxWidth * 3 / 4 - w / 2, Parameters.dMaxHeight / 2);
        btn = new Button(ButtonID.NEXT_STAGE, bmp, pos, w, h);
        AddButton(btn);

        // connect
        bmp = BitmapFactory.decodeResource(App.getMyContext().getResources(), R.drawable.connect);
        w = Parameters.dZoomParam * 3;
        h = bmp.getHeight() * w / bmp.getWidth();
        pos = new Point(Parameters.dMaxWidth / 2 - w / 2, Parameters.dMaxHeight * 5 / 6 - h);
        btn = new Button(ButtonID.HOST, bmp, pos, w, h);
        AddButton(btn);

        // return
        Button btnReturn = new Button(ButtonID.RETURN, Parameters.bmpBtnReturn, Parameters.posBtnReturn,
                Parameters.bmpBtnReturn.getWidth(), Parameters.bmpBtnReturn.getHeight());
        AddButton(btnReturn);
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
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setTextSize(Parameters.dZoomParam * 3 / 4);

        canvas.drawRoundRect(rect, Parameters.dZoomParam / 2, Parameters.dZoomParam / 2, rectPaint);
        Helper.drawTextWithMultipleLines(canvas, "Total candies: " + Integer.toString(this.maxCandies), new Point(
                w / 2, h / 5 + Parameters.dZoomParam), textPaint);
        textPaint.setTextSize(Parameters.dZoomParam * 4 / 3);
        Helper.drawTextWithMultipleLines(canvas, "Bet candies: " + Integer.toString(this.bet), new Point(w / 2, h / 2
                - Parameters.dZoomParam * 2 + Parameters.dZoomParam), textPaint);
        Helper.drawTextWithMultipleLines(canvas, "Stage: " + Integer.toString(this.stage), new Point(w / 2, h / 2
                + Parameters.dZoomParam), textPaint);

        // draw buttons
        for (Button btn : buttonList) {
            btn.show(canvas);
        }
    }

    @Override
    public boolean Action(Point p, Object o) {
        ButtonID ResultButtonID = (ButtonID) ClickedButton(p);

        if (ResultButtonID == null)
            return false;

        switch (ResultButtonID) {
        case MINUS_BET:
            CallMinusBet();
            break;
        case PLUS_BET:
            CallPlusBet();
            break;
        case PREV_STAGE:
            CallPrevStage();
            break;
        case NEXT_STAGE:
            CallNextStage();
            break;
        case HOST:
            CallConnect();
            break;
        case RETURN:
            CallReturn();
            break;
        default:
            return false;
        }

        return true;
    }

    private void CallMinusBet() {
        if (this.bet - this.betQuantum > 0) {
            this.bet -= this.betQuantum;
            GameManager.mainView.invalidate();
            String betStr = "" + bet;
            String levelStr = "" + stage;
            String deviceName = getDeviceName() + "\t" + betStr + "\t" + levelStr;
            
            setDeviceName(deviceName);
        }
    }

    private void CallPlusBet() {
        if (this.bet + this.betQuantum <= this.maxCandies) {
            this.bet += this.betQuantum;
            GameManager.mainView.invalidate();
            String betStr = "" + bet;
            String levelStr = "" + stage;
            String deviceName = getDeviceName() + "\t" + betStr + "\t" + levelStr;
            
            setDeviceName(deviceName);
        }
    }

    private void CallPrevStage() {
        if (this.stage > 1) {
            this.stage--;
            GameManager.mainView.invalidate();
            String betStr = "" + bet;
            String levelStr = "" + stage;
            String deviceName = getDeviceName() + "\t" + betStr + "\t" + levelStr;
            
            setDeviceName(deviceName);
        }
    }

    private void CallNextStage() {
        if (this.stage < maxStage) {
            this.stage++;
            GameManager.mainView.invalidate();
            String betStr = "" + bet;
            String levelStr = "" + stage;
            String deviceName = getDeviceName() + "\t" + betStr + "\t" + levelStr;
            
            setDeviceName(deviceName);
        }
    }

    private void CallConnect() {
        // TODO
        // accept a connection
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(App.getMyContext(), "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(App.getMyContext(), "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(App.getMyContext(), "Waiting for opponent...", Toast.LENGTH_LONG).show();
        //new FileServerAsyncTask(App.getMyContext(), null).execute();
    }

    private void CallReturn() {
        try {
            server.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GameManager.setCurrentState(GameManager.GameState.MULTIPLAYER_MENU);
        GameManager.mainView.invalidate();
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        
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
            Toast.makeText(App.getMyContext(), "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(App.getMyContext(), App.getMyContext().getMainLooper(), this);
        } else {
            Toast.makeText(App.getMyContext(),
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {
    }
    
    public void setDeviceName(String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = ActionListener.class;
            Method setDeviceName = manager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = channel;
            arglist[1] = devName;
            arglist[2] = new ActionListener() {

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                }
            };

            setDeviceName.invoke(manager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public String getDeviceName() {
        String model = Build.MODEL;
        return model;
     }
}
