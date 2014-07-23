package com.example.jmccrae.gradletest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;


public class SplashScreenActivity extends Activity {

    ProgressBar myProgressBar;

    int myProgress = 0;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);



        myProgressBar=(ProgressBar)findViewById(R.id.progressBar1);



        //clear previously stored coordinates

        String myLat = "none";

        String myLon = "none";

        SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = loginSettings.edit();

        prefEditor.putString("mLat", myLat);

        prefEditor.putString("mLon", myLon);





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

                    startActivity(new Intent(SplashScreenActivity.this,

                            Login.class));

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

}