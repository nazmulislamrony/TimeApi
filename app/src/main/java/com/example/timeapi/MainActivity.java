package com.example.timeapi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeapi.datas.DataClassApi;
import com.example.timeapi.datas.Timings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String DEFAULT = "N/A";

    TextView prayerList, prayerCalculation, location;
    String fajar, duhur, asr, maghrib, esha;
///////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
    GPSTracker gpsTracker;
    String lat;
    String lon;
    String country, city;
    Context context=MainActivity.this;
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////

    JsonPlaceHolderApi jsonPlaceHolderApi;

    PrayerDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsTracker=new GPSTracker(this);

        db = new PrayerDatabase(this);

        sharedPreferences=getSharedPreferences("userloc",MODE_PRIVATE);
        editor=sharedPreferences.edit();


        String getCity=sharedPreferences.getString("city",DEFAULT);
        String getCountry=sharedPreferences.getString("country", DEFAULT);


        prayerList=findViewById(R.id.textView);
        location=findViewById(R.id.location);
        prayerCalculation=findViewById(R.id.prayerCounter);

        Toast.makeText(context,db.getTimming().toString(),Toast.LENGTH_LONG).show();

        if (getCity.equals(DEFAULT) && getCountry.equals(DEFAULT)) {
            Toast.makeText(MainActivity.this, "No Data Avaiable", Toast.LENGTH_SHORT).show();
        }else {
            location.setText(getCity +" : "+ getCountry);
        }

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.aladhan.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);




        retrofitMethod();
        gpsMethod();


    }
// -----------------------------Retrofit and Time api -----------------------------------------------------
    private void retrofitMethod() {
        Call<DataClassApi> myCall=jsonPlaceHolderApi.getPost("Dhaka","Bangladesh");
        myCall.enqueue(new Callback<DataClassApi>() {
            @Override
            public void onResponse(Call<DataClassApi> call, Response<DataClassApi> response) {
                if (!response.isSuccessful()) {
                    prayerList.setText(response.code());
                    return;
                }

                DataClassApi dataClassApi=response.body();

                fajar=dataClassApi.getData().getTimings().getFajr();
                duhur=dataClassApi.getData().getTimings().getDhuhr();
                asr=dataClassApi.getData().getTimings().getAsr();
                maghrib=dataClassApi.getData().getTimings().getMaghrib();
                esha=dataClassApi.getData().getTimings().getIsha();

                String content="";
                content+="Fajar: "+dataClassApi.getData().getTimings().getFajr() + "\n";
                content+="Duhur: "+dataClassApi.getData().getTimings().getDhuhr()+ "\n";
                content+="Asar: "+asr+ "\n";
                content+="Maghrib: "+maghrib+ "\n";
                content+="Esha: "+esha+ "\n";

                String apidate = dataClassApi.getData().getDate().getReadable();
                Log.d("apidate", "onResponse: "+apidate);

                prayerList.append(content);
//this empty table
                db.delete();
                Timings timings = dataClassApi.getData().getTimings();

                //it insert data
                //understand, one more question brother
                if(db.insert(timings)){

                }

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                try {

                    String time = apidate+":";
                    time += fajar;


                    Log.d("myFajarTime","fajar"+fajar);

                    SimpleDateFormat sdfApi=new SimpleDateFormat("dd MMM yyyy:HH:mm");
                    SimpleDateFormat sdfCurrentTime=new SimpleDateFormat("dd MMM yyyy:HH:mm:ss");
                    String currentTime=sdfCurrentTime.format(new Date());

                    Date currentParse=sdfCurrentTime.parse(currentTime);
                    Date prayerParse=sdfApi.parse(time);
                    long difference=currentParse.getTime()-prayerParse.getTime();

                    Log.d("diffout","difference: "+difference);

                    if (difference > 0) {
                        time = apidate+":"+duhur;
                        prayerParse=sdfApi.parse(time);
                        difference=currentParse.getTime()-prayerParse.getTime();

                        if (difference > 0) {
                            time = apidate+":"+asr;
                            prayerParse=sdfApi.parse(time);
                            difference=currentParse.getTime()-prayerParse.getTime();

                            if (difference > 0) {
                                time = apidate+":"+maghrib;
                                prayerParse=sdfApi.parse(time);
                                difference=currentParse.getTime()-prayerParse.getTime();

                                if (difference > 0) {
                                    time = apidate+":"+esha;
                                    prayerParse=sdfApi.parse(time);
                                    difference=currentParse.getTime()-prayerParse.getTime();

                                    if (difference < 0) {
                                        time = apidate+":"+fajar;
                                        prayerParse=sdfApi.parse(time);
                                        difference=prayerParse.getTime()-currentParse.getTime();
                                    }else{
                                        Calendar cal = new GregorianCalendar();
                                        cal.add(Calendar.DATE, -1);
                                        String currtime = sdfCurrentTime.format(cal.getTime());
                                        time = apidate+":"+fajar;
                                        prayerParse=sdfApi.parse(time);
                                        difference=sdfCurrentTime.parse(currtime).getTime() - prayerParse.getTime();

                                    }
                                }
                            }
                        }
                    }

                    long finalDiff=difference<0? -difference:difference;
//                    long finalDiff=difference;

                    CountDownTimer c=new CountDownTimer(finalDiff,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long millisUn=millisUntilFinished;

                            long diffMin=millisUn / (60 * 1000) % 60;
                            long diffHours = millisUn / (60 * 60 * 1000);
                            long diffSeconds = millisUn / 1000 % 60;

                            prayerCalculation.setText("-"+diffHours+":"+diffMin+":"+diffSeconds);

                        }

                        @Override
                        public void onFinish() {

                        }
                    };
                    c.start();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<DataClassApi> call, Throwable t) {
                prayerList.setText(t.getMessage());
            }
        });
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
    void gpsMethod(){

        final Handler handler=new Handler();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                if (Permissions.checkLocationPermission(context)){
                    address();
                }
            }
        };
        handler.postDelayed(runnable,10000);
    }
    void address(){
        lat=String.valueOf(gpsTracker.getLatitude());
        lon=String.valueOf(gpsTracker.getLatitude());

        Log.d("latlong","Location: "+lat+lon);

        if (lat.equals("0.0")){
            statusCheck();
        }else {
            try {
                gpsTracker.findad();
                String cityName=gpsTracker.addresses.get(0).getSubAdminArea();
                String countryName=gpsTracker.addresses.get(0).getCountryName();

                String[] parts = cityName.split(" ");
                String city = parts[0];


                editor.putString("city",city);
                editor.putString("country",countryName);
                editor.apply();
                editor.commit();

// Sharedpreference offline mode not given the output


                    String getCity=sharedPreferences.getString("city",DEFAULT);
                    String getCountry=sharedPreferences.getString("country", DEFAULT);

                    if (getCity.equals(DEFAULT) && getCountry.equals(DEFAULT)) {
                        Toast.makeText(MainActivity.this, "No Data Insert", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Data is inserted Successfully", Toast.LENGTH_SHORT).show();
                        location.setText(getCity +" : "+ getCountry);
                    }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        address();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private void statusCheck() {
        LocationManager locationManager=(LocationManager) getSystemService(context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessage();
        }

    }

    public void buildAlertMessage(){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();


        }
    }