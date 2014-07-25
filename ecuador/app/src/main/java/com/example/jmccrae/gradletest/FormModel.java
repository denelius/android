package com.example.jmccrae.gradletest;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.loopj.android.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormModel {

    //private variables
    int _id;
    String _user;
    String _proj;
    String _locname;
    String _locdepth;
    String _locpriority;
    String _locdesc;
    String _image;
    String _date;
    String _time;
    String _gpslat;
    String _gpslon;
    String _ulat;
    String _ulon;

    // Empty constructor
    public FormModel(){

    }

    // constructor
    public FormModel(int id, String vuser, String vproj, String vlocname, String vlocdepth, String vlocpriority, String vlocdesc, String vimage, String vdate, String vtime, String vulat, String vulon, String vgpslat, String vgpslon){
        this._id = id;
        this._user = vuser;
        this._proj = vproj;
        this._locname = vlocname;
        this._locdepth = vlocdepth;
        this._locpriority = vlocpriority;
        this._locdesc = vlocdesc;
        this._image = vimage;
        this._date = vdate;
        this._time = vtime;
        this._gpslat = vgpslat;
        this._gpslon = vgpslon;
        this._ulat = vulat;
        this._ulon = vulon;
    }

    public RequestParams toHttpParams() {
        RequestParams params = new RequestParams();
        params.put("usr", _user);
        params.put("prj", _proj);
        params.put("loc", _locname);
        params.put("dep", _locdepth);
        params.put("pri", _locpriority);
        params.put("com", _locdesc);
        params.put("dat", _date);
        params.put("tim", _time);
        params.put("gla", _gpslat);
        params.put("glo", _gpslon);
        params.put("ula", _ulat);
        params.put("ulo", _ulon);
        if(!this._image.equals("boogin")) {
            try {
                params.put("img", new File(this._image));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    public static class SplashScreen extends Activity {
        AsyncUse mobile_server;
        boolean alock;
        boolean pexpired = true;
        String icheck = "";
        String xdate;
        String dproj;
        ProgressBar myProgressBar;
        int myProgress = 0;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);



            if (isNetAvailable(getApplicationContext())){
                SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                String fName = loginSettings.getString("uNam", null);
                String fPass = loginSettings.getString("uPas", null);
                String fProj = loginSettings.getString("uPro", null);

                this.mobile_server = new AsyncUse("http://apollo.newfields.com/mobile/");
                RequestParams params = new RequestParams();
                params.put("uname", fName);
                params.put("upass", fPass);
                params.put("uproj", fProj);


                this.mobile_server.post("moblogin.php", params, new AsyncHttpResponseHandler() {
                    public void onSuccess(String data) {
                        String[] parts;
                        String rstatus = "boogin";
                        String xdate = "boogin";
                        //Toast.makeText(LogIn.this, "img status: " + data, Toast.LENGTH_LONG).show();
                        if (data.contains("@")) {
                            parts = data.split("@");
                            rstatus = parts[0];
                            xdate = parts[1];
                            //Toast.makeText(LogIn.this, "date stuff: " + xdate, Toast.LENGTH_LONG).show();
                        }
                        if (rstatus.equals("ok")){
                            SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = loginSettings.edit();
                            prefEditor.putBoolean("lock", false);
                            prefEditor.putString("xDat", xdate);
                            prefEditor.putString("fPriority", "select priority");
                            prefEditor.putString("fPos", "gps");
                            prefEditor.putString("fLocname", "");
                            prefEditor.putString("fComment", "");
                            prefEditor.putString("fdepth", "");
                            prefEditor.putString("fImagepath", "boogin");
                            prefEditor.commit();
                        } else {
                            SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = loginSettings.edit();
                            prefEditor.putBoolean("lock", true);
                            prefEditor.putString("xDat", "boogin");
                            prefEditor.putString("uNam", "boogin");
                            prefEditor.putString("uPas", "boogin");
                            prefEditor.putString("uPro", "boogin");
                            prefEditor.putString("fPriority", "select priority");
                            prefEditor.putString("fPos", "gps");
                            prefEditor.putString("fLocname", "");
                            prefEditor.putString("fComment", "");
                            prefEditor.putString("fdepth", "");
                            prefEditor.putString("fImagepath", "boogin");
                            prefEditor.commit();
                        }
                    }
                });
            }



            myProgressBar=(ProgressBar)findViewById(R.id.progressBar1);

            //clear previously stored coordinates
            SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = loginSettings.edit();
            alock = loginSettings.getBoolean("lock", false);
            xdate = loginSettings.getString("xDat", null);
            //dproj = loginSettings.getString("uPro", null);
            prefEditor.putString("uLat", "");
            prefEditor.putString("uLon", "");
            prefEditor.putString("fPriority", "select priority");
            prefEditor.putString("fPos", "gps");
            prefEditor.putString("fLocname", "");
            prefEditor.putString("fComment", "");
            prefEditor.putString("fdepth", "");
            prefEditor.putString("fImagepath", "boogin");
            prefEditor.commit();

            //Toast.makeText(SplashScreen.this, "proj stuff: " + dproj, Toast.LENGTH_LONG).show();

            if (xdate != null) {
                try{
                    //Toast.makeText(SplashScreen.this, "proj stuff: " + dproj, Toast.LENGTH_LONG).show();
                    Calendar now = Calendar.getInstance();
                    DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
                    String nowDate1 = dformat.format(now.getTime());
                    Date nowDate = dformat.parse(nowDate1);
                    Date sdate = dformat.parse(xdate);

                    if(sdate.compareTo(nowDate) > 0){
                        pexpired = false;
                    } else {
                        pexpired = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            new Thread(myThread).start();
            /** set time to splash out */
            final int welcomeScreenDisplay = 3000;
            /** create a thread to show splash up to splash time */
            Thread welcomeThread = new Thread() {

                int wait = 0;

                @Override
                public void run() {
                    try {
                        super.run();
                        /**
                         * use while to get the splash time. Use sleep() to increase
                         * the wait variable for every 100L.
                         */
                        while (wait < welcomeScreenDisplay) {
                            sleep(100);
                            wait += 100;
                        }
                    } catch (Exception e) {
                        System.out.println("EXc=" + e);
                    } finally {
                        /**
                         * Called after splash times up. Do some action after splash
                         * times up. Here we moved to another main activity class
                         */
                        if ((alock == false) && (pexpired == false)) {
                            if (isNetAvailable(getApplicationContext())){
                                startActivity(new Intent(SplashScreen.this,MapMain.class));
                            } else {
                                startActivity(new Intent(SplashScreen.this,FormView.class));
                            }
                            //FormView.class));
                        } else {
                            startActivity(new Intent(SplashScreen.this,
                                    Login.class));
                        }
                        finish();
                    }
                }
            };
            welcomeThread.start();

        }



        private Runnable myThread = new Runnable(){

            public void run() {
                // TODO Auto-generated method stub
                while (myProgress<100){
                    try{
                        myHandle.sendMessage(myHandle.obtainMessage());
                        Thread.sleep(30);
                    }
                    catch(Throwable t){ }
                }
            }

            Handler myHandle = new Handler(){

                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    myProgress++;
                    myProgressBar.setProgress(myProgress);
                }
            };
        };

        public Boolean isNetAvailable(Context con)  {

            try{
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        con.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileInfo =
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
                    return true;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }



    }
}
