package com.example.simlesschat.Connection;

import android.content.Context;
import android.os.AsyncTask;


import com.example.simlesschat.Classes.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendMessageClient extends AsyncTask<Message, Message, Message>{
	private static final String TAG = "SendMessageClient";
	private Context mContext;
	private static final int SERVER_PORT = 4445;
	private InetAddress mServerAddr;
	private boolean isMine;
	
	public SendMessageClient(Context context,InetAddress serverAddr){
		mContext = context;
		mServerAddr = serverAddr;
	}
	
	@Override
	protected Message doInBackground(Message... msg) {

		
		//Send the message
		Socket socket = new Socket();
		try {
			socket.setReuseAddress(true);
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			OutputStream outputStream = socket.getOutputStream();
			new ObjectOutputStream(outputStream).writeObject(msg[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		            	e.printStackTrace();
		            }
		        }
		    }
		}
		
		return msg[0];
	}

	@Override
	protected void onProgressUpdate(Message... msg) {
		super.onProgressUpdate(msg);
	}

	@Override
	protected void onPostExecute(Message result) {
		super.onPostExecute(result);
	}
	

}
