package com.example.jmccrae.gradletest;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;
import com.loopj.android.http.*;



public class FormView extends Activity {

    FormModel model;

    private static TextView tv_uname;
    private static TextView tv_project;
    private RadioButton rb_locgps;
    private RadioButton rb_locusr;
    EditText et_locid;
    EditText et_depth;
    EditText et_comment;
    ImageButton checkCart;
    ImageButton cameraButton;
    Button submitForm;
    ImageView iv_camera;
    Spinner spinner;

    private Boolean gpsActive = false;
    private File imageFile;
    private ImageButton carticon;
    private static final int TAKE_PICTURE = 0;
    private Uri mUri;
    private Bitmap mPhoto;
    private AsyncUse server = new AsyncUse("http://apollo.newfields.com/mobile/");
    private DatabaseHandler db = new DatabaseHandler(this);
    private ImageUtil imageUtil;

    public GpsLocation.LocationResult locationResult = new GpsLocation.LocationResult(){
        @Override
        public void gotLocation(final Location location) {
            gpsActive = true;
            model._gpslat = Double.toString(location.getLatitude());
            model._gpslon = Double.toString(location.getLongitude());
        };
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////// Widgets //////
        setContentView(R.layout.form);

        carticon = (ImageButton)findViewById(R.id.IB_cart);

        spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner,getResources().getStringArray(R.array.country_arrays));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        tv_uname = (TextView)findViewById(R.id.TV_uname);
        tv_project = (TextView)findViewById(R.id.TV_project);
        rb_locgps = (RadioButton)findViewById(R.id.RB_gps);
        rb_locusr = (RadioButton)findViewById(R.id.RB_usr);
        checkCart = (ImageButton) findViewById(R.id.IB_cart);
        cameraButton = (ImageButton) findViewById(R.id.IB_cam);
        submitForm = (Button) findViewById(R.id.B_submit);
        iv_camera = (ImageView)findViewById(R.id.IV_camera);

        et_locid = (EditText)findViewById(R.id.ET_locid);
        et_depth = (EditText)findViewById(R.id.ET_depth);
        et_comment = (EditText)findViewById(R.id.ET_comment);

        ////// Real Work //////
        //get gps warmed up
        getPosition();
        //set up our model
        model = new FormModel();
        updateCartImage();
        //reload prior data entry
        reloadForm();
        imageUtil = new ImageUtil(getContentResolver());

        ////// Listeners //////

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(pos).toString();
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    model._locpriority = item;
                    if (item.equals("select priority")) {
                        selectedText.setTextColor(getResources().getColor(R.color.hint));
                    } else {
                        selectedText.setTextColor(getResources().getColor(R.color.nftxt));
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        checkCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.getContactsCount() == 0){
                    alert("no items are stored on the device");
                } else if  (db.getContactsCount() == 1){
                    alert("there is 1 item stored on the device - sync to upload - requires internet connection");
                } else {
                    alert("there are " + db.getContactsCount() + " items stored on the device - sync to upload - requires internet connection");
                }
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                if (isSDPresent) {
                    FormView.this.model._image = freshName();
                    Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                    imageFile = new File(FormView.this.model._image);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                    mUri = Uri.fromFile(imageFile);
                    startActivityForResult(i, TAKE_PICTURE);
                } else {
                    alert("SD card is not present");
                }

            }
        });
        submitForm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPosition();
                model._locname = et_locid.getText().toString();
                model._locdepth = et_depth.getText().toString();
                model._locdesc = et_comment.getText().toString();
                postForm();
            }
        });
    } // end onCreate

    @Override
    protected void onResume() {
        super.onResume();
        String jsonName = getIntent().getStringExtra("locName");
        String jsonLat = getIntent().getStringExtra("locLat");
        String jsonLon = getIntent().getStringExtra("locLon");
        if( jsonName == null )
        {
            // TODO Not from Map, clear values in form
        } else {
            SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = loginSettings.edit();
            prefEditor.putString("uLat", jsonLat);
            prefEditor.putString("uLon", jsonLon);
            prefEditor.commit();
            model._ulat = jsonLat;
            model._ulon = jsonLon;

            rb_locgps.setChecked(false);
            rb_locusr.setChecked(true);
            et_locid.setText(jsonName);

            getIntent().removeExtra("locName");
            getIntent().removeExtra("locLat");
            getIntent().removeExtra("locLon");
        }
    }

    @SuppressWarnings("javadoc")
    public void postForm() {
        model._date = getDate();
        model._time = getTime();

        if (!model._locname.equals("")
                && !model._locdepth.equals("")
                && !model._locpriority.equals("select priority") )
        {
            if (isNetAvailable()) {
                if (rb_locgps.isChecked()) {
                    if (!gpsActive) {
                        alert("no GPS fix, is GPS on, consider user defined location");
                        return;
                    } else {}
                } else {
                    if (model._ulat.equals("")) {
                        alert("no user defined location - use map to define location");
                        return;
                    }
                    model._gpslat = "";
                    model._gpslon = "";
                }
            } else {
                if (gpsActive){
                    db.addContact(model);
                    carticon.setImageResource(R.drawable.ic_fcart48);
                    alert("no internet connection - your data has been stored on the mobile device, sync to upload - requires an internet connection");
                    clearForm();
                    return;
                } else {
                    alert("waiting for location, check device GPS settings");
                    return;
                }
            }
        } else {
            alert("ensure at least location, priority and depth have been filled out");
            return;
        }

        ////// Callback //////
        this.server.post("mobupload.php", model.toHttpParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String data) {
                String status = data.split("@")[1];
                if (status.equals("ok")) {
                    clearForm();
                    alert("upload complete");
                } else {
                    alert("upload error" + status);
                }
            }
        });

    }

    @SuppressWarnings("javadoc")
    public void postSync() {
        List<FormModel> xs = db.getAllContacts();
        for (final FormModel x : xs) {
            this.server.post("mobupload.php", x.toHttpParams(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String data) {
                    String part2 = data.split("@")[1];
                    if (part2.equals("ok")) {
                        int fast = db.getContactsCount();
                        db.deleteContact(Integer.toString(x._id));
                        if (fast == 0) {
                            alert("sync complete");
                            carticon.setImageResource(R.drawable.ic_ecart48);
                        } else {
                            alert("item " + fast + " sync upload complete");
                        }
                    } else {
                        alert("sync error, please try again later");
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    getContentResolver().notifyChange(mUri, null);
                    try {
                        mPhoto = imageUtil.decodeSampledBitmapFromUri(mUri, 200, 200);
                        mPhoto = imageUtil.scaleDown(mPhoto, 300, true);
                        //adjust original size to same as scaled bitmap
                        mPhoto.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(imageFile));
                        iv_camera.setImageBitmap(mPhoto);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        alert(e.toString());
                    } catch (Exception e) {
                        alert(e.getMessage());
                    }
                }
        }
    }

    private void clearForm() {
        iv_camera.setImageDrawable(null);
        et_locid.setText("");
        et_depth.setText("");
        et_comment.setText("");
        rb_locgps.setChecked(true);
        rb_locusr.setChecked(false);
        model._ulat = "";
        model._ulon = "";
        spinner.setSelection(0);
        model._image = "boogin";
    }

    private void saveForm(){
        String loc_pos;
        String floc = et_locid.getText().toString();
        model._locdepth = et_depth.getText().toString();
        model._locdesc = et_comment.getText().toString();
        if (rb_locgps.isChecked()){
            loc_pos = "gps";
        } else {
            loc_pos = "usr";
        }

        SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = loginSettings.edit();
        prefEditor.putString("fPriority", model._locpriority);
        prefEditor.putString("fPos", loc_pos);
        prefEditor.putString("fLocname", floc);
        prefEditor.putString("fdepth", model._locdepth);
        prefEditor.putString("fComment", model._locdesc);
        prefEditor.putString("fImagepath", model._image);
        prefEditor.commit();

    }

    private void reloadForm() {
        SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
        model._locpriority = loginSettings.getString("fPriority", null);
        String fpos = loginSettings.getString("fPos", null);
        model._locname = loginSettings.getString("fLocname", null);
        model._locdepth = loginSettings.getString("fdepth", null);
        model._locdesc = loginSettings.getString("fComment", null);
        model._image = loginSettings.getString("fImagepath", null);
        model._user = loginSettings.getString("uNam", null);
        model._proj = loginSettings.getString("uPro", null);
        model._ulat = loginSettings.getString("uLat", null);
        model._ulon = loginSettings.getString("uLon", null);

        ArrayAdapter <String> myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = myAdap.getPosition(model._locpriority);
        spinner.setSelection(spinnerPosition);

        Bitmap restoreBitmap = BitmapFactory.decodeFile(model._image);
        if (model._image.equals("boogin")) {
            iv_camera.setImageDrawable(null);
        } else {
            iv_camera.setImageBitmap(restoreBitmap);
        }

        tv_uname.setText(model._user);
        tv_project.setText(model._proj);

        et_locid.setText(model._locname);
        et_depth.setText(model._locdepth);
        et_comment.setText(model._locdesc);


        if (fpos.equals("gps")){
            rb_locgps.setChecked(true);
            rb_locusr.setChecked(false);
        } else {
            rb_locgps.setChecked(false);
            rb_locusr.setChecked(true);
        }

    }

    private void getPosition(){
        try{
            GpsLocation myLocation = new GpsLocation();
            myLocation.getLocation(this, locationResult);
        }
        catch(Exception e){
            gpsActive = false;
            Toast.makeText(this, "gps pooped", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (!model._image.equals("boogin")) {
            iv_camera.setImageBitmap(BitmapFactory.decodeFile(model._image));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch( itemId )
        {
            case R.id.item_map:
                if (isNetAvailable()){
                    saveForm();
                    Intent intent = new Intent(FormView.this, MapMain.class);
                    startActivity(intent);
                }
                else {
                    alert("map view requires an internet connection");
                }
                break;
            case R.id.item_sync:
                if (isNetAvailable()) {
                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                    int fast = db.getContactsCount();
                    if (fast > 0){
                        if(isSDPresent)
                        {
                            postSync();
                        } else {
                            alert("SD card is not present");
                        }
                    } else {
                        alert("there are currently no items to sync");
                    }
                }
                else {
                    alert("sync requires an internet connection");
                }
                break;
            case R.id.item_about:
                saveForm();
                Intent intent2 = new Intent(FormView.this, InforMation.class);
                startActivity(intent2);
                break;
            case R.id.item_logout:
                SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = loginSettings.edit();
                prefEditor.putBoolean("lock", true);
                prefEditor.putString("uNam", "boogin");
                prefEditor.putString("uPas", "boogin");
                prefEditor.putString("uPro", "boogin");
                prefEditor.putString("uLat", "");
                prefEditor.putString("uLon", "");
                prefEditor.putString("xDat", "2010-03-11");
                prefEditor.commit();
                Intent intent3 = new Intent(FormView.this, LogIn.class);
                startActivity(intent3);
                break;
        }
        return true;
    }

    ////// Helpers //////

    private static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private static String freshName() {
        String[] tempDate = getDate().split("-");
        String[] tempTime = getTime().split(":");
        String basename = tempDate[0] + tempDate[1] + tempDate[2] + tempTime[0] + tempTime[1] + tempTime[2];
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return "/" + sdcardPath + "/DCIM/Camera/" + basename + ".jpg";
    }

    private void updateCartImage() {
        int fast = db.getContactsCount();
        if (fast > 0){
            carticon.setImageResource(R.drawable.ic_fcart48);
        } else {
            carticon.setImageResource(R.drawable.ic_ecart48);
        }
    }

    public Boolean isNetAvailable()  {
        Context con = getApplicationContext();
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

    private void alert(String msg) {
        Toast.makeText(FormView.this, msg, Toast.LENGTH_LONG).show();
    }

    ////// Boilerplate //////
    @Override
    public void onBackPressed() {
        saveForm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_menu, menu);
        return true;
    }


}
