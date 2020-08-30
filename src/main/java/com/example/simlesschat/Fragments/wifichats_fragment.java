package com.example.simlesschat.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simlesschat.Adapters.personAdapter;
import com.example.simlesschat.Classes.person;
import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;

import java.util.ArrayList;

public class wifichats_fragment extends Fragment{

    ArrayList<person> chatlist;
    personAdapter chatadapter;
    SharedPreferences sharedPreferences;
    Database database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.wifi_chats_recyclerview,container,false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.wifi_allchats_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String name=sharedPreferences.getString(getString(R.string.username),"No name");
        chatlist = new ArrayList<person>();
        database=new Database(getActivity());
        ArrayList<Pair<String,String>>Allchats=database.get_all_wifichats(name);


        if(Allchats!=null) {
            for (int i = 0; i < Allchats.size(); i++) {
                person chat = new person();
                chat.setName(Allchats.get(i).first);
                chat.setLastmessage(Allchats.get(i).second);
                chatlist.add(chat);
            }
        }

        chatadapter = new personAdapter(chatlist);
        recyclerView.setAdapter(chatadapter);


        return view;
    }
}
