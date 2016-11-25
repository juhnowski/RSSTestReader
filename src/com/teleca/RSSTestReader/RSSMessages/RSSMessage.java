package com.teleca.RSSTestReader.RSSMessages;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.teleca.RSSTestReader.ContentProvider.RSSMessagesContentProvider;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * RSS Message
 * @author ruinilyu
 *
 */
public class RSSMessage implements Comparable<RSSMessage> {
    
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
    
    private long mId;
    private String mTitle;
    private URL mLink;
    private String mDescription="empty";
    private Date mDate;
    
    /**
     * Constructor
     */
    public RSSMessage()    { }
    
    /**
     * Constructor
     * @param id ID
     * @param title Title
     * @param link Content URL
     * @param description Description
     * @param date Produce date
     */
    public RSSMessage(long id, String title, String link, String description, String date) {
        this.mId = id;
        this.setTitle(title);
        this.setLink(link);
        this.setDescription(description);
        this.setSQLDate(date);
    }
    
    /**
     * Get ID
     * @return ID
     */
    public long getId()    {
        return mId;
    }
    
    /**
     * Set ID
     * @param id ID
     */
    public void setId(long id) {
        this.mId = id;
    }
    
    /**
     * Get Title Description statement
     * @return Title Description statement
     */
    public String getTitleDescr() {
        return getDate()+ "\n" +mTitle + "\n" + mDescription;//description.substring(0, description.indexOf(".")+1) 
    }
    
    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set title
     * @param title title
     */
    public void setTitle(String title) {
        this.mTitle = title.trim();
    }

    /**
     * Get content link
     * @return URL
     */
    public URL getLink() {
        return mLink;
    }
    
    /**
     * Set content link
     * @param link URL
     */
    public void setLink(String link) {
        try {
            this.mLink = new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get description
     * @return description description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set description
     * @param description description
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * Get date
     * @return pure date
     */
    public Date getNoneFormatDate()    {
        return mDate;
    }
    
    /**
     * Get date in FORMATTER format
     * @return date in FORMATTER format
     */
    public String getDate() {
        String res ="";
        try {
            res = FORMATTER.format(this.mDate);
        }
        catch (Exception e)    {
            res = "Unknown date";
        }
        return res;
    }

    /**
     * Get date in SQLFORMATTER format
     * @return date in SQLFORMATTER format
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
     *     Set date in SQLFORMATTER format
     * @param date date in SQLFORMATTER format
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
     *     Set date in FORMATTER format
     * @param date date in FORMATTER format
     */
    public void setDate(String date) 
    {
        try    {
            this.mDate = FORMATTER.parse(date.trim());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Override copy() method
     * @return RSSMessage instance
     */
    public RSSMessage copy() {
        RSSMessage copy = new RSSMessage();
        copy.mTitle = mTitle;
        copy.mLink = mLink;
        copy.mDescription = mDescription;
        copy.mDate = mDate;
        return copy;
    }
    
    /**
     * Override toString() method
     */
    @Override
    public String toString() {
        return getTitleDescr();
    }

    /**
     * Override  hashCode() method
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mDate == null) ? 0 : mDate.hashCode());
        result = prime * result
                + ((mDescription == null) ? 0 : mDescription.hashCode());
        result = prime * result + ((mLink == null) ? 0 : mLink.hashCode());
        result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
        return result;
    }
    
    /**
     * Override equals method
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RSSMessage other = (RSSMessage) obj;
        if (mDate == null) {
            if (other.mDate != null)
                return false;
        } else if (!mDate.equals(other.mDate))
            return false;
        if (mDescription == null) {
            if (other.mDescription != null)
                return false;
        } else if (!mDescription.equals(other.mDescription))
            return false;
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
     * Override compareTo method
     */
    public int compareTo(RSSMessage another) {
        if (another == null) return 1;
        return another.mDate.compareTo(mDate);
    } 
    
    /**
     * RSSMessages class
     * @author ruinilyu
     *
     */
    public static final class RSSMessages implements BaseColumns {
        /**
         * Content URI constant {@value} 
         */        
        public static final Uri CONTENT_URI = Uri.parse("content://"+ RSSMessagesContentProvider.AUTHORITY + "/msgs");
        
        /**
         * Content type constant {@value} 
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.teleca.msgs";
        
        /**
         * Message ID field name {@value} 
         */        
        public static final String MSG_ID = "_id";
        
        /**
         * Title field name {@value} 
         */
        public static final String TITLE = "title";
        
        /**
         * URL field name {@value} 
         */
        public static final String URL = "url";
        
        /**
         * Description field name {@value} 
         */
        public static final String DESCRIPTION = "des";
        
        /**
         * Created time field name {@value} 
         */
        public static final String CREATED_TIME ="created_time";
    }
}
