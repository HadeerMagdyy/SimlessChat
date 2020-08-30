package com.example.simlesschat.Activites;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageView;


import com.example.simlesschat.Utilites.FileUtilities;
import com.example.simlesschat.R;

import java.io.IOException;

public class RecordAudioActivity extends Activity{
	private static final String TAG = "RecordAudioActivity";
	private ImageView buttonRecord;
	private ImageView buttonPlay;
	private ImageView buttonOk;
	private MediaRecorder mRecorder;
	private String mFileName;
	private MediaPlayer mPlayer;
	private boolean isRecording = false;
	private Chronometer timer;
	private  boolean isplaying=false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_audio);
		
		mFileName = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        mFileName += "/" + FileUtilities.fileName() + ".3gp";
        
        buttonRecord = (ImageView) findViewById(R.id.record_audio);
        buttonPlay = (ImageView) findViewById(R.id.play_audio);
        buttonOk = (ImageView) findViewById(R.id.ok);
        buttonPlay.setVisibility(View.GONE);
        buttonOk.setVisibility(View.GONE);
        timer=(Chronometer)findViewById(R.id.record_timer);
        
		buttonRecord.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!isRecording){
					isRecording = true;
					buttonRecord.setImageDrawable(getResources().getDrawable(R.drawable.microphone_stop));
					buttonPlay.setVisibility(View.GONE);
			        buttonOk.setVisibility(View.GONE);
					startRecording();					
				}
				else{
					isRecording = false;
					buttonRecord.setImageDrawable(getResources().getDrawable(R.drawable.microphone_start));
					stopRecording();
					buttonPlay.setVisibility(View.VISIBLE);
			        buttonOk.setVisibility(View.VISIBLE);
				}
			}
		});
		
		buttonPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(!isplaying){
					isplaying = true;
					buttonPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_stop));
					startPlaying();
				}
				else{
					isplaying = false;
					buttonPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
					stopPlaying();
				}
			}
		});
		
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("audioPath", mFileName);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	@Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

	public void startRecording(){

		timer.setBase(SystemClock.elapsedRealtime());
		timer.start();

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
	}
	
	public void stopRecording(){
		mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        timer.stop();
	}
	
	public void startPlaying(){
		mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
	}
	
	public void stopPlaying(){
		mPlayer.release();
        mPlayer = null;
	}
}
