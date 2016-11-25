package com.teleca.RSSTestReader.ContentProvider;

import java.util.HashMap;

import com.teleca.RSSTestReader.Feeds.Feed.Feeds;

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

/**
 * Content provider for RSS feeds instance
 * @see android.content.ContentProvider
 * @author ruinilyu
 *
 */
public class FeedsContentProvider extends ContentProvider {

    /**
     * Tag of the class {@value}
     */
    private static final String TAG = "FeedsContentProvider";
    
    /**
     * Database name {@value}
     */
    private static final String DATABASE_NAME = "rssdb.db";
    
    /**
     * Table name {@value}
     */
    private static final String FEEDS_TABLE_NAME = "feeds";
    
    /**
     * Database version {@value}
     */
    private static final int DATABASE_VERSION = 2;
    
    /**
     * Create table SQL statement {@value}
     */
    private static final String CREATE_TABLE_FEEDS = 
            "create table feeds (feed_id integer primary key autoincrement , "
            + "title text not null, url text not null, updated_time date);";
    
    
    private static final int FEEDS = 1;
    
    /**
     * Authority {@value}
     */
    public static final String AUTHORITY = 
            "com.teleca.RSSTestReader.ContentProvider.FeedsContentProvider";
    private static UriMatcher sUriMatcher;
    private static HashMap<String, String> sFeedsProjectionMap;

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
                
                /**
                 * Called when the database is first created.
                 * @param db SQLiteDatabase instance
                 */
                @Override
                public void onCreate(SQLiteDatabase db) {
                    db.execSQL(CREATE_TABLE_FEEDS);
                }
                
                /**
                 * Called when the databases version is changed.
                 * @param db SQLiteDatabase instance
                 * @param oldVersion old version
                 * @param newVersion
                 * @see com.teleca.RSSTestReader.ContentProvider.FeedsContentProvider.DATABASE_VERSION
                 */
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                            + ", which will destroy all old data");
                    db.execSQL("DROP TABLE IF EXISTS " + FEEDS_TABLE_NAME);
                    onCreate(db);
                }
            }
    /**
     * Empty constructor
     */
    public FeedsContentProvider() {    }

    /**
     * Delete RSS feed from database
     * @param uri content providers URI
     * @param where where condition for SQL statement
     * @param whereArgs values for where condition
     * @return count of deleted rows
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        
        int count;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        switch (sUriMatcher.match(uri)) 
        {
            case FEEDS:
                      count = db.delete(FEEDS_TABLE_NAME, where, whereArgs);
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
            case FEEDS:
                return Feeds.CONTENT_TYPE;
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
        if (sUriMatcher.match(uri) != FEEDS) {
            throw new IllegalArgumentException("Unknown URI " + uri); 
        }
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } 
        else {
            values = new ContentValues();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(FEEDS_TABLE_NAME, Feeds.TITLE, values);
        
        if (rowId > 0) {
            Uri feedUri = ContentUris.withAppendedId(Feeds.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(feedUri, null);
            return feedUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
                     
    }

    /**
     * Called when the FeedsContentProvider is first created.
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
            String[] selectionArgs, String sortOrder) {
        
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
           case FEEDS:
               qb.setTables(FEEDS_TABLE_NAME);
               qb.setProjectionMap(sFeedsProjectionMap);
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
            case FEEDS:
                count = db.update(FEEDS_TABLE_NAME, values, where, whereArgs);
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
                sUriMatcher.addURI(AUTHORITY, FEEDS_TABLE_NAME, FEEDS);
                sFeedsProjectionMap = new HashMap<String, String>();
                sFeedsProjectionMap.put(Feeds.FEED_ID, Feeds.FEED_ID);
                sFeedsProjectionMap.put(Feeds.TITLE, Feeds.TITLE);
                sFeedsProjectionMap.put(Feeds.URL, Feeds.URL);
                sFeedsProjectionMap.put(Feeds.UPDATED_TIME, Feeds.UPDATED_TIME);
            }
    
}
