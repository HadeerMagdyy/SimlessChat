package com.example.simlesschat.Connection;

import android.content.Context;
import android.os.Build;

import com.example.simlesschat.Classes.AbstractReceiver;
import com.example.simlesschat.Classes.Message;
import com.example.simlesschat.Activites.WifiChatActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveMessageClient extends AbstractReceiver{
	private static final int SERVER_PORT = 4446;
	private Context mContext;
	private ServerSocket socket;

	public ReceiveMessageClient(Context context){
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket destinationSocket = socket.accept();
				
				InputStream inputStream = destinationSocket.getInputStream();
				BufferedInputStream buffer = new BufferedInputStream(inputStream);
				ObjectInputStream objectIS = new ObjectInputStream(buffer);
				Message message = (Message) objectIS.readObject();

				destinationSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
		return null;
	}

	@Override
	protected void onCancelled() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			playNotification(mContext, values[0]);
		}

		//If the message contains a video or an audio, we saved this file to the external storage
		int type = values[0].getmType();
		if(type==Message.AUDIO_MESSAGE || type==Message.VIDEO_MESSAGE || type==Message.FILE_MESSAGE || type==Message.DRAWING_MESSAGE){
			values[0].saveByteArrayToFile(mContext);
		}

		WifiChatActivity.refreshList(values[0], false);

	}

}
