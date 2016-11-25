package com.teleca.RSSTestReader.CursorAdapter;

import com.teleca.RSSTestReader.R;
import com.teleca.RSSTestReader.RSSMessages.RSSMessagesDB;
import com.teleca.RSSTestReader.RSSMessages.RSSMessage.RSSMessages;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Custom cursor adapter which extends SimpleCursorAdapter.
 * @see android.widget.SimpleCursorAdapter 
 * @author ruinilyu
 */
public class RSSMessagesCursorAdapter extends SimpleCursorAdapter implements Filterable 
{

    private Context mContext;

    private int mLayout;
    
    public RSSMessagesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) 
    {
            super(context, layout, c, from, to);
            this.mContext = context;
            this.mLayout = layout;
    }
    
    /**
     * Override SimpleCursorAdapter newView method.
     * @param context Context
     * @param cursor Cursor
     * @param parent ViewGroup
     */
    @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Cursor c = getCursor();
            final LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(mLayout, parent, false);
            
            int titleCol = c.getColumnIndex(RSSMessages.TITLE);
            String title = c.getString(titleCol);
            TextView name_text = (TextView) v.findViewById(R.id.title);
            if (name_text != null) {
                name_text.setText(title);
            }
            
            int descCol = c.getColumnIndex(RSSMessages.DESCRIPTION);
            String desc = c.getString(descCol);
            TextView desc_text = (TextView) v.findViewById(R.id.description);
            if (desc_text != null) {
            	desc_text.setText(Html.fromHtml(desc));
            }            
            
            int createdTimeCol = c.getColumnIndex(RSSMessages.CREATED_TIME);
            String createdTime = c.getString(createdTimeCol);
            TextView createdTime_text = (TextView) v.findViewById(R.id.created_time);
            if (createdTime_text != null) {
            	createdTime_text.setText(createdTime);
            }            
            
            return v;
        }
    
    /**
     * Override SimpleCursorAdapter bindView method.
     * @param context Context
     * @param cursor Cursor
     * @param parent ViewGroup
     */
    @Override
    public void bindView(View v, Context context, Cursor c) 
    {
        int titleCol = c.getColumnIndex(RSSMessages.TITLE);
        String title = c.getString(titleCol);
        TextView name_text = (TextView) v.findViewById(R.id.title);
        if (name_text != null) {
            name_text.setText(title);
        }
        
        int descCol = c.getColumnIndex(RSSMessages.DESCRIPTION);
        String desc = c.getString(descCol);
        TextView desc_text = (TextView) v.findViewById(R.id.description);
        if (desc_text != null) {
        	desc_text.setText(Html.fromHtml(desc));
        }            
        
        int createdTimeCol = c.getColumnIndex(RSSMessages.CREATED_TIME);
        String createdTime = c.getString(createdTimeCol);
        TextView createdTime_text = (TextView) v.findViewById(R.id.created_time);
        if (createdTime_text != null) {
        	createdTime_text.setText("-=" + createdTime + "=-");
        }
    }
    
    /**
     * Override SimpleCursorAdapter runQueryOnBackgroundThread method.
     * @param constraint constraint
     */    
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) 
    {
    	RSSMessagesDB rdb = RSSMessagesDB.getInstance();
    	return rdb.getRSSMessageCursor(mContext.getContentResolver());
    }
}
