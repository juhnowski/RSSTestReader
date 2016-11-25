package com.teleca.RSSTestReader.Parser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.teleca.RSSTestReader.Activity.RSSURLList;

import android.content.ContentResolver;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;


/**
 * RSS Parser. Parse incoming xml stream.
 * @author ruinilyu
 *
 */
public class RSSParser {

    /**
     * channel XML tag {@value}
     */
    static final String CHANNEL = "channel";
    
    /**
     * pubDate XML tag {@value}
     */
    static final String PUB_DATE = "pubDate";
    
    /**
     * description XML tag {@value}
     */
    static final  String DESCRIPTION = "description";
    
    /**
     * link XML tag {@value}
     */
    static final  String LINK = "link";
    
    /**
     * title XML tag {@value}
     */
    static final  String TITLE = "title";

    /**
     * item XML tag {@value}
     */
    static final  String ITEM = "item";
    
    private ContentResolver mCr;
    private URL rssUrl;
    
    /**
     * Constructor
     * @param rssUrl
     */
    public RSSParser(String rssUrl)    {
        setRssUrl(rssUrl);    
    }

    /**
     * Constructor
     */
    public RSSParser() { }
    
    /**
     * Set RSS URL
     * @param rssUrl RSS URL
     */
    public void setRssUrl(String rssUrl) {
        try {
            this.rssUrl = new URL(rssUrl);
        }
        catch (MalformedURLException e)    {
            throw new RuntimeException(e);
        }            
    }
    
    /**
     * Get input stream
     * @return input stream
     */
    public InputStream getInputStream() {
        try {
            return rssUrl.openConnection().getInputStream();
        } 
        catch (IOException e) {
            Log.i("RSSParse","InputStream: "+e.toString());
            throw new RuntimeException(e);
        }
    }    
    
    /**
     * Parse incoming stream by SAX parser. The implementation is uncompleted. 
     * Only utf-8 and ISO-8859-1 are valid.
     */
    public void parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        InputStream inputStream;
        try {
            SAXParser parser = factory.newSAXParser();
            RssHandler handler = new RssHandler();
            handler.setContentResolver(mCr);
            inputStream = this.getInputStream();
            try {
            parser.parse(inputStream, handler);
            } catch (SAXException ex)
            {
                //  TODO: evaluate encoding. if encoding data is absent in the incoming xml data, try 
                // to parse with frequently used code page
                InputSource is = new InputSource( this.getInputStream());
                is.setEncoding("ISO-8859-1");
                parser.parse(is, handler);
            }
        }
        catch (Exception e)    {
            Log.i("RSSParse","parse: "+e.toString());
        } 
    }
    
    public boolean parse(RSSURLList act) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        InputStream inputStream;
        try {
            SAXParser parser = factory.newSAXParser();
            RssHandler handler = new RssHandler();
            handler.setContentResolver(mCr);
            inputStream = this.getInputStream();
            try {
            parser.parse(inputStream, handler);
            } catch (SAXException ex)
            {
                //  TODO: evaluate encoding. if encoding data is absent in the incoming xml data, try 
                // to parse with frequently used code page
                InputSource is = new InputSource( this.getInputStream());
                is.setEncoding("ISO-8859-1");
                parser.parse(is, handler);
            }
        }
        catch (Exception e)    {
            if (e.getMessage().equals(RssHandler.STOP_PARSER_STRING)){
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }
    /**
     * Get content resolver
     * @return content resolver
     */    
    public ContentResolver getContentResolver()    {
        return mCr;
    }
    
    /**
     * Set content resolver
     * @param contentResolver content resolver
     */    
    public void setContentResolver(ContentResolver contentResolver)    {
        mCr = contentResolver;
    }    
}
