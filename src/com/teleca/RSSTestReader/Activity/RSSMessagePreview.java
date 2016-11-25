/**
 * 
 */
package com.teleca.RSSTestReader.Activity;

import com.teleca.RSSTestReader.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Activity for preview RSS Message
 * @author ruinilyu
 *
 */
public class RSSMessagePreview extends Activity {
    TextView mTextTitle;
    TextView mTextDescription;
    String url;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_preview);
        mTextTitle = (TextView) this.findViewById(R.id.txtTitle);
        mTextDescription = (TextView) this.findViewById(R.id.txtDesc);
        
        if (savedInstanceState != null) {
            mTextDescription.setText(Html.fromHtml(savedInstanceState.getString("description")));
            setTitle(savedInstanceState.getString("title"));
            url = savedInstanceState.getString("url");
        }
        else {
            Bundle extras = getIntent().getExtras();
            setTitle(extras.getString("title"));
            mTextDescription.setText(Html.fromHtml(extras.getString("description")));
            url =extras.getString("url");
        }
        
        mTextTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent viewMessage = new Intent(Intent.ACTION_VIEW,    Uri.parse(url));
                startActivity(viewMessage);
            }          
        });    
    }
    
    private void setTitle(String title)
    {
        SpannableString content = new SpannableString(title);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mTextTitle.setText(content);
    }
}
