package com.teleca.RSSTestReader.Activity;

import android.os.Bundle;
import com.teleca.RSSTestReader.R;
import com.teleca.RSSTestReader.CursorAdapter.RSSMessagesCursorAdapter;
import com.teleca.RSSTestReader.RSSMessages.RSSMessagesDB;
import com.teleca.RSSTestReader.RSSMessages.RSSMessage.RSSMessages;
import com.teleca.RSSTestReader.Service.RSSUpdateService;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.os.CountDownTimer;
/**
 * This is a RSS message reader activity. This activity contains a list of rss messages and two menu items:
 *    <ol>
 *  <li>RSS Edit Url - rss feeds list editor.</li>
 *  <li>Refresh - refresh RSS message list.</li>
 *  </ol>
 * @see ListActivity
 * @author ruinilyu
 */
public class RSSTestReaderActivity extends ListActivity {
    private String mPreview = "Preview";
    private String mView = "View";
    private RSSMessagesDB mRdb = RSSMessagesDB.getInstance();
    private int[] mTextViews = new int[] { R.id.title, R.id.description, R.id.created_time };
    public static final String[] PROJECTION = new String[] {RSSMessages.TITLE, RSSMessages.DESCRIPTION, RSSMessages.CREATED_TIME};
    private Cursor mCursor;
    
    /**
     *  Called when the activity is first created. 
     *  @param savedInstanceState saved instance state 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        registerForContextMenu(this.getListView());
        startService(new Intent(this, RSSUpdateService.class));
        refresh();
      //10*60*1000=10min is the starting number (in milliseconds)
      //1 is the number to count down each time (in milliseconds)
        RSSUpdater updater = new RSSUpdater(this,10*60*1000,1);
        updater.start();
    }

    /**
     * Called when the context menu is first created. 
     */
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
        super.onCreateContextMenu(menu, v, menuInfo);  
        menu.setHeaderTitle("View");  
        menu.add(0, v.getId(), 0, mPreview);  
        menu.add(0, v.getId(), 0, mView);  
    }  

       /**
        * Handle context item menu selection
        * @param item selected menu item
        */
       @Override 
       public boolean onContextItemSelected(MenuItem item) {  

            int urlCol = mCursor.getColumnIndex(RSSMessages.URL);
            String url = mCursor.getString(urlCol);
            
            int titleCol = mCursor.getColumnIndex(RSSMessages.TITLE);
            String title = mCursor.getString(titleCol);
            
            int descCol = mCursor.getColumnIndex(RSSMessages.DESCRIPTION);
            String desc = mCursor.getString(descCol);
            
        if(item.getTitle().equals(mPreview)){
            Intent myIntent = new Intent(RSSTestReaderActivity.this, RSSMessagePreview.class);

            myIntent.putExtra("title", title);
            myIntent.putExtra("description", desc);
            myIntent.putExtra("url", url); 
            
            RSSTestReaderActivity.this.startActivity(myIntent);
        }  
        else if(item.getTitle().equals(mView)){
            Intent viewMessage = new Intent(Intent.ACTION_VIEW,    Uri.parse(url));
            this.startActivity(viewMessage);
        }  
        else {return false;}  
    return true;  
    }  
       
    /**
     *  Called when the menu is first created.
     *  @see android.view.Menu
     *  @param menu menu instance 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu, menu);
        return true;
    }
    
    /** Handle item selection. There are two items:
     *    <ol>
      *  <li>RSS Edit Url - rss feeds list editor.</li>
      *  <li>Refresh - refresh RSS message list.</li>
      *  </ol>
     * @param item menu item instance
     * 
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * 
     * @return boolean handler result. true - if the everything is OK otherwise is false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.rss_refresh:
            Log.i("RSSTestReader","Menu select: rss_refresh");
            refresh();
            return true;
        case R.id.rss_edit:
            showRSSUrlEditor();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Call RSSURLList activity
     */
    private void showRSSUrlEditor()
    {
        Log.i("RSSTestReader","rss_edit menu activity is starting");
        Intent myIntent = new Intent(RSSTestReaderActivity.this, RSSURLList.class);

        myIntent.putExtra("title", "Androidster");
        myIntent.putExtra("url", "http://www.androidster.com/android_news.rss"); 
        
        RSSTestReaderActivity.this.startActivity(myIntent);
        Log.i("RSSTestReader","rss_edit menu activity was started");
    }
    /**
     * Refresh RSS message list
     */
    private void refresh() {
        int i = 0;
        int app_timeout = 10;
               
        while ((mRdb.getRSSMessageCursor(getContentResolver()).getCount()==0) && (i++<app_timeout))    {
            try    {
                Thread.sleep(100);
               }
            catch (Exception e)    {
                Log.i("RSSTestReaderActivity", e.toString());
               }
           }
        mCursor = mRdb.getRSSMessageCursor(getContentResolver()); 
        RSSMessagesCursorAdapter adapter = 
                new RSSMessagesCursorAdapter(this, R.layout.simple_cursor, mCursor, PROJECTION, mTextViews);
        setListAdapter(adapter);
        
    }
 
    public class RSSUpdater extends CountDownTimer {
        RSSTestReaderActivity act;
        
        public RSSUpdater(RSSTestReaderActivity act,long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.act = act;
        }
        @Override
        public void onFinish() {
            startService(new Intent(act, RSSUpdateService.class));
            this.start();
        }
        @Override
        public void onTick(long millisUntilFinished) {
            
        }
    }
}