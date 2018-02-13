package com.example.spiro.saftyride;





import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login_Activity extends Activity {


    LoginDataBaseAdapter loginDataBaseAdapter;
    private Button login;
    private EditText mail;
    private EditText pass;
    private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        // create a instance of SQLite Database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        login=(Button)findViewById(R.id.btn_login);
        signup=(Button)findViewById(R.id.btn_signUp);
        mail=(EditText)findViewById(R.id.ed_email);
        pass=(EditText)findViewById(R.id.ed_password);

        signup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent i=new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get The User name and Password
                String userName=mail.getText().toString();
                String password=pass.getText().toString();

                if(userName.equals("")||password.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
                    return;
                }
                // fetch the Password form database for respective user name
                String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);

                // check if the Stored password matches with  Password entered by user
                if(password.equals(storedPassword))
                {
                    Toast.makeText(Login_Activity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(),Start_activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);

                }
                else
                {
                    Toast.makeText(Login_Activity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                    mail.setText("");
                    pass.setText("");
                }
            }
        });


    }
}