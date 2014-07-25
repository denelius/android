package com.example.jmccrae.gradletest;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.example.jmccrae.gradletest.JsonLoader.JsonObject;

public class JsonLoader extends AsyncTaskLoader<List<JsonObject>>
{
	/*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/

	/*--------------------------------------------
	|    I N S T A N C E   V A R I A B L E S    |
	============================================*/

    //get prefs and append to json request url
    SharedPreferences loginSettings = NoMad.getContext().getSharedPreferences("MyLoginPreferences", NoMad.getContext().MODE_PRIVATE);
    String uname = loginSettings.getString("uNam", null);
    String uproj = loginSettings.getString("uPro", null);
    String urlSupplement = "uname=" + uname + "&uproj=" + uproj;

    private static final String SERVICE_ROOT = "http://apollo.newfields.com/mobile/mobjson.php?";
    private List<JsonObject> currentList;
    private JsonObserver observer;

    private double lat = MapMain.TECH_TOWER_LAT;
    private double lng = MapMain.TECH_TOWER_LNG;

	/*--------------------------------------------
	|         C O N S T R U C T O R S           |
	============================================*/

    public JsonLoader( Context context )
    {
        super( context );
    }




	/*--------------------------------------------
	|   P U B L I C    A P I    M E T H O D S   |
	============================================*/

    @Override
    public List<JsonObject> loadInBackground()
    {

        List<JsonObject> objectList = new ArrayList<JsonLoader.JsonObject>();
        try
        {
            StringBuilder url = new StringBuilder( SERVICE_ROOT );
            url.append(urlSupplement);
            //url.append( "lat=" ).append( this.lat ).append( "&" );
            //url.append( "lon=" ).append( this.lng );

            URL newfieldsurl = new URL( url.toString() );
            URLConnection tc = newfieldsurl.openConnection();
            BufferedReader in = new BufferedReader( new InputStreamReader( tc.getInputStream() ) );

            String line = in.readLine();
            JSONArray ja = new JSONArray( line );

            for ( int i = 0; i < ja.length(); i++ )
            {
                JSONObject jo = (JSONObject) ja.get( i );
                String title = jo.getString( "title" );
                String meta = jo.getString( "meta" );
                String name = jo.getString( "icon" );
                String lat = jo.getString( "lat" );
                String lon = jo.getString( "lon" );

                JsonObject obj = new JsonObject();
                obj.setName( name );
                obj.setMeta( meta );
                obj.setLat( Double.parseDouble( lat ) );
                obj.setLng( Double.parseDouble( lon ) );
                obj.setRaw( title );

                if (name.equals( "a" ))
                    obj.setIcon( R.drawable.black_a,  R.drawable.black_a_l );
                else if (name.equals( "b" ))
                    obj.setIcon( R.drawable.orange_b,  R.drawable.orange_b_l );
                else if (name.equals( "c" ))
                    obj.setIcon( R.drawable.pink_c,  R.drawable.pink_c_l );
                else if (name.equals( "d" ))
                    obj.setIcon( R.drawable.pink_d,  R.drawable.pink_d_l );
                else if (name.equals( "e" ))
                    obj.setIcon( R.drawable.green_e,  R.drawable.green_e_l );
                else if (name.equals( "f" ))
                    obj.setIcon( R.drawable.blue_f,  R.drawable.blue_f_l );
                else if (name.equals( "g" ))
                    obj.setIcon( R.drawable.blue_g,  R.drawable.blue_g_l );
                else if (name.equals( "h" ))
                    obj.setIcon( R.drawable.orange_h,  R.drawable.orange_h_l );
                else if (name.equals( "i" ))
                    obj.setIcon( R.drawable.pink_i,  R.drawable.pink_i_l );
                else if (name.equals( "j" ))
                    obj.setIcon( R.drawable.green_j,  R.drawable.green_j_l );
                else if (name.equals( "k" ))
                    obj.setIcon( R.drawable.grey_k,  R.drawable.grey_k_l );
                else if (name.equals( "l" ))
                    obj.setIcon( R.drawable.blue_l,  R.drawable.blue_l_l );
                else if (name.equals( "m" ))
                    obj.setIcon( R.drawable.pink_m,  R.drawable.pink_m_l );
                else if (name.equals( "n" ))
                    obj.setIcon( R.drawable.grey_n,  R.drawable.grey_n_l );
                else if (name.equals( "o" ))
                    obj.setIcon( R.drawable.grey_o,  R.drawable.grey_o_l );
                else if (name.equals( "p" ))
                    obj.setIcon( R.drawable.orange_p,  R.drawable.orange_p_l );
                else if (name.equals( "q" ))
                    obj.setIcon( R.drawable.green_q,  R.drawable.green_q_l );
                else if (name.equals( "r" ))
                    obj.setIcon( R.drawable.blue_r,  R.drawable.blue_r_l );
                else if (name.equals( "s" ))
                    obj.setIcon( R.drawable.gold_s,  R.drawable.orange_s_l );
                else if (name.equals( "t" ))
                    obj.setIcon( R.drawable.blue_t,  R.drawable.blue_t_l );
                else if (name.equals( "u" ))
                    obj.setIcon( R.drawable.pink_u,  R.drawable.pink_u_l );
                else if (name.equals( "v" ))
                    obj.setIcon( R.drawable.green_v,  R.drawable.green_v_l );
                else if (name.equals( "w" ))
                    obj.setIcon( R.drawable.pink_w,  R.drawable.pink_w_l );
                else if (name.equals( "x" ))
                    obj.setIcon( R.drawable.blue_x,  R.drawable.blue_x_l );
                else if (name.equals( "y" ))
                    obj.setIcon( R.drawable.gold_y,  R.drawable.gold_y_l );
                else if (name.startsWith( "z" ))
                    obj.setIcon( R.drawable.green_z,  R.drawable.green_z_l );
                else
                    obj.setIcon( R.drawable.red_blank,  R.drawable.red_blank_l);

                if (! obj.getName().startsWith( "48@" ))
                {
                    objectList.add( obj );
                }
            }
        }
        catch ( Exception e )
        {

            e.printStackTrace();
        }

        return objectList;
    }

    @Override
    public void deliverResult( List<JsonObject> data )
    {
        if (isReset())
        {
            onReleaseResources( data );
            return;
        }

        List<JsonObject> oldData = this.currentList;
        this.currentList = data;

        if (isStarted()) super.deliverResult( data );
        if (oldData != null && oldData != data) onReleaseResources( oldData );
    }

    @Override
    protected void onStartLoading()
    {
        if (this.currentList != null) deliverResult( this.currentList );
        if (observer == null) observer = new JsonObserver( this );
        if (takeContentChanged() || this.currentList == null) forceLoad();
    }

    @Override
    protected void onStopLoading()
    {
        cancelLoad();
    }


    @Override
    protected void onReset()
    {
        onStopLoading();
        if (this.currentList != null)
        {
            onReleaseResources( this.currentList );
            this.currentList = null;
        }

        if (this.observer != null) this.observer = null;
    }

    @Override
    public void onCanceled( List<JsonObject> data )
    {
        super.onCanceled( data );
        onReleaseResources( data );
    }

	/*--------------------------------------------
	|    N O N - P U B L I C    M E T H O D S   |
	============================================*/

    protected void onReleaseResources( List<JsonObject> data )
    {
        this.currentList = null;
    }

	/*--------------------------------------------
	|  A C C E S S O R S / M O D I F I E R S    |
	============================================*/

    public double getLat()
    {
        return lat;
    }

    public void setLat( double lat )
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng( double lng )
    {
        this.lng = lng;
    }

	/*--------------------------------------------
	|       I N L I N E    C L A S S E S        |
	============================================*/

    public static class JsonObject
    {
        String name;
        String meta;
        double lat;
        double lng;
        private int icon;
        private int lrg_icon;
        String raw;
        Marker marker = null;

        public JsonObject()
        {
        }

        public String getRaw()
        {
            return raw;
        }

        public void setRaw( String raw )
        {
            this.raw = raw;
        }

        public double getLat()
        {
            return lat;
        }

        public void setLat( double lat )
        {
            this.lat = lat;
        }

        public double getLng()
        {
            return lng;
        }

        public void setLng( double lng )
        {
            this.lng = lng;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }


        public String getMeta()
        {
            return meta;
        }

        public void setMeta( String meta )
        {
            this.meta = meta;
        }



        public int getIcon()
        {
            return icon;
        }

        public int getLrgIcon()
        {
            return lrg_icon;
        }



        public void setIcon( int icon)
        {
            this.icon = icon;
        }

        public void setIcon( int icon, int lrg_icon)
        {
            this.icon = icon;
            this.lrg_icon = lrg_icon;
        }

        public Marker getMarker()
        {
            return marker;
        }

        public void setMarker( Marker marker )
        {
            this.marker = marker;
        }

    }


}
