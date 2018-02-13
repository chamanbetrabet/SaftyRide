package com.example.spiro.saftyride;





import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity
{
    EditText editTextUserName,editTextPassword,editTextConfirmPassword;
    Button btnCreateAccount;

    LoginDataBaseAdapter loginDataBaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);

        // get Instance  of Database Adapter
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        // Get Refferences of Views
        editTextUserName=(EditText)findViewById(R.id.editTextUserName);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);

        editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);

        btnCreateAccount=(Button)findViewById(R.id.buttonCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();
                String confirmPassword=editTextConfirmPassword.getText().toString();
                if(userName.equals("")||password.equals("")||confirmPassword.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
                    return;
                }

                else if(!isValidate(password))
                {
                    Toast.makeText(getApplicationContext(), "Minimum Four Digit", Toast.LENGTH_LONG).show();
                    return;
                }

                // check if any of the fields are vaccant

                // check if both password matches
                else if(!password.equals(confirmPassword))
                {
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();

                    return;


                }

                else
                {
                    // Save the Data in Database
                    loginDataBaseAdapter.insertEntry(userName, password);
                    Toast.makeText(getApplicationContext(), "Please Login ", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(),Login_Activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                }
            }
        });
    }
    protected boolean isValidate(String password) {
        // TODO Auto-generated method stub
        if(password.length()>=4)
        {
            return true;
        }
        return false;

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        loginDataBaseAdapter.close();
    }
}

