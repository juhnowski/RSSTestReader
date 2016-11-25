package com.teleca.RSSTestReader.Service;

import java.util.ArrayList;
import java.util.List;
import com.teleca.RSSTestReader.Feeds.Feed;
import com.teleca.RSSTestReader.Feeds.FeedsDB;
import com.teleca.RSSTestReader.Parser.RSSParser;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * RSS update service
 * @author ruinilyu
 *
 */
public class RSSUpdateService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private FeedsDB mFdb = FeedsDB.getInstance();
    private List<Feed> mRssUrlList = new ArrayList<Feed>();
    
    /**
     * LocalBinder class
     * @author ruinilyu
     *
     */
    public class LocalBinder extends Binder
    {
        /**
         * Get service
         * @return service instance
         */
        RSSUpdateService getService() 
        {
            return RSSUpdateService.this;        
        }    
    }    
    
    /**
     * Override onBind method
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    /**
     * Override onStartCommand method
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {        
        startUpdater();
        Log.i("RSSUpdateService", "Received start id " + startId + ": " + intent);
        return START_STICKY;    
    }    
    
    /**
     * Start update RSS messages
     */
    private void startUpdater() {
        mRssUrlList = mFdb.getFeedList(getContentResolver());
        
           RSSParser parser = new RSSParser();
           parser.setContentResolver(getContentResolver());
        for (Feed url : mRssUrlList){
            try    {
                    parser.setRssUrl(url.getLink().toString());
                    parser.parse();
                }
                catch(Exception e) {
                    Log.i("RSSTestReader",e.toString());
                }
            }
    }
}


