package com.teleca.RSSTestReader.Activity;

import java.net.URL;
import java.util.List;
import com.teleca.RSSTestReader.R;
import com.teleca.RSSTestReader.Feeds.Feed;
import com.teleca.RSSTestReader.Feeds.FeedsDB;
import com.teleca.RSSTestReader.Parser.RSSParser;
import com.teleca.RSSTestReader.Service.RSSUpdateService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This is a RSS url editor. It response following actions for RSS feeds:
 *    <ol>
 *  <li>add</li>
 *  <li>edit</li>
 *  <li>delete</li>
 *  </ol>
 * @see android.app.Activity
 * @author ruinilyu
 */
public class RSSURLList extends Activity 
{
    EditText mEdTitle, mEdUrl;
    ListView mFeedListView;
    FeedsDB mFdb = FeedsDB.getInstance();
    Feed mFd;
    List<Feed> mFeeds;
    AlertDialog.Builder mBuilder, mAddBuilder;
    
    /**
     *  Called when the activity is first created. 
     *  @param savedInstanceState saved instance state 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
        setContentView(R.layout.feed_editor);
        Log.i("RSSURLList","RSSURLList.onCreate");
        mEdTitle = (EditText) this.findViewById(R.id.edTitle);
        mEdUrl = (EditText) this.findViewById(R.id.edUrl);
        mFeedListView = (ListView)this.findViewById(R.id.feedlist);
        refresh();
    
        mFeedListView.setOnItemClickListener(new RSSURLList.OnItemClickListener1());
    
        if (savedInstanceState != null) {
            mEdUrl.setText(savedInstanceState.getString("url"));
            mEdTitle.setText(savedInstanceState.getString("title"));
        }
        else {
            Bundle extras = getIntent().getExtras();
            mEdTitle.setText(extras.getString("title"));
            mEdUrl.setText(extras.getString("url"));
        }
    
  
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }          
        });

        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                okClicked();
            }          
        });    
    
        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Feed feed = new Feed();
                feed.setTitle(mEdTitle.getText().toString());
                feed.setLink(mEdUrl.getText().toString());
                if (mFeeds.contains(feed)) {
                    Toast.makeText(RSSURLList.this, "Current feed already exist.", Toast.LENGTH_SHORT).show();
                } else {
                    if (validateURL(mEdUrl.getText().toString())) {
                    mFdb.addNewFeed(getContentResolver(), feed);
                    refresh();
                    } else {
                        AlertDialog alertAdd = mAddBuilder.create();
                        alertAdd.show();
                    }
                }
            }          
        });    
    
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.i("RSSURLList","Feed " + mFd + " try to update");
                mFd.setTitle(mEdTitle.getText().toString());
                mFd.setLink(mEdUrl.getText().toString());
            
                mFdb.updateFeed(getContentResolver(), mFd);
                Log.i("RSSURLList","Feed " + mFd + " has been updated");
                refresh();
            }          
        });
    
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                AlertDialog alert = mBuilder.create();
                alert.show();
            }
        });
        
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage("Are you sure you want to delit it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mFdb.deleteFeed(getContentResolver(), mFd);
                       refresh();
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        
        mAddBuilder = new AlertDialog.Builder(this);
        mAddBuilder.setMessage("Are you sure you want to add this problem feed? ").setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               Feed feed = new Feed();
               feed.setTitle(mEdTitle.getText().toString());
               feed.setLink(mEdUrl.getText().toString());
               mFdb.addNewFeed(getContentResolver(), feed);
               refresh();
           }
       })
       .setNegativeButton("No", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
           }
       });
        
        
    }

    /**
     * Refresh RSS url list after changes
     */
    protected void refresh() {

        getFeedList();
    
        ArrayAdapter<Feed> adapter = new ArrayAdapter<Feed>(RSSURLList.this, R.layout.row, mFeeds);
        mFeedListView.setAdapter(adapter);
    
        if (mFeeds.size()>0)
        {
            mFd = mFeeds.get(0);
        }
    
        if (mFd!=null)
        {
            mEdUrl.setText(mFd.getLink().toString());
            mEdTitle.setText(mFd.getTitle());        
        }    
    }
    
    /**
     * Get feed list 
     */
    protected void getFeedList() {
        mFeeds = mFdb.getFeedList(getContentResolver());
    }

    /**
     * Handle Ok button click
     */
    protected void okClicked() {
        startService(new Intent(this, RSSUpdateService.class));
        finish();
    }

    /**
     * Handler class for item RSS url's click 
     * @author ruinilyu
     *
     */
    class OnItemClickListener1 implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)    {
            mFd = (Feed)arg0.getAdapter().getItem(position);
        
            if (mFd!=null) {
                mEdUrl.setText(mFd.getLink().toString());
                mEdTitle.setText(mFd.getTitle());        
            }
        }
    
    }
    
    private boolean validateURL(String url) {
        
        URL rssUrl;
        
        try {
            rssUrl = new URL(url);
        }
        catch (Exception e)
        {
            Toast.makeText(RSSURLList.this, "Invalid URL. Use format http://...", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        try {
            rssUrl.openConnection().getInputStream();
        }
        catch (Exception e) {
            Toast.makeText(RSSURLList.this, "Can't open connection...", Toast.LENGTH_SHORT).show();
            return false;
        }
       try {
        RSSParser parser = new RSSParser();
        parser.setContentResolver(getContentResolver());
        parser.setRssUrl(url);
        if (parser.parse(this)) {
            return true;
        } else {
            Toast.makeText(RSSURLList.this, "Can't parse this feed.", Toast.LENGTH_SHORT).show();
            return false;
        }
       } catch (Exception e)
       {
           Toast.makeText(RSSURLList.this, "Can't parse this feed." + e.getMessage(), Toast.LENGTH_SHORT).show();
           Log.i("RSSURLList", e.toString());
           return false;
       }
    }
}