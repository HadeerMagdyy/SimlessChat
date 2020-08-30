package com.example.simlesschat.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simlesschat.Adapters.personAdapter;
import com.example.simlesschat.BluetoothchatActivity;
import com.example.simlesschat.Classes.person;
import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;

import java.util.ArrayList;


public class bluetoothchats_fragment extends Fragment{

    ArrayList<person> chatslist;
    ArrayList<String> allchats;
    personAdapter chatadapter;
    Database database;
    SharedPreferences sharedPreferences;
    String username;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        final View view=inflater.inflate(R.layout.bluetooth_chats_recyclerview,container,false);


        recyclerView = (RecyclerView)view.findViewById(R.id.bluetooth_allchats_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        chatslist = new ArrayList<person>();
        allchats=new ArrayList<String>();
        database=new Database(this.getContext());

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        username=sharedPreferences.getString(getString(R.string.username),"No name");
        String connecteduser=sharedPreferences.getString(getString(R.string.connecteduser),"no name");

        if(!connecteduser.equals("no name"))
        {
            person p =new person();
            p.setName(connecteduser);
            p.setLastmessage("");
            chatslist.add(p);
        }

/*
        allchats=database.get_all_bluetoothchats(username);

        for(int i=0 ; i<allchats.size();i++)
        {
            String []details=allchats.get(i).split(".Simlesschat");
            person p = new person();
            p.setName(details[0]);
            p.setLastmessage(details[1]);
            chatslist.add(p) ;
        }
*/
        chatadapter = new personAdapter(chatslist);
        recyclerView.setAdapter(chatadapter);

        
        recyclerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getActivity(),BluetoothchatActivity.class);
                getActivity().startActivity(i);
            }
        });

        return view;
    }
}
