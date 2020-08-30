package com.example.simlesschat.Activites;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simlesschat.Adapters.MessageAdapter;
import com.example.simlesschat.Classes.Image;
import com.example.simlesschat.Classes.MediaFile;
import com.example.simlesschat.Classes.Message;
import com.example.simlesschat.Classes.MessageService;
import com.example.simlesschat.Connection.SendMessageClient;
import com.example.simlesschat.Connection.SendMessageServer;
import com.example.simlesschat.Connection.WifiDirectBroadcastReceiver;
import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;
import com.example.simlesschat.Utilites.FileUtilities;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class WifiChatActivity extends AppCompatActivity{
    private static final String TAG = "ChatActivity";
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int RECORD_AUDIO = 3;
    private static final int RECORD_VIDEO = 4;
    private static final int CHOOSE_FILE = 5;
    private static final int DRAWING = 6;
    private static final int REQUEST_PERMISSIONS_REQUIRED = 7;

    private WifiP2pManager mManager;
    private Channel mChannel;
    public static WifiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private EditText edit;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static List<Message> listMessage;
    public static MessageAdapter messageAdapter;
    private ImageButton recordbtn;
    private Uri fileUri;
    private String fileURL;
    private ArrayList<Uri> tmpFilesUri;
    private Uri mPhotoUri;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_chat);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(wifi_connection_activity.chatName);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmActivity(this);

        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_REQUIRED);
        }

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        database=new Database(this);

        //Start the service to receive message
        startService(new Intent(this, MessageService.class));

        edit = (EditText) findViewById(R.id.Messagetxt_id);

        //Initialize the adapter for the chat
        recyclerView = (RecyclerView) findViewById(R.id.Recyclerview_messages);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listMessage = new ArrayList<Message>();
        messageAdapter = new MessageAdapter(this, listMessage);
        recyclerView.setAdapter(messageAdapter);


        //Initialize the list of temporary files URI
        tmpFilesUri = new ArrayList<Uri>();

        //Send a message
        ImageButton button = (ImageButton) findViewById(R.id.sendmessagebtn_wifichat_id);


        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!edit.getText().toString().equals("")){
//					Log.v(TAG, "Send message");
                    sendMessage(Message.TEXT_MESSAGE);
                }
                else{
                    Toast.makeText(WifiChatActivity.this, "Please enter a not empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recordbtn=(ImageButton)findViewById(R.id.recordmessagebtn_wifichat_id) ;
        recordbtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Log.v(TAG, "Start activity to record audio");
                startActivityForResult(new Intent(WifiChatActivity.this, RecordAudioActivity.class), RECORD_AUDIO);
            }
        });


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
        saveStateForeground(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        saveStateForeground(false);
    }


    @Override
    protected void onDestroy() {
        super.onStop();
        super.onDestroy();
        clearTmpFiles(getExternalFilesDir(null));
    }

    // Handle the data sent back by the 'for result' activities (pick/take image, record audio/video)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case PICK_IMAGE:
                if (resultCode == RESULT_OK && data.getData() != null) {
                    fileUri = data.getData();
                    sendMessage(Message.IMAGE_MESSAGE);
                }
                break;
            case TAKE_PHOTO:

                if (resultCode == RESULT_OK) {

                    fileUri = mPhotoUri;
                    sendMessage(Message.IMAGE_MESSAGE);
                    tmpFilesUri.add(fileUri);
                }

                else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                } else {
                    // Image capture failed, advise user
                }

                break;
            case RECORD_AUDIO:
                if (resultCode == RESULT_OK) {
                    fileURL = (String) data.getStringExtra("audioPath");
                    sendMessage(Message.AUDIO_MESSAGE);
                }
                break;
            case RECORD_VIDEO:
                if (resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    fileURL = MediaFile.getRealPathFromURI(this, fileUri);
                    sendMessage(Message.VIDEO_MESSAGE);
                }
                break;
            case CHOOSE_FILE:
                if (resultCode == RESULT_OK) {
                    fileURL = (String) data.getStringExtra("filePath");
                    sendMessage(Message.FILE_MESSAGE);
                }
                break;
            case DRAWING:
                if(resultCode == RESULT_OK){
                    fileURL = (String) data.getStringExtra("drawingPath");
                    sendMessage(Message.DRAWING_MESSAGE);
                }
                break;
        }
    }

    public void sendMessage(int type){

        Message mes;
        mes = new Message(type,edit.getText().toString(),null);

        switch(type){
            case Message.IMAGE_MESSAGE:
                Image image = new Image(this, fileUri);
                Log.e(TAG, "Bitmap from url ok" + fileUri);
                mes.setByteArray(image.bitmapToByteArray(image.getBitmapFromUri()));
                mes.setFileName(image.getFileName());
                mes.setFileSize(image.getFileSize());
                Log.e(TAG, "Set byte array to image ok"+image.getFileSize()+"-"+image.getFileName());

                break;
            case Message.AUDIO_MESSAGE:
                MediaFile audioFile = new MediaFile(fileURL,Message.AUDIO_MESSAGE);
                mes.setByteArray(audioFile.fileToByteArray());
                mes.setFileName(audioFile.getFileName());
                mes.setFilePath(audioFile.getFilePath());
                break;
            case Message.VIDEO_MESSAGE:
                MediaFile videoFile = new MediaFile( fileURL,Message.VIDEO_MESSAGE);
                mes.setByteArray(videoFile.fileToByteArray());
                mes.setFileName(videoFile.getFileName());
                mes.setFilePath(videoFile.getFilePath());
                tmpFilesUri.add(fileUri);
                break;
            case Message.FILE_MESSAGE:
                MediaFile file = new MediaFile( fileURL,Message.FILE_MESSAGE);
                mes.setByteArray(file.fileToByteArray());
                mes.setFileName(file.getFileName());
                break;
            case Message.DRAWING_MESSAGE:
                MediaFile drawingFile = new MediaFile(fileURL,Message.DRAWING_MESSAGE);
                mes.setByteArray(drawingFile.fileToByteArray());
                mes.setFileName(drawingFile.getFileName());
                mes.setFilePath(drawingFile.getFilePath());
                break;
        }

        if(listMessage.size()==0)
        {
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            String name=sharedPreferences.getString(getString(R.string.username),"No name");
            database.insert_new_wifidevice(wifi_connection_activity.chatName,date,name);
        }

        if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
            Log.e(TAG, "Message hydrated, start SendMessageServer AsyncTask");
            new SendMessageServer(WifiChatActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
            refreshList(mes,true);
        }
        else if(mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
            Log.e(TAG, "Message hydrated, start SendMessageClient AsyncTask");
            new SendMessageClient(WifiChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
            refreshList(mes,true);
        }

        edit.setText("");
    }

    public static void refreshList(Message message, boolean isMine){

        message.setMine(isMine);
        listMessage.add(message);
        messageAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(listMessage.size() - 1);

    }


    // Save the app's state (foreground or background) to a SharedPrefereces
    public void saveStateForeground(boolean isForeground){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor edit = prefs.edit();
        edit.putBoolean("isForeground", isForeground);
        edit.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    // Handle click on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch(idItem){
            case R.id.send_image:
                showPopup(edit);
                return true;

            case R.id.send_video:
                Log.v(TAG, "Start activity to record video");
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, RECORD_VIDEO);
                }
                return true;

            case R.id.send_file:
                Log.v(TAG, "Start activity to choose file");
                Intent chooseFileIntent = new Intent(this, FilePickerActivity.class);
                startActivityForResult(chooseFileIntent, CHOOSE_FILE);
                return true;

            case R.id.send_drawing:
                Log.v(TAG, "Start activity to draw");
                Intent drawIntent = new Intent(this, DrawingActivity.class);
                startActivityForResult(drawIntent, DRAWING);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Show the popup menu
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pick_image:
                        Log.e(TAG, "Pick an image");
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, PICK_IMAGE);
                        }
                        break;

                    case R.id.take_photo:
                        Log.e(TAG, "Take a photo");

                        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                        Intent intent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent4.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        startActivityForResult(intent4, TAKE_PHOTO);

                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.send_image);
        popup.show();
    }

    private void clearTmpFiles(File dir){
        File[] childDirs = dir.listFiles();
        for(File child : childDirs){
            if(child.isDirectory()){
                clearTmpFiles(child);
            }
            else{
                child.delete();
            }
        }
        for(Uri uri: tmpFilesUri){
            getContentResolver().delete(uri, null, null);
        }
        FileUtilities.refreshMediaLibrary(this);
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

}
