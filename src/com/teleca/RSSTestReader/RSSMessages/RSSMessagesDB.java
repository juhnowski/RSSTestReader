package com.teleca.RSSTestReader.RSSMessages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.teleca.RSSTestReader.RSSMessages.RSSMessage.RSSMessages;

/**
 * RSSMessages Wrapper class
 * @author ruinilyu
 *
 */
public class RSSMessagesDB 
{
    private static final RSSMessagesDB  sInstance = new RSSMessagesDB();
    public static final String[] sProjection = new String[] {RSSMessages.MSG_ID, RSSMessages.TITLE, RSSMessages.URL, RSSMessages.DESCRIPTION, RSSMessages.CREATED_TIME};
    
    /**
     * Singleton factory method
     * @return the instance
     */
    public static RSSMessagesDB  getInstance() {
        return sInstance;
    }    
    
    /**
     * Add new RSS message 
     * @param contentResolver content resolver
     * @param msg RSS message
     */
    public void addNewRSSMessage(ContentResolver contentResolver, RSSMessage msg) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(RSSMessages.MSG_ID, msg.hashCode());
        contentValue.put(RSSMessages.TITLE, msg.getTitle());
        contentValue.put(RSSMessages.URL, msg.getLink().toString());
        contentValue.put(RSSMessages.DESCRIPTION, msg.getDescription());
        contentValue.put(RSSMessages.CREATED_TIME, msg.getSQLDate());
        contentResolver.insert(RSSMessages.CONTENT_URI, contentValue);
    }    
    
    /**
     * Delete RSS message 
     * @param contentResolver content resolver
     * @param msg RSS message
     */
    public void deleteRSSMessage(ContentResolver contentResolver, RSSMessage msg) {
        contentResolver.delete(RSSMessages.CONTENT_URI, RSSMessages.MSG_ID + "="+ msg.getId(), null);
    }    
    
    /**
     * Get RSS message cursor
     * @param contentResolver content resolver
     * @return cursor
     */
    public Cursor getRSSMessageCursor(ContentResolver contentResolver) {
        return contentResolver.query(RSSMessages.CONTENT_URI, sProjection,  null, null, null);
    }
    
    /**
     * Check message exist in database
     * @param contentResolver content resolver
     * @param msg RSS message
     * @return true when exist
     */
    public boolean isRSSMessageInDB(ContentResolver contentResolver, RSSMessage msg) {
        boolean ret = false;
        Cursor cursor = contentResolver.query(RSSMessages.CONTENT_URI, null, RSSMessages.MSG_ID + "=" + String.valueOf(msg.hashCode()), null, null);
        if (null != cursor && cursor.moveToNext()) {
            ret = true;
        }
        cursor.close();
        return ret;
    }
    
    
}
