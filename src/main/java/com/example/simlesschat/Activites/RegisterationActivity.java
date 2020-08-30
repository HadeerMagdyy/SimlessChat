package com.example.simlesschat.Activites;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simlesschat.Activites.LoginActivity;
import com.example.simlesschat.Database.Database;
import com.example.simlesschat.R;
import com.sarnava.textwriter.TextWriter;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterationActivity extends AppCompatActivity {

    TextView profileimage;
    EditText username;
    EditText password;
    Button signupbtn;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        getSupportActionBar().hide();

        /*circleImageView =(CircleImageView) findViewById(R.id.profileimage_loginactivity_id);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Profile Pic", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"),0);
            }
        }); */
        TextWriter textWriter;
        textWriter = findViewById(R.id.textWriter);
        textWriter
                .setWidth(12)
                .setDelay(30)
                .setColor(Color.YELLOW)
                .setConfig(TextWriter.Configuration.INTERMEDIATE)
                .setSizeFactor(30f)
                .setLetterSpacing(35f)
                .setText("JOIN US")
                .setListener(new TextWriter.Listener() {
                    @Override
                    public void WritingFinished() {

                    }
                })
                .startAnimation();
        profileimage=(TextView)findViewById(R.id.profileimage_registerationactivity_id);
        username=(EditText)findViewById(R.id.username_registerationactivity_id);
        password=(EditText) findViewById(R.id.password_registerationactivity_id);
        signupbtn=(Button)findViewById(R.id.signupbtn_registerationactivity_id);
        database= new Database(this);

        username.addTextChangedListener(new TextWatcher() {
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

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("") || password.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Please Fill all informations",Toast.LENGTH_LONG).show();
                else
                {
                    String name=username.getText().toString();
                    String pass=password.getText().toString();

                    username.setText("");
                    password.setText("");

                    boolean existance=database.check_username_exists(name);

                    if(existance)
                        Toast.makeText(getApplicationContext(),"Username is already taken , try another one ",Toast.LENGTH_LONG).show();
                    else
                    {
                        database.insert_user(name,pass);
                        Toast.makeText(getApplicationContext(),"Registeration is Completed",Toast.LENGTH_LONG).show();



                        Intent i = new Intent(RegisterationActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
            }
        });


    }
}
