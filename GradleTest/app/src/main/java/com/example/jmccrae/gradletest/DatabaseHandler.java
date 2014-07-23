package com.example.jmccrae.gradletest;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_FORMCONTENT = "contacts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_LOCPROJ = "locproj";
    private static final String KEY_LOCNAME = "locname";
    private static final String KEY_LOCDEPTH = "locdepth";
    private static final String KEY_LOCPRIORITY = "locpriority";
    private static final String KEY_LOCDESC = "locdesc";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_GPSLAT = "gpslat";
    private static final String KEY_GPSLON = "gpslon";
    private static final String KEY_ULAT = "ulat";
    private static final String KEY_ULON = "ulon";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE_FORMCONTENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER + " TEXT,"
                + KEY_LOCPROJ + " TEXT,"
                + KEY_LOCNAME + " TEXT,"
                + KEY_LOCDEPTH + " TEXT,"
                + KEY_LOCPRIORITY + " TEXT,"
                + KEY_LOCDESC + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_GPSLAT + " TEXT,"
                + KEY_GPSLON + " TEXT,"
                + KEY_ULAT + " TEXT,"
                + KEY_ULON + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMCONTENT);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addContact(FormModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER, contact._id); // Contact Phone
        values.put(KEY_LOCPROJ, contact._proj); // Contact Phone
        values.put(KEY_LOCNAME, contact._locname); // Contact Name
        values.put(KEY_LOCDEPTH, contact._locdepth); // Contact Name
        values.put(KEY_LOCPRIORITY, contact._locpriority); // Contact Name
        values.put(KEY_LOCDESC, contact._locdesc); // Contact Phone
        values.put(KEY_IMAGE, contact._image); // Contact Phone
        values.put(KEY_DATE, contact._date); // Contact Phone
        values.put(KEY_TIME, contact._time); // Contact Phone
        values.put(KEY_GPSLAT, contact._gpslat); // Contact Phone
        values.put(KEY_GPSLON, contact._gpslon); // Contact Phone
        values.put(KEY_ULAT, contact._ulat); // Contact Phone
        values.put(KEY_ULON, contact._ulon); // Contact Phone

        // Inserting Row
        db.insert(TABLE_FORMCONTENT, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    FormModel getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FORMCONTENT, new String[] { KEY_ID, KEY_USER, KEY_LOCPROJ, KEY_LOCNAME, KEY_LOCDEPTH, KEY_LOCPRIORITY, KEY_LOCDESC, KEY_IMAGE, KEY_DATE, KEY_TIME, KEY_GPSLAT, KEY_GPSLON, KEY_ULAT, KEY_ULON}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FormModel contact = new FormModel(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
        // return contact
        return contact;
    }

    // Getting All Contacts
    public List<FormModel> getAllContacts() {
        List<FormModel> contactList = new ArrayList<FormModel>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_FORMCONTENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);




        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FormModel contact = new FormModel();
                contact._id = Integer.parseInt(cursor.getString(0));
                contact._user = cursor.getString(1);
                contact._proj = cursor.getString(2);
                contact._locname = cursor.getString(3);
                contact._locdepth = cursor.getString(4);
                contact._locpriority = cursor.getString(5);
                contact._locdesc = cursor.getString(6);
                contact._image = cursor.getString(7);
                contact._date = cursor.getString(8);
                contact._time = cursor.getString(9);
                contact._gpslat = cursor.getString(10);
                contact._gpslon = cursor.getString(11);
                contact._ulat = cursor.getString(12);
                contact._ulon = cursor.getString(13);

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(FormModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER, contact._user); // Contact Phone
        values.put(KEY_LOCPROJ, contact._proj); // Contact Phone
        values.put(KEY_LOCNAME, contact._locname); // Contact Name
        values.put(KEY_LOCDEPTH, contact._locdepth); // Contact Name
        values.put(KEY_LOCPRIORITY, contact._locpriority); // Contact Name
        values.put(KEY_LOCDESC, contact._locdesc); // Contact Phone
        values.put(KEY_IMAGE, contact._image); // Contact Phone
        values.put(KEY_DATE, contact._date); // Contact Phone
        values.put(KEY_TIME, contact._time); // Contact Phone
        values.put(KEY_GPSLAT, contact._gpslat); // Contact Phone
        values.put(KEY_GPSLON, contact._gpslon); // Contact Phone
        values.put(KEY_ULAT, contact._ulat); // Contact Phone
        values.put(KEY_ULON, contact._ulon); // Contact Phone);

        // updating row
        return db.update(TABLE_FORMCONTENT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact._id) });
    }

    // Deleting single contact
    public void deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_FORMCONTENT, KEY_ID + " = ?",
        // new String[] {id} );
        //new String[] { String.valueOf(contact.getID()) });

        getWritableDatabase().delete(TABLE_FORMCONTENT, KEY_ID + "="+id,null);

        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_FORMCONTENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
        cursor.moveToFirst();
        // return count
        return cursor.getCount();
    }

}