package com.example.simlesschat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simlesschat.Activites.wifi_connection_activity;
import com.example.simlesschat.Classes.person;
import com.example.simlesschat.R;
import com.example.simlesschat.Activites.WifiChatActivity;

public class personAdapter  extends  RecyclerView.Adapter<personAdapter.MyViewHolder>{

    private ArrayList<person> requestList;

    public personAdapter(ArrayList<person> citylist ) {
        requestList  = citylist ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw, parent, false);
        final MyViewHolder viewHolder=new MyViewHolder(view);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final person request = requestList.get(position);

        holder.chatname.setText(request.getName());
        holder.lastmessage.setText(request.getLastmessage());
        holder.imageView.setText(String.valueOf(request.getName().charAt(0)).toUpperCase());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                person ob = requestList.get(position);
                Context current=view.getContext();
               // wifi_connection_activity.chatName=ob.getName();
             //   view.getContext().startActivity(new Intent(current,WifiChatActivity.class));

            }
        });

    }

    @Override
    public int getItemCount() {
        if (requestList == null) return 0;
        return requestList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lastmessage;
        TextView chatname;
        TextView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            chatname = (TextView) itemView.findViewById(R.id.chatname_allchatsactivity_txt);
            lastmessage = (TextView) itemView.findViewById(R.id.chattime_allchatsactivity_txt);
            imageView = (TextView) itemView.findViewById(R.id.chatimage_allchatsactivity_id);
        }
    }



}
