package com.example.simlesschat.Activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;

public class EditprofileActivity extends AppCompatActivity {

    EditText newname,newpassword,oldpassword;
    Button submitbtn;
    TextView profileimage;
    Database database;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        newname=(EditText)findViewById(R.id.editusername_editactivity_id);
        newpassword=(EditText)findViewById(R.id.newpassword_registerationactivity_id);
        oldpassword=(EditText)findViewById(R.id.oldpassword_registerationactivity_id);
        submitbtn=(Button)findViewById(R.id.editbtn_editactivity_id);
        profileimage=(TextView)findViewById(R.id.profileimage_editactivity_id);
        database=new Database(this);
        final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        newname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0) {
                    char character=s.charAt(0);
                    String name=String.valueOf(character);
                    profileimage.setText(name.toUpperCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    profileimage.setText("");
            }
        });


        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newname.getText().toString().equals("") || newpassword.getText().toString().equals("") || oldpassword.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please Fill all information",Toast.LENGTH_LONG).show();
                    return;
                }
                String username=sharedPreferences.getString(getString(R.string.username),"No name");
                boolean check_validity=database.check_login(username,oldpassword.getText().toString());
                if(check_validity) {
                    database.edit_profile(oldpassword.getText().toString(),newname.getText().toString(),newpassword.getText().toString());
                    oldpassword.setText("");
                    newname.setText("");
                    newpassword.setText("");
                    Intent i = new Intent(EditprofileActivity.this,AllchatsActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Wrong Password try Again",Toast.LENGTH_LONG).show();
                    oldpassword.setText("");
                }
            }
        });



    }
}
