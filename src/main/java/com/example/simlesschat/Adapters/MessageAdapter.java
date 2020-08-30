package com.example.simlesschat.Adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simlesschat.Activites.AllchatsActivity;
import com.example.simlesschat.BluetoothchatActivity;
import com.example.simlesschat.Utilites.FileUtilities;
import com.example.simlesschat.Classes.Message;
import com.example.simlesschat.Activites.PlayVideoActivity;
import com.example.simlesschat.R;
import com.example.simlesschat.Activites.ViewImageActivity;
import com.example.simlesschat.Activites.WifiChatActivity;
import com.example.simlesschat.Activites.wifi_connection_activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageviewHolder> {

	public static String TAG = "ChatAdapter";
	private List<Message> listMessage;
	private LayoutInflater inflater;
	public static Bitmap bitmap;
	private Context mContext;
	private HashMap<String, Bitmap> mapThumb;


	public MessageAdapter(Context context,List<Message> listMessage){
		this.listMessage = listMessage;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mapThumb = new HashMap<String, Bitmap>();
	}

	@NonNull
	@Override
	public MessageviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message,parent,false);
		return new MessageviewHolder(view);
	}


	@Override
	public void onBindViewHolder(@NonNull final MessageviewHolder holder,final int position){

		Message mes = listMessage.get(position);
		int type = mes.getmType();

		disableAllMediaViews(holder);


		if(!listMessage.get(position).isMine()) {
			if(mContext instanceof WifiChatActivity ){
				holder.receiver_profile.setText(String.valueOf(wifi_connection_activity.chatName.charAt(0)));
			}

			else{
				holder.receiver_profile.setText(String.valueOf(BluetoothchatActivity.chatname.charAt(0)));
			}
		}
		/***********************************************
		 Text Message
		 ***********************************************/
		if(type == Message.TEXT_MESSAGE){
			enableTextView(holder, mes.getmText(),listMessage.get(position).isMine(),position);
		}

		/***********************************************
		 Image Message
		 ***********************************************/
		else if(type == Message.IMAGE_MESSAGE){
			if(listMessage.get(position).isMine()) {
				holder.senderimage.setVisibility(View.VISIBLE);

				if(!mapThumb.containsKey(mes.getFileName())){
					Bitmap thumb = mes.byteArrayToBitmap(mes.getByteArray());
					mapThumb.put(mes.getFileName(), thumb);
				}
				holder.senderimage.setImageBitmap(mapThumb.get(mes.getFileName()));
				holder.senderimage.setTag(position);

				holder.senderimage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Message mes = listMessage.get((Integer) v.getTag());
						bitmap = mes.byteArrayToBitmap(mes.getByteArray());

						Intent intent = new Intent(mContext, ViewImageActivity.class);
						String fileName = mes.getFileName();
						intent.putExtra("fileName", fileName);

						mContext.startActivity(intent);
					}
				});

				holder.senderimage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Save Image to gallery").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadImage(position);
								return false;
							}
						});

						menu.add("Delete Image").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}
			else {
				holder.receiverimage.setVisibility(View.VISIBLE);
				holder.receiver_profile.setVisibility(View.VISIBLE);

				if(!mapThumb.containsKey(mes.getFileName())){
					Bitmap thumb = mes.byteArrayToBitmap(mes.getByteArray());
					mapThumb.put(mes.getFileName(), thumb);
				}
				holder.receiverimage.setImageBitmap(mapThumb.get(mes.getFileName()));
				holder.receiverimage.setTag(position);

				holder.receiverimage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Message mes = listMessage.get((Integer) v.getTag());
						bitmap = mes.byteArrayToBitmap(mes.getByteArray());

						Intent intent = new Intent(mContext, ViewImageActivity.class);
						String fileName = mes.getFileName();
						intent.putExtra("fileName", fileName);

						mContext.startActivity(intent);
					}
				});

				holder.receiverimage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Save Image to gallery").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadImage(position);
								return false;
							}
						});

						menu.add("Delete Image").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});

			}

		}

		/***********************************************
		 Audio Message
		 ***********************************************/
		else if(type == Message.AUDIO_MESSAGE){
			if(listMessage.get(position).isMine())
			{
				holder.sender_audioPlayer.setVisibility(View.VISIBLE);
				holder.sender_audioPlayer.setTag(position);
				holder.sender_audioPlayer.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						MediaPlayer mPlayer = new MediaPlayer();
						Message mes = listMessage.get((Integer) v.getTag());
						try {
							mPlayer.setDataSource(mes.getFilePath());
							mPlayer.prepare();
							mPlayer.start();

							//Disable the button when the audio is playing
							v.setEnabled(false);
							((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_audio_in_progress));

							mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									//Re-enable the button when the audio has finished playing
									v.setEnabled(true);
									((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_audio));
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});

				holder.sender_audioPlayer.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Download Record").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadFile(position);
								return false;
							}
						});

						menu.add("Delete Record").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								 listMessage.remove(position);
								 WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}
			else
			{
				holder.receiver_audioPlayer.setVisibility(View.VISIBLE);
				holder.receiver_profile.setVisibility(View.VISIBLE);
				holder.receiver_audioPlayer.setTag(position);
				holder.receiver_audioPlayer.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						MediaPlayer mPlayer = new MediaPlayer();
						Message mes = listMessage.get((Integer) v.getTag());
						try {
							mPlayer.setDataSource(mes.getFilePath());
							mPlayer.prepare();
							mPlayer.start();

							//Disable the button when the audio is playing
							v.setEnabled(false);
							((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_audio_in_progress));

							mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									//Re-enable the button when the audio has finished playing
									v.setEnabled(true);
									((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_audio));
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});

				holder.receiver_audioPlayer.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Download Record").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadFile(position);
								return false;
							}
						});

						menu.add("Delete Record").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}

		}

		/***********************************************
		 Video Message
		 ***********************************************/
		 else if(type == Message.VIDEO_MESSAGE){
			if(listMessage.get(position).isMine())
			{
				holder.videoPlayer_sender.setVisibility(View.VISIBLE);
				holder.videoPlayerButton_sender.setVisibility(View.VISIBLE);

				if(!mapThumb.containsKey(mes.getFilePath())){
					Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mes.getFilePath(), MediaStore.Images.Thumbnails.MINI_KIND);
					mapThumb.put(mes.getFilePath(), thumb);
				}
				holder.videoPlayer_sender.setImageBitmap(mapThumb.get(mes.getFilePath()));

				holder.videoPlayerButton_sender.setTag(position);
				holder.videoPlayerButton_sender.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Message mes = listMessage.get((Integer) v.getTag());
						Intent intent = new Intent(mContext, PlayVideoActivity.class);
						intent.putExtra("filePath", mes.getFilePath());
						mContext.startActivity(intent);
					}
				});

				holder.videoPlayer_sender.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Download Video").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadFile(position);
								return false;
							}
						});

						menu.add("Delete Video").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}
			else
			{
				holder.videoPlayer_receiver.setVisibility(View.VISIBLE);
				holder.videoPlayerButton_receiver.setVisibility(View.VISIBLE);

				if(!mapThumb.containsKey(mes.getFilePath())){
					Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mes.getFilePath(), MediaStore.Images.Thumbnails.MINI_KIND);
					mapThumb.put(mes.getFilePath(), thumb);
				}
				holder.videoPlayer_receiver.setImageBitmap(mapThumb.get(mes.getFilePath()));
				holder.videoPlayerButton_receiver.setTag(position);
				holder.videoPlayerButton_receiver.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Message mes = listMessage.get((Integer) v.getTag());
						Intent intent = new Intent(mContext, PlayVideoActivity.class);
						intent.putExtra("filePath", mes.getFilePath());
						mContext.startActivity(intent);
					}
				});

				holder.videoPlayer_receiver.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Download Video").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadFile(position);
								return false;
							}
						});

						menu.add("Delete Video").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}

		}

		/***********************************************
		 File Message
		 ***********************************************/
		else if(type == Message.FILE_MESSAGE){
			if(listMessage.get(position).isMine())
			{
				holder.fileSavedIcon_sender.setVisibility(View.VISIBLE);
				holder.fileSaved_sender.setVisibility(View.VISIBLE);
				holder.fileSaved_sender.setText(mes.getFileName());

				holder.fileSaved_sender.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Download File").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadFile(position);
								return false;
							}
						});

						menu.add("Delete File").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}
			else
			{
				holder.fileSavedIcon_receiver.setVisibility(View.VISIBLE);
				holder.fileSaved_receiver.setVisibility(View.VISIBLE);
				holder.fileSaved_receiver.setText(mes.getFileName());

				holder.fileSaved_receiver.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Download File").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadFile(position);
								return false;
							}
						});

						menu.add("Delete File").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}

		}

		/***********************************************
		 Drawing Message
		 ***********************************************/
	 	else if(type == Message.DRAWING_MESSAGE){
			if(listMessage.get(position).isMine()) {
				holder.senderimage.setVisibility(View.VISIBLE);

				if(!mapThumb.containsKey(mes.getFileName())){
					Bitmap thumb = mes.byteArrayToBitmap(mes.getByteArray());
					mapThumb.put(mes.getFileName(), thumb);
				}
				holder.senderimage.setImageBitmap(mapThumb.get(mes.getFileName()));
				holder.senderimage.setTag(position);

				holder.senderimage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Message mes = listMessage.get((Integer) v.getTag());
						bitmap = mes.byteArrayToBitmap(mes.getByteArray());

						Intent intent = new Intent(mContext, ViewImageActivity.class);
						String fileName = mes.getFileName();
						intent.putExtra("fileName", fileName);

						mContext.startActivity(intent);
					}
				});

				holder.senderimage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Save Image to gallery").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadImage(position);
								return false;
							}
						});

						menu.add("Delete Image").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}
			else {
				holder.receiverimage.setVisibility(View.VISIBLE);
				holder.receiver_profile.setVisibility(View.VISIBLE);

				if(!mapThumb.containsKey(mes.getFileName())){
					Bitmap thumb = mes.byteArrayToBitmap(mes.getByteArray());
					mapThumb.put(mes.getFileName(), thumb);
				}
				holder.receiverimage.setImageBitmap(mapThumb.get(mes.getFileName()));
				holder.receiverimage.setTag(position);

				holder.receiverimage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Message mes = listMessage.get((Integer) v.getTag());
						bitmap = mes.byteArrayToBitmap(mes.getByteArray());

						Intent intent = new Intent(mContext, ViewImageActivity.class);
						String fileName = mes.getFileName();
						intent.putExtra("fileName", fileName);

						mContext.startActivity(intent);
					}
				});

				holder.receiverimage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Save Image to gallery").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								downloadImage(position);
								return false;
							}
						});

						menu.add("Delete Image").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});

			}
		}
	}

	@Override
	public int getItemCount() {
		return listMessage.size();
	}

	public void add(Message msg)
	{
		listMessage.add(msg);
	}

	public class MessageviewHolder extends  RecyclerView.ViewHolder
	{
		public TextView sendertext;
		public TextView receivertext;
		public ImageView senderimage;
		public ImageView receiverimage;
		public ImageView sender_audioPlayer;
		public ImageView receiver_audioPlayer;
		public TextView receiver_profile;
		public ImageView videoPlayer_sender;
		public ImageView videoPlayerButton_sender;
		public ImageView videoPlayer_receiver;
		public ImageView videoPlayerButton_receiver;
		public ImageView fileSavedIcon_sender;
		public TextView fileSaved_sender;
		public ImageView fileSavedIcon_receiver;
		public TextView fileSaved_receiver;


		public MessageviewHolder(@NonNull View itemView) {
			super(itemView);

			receivertext = (TextView) itemView.findViewById(R.id.receivermessagetxt);
			receiverimage = (ImageView) itemView.findViewById(R.id.receivermessageimage);
			receiver_audioPlayer = (ImageView) itemView.findViewById(R.id.playAudio_receiver);
			sendertext = (TextView) itemView.findViewById(R.id.sendermessagetxt);
			senderimage = (ImageView) itemView.findViewById(R.id.sendermessageimage);
			sender_audioPlayer = (ImageView) itemView.findViewById(R.id.playAudio_sender);
			receiver_profile = (TextView) itemView.findViewById(R.id.profileimage_receiver);
			videoPlayerButton_sender = (ImageView) itemView.findViewById(R.id.buttonPlayVideo_sender);
			videoPlayer_sender = (ImageView) itemView.findViewById(R.id.playVideo_sender);
			videoPlayerButton_receiver = (ImageView) itemView.findViewById(R.id.buttonPlayVideo_receiver);
			videoPlayer_receiver = (ImageView) itemView.findViewById(R.id.playVideo_receiver);
			fileSaved_receiver= (TextView) itemView.findViewById(R.id.fileSaved_receiver);
			fileSavedIcon_receiver = (ImageView) itemView.findViewById(R.id.file_attached_icon_receiver);
			fileSaved_sender= (TextView) itemView.findViewById(R.id.fileSaved_sender);
			fileSavedIcon_sender = (ImageView) itemView.findViewById(R.id.file_attached_icon_sender);

		}

	}

	private void disableAllMediaViews(MessageviewHolder holder){
		holder.senderimage.setVisibility(View.GONE);
		holder.receiverimage.setVisibility(View.GONE);
		holder.sender_audioPlayer.setVisibility(View.GONE);
		holder.receiver_audioPlayer.setVisibility(View.GONE);
		holder.sendertext.setVisibility(View.GONE);
		holder.receivertext.setVisibility(View.GONE);
		holder.receiver_profile.setVisibility(View.GONE);
 		holder.videoPlayerButton_sender.setVisibility(View.GONE);
		holder.videoPlayerButton_receiver.setVisibility(View.GONE);
		holder.videoPlayer_sender.setVisibility(View.GONE);
		holder.videoPlayer_receiver.setVisibility(View.GONE);
		holder.fileSaved_sender.setVisibility(View.GONE);
		holder.fileSaved_receiver.setVisibility(View.GONE);
		holder.fileSavedIcon_sender.setVisibility(View.GONE);
		holder.fileSavedIcon_receiver.setVisibility(View.GONE);


	}
	private void enableTextView(MessageviewHolder holder,String text,boolean ismine,final int position){
		if (! text.equals("")) {
			if (ismine) {
				holder.sendertext.setVisibility(View.VISIBLE);
				holder.sendertext.setText(text);
				Linkify.addLinks(holder.sendertext,Linkify.PHONE_NUMBERS);
				Linkify.addLinks(holder.sendertext,Patterns.WEB_URL,"myweburl:");

				holder.sendertext.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){

						menu.add("Copy text").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								copyTextToClipboard(position);
								return false;
							}
						});

						menu.add("Share text").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								shareMedia(position);
								return false;
							}
						});

						menu.add("Delete Message").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});

			} else {
				holder.receivertext.setVisibility(View.VISIBLE);
				holder.receiver_profile.setVisibility(View.VISIBLE);
				holder.receivertext.setText(text);
				Linkify.addLinks(holder.receivertext,Linkify.PHONE_NUMBERS);
				Linkify.addLinks(holder.receivertext,Patterns.WEB_URL,"myweburl:");

				holder.receivertext.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
					@Override
					public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
						menu.add("Copy text").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								copyTextToClipboard(position);
								return false;
							}
						});

						menu.add("Share text").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								shareMedia(position);
								return false;
							}
						});

						menu.add("Delete Message").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
							@Override
							public boolean onMenuItemClick(MenuItem item){
								listMessage.remove(position);
								WifiChatActivity.messageAdapter.notifyDataSetChanged();
								return false;
							}
						});
					}
				});
			}
		}
	}
	private void copyTextToClipboard(int position){
		Message mes = listMessage.get(position);
		ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("message", mes.getmText());
		clipboard.setPrimaryClip(clip);
		Toast.makeText(mContext, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
	}
	private void shareMedia(int position){
		Message mes = listMessage.get(position);

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, mes.getmText());
		sendIntent.setType("text/plain");
		mContext.startActivity(sendIntent);

	}

	public void downloadImage(int position){
		Message mes = listMessage.get(position);
		Bitmap bm = mes.byteArrayToBitmap(mes.getByteArray());

		FileUtilities.savetogallery((Activity)mContext,bm,mes.getFileName());
		FileUtilities.refreshMediaLibrary((Activity)mContext);
	}

	public void downloadFile(int position){
		Message mes = listMessage.get(position);
		String sourcePath = mes.getFilePath();
		String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		FileUtilities.copyFile((Activity)mContext, sourcePath, destinationPath, mes.getFileName());
		FileUtilities.refreshMediaLibrary((Activity)mContext);
	}


}
