package com.example.jmccrae.gradletest;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;


public class InforMation extends Activity {

    private static TextView tv_uname;
    private static TextView tv_project;
    private String uname;
    private String uproj;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);


    }

    @Override
    protected void onResume() {
        super.onResume();

        //setup display of user and pwd from prefs grabbed from login details
        SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
        uname = loginSettings.getString("uNam", null);
        uproj = loginSettings.getString("uPro", null);

        tv_uname = (TextView)findViewById(R.id.tv_user);
        tv_project = (TextView)findViewById(R.id.tv_project);

        String idName = getIntent().getStringExtra("originId");
        if( idName == null )
        {
            tv_uname.setText(uname);
            tv_project.setText(uproj);
        } else {
            tv_uname.setText("not logged in");
            tv_project.setText("not logged in");
        }
    }

}
