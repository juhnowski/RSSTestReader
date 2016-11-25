package com.teleca.RSSTestReader.Feeds;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.teleca.RSSTestReader.ContentProvider.FeedsContentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * RSS Feed class.
 * @author ruinilyu
 *
 */
public class Feed 
{
    private String mTitle;
    private URL mLink;
    private Date mDate;
    private int mId;
    
    /**
     * Date formatter EEE, dd MMM yyyy HH:mm:ss
     */
    static SimpleDateFormat FORMATTER = 
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
    
    /**
     * SQL date formatter yyyy-MM-dd
     */    
    static SimpleDateFormat SQLFORMATTER = 
            new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor.<br> By default: title = "empty" and link = "http://"
     */
    public Feed() {
        this.mTitle = "empty";
        this.setLink("http://");
    }
    
    /**
     * Constructor.
     * @param title Title
     * @param link Link
     */
    public Feed(String title, String link) {
        this.mTitle = title;
        this.setLink(link);
    }

    /**
     * Constructor.
     * @param id ID
     * @param title Title
     * @param link Link
     */
    public Feed(int id, String title, String link) {
        this.mId = id;
        this.mTitle = title;
        this.setLink(link);
    }
    
    /**
     * @return string in format:<br> <b>Title<br>Link</b>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTitle());
        sb.append('\n');
        sb.append(this.getLink());
        
        return sb.toString(); 
    }
    /**
     * Get ID
     * @return ID
     */
    public int getId()    {
        return mId;
    }
    
    /**
     * Set ID
     * @param id
     */
    public void setId(int id) {
        this.mId = id;
    }
    
    /**
     * Get RSS feed link
     * @return RSS feed link
     * @see URL
     */
    public URL getLink() {
        return mLink;
    }
    
    /**
     * Set RSS feed link
     * @param link RSS feed link
     */
    public void setLink(String link) {
        try {
            this.mLink = new URL(link);
        }
        catch (MalformedURLException e)    {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get title
     * @return Title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set title
     * @param title Title
     */
    public void setTitle(String title) {
        this.mTitle = title.trim();
    }    
    
    /**
     * Get date of last update
     * @return pure date
     */
    public Date getNoneFormatDate() {
        return mDate;
    }
    
    /**
     * Get date of last update
     * @return pure date in FORMATTER format
     */    
    public String getDate()    {
        String res ="";
        try    {
            res = FORMATTER.format(this.mDate);
        } 
        catch (Exception e){
            res = "Unknown date";
        }
        return res;
    }

    /**
     * Get date of last update
     * @return pure date in SQLFORMATTER format
     */        
    public String getSQLDate() {
        String res ="";
        try    {
            res = SQLFORMATTER.format(this.mDate);
        }
        catch (Exception e)    {
            res = "null";
        }
        return res;
    }    
    
    /**
     * Set the date of last update
     * @param date date in SQL format
     */
    public void setSQLDate(String date)    {
        try    {
            this.mDate = SQLFORMATTER.parse(date);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }        
    }
    
    /**
     * Set the date of last update
     * @param date date in FORMATTER format
     */
    public void setDate(String date) {
        try {
            this.mDate = FORMATTER.parse(date.trim());
        }
        catch (ParseException e){
            throw new RuntimeException(e);
        }
    }    
    
    /**
     * Override hash code of instance
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mLink == null) ? 0 : mLink.hashCode());
        result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
        return result;
    }
    
    /**
     * Override equals
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Feed other = (Feed) obj;
        if (mLink == null) {
            if (other.mLink != null)
                return false;
        } else if (!mLink.equals(other.mLink))
            return false;
        if (mTitle == null) {
            if (other.mTitle != null)
                return false;
        } else if (!mTitle.equals(other.mTitle))
            return false;
        return true;
    }

    /**
     * @deprecated
     * @param another
     * @return
     */
    public int compareTo(Feed another) {
        if (another == null) return 1;
        return another.mLink.toString().compareTo(mLink.toString());
    } 
    
    /**
     * Feeds class.
     * @author ruinilyu
     *
     */
    public static final class Feeds implements BaseColumns {
        private Feeds() { }
        
        /**
         * Content URI constant {@value} 
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"+ FeedsContentProvider.AUTHORITY + "/feeds");

        /**
         * Content type constant {@value} 
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.teleca.feeds";
        
        /**
         * Feed ID field name {@value} 
         */
        public static final String FEED_ID = "feed_id";

        /**
         * Title field name {@value} 
         */
        public static final String TITLE = "title";
        
        /**
         * URL field name {@value} 
         */
        public static final String URL = "url";
        
        /**
         * Updated time field name {@value} 
         */
        public static final String UPDATED_TIME ="updated_time";
    }
}
