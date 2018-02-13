package com.example.spiro.saftyride;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,SensorEventListener {

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    Button btnFusedLocation;
    TextView lat9,lng9;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    //for sensor

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayList<LatLng> location_latlon;
    private  ArrayList<String> location_name;
    LatLng tempname;
    int temppos=0;


    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;
    double lat1;
    double lon;
    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;

    String number=null,message=null;

    String num2;
    private SQLiteDatabase db;
    Cursor c;



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_main);
        lat9 = (TextView) findViewById(R.id.lat1);
        lng9 = (TextView) findViewById(R.id.log1);


                updateUI();

        location_latlon=new ArrayList<>();
        location_name=new ArrayList<>();

      //  addambulance();


        initializeViews();


        //	System.out.println("number---->"+number+", message--->"+message);

        db=openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);
        c=db.rawQuery("SELECT * FROM contactdetails" , null);
        //Cursor c=db.rawQuery("SELECT * FROM contactdetails where number='"+et_number.getText().toString()+"'" , null);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            number = c.getString(0);
            num2 = c.getString(1);

            message = c.getString(2);
            //et_message.setText(c.getString(1));
            //Toast.makeText(getApplicationContext()," SUCCESSFULLY FETCHED", 2000).show();
			/*System.out.println("number--->"+number);
			System.out.println("message--->"+message);*/
            c.close();
        }
        else
        {
            Toast.makeText(getApplicationContext()," NO VALUE", Toast.LENGTH_LONG).show();
        }


		/*

		Cursor c=db.rawQuery("SELECT * FROM contactdetails" , null);
		if(c.getCount()>0)
		{
			c.moveToFirst();
			number = c.getString(0);
			//et_message.setText(c.getString(1));
			//Toast.makeText(getApplicationContext()," SUCCESSFULLY FETCHED", 2000).show();
			System.out.println("number--->"+number);
			c.close();
		}
		else
		{
			number = "9876543210";
		}
		*/

        overridePendingTransition(R.anim.pull_in_rigth,
                R.anim.pull_out_left);

        //Toast.makeText(getApplicationContext(), "Activated fall detection", Toast.LENGTH_SHORT);

        Button btn_stop = (Button)findViewById(R.id.btn_start2);

//        Button btn_Back=(Button)findViewById(R.id.btn_start);
//        btn_Back.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v) {
//               /* finish();
//                System.exit(0);*/
//                String no= number;
//                String no1= num2;
//               Intent i =new Intent(MainActivity.this,SensorBackground.class);
//                i.putExtra("no",no);
//                i.putExtra("no1",no1);
//                startActivity(i);
//            }
//        });

        btn_stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                  finish();
                System.exit(0);
            }
        });



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        // initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat1 = String.valueOf(mCurrentLocation.getLatitude());
            String lng1 = String.valueOf(mCurrentLocation.getLongitude());
            
            lat9.setText(lat1);
            lng9.setText(lng1);

        } else {
            Log.d(TAG, "location is null ...............");
        }
    }


    //https://stackoverflow.com/questions/29891287/getting-gps-co-ordinates-quicker-programmatically-without-internet-but-using-ne

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        sensorManager.unregisterListener(this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");

        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax)
        {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax)
        {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax)
        {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }


    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
  /*  public int calculateDistance(double userLat, double userLng,
                                 double venueLat, double venueLng)
    {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));
    }*/

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold)
                || (deltaZ > vibrateThreshold)) {

//			System.out.println("number---->"+number);
//			Toast.makeText(getApplicationContext(),"number---->"+number,Toast.LENGTH_SHORT).show();

            //v.vibrate(1000);

            if(number==null)
            {
                Toast.makeText(getApplicationContext(),"number is not available",Toast.LENGTH_SHORT).show();
            }
            else
            {
				/*System.out.println("number---1->"+number);
				Toast.makeText(getApplicationContext(),"number---->"+number,Toast.LENGTH_SHORT).show();
				*/

				/* LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			        Criteria criteria = new Criteria();
			        String bestProvider = locationManager.getBestProvider(criteria, true);
			        Location location = locationManager.getLastKnownLocation(bestProvider);
			         latitude = location.getLatitude();
			        longitude = location.getLongitude();

			        lat=String.valueOf(latitude);
			        lng=String.valueOf(longitude);

			       // TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
			       // locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
			        Toast.makeText(this, "Latitude:" + latitude + ", Longitude:" + longitude, 3000).show();
			        System.out.println("Latitude:" + latitude + ", Longitude:" + longitude);
			        Toast.makeText(this, "Latitude:" + lat + ", Longitude:" + lng, 3000).show();
			        System.out.println("Latitude:" + lat + ", Longitude:" + lng);*/

                // finding gprs location-------------------------------------------------------------



                // check if GPS enabled



                  String s =lat9.getText().toString();
                  String s1 =lng9.getText().toString();

                    String no= number;
                    String no1= num2;

                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                    message = message +"\n" + s + "\n" + s1 + "\n";


                    Log.i("Locationasdfbdnsbf", message);

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(no, null, message, null, null);

                    SmsManager smsManager1= SmsManager.getDefault();
                    smsManager1.sendTextMessage(no1, null, message, null, null);

                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }



//
//								checkhospital();
//
//								SmsManager smsManager2= SmsManager.getDefault();
//								smsManager2.sendTextMessage(location_name.get(0), null, "Patient Location:"+message, null, null);
//								Toast.makeText(getApplicationContext(), "First Your Location is - \nLat: " + lat1 + "\nLong: " + lon+"\n"+"To Number is"+"9443642999", Toast.LENGTH_LONG).show();



							/*SmsManager smsManager3= SmsManager.getDefault();
							smsManager3.sendTextMessage("9443642999", null, "Patient Location:"+message, null, null);


							SmsManager smsManager4= SmsManager.getDefault();
							smsManager4.sendTextMessage("9042690111", null, "Patient Location:"+message, null, null);


							SmsManager smsManager5= SmsManager.getDefault();
							smsManager5.sendTextMessage("9629843734", null, "Patient Location:"+message, null, null);



							SmsManager smsManager6= SmsManager.getDefault();
							smsManager6.sendTextMessage("9952944198", null, "Patient Location:"+message, null, null);



							Toast.makeText(getApplicationContext(),  "Patient Location:"+message, Toast.LENGTH_LONG).show();
							*/

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "First Your Location is - \nLat: " + lat1 + "\nLong: " + lon, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), no+"\n"+no1, Toast.LENGTH_LONG).show();
                    Log.e("Locationssss", no+"\n"+no1);




                //----------------------------------------------------------
//				String no=number.toString();
//
//				message = message +"\n" + lat1 + "\n" + lon + "\n";
//
//				SmsManager smsManager = SmsManager.getDefault();
//				smsManager.sendTextMessage(no, null, message, null, null);
//				System.out.println("message--2-->"+message);
//				Toast.makeText(getApplicationContext()," Message ---->"+message,Toast.LENGTH_SHORT).show();

                finish();
                System.exit(0);

            }
            // Toast.makeText(getApplicationContext(),
            // "alarm up",Toast.LENGTH_SHORT).show();
        }
    }




/*

    private void addambulance()
    {
        // TODO Auto-generated method stub
        location_latlon.clear();
        location_name.clear();

        location_latlon.add(new LatLng(12.833573, 79.704526));
        location_name.add("9442312666");

        location_latlon.add(new LatLng(12.834572, 79.703682));
        location_name.add("9443642999");

        location_latlon.add(new LatLng(12.833872, 79.703670));
        location_name.add("9042690111");

        location_latlon.add(new LatLng(12.837148, 79.704480));
        location_name.add("9629843734");

        location_latlon.add(new LatLng(12.837554, 79.703585));
        location_name.add("9952944198");
    }
*/

}

