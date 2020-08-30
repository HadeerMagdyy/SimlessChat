package com.example.simlesschat.Activites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.simlesschat.Classes.ClientInit;
import com.example.simlesschat.Classes.ServerInit;
import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;
import com.example.simlesschat.Connection.WifiDirectBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class wifi_connection_activity extends AppCompatActivity{

    public static final String TAG = "MainActivity";
    public static final String DEFAULT_CHAT_NAME = "";
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiManager wificontrol;
    private WifiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private Button goToChat;

    List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
    String[]deviceNameArray;
    WifiP2pDevice[] devicesArray;

    private ListView wifi_devices;
    private ImageButton scanbtn;
    private TextView wifi_connectionText;
    private TextView setChatNameLabel;
    private EditText setChatName;
    private ImageView disconnect;
    public static String chatName;
    public static ServerInit server;
    private static final int REQUEST_PERMISSIONS_REQUIRED = 1;
    LocationManager locationManager ;
    boolean GpsStatus ;
    public WifiP2pManager.PeerListListener peerListList;
    public WifiP2pManager.PeerListListener getpeer(){return peerListList;}



    //Getters and Setters
    public WifiP2pManager getmManager() { return mManager; }
    public WifiP2pManager.Channel getmChannel() { return mChannel; }
    public WifiDirectBroadcastReceiver getmReceiver() { return mReceiver; }
    public IntentFilter getmIntentFilter() { return mIntentFilter; }
    public Button getGoToChat(){ return goToChat; }
    public TextView getSetChatNameLabel() { return setChatNameLabel; }
    public ListView getlistview() { return wifi_devices; }
    public EditText getSetChatName() { return setChatName; }
    public TextView getText() { return wifi_connectionText; }
    public ImageView getDisconnect() { return disconnect; }
    public ImageButton getImagebutton(){return scanbtn;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connection_activity);

        //Init the Channel, Intent filter and Broadcast receiver
        init();

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        if(!wificontrol.isWifiEnabled()) {
            wificontrol.setWifiEnabled(true);
            Toast.makeText(getApplicationContext(),"Wifi is enabled Successfully for Discovering",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Wifi is already enabled ",Toast.LENGTH_LONG).show();

        }


        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_REQUIRED);
        }

        boolean gps_enable=CheckGpsStatus();

        if(!gps_enable)
        {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        //Button Go to Settings
        wifi_devices = findViewById(R.id.discoverlistview_wifi);
        scanbtn=(ImageButton)findViewById(R.id.scanbutton_wifi);
        fill_listview();

        //Go to Settings text
        wifi_connectionText = findViewById(R.id.discovertxtview_wifi);

        //Button Go to Chat
        goToChat = findViewById(R.id.goToChat);
        goToChat();

        //Set the chat name
        setChatName = findViewById(R.id.setChatName);
        setChatNameLabel = findViewById(R.id.setChatNameLabel);

        //Button Disconnect
        disconnect = findViewById(R.id.disconnect);
        disconnect();
    }

    public static boolean hasPermissions(Context context,String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.v(TAG, "Discovery process succeeded");
            }

            @Override
            public void onFailure(int reason) {
                Log.v(TAG, "Discovery process failed");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void init(){
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmManager(mManager);
        mReceiver.setmChannel(mChannel);
        mReceiver.setmActivity(this);
        wificontrol=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void goToChat(){
        final Database database=new Database(this);
        goToChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!setChatName.getText().toString().equals("")){

                    if(database.check_wifidevice_existance(setChatName.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(),"This Chat name has been Already Taken please enter another name ",Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Set the chat name
                    saveChatName(wifi_connection_activity.this, setChatName.getText().toString());
                    chatName = loadChatName(wifi_connection_activity.this);

                    //Start the init process
                    if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.IS_OWNER){
                        Toast.makeText(wifi_connection_activity.this, "I'm the group owner  " + mReceiver.getOwnerAddr().getHostAddress(), Toast.LENGTH_SHORT).show();
                        server = new ServerInit();
                        server.start();
                    }
                    else if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.IS_CLIENT){
                        Toast.makeText(wifi_connection_activity.this, "I'm the client", Toast.LENGTH_SHORT).show();
                        ClientInit client = new ClientInit(mReceiver.getOwnerAddr());
                        client.start();
                    }

                    //Open the ChatActivity
                    Intent intent = new Intent(getApplicationContext(), WifiChatActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(wifi_connection_activity.this, "Please enter a chat name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void disconnect(){
        disconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mManager.removeGroup(mChannel, null);
                finish();
            }
        });
    }

    public void fill_listview(){

        scanbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.v(TAG, "Discovery process succeeded");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.v(TAG, "Discovery process failed");
                    }
                });

                peerListList=new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        if(!peerList.getDeviceList().equals(peers))
                        {
                            peers.clear();
                            peers.addAll(peerList.getDeviceList());

                            deviceNameArray=new String[peerList.getDeviceList().size()];
                            devicesArray=new WifiP2pDevice[peerList.getDeviceList().size()];
                            int index=0;

                            for(WifiP2pDevice device : peerList.getDeviceList())
                            {
                                deviceNameArray[index]=device.deviceName;
                                devicesArray[index]=device;
                                index++;
                            }
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray)
                            {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view =super.getView(position, convertView, parent);

                                    TextView textView=(TextView) view.findViewById(android.R.id.text1);

                                    /*YOUR CHOICE OF COLOR*/
                                    textView.setTextSize(15);
                                    textView.setTextColor(Color.WHITE);

                                    return view;
                                }
                            };
                            wifi_devices.setAdapter(adapter);
                        }
                        if(peers.size()==0) {
                            Toast.makeText(getApplicationContext(), "No devices found !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };

                wifi_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        final WifiP2pDevice device = devicesArray[position];
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;

                        mManager.connect(mChannel,config,new WifiP2pManager.ActionListener(){
                            @Override
                            public void onSuccess( ){
                            }

                            @Override
                            public void onFailure(int reason){
                            }
                        });

                    }
                });
            }
        });

    }

    //Save the chat name to SharedPreferences
    public void saveChatName(Context context, String chatName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("chatName", chatName);
        edit.commit();
    }

    //Retrieve the chat name from SharedPreferences
    public static String loadChatName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("chatName", DEFAULT_CHAT_NAME);
    }

    public boolean CheckGpsStatus(){
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }
}
