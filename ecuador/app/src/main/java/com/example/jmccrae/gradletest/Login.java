package com.example.jmccrae.gradletest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.*;

public class Login extends Activity {

    AsyncUse mobile_server;
    private static EditText et_uname;
    private static EditText et_upass;
    private static EditText et_uproj;
    private static TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //following three lines ensure password hint text is same as user and project
        EditText password = (EditText) findViewById(R.id.ET_upass);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        this.mobile_server = new AsyncUse("http://apollo.newfields.com/mobile/");
        et_uname = (EditText)findViewById(R.id.ET_uname);
        et_upass = (EditText)findViewById(R.id.ET_upass);
        et_uproj = (EditText)findViewById(R.id.ET_uproj);
        tv_status = (TextView)findViewById(R.id.TV_report);

        final Button loginStuff = (Button) findViewById(R.id.gotoLogin);
        loginStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* send login credentials */

                if (isNetAvailable(getApplicationContext())){
                    postSend();
                } else {
                    Toast.makeText(Login.this, "no internet connection - please try again later", Toast.LENGTH_LONG).show();
                };
            }
        });

    }

    public void postSend() {

        final String fName = et_uname.getText().toString();
        final String fPass = et_upass.getText().toString();
        final String fProj = et_uproj.getText().toString();


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
                    prefEditor.putString("uNam", fName);
                    prefEditor.putString("uPas", fPass);
                    prefEditor.putString("uPro", fProj);
                    prefEditor.putString("xDat", xdate);
                    prefEditor.commit();
                    tv_status.setTextColor(getResources().getColor(R.color.status));
                    tv_status.setText("go to map view");

                    //TODO change to map
                    Intent intent = new Intent(Login.this, MapMain.class);
                    startActivity(intent);

                } else {
                    SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = loginSettings.edit();
                    prefEditor.putBoolean("lock", true);
                    prefEditor.commit();
                    tv_status.setTextColor(getResources().getColor(R.color.status));
                    tv_status.setText(data);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (  Integer.valueOf(android.os.Build.VERSION.SDK) < 7 //Instead use android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch( itemId )
        {
            case R.id.item_about:
                Intent myIntent = new Intent(this, InforMation.class);
                myIntent.putExtra("originId", "login");
                startActivity(myIntent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(LogIn.this, "this should not go back", Toast.LENGTH_LONG).show();
        return;
    }




}