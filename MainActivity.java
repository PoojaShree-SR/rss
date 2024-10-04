package com.example.rssfeedapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView rssFeedListView;
    private List<HashMap<String, String>> rssItemList = new ArrayList<>();
    private String rssFeedUrl = "https://example.com/rss";  // Replace with your RSS feed URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rssFeedListView = findViewById(R.id.rssFeedListView);

        // Start AsyncTask to fetch RSS feed
        new FetchRSSFeedTask().execute(rssFeedUrl);
    }

    private class FetchRSSFeedTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                parseRSSFeed(inputStream);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                SimpleAdapter adapter = new SimpleAdapter(
                        MainActivity.this,
                        rssItemList,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "link"},
                        new int[]{android.R.id.text1, android.R.id.text2}
                );
                rssFeedListView.setAdapter(adapter);
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch RSS feed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseRSSFeed(InputStream inputStream) throws XmlPullParserException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null);

            boolean insideItem = false;
            String title = null, link = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (insideItem && parser.getName().equalsIgnoreCase("title")) {
                        title = parser.nextText();
                    } else if (insideItem && parser.getName().equalsIgnoreCase("link")) {
                        link = parser.nextText();
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                    if (title != null && link != null) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put("title", title);
                        item.put("link", link);
                        rssItemList.add(item);
                    }
                    title = null;
                    link = null;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
