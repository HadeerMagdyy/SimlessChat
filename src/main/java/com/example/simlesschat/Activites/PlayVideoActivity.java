package com.example.simlesschat.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simlesschat.R;

public class PlayVideoActivity extends AppCompatActivity{
	
	private VideoView videoPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video);
		
		getSupportActionBar().hide();
		
		Intent intent = getIntent();
		String filePath = (String) intent.getStringExtra("filePath");
		System.out.println("PlayVideoActivity   " + filePath);
		
		videoPlayer = (VideoView) findViewById(R.id.videoPlayer);
		videoPlayer.setVideoPath(filePath);
		videoPlayer.setMediaController(new MediaController(this));
		
		videoPlayer.start();
	}
}
