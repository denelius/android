package com.example.jmccrae.gradletest;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class JsonObserver extends BroadcastReceiver
{
	/*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/

    private static final String TAG = "JsonObserver";

	/*--------------------------------------------
	|    I N S T A N C E   V A R I A B L E S    |
	============================================*/

    private JsonLoader loader;

	/*--------------------------------------------
	|         C O N S T R U C T O R S           |
	============================================*/

    public JsonObserver( JsonLoader loader )
    {
        this.loader = loader;
        IntentFilter filter = new IntentFilter( MapMain.CAMERA_CHANGE );
        loader.getContext().registerReceiver( this, filter );
    }

    @Override
    public void onReceive( Context context, Intent intent )
    {
        Log.v( TAG, "Detected a Camera Change" );

        Object parcel = intent.getParcelableExtra( MapMain.CAMERA_LAT_LNG );
        if (( parcel != null ) && ( parcel instanceof LatLng ))
        {
            LatLng point = (LatLng) parcel;

            this.loader.setLat( point.latitude );
            this.loader.setLng( point.longitude );
            this.loader.onContentChanged();
        }
    }

	/*--------------------------------------------
	|   P U B L I C    A P I    M E T H O D S   |
	============================================*/

    public void unregister()
    {
        loader.getContext().unregisterReceiver( this );
    }

	/*--------------------------------------------
	|    N O N - P U B L I C    M E T H O D S   |
	============================================*/

	/*--------------------------------------------
	|  A C C E S S O R S / M O D I F I E R S    |
	============================================*/

	/*--------------------------------------------
	|       I N L I N E    C L A S S E S        |
	============================================*/
}
