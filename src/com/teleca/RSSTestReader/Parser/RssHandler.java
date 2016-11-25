package com.teleca.RSSTestReader.Parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.teleca.RSSTestReader.RSSMessages.RSSMessage;
import com.teleca.RSSTestReader.RSSMessages.RSSMessagesDB;
import android.content.ContentResolver;
import android.util.Log;
import static com.teleca.RSSTestReader.Parser.RSSParser.*;

/**
 * Basic RSS Handler
 * @author ruinilyu
 *
 */
public class RssHandler extends DefaultHandler
{
    private RSSMessage mCurrentMessage;
    private StringBuilder mBuilder;
    private ContentResolver mCr;
    public static final String STOP_PARSER_STRING = "Stop parser";
    RSSMessagesDB mRdb = RSSMessagesDB.getInstance();
    
    /**
     * Override characters method
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        mBuilder.append(ch, start, length);
    }

    /**
     * Handle end element
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);
        if (this.mCurrentMessage != null){
            if (localName.equalsIgnoreCase(TITLE)){
                mCurrentMessage.setTitle(mBuilder.toString());
            } else if (localName.equalsIgnoreCase(LINK)){
                mCurrentMessage.setLink(mBuilder.toString());
            } else if (localName.equalsIgnoreCase(DESCRIPTION)){
                mCurrentMessage.setDescription(mBuilder.toString());
                
            } else if (localName.equalsIgnoreCase(PUB_DATE)){
                mCurrentMessage.setDate(mBuilder.toString());
            } else if (localName.equalsIgnoreCase(ITEM)){
                
                if (!mRdb.isRSSMessageInDB(getContentResolver(), mCurrentMessage)) {
                    mRdb.addNewRSSMessage(getContentResolver(), mCurrentMessage);
                    Log.i("RssHandler","Added:" + mCurrentMessage);
                }
                else {
                    /*
                     * Stop parsing. We already have following messages.
                     */
                    throw new SAXException(STOP_PARSER_STRING);
                }
            }
            mBuilder.setLength(0);    
        }
    }

    /**
     * Handle start document
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        mBuilder = new StringBuilder();
    }

    /**
     * Handle start element
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) 
            throws SAXException    {
        
        super.startElement(uri, localName, name, attributes);
        
        if (localName.equalsIgnoreCase(ITEM)) {
            mBuilder = new StringBuilder();
            this.mCurrentMessage = new RSSMessage();
        }
        //Log.i("RssHandler","localName=" + localName +);
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
