package com.teleca.RSSTestReader.Feeds;
import static com.teleca.RSSTestReader.Feeds.Feed.Feeds;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Feeds Wrapper class
 * @author ruinilyu
 *
 */
public class FeedsDB {
    
    private static final FeedsDB  sInstance = new FeedsDB();

    /**
     * Singleton factory method
     * @return the instance
     */
    public static FeedsDB  getInstance() {
        return sInstance;
    }

    /**
     * Add new feed
     * @param contentResolver content resolver
     * @param feed added feed
     */
    public void addNewFeed(ContentResolver contentResolver, Feed feed) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(Feeds.TITLE, feed.getTitle());
        contentValue.put(Feeds.URL, feed.getLink().toString());
        Uri u = contentResolver.insert(Feeds.CONTENT_URI, contentValue);
        Log.i("FeedsDB","addNewFeed URI="+u.toString() + "Feed:" + feed.toString());
    }

    /**
     *  Update feed
     * @param contentResolver content resolver
     * @param feed updated feed
     */
    public void updateFeed(ContentResolver contentResolver, Feed feed) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(Feeds.URL, feed.getLink().toString());
        contentValue.put(Feeds.TITLE, feed.getTitle());
        contentResolver.update(Feeds.CONTENT_URI, contentValue, Feeds.FEED_ID + "=" + feed.getId(), null);
    }
    
    /**
     * Set updated time
     * @param contentResolver content resolver
     * @param feed updated feed
     */
    public void updateUpdatedTime(ContentResolver contentResolver, Feed feed) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(Feeds.UPDATED_TIME, feed.getSQLDate());
        contentResolver.update(Feeds.CONTENT_URI, contentValue, Feeds.URL + "=" + feed.getLink(), null);
    }
    
    /**
     * Delete feed
     * @param contentResolver content resolver
     * @param feed deleted feed
     */
    public void deleteFeed(ContentResolver contentResolver, Feed feed) {
        int delete = contentResolver.delete(Feeds.CONTENT_URI, Feeds.FEED_ID + "="+ feed.getId(), null);
        System.out.println("DELETED " + delete + " RECORDS FROM CONTACTS DB");
    }
    
    /**
     * Get feed list
     * @param contentResolver content resolver
     * @return list of feeds
     */
    public List<Feed> getFeedList(ContentResolver contentResolver) {
        List<Feed> ret = new ArrayList<Feed>();
        String[] projection = new String[] { Feeds.FEED_ID, Feeds.TITLE, Feeds.URL };

        Cursor cursor = contentResolver.query(Feeds.CONTENT_URI, projection,  Feeds.FEED_ID +">0", null, null);
        Log.i("FeedsDB","cursor.getCount()="+cursor.getCount());
        
        if (cursor.moveToFirst()) {
            do {
                ret.add(new Feed(cursor.getInt(cursor.getColumnIndex(Feeds.FEED_ID)),
                        cursor.getString(cursor.getColumnIndex(Feeds.TITLE)),
                        cursor.getString(cursor.getColumnIndex(Feeds.URL))));                
            } while(cursor.moveToNext());
        }

        cursor.close();
        return ret;
    }
}
