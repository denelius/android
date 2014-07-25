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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashScreen extends Activity {
    AsyncUse mobile_server;
    ProgressBar myProgressBar;
    int myProgress = 0;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        myProgressBar=(ProgressBar)findViewById(R.id.progressBar1);
        //clear previously stored coordinates

        SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = loginSettings.edit();
        prefEditor.putString("mLat", "boogin");
        prefEditor.putString("mLon", "boogin");
        prefEditor.putString("fPriority", "boogin");
        prefEditor.putString("fPos", "boogin");
        prefEditor.putString("fLocname", "boogin");
        prefEditor.putString("fdepth", "boogin");
        prefEditor.putString("fComment", "boogin");
        prefEditor.putString("fImagepath", "boogin");

        prefEditor.commit();

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
                    //startActivity(new Intent(SplashScreen.this, Login.class));
                    if (isNetAvailable(getApplicationContext())){
                        postSend();
                    } else {
                        Intent intent = new Intent(SplashScreen.this, Login.class);
                        startActivity(intent);
                    };

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

    public void postSend() {

        String uname;
        String uproj;
        String upass;

        SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
        uname = loginSettings.getString("uNam", null);
        uproj = loginSettings.getString("uPro", null);
        upass = loginSettings.getString("uPas", null);


        RequestParams params = new RequestParams();
        params.put("uname", uname);
        params.put("upass", upass);
        params.put("uproj", uproj);
        this.mobile_server = new AsyncUse("http://apollo.newfields.com/mobile/");
        this.mobile_server.post("moblogin.php", params, new AsyncHttpResponseHandler() {
            public void onSuccess(String data) {
                String[] parts;
                String rstatus = "boogin";
                //Toast.makeText(LogIn.this, "img status: " + data, Toast.LENGTH_LONG).show();
                if (data.contains("@")) {
                    parts = data.split("@");
                    rstatus = parts[0];
                    //Toast.makeText(LogIn.this, "date stuff: " + xdate, Toast.LENGTH_LONG).show();
                }
                if (rstatus.equals("ok")){
                    SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = loginSettings.edit();
                    prefEditor.putBoolean("lock", false);
                    prefEditor.commit();

                    //TODO change to map
                    Intent intent = new Intent(SplashScreen.this, MapMain.class);
                    startActivity(intent);

                } else {
                    SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = loginSettings.edit();
                    prefEditor.putBoolean("lock", true);
                    prefEditor.commit();
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }
            }
        });
    }

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