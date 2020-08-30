package com.example.simlesschat.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static String databasename="Kaleemny";
    SQLiteDatabase Kaleemnydatabase;

    public Database(Context context) {
        super(context,databasename,null,14);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user (username text primary key  not null , password text not null)");
        //db.execSQL("create table bluetoothdevices (receiver_macaddress text not null, receiver_username text not null , Primary key (receiver_macaddress,receiver_username))");
        //db.execSQL("create table message (msg_ID integer primary key autoincrement ,receiver_macaddress text not null, msg_Date text not null, msg_Content text not null,msg_Type text not null,username text not null ,receiver_username text not null, foreign key(receiver_macaddress) references bluetoothdevices(receiver_macaddress) ,foreign key(receiver_username) references bluetoothdevices(receiver_username),foreign key(username) references user(username) )");
        db.execSQL("create table wifidevices (chatname text primary key not null, time text not null,username text not null , Foreign key(username) references user(username))");
        //db.execSQL("create table user_bluetoothdevices(username text unique not null ,receiver_macaddress text unique not null, receiver_username text unique not null , last_message text not null, Primary key (username,receiver_macaddress,recevier_username) ,foreign key(username) references user(username),foreign key(receiver_macaddress) references bluetoothdevices(receiver_macaddress),foreign key(receiver_username) references bluetoothdevices(receiver_username))");
        //db.execSQL("create table user_wifidevices(username text not null ,receiver_macaddress text not null, receiver_username text not null ,foreign key(username) references user(username),foreign key(receiver_macaddress) references wifidevices(receiver_username),foreign key(receiver_username) references wifidevices(receiver_username), Primary key (username,receiver_macaddress,recevier_username))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user ");
        //db.execSQL("drop table if exists bluetoothdevices ");
        db.execSQL("drop table if exists wifidevices ");
        //db.execSQL("drop table if exists message ");
        onCreate(db);
    }

   //----------------------registeration and login database-----------------------------------------------
    public void insert_user(String username, String password)
    {
        ContentValues row=new ContentValues();
        row.put("username",username);
        row.put("password",password);
        Kaleemnydatabase=getWritableDatabase();
        Kaleemnydatabase.insert("user",null,row);
        Kaleemnydatabase.close();
    }

    public boolean check_username_exists(String username)
    {
        Kaleemnydatabase=getReadableDatabase();
        Cursor cur=Kaleemnydatabase.rawQuery("select username from user where username = ?",new String[]{username});
        cur.moveToFirst();
        if(cur.getCount()!=0)
            return true;
        return false;
    }

    public boolean check_login(String username,String password)
    {
        Kaleemnydatabase=getReadableDatabase();
        Cursor cur=Kaleemnydatabase.rawQuery("select * from user where username = ? and password = ?",new String[]{username,password});
        cur.moveToFirst();
        if(cur.getCount()==0)
            return false;
        return true;
    }

    public void edit_profile(String oldpassword,String newusername , String newpassword)
    {
        Kaleemnydatabase=getWritableDatabase();
        ContentValues row=new ContentValues();
        row.put("username",newusername);
        row.put("password",newpassword);
        Kaleemnydatabase.update("user",row,"password = ?",new String[]{oldpassword});
        Kaleemnydatabase.close();

    }



    //------------------- Wifi Chats--------------------------------------

    public void insert_new_wifidevice(String rec_username,String time,String username)
    {
        ContentValues row=new ContentValues();
        row.put("chatname",rec_username);
        row.put("time",time);
        row.put("username",username);
        Kaleemnydatabase=getWritableDatabase();
        if(!check_wifidevice_existance(rec_username))
            Kaleemnydatabase.insert("wifidevices",null,row);
        Kaleemnydatabase.close();
    }

    public boolean check_wifidevice_existance(String rec_username)
    {
        Kaleemnydatabase=getReadableDatabase();
        Cursor cur=Kaleemnydatabase.rawQuery("select * from wifidevices where chatname = ?",new String[]{rec_username});
        cur.moveToFirst();
        if(cur.getCount()!=0)
        {
            return true;
        }
        return false;
    }

    public ArrayList<Pair<String,String>> get_all_wifichats(String username)
    {
        Kaleemnydatabase=getReadableDatabase();
        ArrayList<Pair<String,String>> arrayList=new ArrayList<>();
        Cursor cur=Kaleemnydatabase.rawQuery("select * from wifidevices where username = ? ",new String[]{username});
        cur.moveToFirst();
        while(!cur.isAfterLast())
        {
            Pair<String,String> chat=new Pair<String,String>(cur.getString(0),cur.getString(1));
            arrayList.add(chat);
            cur.moveToNext();
        }
        return arrayList;
    }


    //---------------------Messages-----------------------------------------
    public void insert_new_message(String receiver_macaddress,String msg_Date ,String msg_Content,String msg_Type ,String username ,String receiver_username)
    {
        ContentValues row=new ContentValues();
        row.put("receiver_macaddress",receiver_macaddress);
        row.put("msg_Date",msg_Date);
        row.put("msg_Content",msg_Content);
        row.put("msg_Type",msg_Type);
        row.put("username",username);
        row.put("receiver_username",receiver_username);
        Kaleemnydatabase=getWritableDatabase();
        Kaleemnydatabase.insert("message",null,row);
        Kaleemnydatabase.close();
    }





}
