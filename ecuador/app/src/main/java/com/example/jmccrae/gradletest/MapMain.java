package com.example.jmccrae.gradletest;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmccrae on 6/26/13.
 */
public class MapMain extends Activity implements LoaderManager.LoaderCallbacks<List<JsonLoader.JsonObject>>, JsonListFragment.JsonSelectedHandler, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener
{
	/*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/

    private static final String LONG_PRESS_OBJECT_MARKER = "LONG_PRESS_OBJECT_MARKER";

    private static final String TAG = "MapMain";

    public static final String CAMERA_CHANGE = "MapMain.CameraChange";
    public static final String CAMERA_LAT_LNG = "MapMain.CameraLatLng";

    public static final String SAVE_LAT_LNG = "MapMain.SaveLatLng";
    public static final String SAVE_ZOOM = "MapMain.SaveZoom";

    public static final String LONG_LAT_LNG = "MapMain.LongLatLng";

    private static final int JSON_LOADER_ID = 100;

    public static final double TECH_TOWER_LAT = 33.77243;
    public static final double TECH_TOWER_LNG = -84.394972;
    public static final float DEFAULT_ZOOM = 19.0f;
    public static final float ZOOMTO_ZOOM = 17.0f;

	/*--------------------------------------------
	|    I N S T A N C E   V A R I A B L E S    |
	============================================*/

    private GoogleMap map;
    private Double gpsLat;
    private Double gpsLon;
    private JsonListFragment listFragment = null;
    private Map<String, JsonLoader.JsonObject> markerMap = new HashMap<String, JsonLoader.JsonObject>();
    private Map<Marker,JsonLoader.JsonObject> jsonMap = new HashMap<Marker,JsonLoader.JsonObject>();

    private LatLng currentLatLng = new LatLng( TECH_TOWER_LAT, TECH_TOWER_LNG );
    private float currentZoom = DEFAULT_ZOOM;

    private LatLng longLatLng = null;



	/*--------------------------------------------
	|         C O N S T R U C T O R S           |
	============================================*/

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.map );
        getLoaderManager().initLoader( JSON_LOADER_ID, null, this );


        setUp();
    }

	/*--------------------------------------------
	|         L O A D E R    M E T H O D S       |
	============================================*/

    public Loader<List<JsonLoader.JsonObject>> onCreateLoader( int id, Bundle args )
    {
        switch ( id )
        {
            case JSON_LOADER_ID:
                return new JsonLoader( this );
            default:
                return null;
        }
    }

    public void onLoadFinished( Loader<List<JsonLoader.JsonObject>> loader, List<JsonLoader.JsonObject> list )
    {
        for ( JsonLoader.JsonObject o : list )
        {
            if (! markerMap.containsKey( o.getName() ))
            {
                if (this.map != null)
                {
                    MarkerOptions options = new MarkerOptions();
                    options.position( new LatLng( o.getLat(), o.getLng() ) );
                    options.title( o.getRaw() );
                    options.snippet( o.getMeta() );
                    options.icon( BitmapDescriptorFactory.fromResource(o.getIcon()) );

                    Marker marker = map.addMarker( options );
                    o.setMarker( marker );

                    this.markerMap.put( o.getName(), o );
                    this.jsonMap.put( marker, o );
                }
            }
        }

        if (this.listFragment != null)
        {
            this.listFragment.setJsonObjectList( list );
        }


    }



    public void onLoaderReset( Loader<List<JsonLoader.JsonObject>> loader )
    {
        // Remove any markers that are no longer valid. This would be handy if the JSON Request had a "bounds"
    }

	/*--------------------------------------------
	|   P U B L I C    A P I    M E T H O D S   |
	============================================*/

    @Override
    protected void onResume()
    {
        super.onResume();
        setUp();
    }

    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState )
    {
        super.onRestoreInstanceState( savedInstanceState );
        if (savedInstanceState.get( SAVE_LAT_LNG ) != null) this.currentLatLng = (LatLng) savedInstanceState.get( SAVE_LAT_LNG );
        if (savedInstanceState.get( LONG_LAT_LNG ) != null) this.longLatLng = (LatLng) savedInstanceState.get( LONG_LAT_LNG );
        this.currentZoom = savedInstanceState.getFloat( SAVE_ZOOM, DEFAULT_ZOOM );

        setPosition();
    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        outState.putParcelable( SAVE_LAT_LNG, this.currentLatLng );
        outState.putFloat( SAVE_ZOOM, this.currentZoom );
        if (this.longLatLng != null)
        {
            outState.putParcelable( LONG_LAT_LNG, this.longLatLng );
        }
    }

    public void onSelected( JsonLoader.JsonObject object )
    {
        LatLng objPosition = new LatLng( object.getLat(), object.getLng() );
        CameraPosition position = CameraPosition.builder().target( objPosition ).zoom( ZOOMTO_ZOOM ).build();
        this.map.animateCamera( CameraUpdateFactory.newCameraPosition(position) );
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        JsonLoader.JsonObject object = this.jsonMap.get(marker);
        if( object != null )
        {
            Toast.makeText(this, "markers location has been assigned to user defined location", Toast.LENGTH_LONG ).show();
            Intent myIntent = new Intent(this, FormView.class);
            myIntent.putExtra("locName", object.getRaw());
            String latString = Double.toString(object.getLat());
            String lonString = Double.toString(object.getLng());
            myIntent.putExtra("locLat",latString);
            myIntent.putExtra("locLon", lonString);
            startActivity(myIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch( itemId )
        {
            case R.id.item_satellite:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.item_street:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.item_form:
                Intent intent = new Intent(MapMain.this, FormView.class);
                startActivity(intent);
                break;
            case R.id.item_about:
                Intent intent2 = new Intent(MapMain.this, InforMation.class);
                startActivity(intent2);
                break;
            case R.id.item_logout:
                SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = loginSettings.edit();
                prefEditor.putBoolean("lock", true);
                prefEditor.putString("uNam", "boogin");
                prefEditor.putString("uPas", "boogin");
                prefEditor.putString("uPro", "boogin");
                prefEditor.putString("uLat", "boogin");
                prefEditor.putString("uLon", "boogin");
                prefEditor.putString("xDat", "2010-03-11");
                prefEditor.commit();
                Intent intent3 = new Intent(MapMain.this, Login.class);
                startActivity(intent3);
                break;
        }
        return true;
    }

    public void onMapLongClick( LatLng point )
    {
        Log.i(TAG, "Long Click");
        JsonLoader.JsonObject newPosition = this.markerMap.get( LONG_PRESS_OBJECT_MARKER );
        if (newPosition == null)
        {
            newPosition = new JsonLoader.JsonObject();
            newPosition.setName( "Long-Press" );
            newPosition.setIcon( R.drawable.myloc  );
            newPosition.setLat( point.latitude );
            newPosition.setLng( point.longitude );
            newPosition.setRaw( "New Point @ " + point.latitude + ", " + point.longitude );
            //store longpress lat long to prefs
            String tempLat = String.valueOf(point.latitude);
            String tempLon = String.valueOf(point.longitude);
            SharedPreferences loginSettings = getSharedPreferences("MyLoginPreferences", MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = loginSettings.edit();
            prefEditor.putString("uLat", tempLat);
            prefEditor.putString("uLon", tempLon);
            prefEditor.commit();

        }
        else
        {
            newPosition.getMarker().remove();
        }

        MarkerOptions options = new MarkerOptions();
        options.position( point );
        options.title( newPosition.getName() );
        options.snippet( newPosition.getRaw() );
        options.icon( BitmapDescriptorFactory.fromResource( newPosition.getIcon() ) );

        Marker marker = map.addMarker( options );
        newPosition.setMarker( marker );

        this.markerMap.put( LONG_PRESS_OBJECT_MARKER, newPosition );

        if (( this.longLatLng == null ) || ( ! this.longLatLng.equals( point ) ))
        {
            Toast.makeText(this, newPosition.getRaw(), Toast.LENGTH_SHORT).show();
        }
        this.longLatLng = point;
    }

    public void onCameraChange( CameraPosition position )
    {
        Log.i( TAG, "Camera Change" );
        Intent changeCamera = new Intent( CAMERA_CHANGE );
        changeCamera.putExtra( CAMERA_LAT_LNG, position.target );
        this.sendBroadcast( changeCamera );

        this.currentLatLng = position.target;
        this.currentZoom = position.zoom;
    }

	/*--------------------------------------------
	|    N O N - P U B L I C    M E T H O D S   |
	============================================*/

    private void setUp()
    {
        if (this.map == null)
        {
            this.map = ( (MapFragment) getFragmentManager().findFragmentById( R.id.map ) ).getMap();
            if (this.map != null)
            {
                this.map.setOnCameraChangeListener(this);
                this.map.setOnMapLongClickListener(this);
                this.map.setOnInfoWindowClickListener(this);
                this.map.setMyLocationEnabled(true);


                setPosition();
            }
        }

        if (this.listFragment == null)
        {
            Fragment fragment = getFragmentManager().findFragmentById( R.id.list );
            if (fragment != null)
            {
                this.listFragment = (JsonListFragment) fragment;
            }
        }





    }

    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location

            try {
                myLocation = lm.getLastKnownLocation(provider);
            } catch (Exception e){
                Toast.makeText(this, "no gps fix, check device settings", Toast.LENGTH_SHORT).show();
                Location newl = new Location("boogin");
                newl.setLatitude(33.79157);
                newl.setLongitude(-84.38733);
                myLocation = newl;
            }

        }



        return myLocation;
        //return "boogin";
    }

    private void setPosition()
    {

        //LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        //String provider = service.getBestProvider(criteria, false);
        //Location location = service.getLastKnownLocation(provider);
        //LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());


        LatLng userLocation = new LatLng(-23.586230,-46.682810);
        //Location userLocation = getMyLocation();
        //Location d = getMyLocation();
        //Toast.makeText(this, "loc: " + d.getLatitude(), Toast.LENGTH_SHORT).show();
        //LatLng userLocation = new LatLng(d.getLatitude(),d.getLongitude());




        //CameraPosition position = CameraPosition.builder().target( this.currentLatLng ).zoom( this.currentZoom ).build();
        CameraPosition position = CameraPosition.builder().target( userLocation ).zoom( this.currentZoom ).build();
        this.map.animateCamera( CameraUpdateFactory.newCameraPosition( position ) );

        if (this.longLatLng != null)
        {
            this.onMapLongClick( this.longLatLng );
        }
    }


	/*--------------------------------------------
	|  A C C E S S O R S / M O D I F I E R S    |
	============================================*/

	/*--------------------------------------------
	|       I N L I N E    C L A S S E S        |
	============================================*/
}
