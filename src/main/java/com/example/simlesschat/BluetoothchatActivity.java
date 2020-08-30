package com.example.simlesschat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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

import com.example.simlesschat.Adapters.MessageAdapter;
import com.example.simlesschat.Classes.security;
import com.example.simlesschat.Database.Database;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothchatActivity extends AppCompatActivity{
    String outstring ;
    String stringsec ;
    String instring;
    public static String chatname;
    private TextView status;
    private Button btnConnect, btnEnable,btn_visible;
    private ImageButton btn_send, btn_image;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Dialog dialog;
    private EditText messagetxt;
    private MessageAdapter messageAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<com.example.simlesschat.Classes.Message> messageList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private InputStream inputStream=null;
    private byte[]sora;
    private ImageView image;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int IMAGE = 6;
    public static final int IMAGE_READ=7;
    public static final int IMAGE_WRITE=8;
    public static final String DEVICE_OBJECT = "device_name";
    public String receivername="No name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothchat);

        findViewsByIds();

        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,"Bluetooth is not available!",Toast.LENGTH_SHORT).show();
            finish();
        }

        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(this,"Bluetooth is enabled",Toast.LENGTH_LONG).show();
        }

        btnEnable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (bluetoothAdapter.isEnabled())
                    Toast.makeText(getApplicationContext(),"Bluetooth is already enabled",Toast.LENGTH_LONG).show();
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
            }
        });


        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showPrinterPickDialog();
            }
        });

    }


    private Handler handler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(Message msg){
          //  message newmsg;
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            receivername=connectingDevice.getName();
                            chatname=receivername;
                            editor.putString(getString(R.string.connecteduser),connectingDevice.getName());
                            btnConnect.setEnabled(false);
                            break;

                        case BluetoothController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            btnConnect.setEnabled(false);
                            break;

                        case BluetoothController.STATE_LISTEN:

                        case BluetoothController.STATE_NONE:
                            setStatus("Not connected");
                            break;

                    }
                    break;
                case MESSAGE_WRITE:

                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    String username=sharedPreferences.getString(getString(R.string.username),"No name");


                    com.example.simlesschat.Classes.Message newmsg ;
                   newmsg = new com.example.simlesschat.Classes.Message(1,"Me: " + writeMessage,null);
                   newmsg.setMine(true);
                   messageList.add(newmsg);
                   messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    break;

                case MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf,0,msg.arg1);
                    com.example.simlesschat.Classes.Message newmsg1;
                    newmsg1 = new com.example.simlesschat.Classes.Message(1,connectingDevice.getName() + ":  " + readMessage,null);
                   newmsg1.setMine(false);
                    messageList.add(newmsg1);
                   messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    break;

                case MESSAGE_DEVICE_OBJECT:

                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(),"Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TOAST:

                    Toast.makeText(getApplicationContext(),msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;


            }
            return false;
        }
    });


    private void showPrinterPickDialog( ){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.bluetooth_devices_layout);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        dialog.findViewById(R.id.scanButton_bluetooth).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                discoveredDevicesAdapter.clear();
                discoveredDevicesAdapter.notifyDataSetChanged();

                bluetoothAdapter.startDiscovery();
            }
        });

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextSize(15);
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };
        discoveredDevicesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextSize(15);
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList_bluetooth);
        final ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList_bluetooth);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver,filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver,filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton_bluetooth).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });


        dialog.setCancelable(false);
        dialog.show();
    }

    private void setStatus(String s){
        status.setText(s);
    }


    private void connectToDevice(String deviceAddress){
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }

    private void findViewsByIds( ){

        status = (TextView) findViewById(R.id.bluetoothstatus_chat_id);
        btnConnect = (Button) findViewById(R.id.discoverbtn_bluetooth);
        recyclerView = (RecyclerView) findViewById(R.id.Recyclerview_messages_bluetooth);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagetxt = (EditText) findViewById(R.id.Messagetxt_bluetooth_id);
        btnEnable = (Button) findViewById(R.id.enablebtn_bluetooth);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        messageAdapter = new MessageAdapter(this,messageList);
        recyclerView.setAdapter(messageAdapter);
        btn_send = (ImageButton) findViewById(R.id.sendmessagebtn_Bluetoothchat_id);

        btn_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (messagetxt.getText().toString().equals("")) {
                    Toast.makeText(BluetoothchatActivity.this,"Please input some texts",Toast.LENGTH_SHORT).show();
                } else {
                    //TODO: here
                    try {
                        sendMessage(messagetxt.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    messagetxt.setText("");
                }
            }
        });

        btn_visible=(Button)findViewById(R.id.visibleButton_bluetooth);

        btn_visible.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent dIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
                startActivity(dIntent);
                bluetoothAdapter.startDiscovery();
            }
        });


    }



    private void sendMessage(String message) throws Exception {


        if (chatController.getState() != BluetoothController.STATE_CONNECTED) {
            Toast.makeText(this,"Connection was lost!",Toast.LENGTH_SHORT).show();
            return;
        }


        if (message.length() > 0) {
            chatController.write(message.getBytes(),"text");
        }
    }



    @Override
    public void onStart( ){
        super.onStart();
        if (! bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new BluetoothController(this,handler);
        }
    }

    @Override
    public void onResume( ){
        super.onResume();

        if (chatController != null) {
            if (chatController.getState() == BluetoothController.STATE_NONE) {
                chatController.start();
            }
        }
    }

    @Override
    public void onDestroy( ){
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context,Intent intent){
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (device.getName() != null)
                        discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    Toast.makeText(getApplicationContext(),getString(R.string.none_found),Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    public void opengallery(){
        Toast.makeText(getApplicationContext(),"swr",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE);
    }

    public static byte[]getBitmapAsByteArray(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,outputStream);
        return outputStream.toByteArray();
    }



}


