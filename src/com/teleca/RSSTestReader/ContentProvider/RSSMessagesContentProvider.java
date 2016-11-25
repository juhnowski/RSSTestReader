package com.teleca.RSSTestReader.ContentProvider;

import java.util.HashMap;
import com.teleca.RSSTestReader.RSSMessages.RSSMessage.RSSMessages;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/** Content provider for RSS messages
* @see android.content.ContentProvider
* @author ruinilyu
*
*/
public class RSSMessagesContentProvider extends ContentProvider {

    /**
     * Tag of the class {@value}
     */
    private static final String TAG = "RSSMessagesContentProvider";
    
    /**
     * Database name {@value}
     */    
    private static final String DATABASE_NAME = "msgs.db";
    
    /**
     * Table name {@value}
     */    
    private static final String MSGS_TABLE_NAME = "msgs";
    
    /**
     * Database version {@value}
     */
    private static final int DATABASE_VERSION = 25;
    
    /**
     * Create table SQL statement {@value}
     */
    private static final String CREATE_TABLE_MSGS = "create table msgs (_id long primary key , "
        + "title text, url text, des text, created_time date);";
    
    private static final int MSGS = 2;
    /**
     * Authority string {@value}
     */
    public static final String AUTHORITY = 
            "com.teleca.RSSTestReader.ContentProvider.RSSMessagesContentProvider";
    
    private static UriMatcher sUriMatcher;
    private static HashMap<String, String> sMsgsProjectionMap;
    private DatabaseHelper dbHelper;
    
    /**
     * Database helper for create and update database actions
     * @see android.database.sqlite.SQLiteOpenHelper
     * @author ruinilyu
     *
     */    
    private static class DatabaseHelper extends SQLiteOpenHelper {
                DatabaseHelper(Context context) {
                    super(context, DATABASE_NAME, null, DATABASE_VERSION);
                }
         
                @Override
                public void onCreate(SQLiteDatabase db) {
                    db.execSQL(CREATE_TABLE_MSGS);
                }
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                            + ", which will destroy all old data");
                    db.execSQL("DROP TABLE IF EXISTS " + MSGS_TABLE_NAME);
                    onCreate(db);
                }
            }

    /**
     * Delete RSS message from database
     * @param uri content providers URI
     * @param where where condition for SQL statement
     * @param whereArgs values for where condition
     * @return count of deleted rows
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) 
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) 
        {
            case MSGS:
                      count = db.delete(MSGS_TABLE_NAME, where, whereArgs);
                      break;
            default:
                      throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Get type
     * @param uri content providers URI
     * @return content type 
     * @throws IllegalArgumentException if the uri is incorrect
     */
    @Override
    public String getType(Uri uri) 
    {
        switch (sUriMatcher.match(uri)) 
        {
            case MSGS:
                return RSSMessages.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * Insert new RSS feed to database
     * @param uri content providers URI
     * @param initialValues values of inserted record fields 
     * @throws SQLException when insert operation is failed
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) 
    {
        if (sUriMatcher.match(uri) != MSGS) { throw new IllegalArgumentException("Unknown URI " + uri); }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(MSGS_TABLE_NAME, RSSMessages.MSG_ID, values);
        
        if (rowId > 0) 
        {
            Uri msgUri = ContentUris.withAppendedId(RSSMessages.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(msgUri, null);
            return msgUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
                     
    }

    /**
     * Called when the RSSMessagesContentProvider is first created.
     * @return true when DatabaseHelper is successfully created
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * Query action
     * @param uri content providers URI
     * @param projection list of queried fields
     * @param selection select condition
     * @param selectionArgs array of arguments for selection
     * @param sortOrder sort order condition
     * @return cursor, result of the query
     */    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) 
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        switch (sUriMatcher.match(uri)) {
           case MSGS:
               qb.setTables(MSGS_TABLE_NAME);
               qb.setProjectionMap(sMsgsProjectionMap);
               break;
           default:
               throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * Update RSS feed in database
     * @param uri content providers URI
     * @param values content values
     * @param where where condition for SQL statement
     * @param whereArgs values for where condition
     * @return count of updated rows
     */        
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case MSGS:
                count = db.update(MSGS_TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Static initialization of UriMatcher instance
     */    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, MSGS_TABLE_NAME, MSGS);
        sMsgsProjectionMap = new HashMap<String, String>();
        sMsgsProjectionMap.put(RSSMessages.MSG_ID, RSSMessages.MSG_ID);
        sMsgsProjectionMap.put(RSSMessages.TITLE, RSSMessages.TITLE);
        sMsgsProjectionMap.put(RSSMessages.DESCRIPTION, RSSMessages.DESCRIPTION);
        sMsgsProjectionMap.put(RSSMessages.URL, RSSMessages.URL);
        sMsgsProjectionMap.put(RSSMessages.CREATED_TIME, RSSMessages.CREATED_TIME);
    }    
   
}
