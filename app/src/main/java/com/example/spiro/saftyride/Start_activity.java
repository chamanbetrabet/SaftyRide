package com.example.spiro.saftyride;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Start_activity extends Activity implements OnClickListener {

    EditText et_number,et_message,number2;
    Button btn_add,btn_update, btn_start, btn_select;
    SQLiteDatabase db;

    double latitude;
    double longitude;
    TextView ll;
    String lat=null,lng=null;
    int n;
    StringBuffer buffer;
    ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//		getActionBar().hide();

        setContentView(R.layout.activity_start_activity);

        db=openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE IF EXISTS studetails");
        db.execSQL("CREATE TABLE IF NOT EXISTS contactdetails(number VARCHAR,number1 VARCHAR,message VARCHAR)");

        et_number =(EditText)findViewById(R.id.number);
        number2=(EditText)findViewById(R.id.number2);

        et_message =(EditText)findViewById(R.id.message);
        ll=(TextView)findViewById(R.id.listView1);
        im=(ImageView)findViewById(R.id.imageView1);
        btn_start =(Button)findViewById(R.id.btn_start);
        btn_add =(Button)findViewById(R.id.btn_add);
        btn_update =(Button)findViewById(R.id.btn_update);
        btn_select =(Button)findViewById(R.id.btn_select);

        btn_start.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_select.setOnClickListener(this);
        im.setOnClickListener(this);


        Cursor c=db.rawQuery("SELECT * FROM contactdetails" , null);
        n= c.getCount();
        Log.i("count--->", ""+n);
        if(c.getCount()>0)
        {

            c.moveToFirst();


            buffer=new StringBuffer();
            buffer.append(" Registered Number 1 : "+c.getString(0));
            buffer.append("\n");
            buffer.append("\n");

            buffer.append(" Registered Number2 : "+c.getString(1));
            buffer.append("\n");
            buffer.append(" Message : "+c.getString(2));
            buffer.append("\n");

            c.moveToNext();

//					 ArrayAdapter<String> aa=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,users);
            ll.setText(buffer);

            Toast.makeText(getApplicationContext(), buffer, Toast.LENGTH_LONG).show();
        }
        else
        {
            ll.setText("No number registered");
        }




//

		/*getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_start_activity);

		 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

	        Criteria criteria = new Criteria();
	        String bestProvider = locationManager.getBestProvider(criteria, true);
	        Location location = locationManager.getLastKnownLocation(bestProvider);
	        double latitude = location.getLatitude();
	        double longitude = location.getLongitude();
	        //TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
	        //locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
	        Toast.makeText(this, "Latitude:" + latitude + ", Longitude:" + longitude, 3000).show();
	        System.out.println("Latitude:" + latitude + ", Longitude:" + longitude);
	*/
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if(v==im)
        {
            Intent i=new Intent(getApplicationContext(),Login_Activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        }
        if(v==btn_add)
        {
            if((et_number.getText().toString().matches("")) || (et_message.getText().toString().matches(""))|| (number2.getText().toString().matches("")))
            {
                Toast.makeText(getApplicationContext(), "FILL NUMBER and MESSAGE", Toast.LENGTH_LONG).show();

            }
            else if(et_number.getText().toString().length()==10||number2.getText().toString().length()==10)
            {
                Cursor c=db.rawQuery("SELECT * FROM contactdetails where number='"+et_number.getText().toString()+"'", null);
                if(c.moveToFirst())
                {
                    Toast.makeText(getApplicationContext(), "Number already exists", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "You can update the message", Toast.LENGTH_LONG).show();
                }
                else
                {

                    db.execSQL("DROP TABLE IF EXISTS " +"contactdetails");
                    db.execSQL("CREATE TABLE IF NOT EXISTS contactdetails(number VARCHAR,number1 VARCHAR,message VARCHAR)");
                    db.execSQL("INSERT INTO contactdetails VALUES('"+ et_number.getText().toString()+"','"+ number2.getText().toString()+"','"+ et_message.getText().toString()+"')");
                    Toast.makeText(getApplicationContext(), "INSERTED SUCCESSFULLY", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(),Start_activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
//					et_number.setText("");
//					et_message.setText("");
                }





            }
            else
            {
                Toast.makeText(getApplicationContext(), "Enter 10 digit mobile number", Toast.LENGTH_LONG).show();

            }
        }
        if(v==btn_update)
        {
            if((et_number.getText().toString().matches("")) )
            {
                Toast.makeText(getApplicationContext(), "FILL NUMBER", Toast.LENGTH_LONG).show();

            }
            else
            {
                db.execSQL("UPDATE contactdetails set number='"+ et_number.getText().toString()+"',number1='"+ number2.getText().toString()+"',message='"+et_message.getText().toString()+"'");

                Intent i=new Intent(getApplicationContext(),Start_activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "UPDATED SUCCESSFULLY", Toast.LENGTH_LONG).show();
            }
        }
        if(v==btn_select)
        {
            //
            Cursor c=db.rawQuery("SELECT * FROM contactdetails" , null);
            //Cursor c=db.rawQuery("SELECT * FROM contactdetails where number='"+et_number.getText().toString()+"'" , null);
            if(c.getCount()>0)
            {
                c.moveToFirst();
                et_number.setText(c.getString(0));
                number2.setText(c.getString(1));

                et_message.setText(c.getString(2));
                Toast.makeText(getApplicationContext()," SUCCESSFULLY FETCHED", Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(getApplicationContext()," NO VALUE", Toast.LENGTH_LONG).show();
            //}
        }
        if(v==btn_start)
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

    }


}
