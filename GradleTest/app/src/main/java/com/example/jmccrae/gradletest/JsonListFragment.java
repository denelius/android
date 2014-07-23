package com.example.jmccrae.gradletest;


import java.util.List;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jmccrae.gradletest.JsonLoader.JsonObject;

public class JsonListFragment extends ListFragment
{
	/*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/

	/*--------------------------------------------
	|    I N S T A N C E   V A R I A B L E S    |
	============================================*/

    private JsonSelectedHandler selectedHandler;
    private JsonObjectListViewAdapater adapter;

	/*--------------------------------------------
	|         C O N S T R U C T O R S           |
	============================================*/

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );
        LayoutInflater.from( getActivity() );
        getListView().setDividerHeight( 1 );

        this.adapter = new JsonObjectListViewAdapater( getActivity(), R.layout.list_row );
        setListAdapter( this.adapter );
    }

    @Override
    public View onCreateView( LayoutInflater inflataer, ViewGroup container, Bundle savedInstanceState )
    {
        return inflataer.inflate( R.layout.list_view, container );

    }

	/*--------------------------------------------
	|   P U B L I C    A P I    M E T H O D S   |
	============================================*/

    @Override
    public void onAttach( Activity activity )
    {
        super.onAttach( activity );

        try
        {
            this.selectedHandler = (JsonSelectedHandler) activity;
        }
        catch ( ClassCastException e )
        {
            throw new ClassCastException( activity.toString() + " must implement JsonSelectedHandler" );
        }

    }

    @Override
    public void onListItemClick( ListView l, View v, int position, long id )
    {
        JsonObject selection = (JsonObject) getListAdapter().getItem( position );
        this.selectedHandler.onSelected( selection );
    }

    public void setJsonObjectList( List<JsonObject> objectList )
    {
        if (this.adapter != null)
        {
            this.adapter.clear();
            this.adapter.addAll( objectList );
        }
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

    private class JsonObjectListViewAdapater extends ArrayAdapter<JsonObject>
    {

        private Context context;
        private int layoutId;

        public JsonObjectListViewAdapater( Context context, int textViewResourceId )
        {
            super( context, textViewResourceId );

            this.context = context;
            this.layoutId = textViewResourceId;

            setNotifyOnChange( true );
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent )
        {
            View row = convertView;
            JsonObjectRowWrapper wrapper;

            if (row != null)
            {
                wrapper = (JsonObjectRowWrapper) row.getTag();
            }
            else
            {
                row = LayoutInflater.from( this.context ).inflate( this.layoutId, parent, false );
                wrapper = new JsonObjectRowWrapper( row );
                row.setTag( wrapper );
            }

            int backgroundRes = R.drawable.app_list_selector;
            if (position % 2 == 0) backgroundRes = R.drawable.app_list_selector_alt;
            wrapper.getBackground().setBackgroundResource( backgroundRes );

            JsonObject object = getItem( position );

            SpannableString str = new SpannableString( object.getMeta() );
            str.setSpan( new BulletSpan(), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            wrapper.getName().setText( str, TextView.BufferType.SPANNABLE );

            wrapper.getDetail().setText( object.getRaw() );
            ( (ImageView) row.findViewById( R.id.icon ) ).setImageResource( object.getLrgIcon() );

            //ImageView img = new ImageView(this);
            //img.setImageResource(R.drawable.my_image);

            return row;
        }

    }

    private class JsonObjectRowWrapper
    {
        View base;
        RelativeLayout background;
        TextView name;
        TextView detail;

        public JsonObjectRowWrapper( View base )
        {
            this.base = base;
        }

        public RelativeLayout getBackground()
        {
            if (background == null)
            {
                background = (RelativeLayout) base.findViewById( R.id.list_row );
            }
            return background;
        }

        public TextView getDetail()
        {
            if (detail == null)
            {
                detail = (TextView) base.findViewById( R.id.firstLine );
            }
            return detail;
        }

        public TextView getName()
        {
            if (name == null)
            {
                name = (TextView) base.findViewById( R.id.secondLine );
            }
            return name;
        }
    }

    public interface JsonSelectedHandler
    {
        public void onSelected( JsonObject object );
    }
}
