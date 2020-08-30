package com.example.simlesschat.Activites;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simlesschat.Activites.AllchatsActivity;
import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;
import com.sarnava.textwriter.TextWriter;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    Button loginbtn;
    EditText username;
    EditText password;
    TextView register;
    Database database;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name,pass;
    int timercounter=15;
    Dialog timerdialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        username=(EditText)findViewById(R.id.username_loginactivity_id);
        password=(EditText)findViewById(R.id.password_loginactivity_id);
        register=(TextView)findViewById(R.id.registertxt_loginactivity_id);
        database= new Database(this);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        TextWriter textWriter;
        textWriter = findViewById(R.id.textWriter);
        textWriter
                .setWidth(12)
                .setDelay(30)
                .setColor(Color.YELLOW)
                .setConfig(TextWriter.Configuration.INTERMEDIATE)
                .setSizeFactor(30f)
                .setLetterSpacing(35f)
                .setText("KALLMNY")
                .setListener(new TextWriter.Listener() {
                    @Override
                    public void WritingFinished() {

                    }
                })
                .startAnimation();
        loginbtn=(Button)findViewById(R.id.loginbtn_loginactivity_id);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().equals("") || password.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Please Fill all informations",Toast.LENGTH_LONG).show();
                else {

                    name=username.getText().toString();
                    pass=password.getText().toString();

                    username.setText("");
                    password.setText("");

                    boolean success = database.check_login(name, pass);
                    if(!success) {
                        loginbtn.setBackgroundColor((getResources().getColor(R.color.red)));
                        Toast.makeText(getApplicationContext(), "Invalid username or password ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        loginbtn.setBackgroundColor((getResources().getColor(R.color.green)));
                        Intent i = new Intent(LoginActivity.this,AllchatsActivity.class);
                        startActivity(i);
                        editor.putString(getString(R.string.username),name);
                        editor.commit();
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterationActivity.class);
                startActivity(i);
            }
        });
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage(" Simless Chat will set device name as username to other devices , this will take few seconds");
        alert.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.enable();
                bluetoothAdapter.setName(name+".Simlesschat");

                showtimer();

            }
        })
                .setNegativeButton("Deny",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        Toast.makeText(getApplicationContext(),"Sorry , You should accept to login",Toast.LENGTH_LONG).show();
                    }
                });
        return alert.show();
    }

    private void showtimer()
    {
        timerdialog=new Dialog(this);
        timerdialog.setContentView(R.layout.timer_layout);
        final TextView timer;
        timer=(TextView)timerdialog.findViewById(R.id.timer);
        final ProgressBar progressBar=timerdialog.findViewById(R.id.progressBar);

        new CountDownTimer(15000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished){
                timer.setText(String.valueOf(timercounter));
                timercounter--;

            }

            @Override
            public void onFinish( ){
                Intent i = new Intent(LoginActivity.this, AllchatsActivity.class);
                editor.putString(getString(R.string.username),name);
                editor.commit();
                timercounter=15;
                timerdialog.cancel();
                startActivity(i);

            }
        }.start();

        timerdialog.setCancelable(false);
        timerdialog.show();

    }

}

