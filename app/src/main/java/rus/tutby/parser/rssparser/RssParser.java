package rus.tutby.parser.rssparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import rus.tutby.App;
import rus.tutby.entity.News;
import rus.tutby.entity.Provider;
import rus.tutby.utils.Logger;

public class RssParser {

    private static final String TAG_TUT_LAST_BUILD_DATE = "lastBuildDate";
    private static final String TAG_ONLINER_LAST_BUILD_DATE = "pubDate";
    private static final String TAG_RSS = "rss";
    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_URL = "url";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_MEDIA_CONTENT = "media:content";
    private static final String TAG_MEDIA_THUMBNAIL = "media:thumbnail";

    private String url;
    private String lastBuildDate;
    @Inject
    Provider provider;
    private JSONObject jsonObject;
    private int size;

    public RssParser(String url) throws IOException, JSONException, ParseException {
        App.getAppComponent().inject(this);
        this.url = url;
        //Log.d(Constants.TAG, url);
        this.jsonObject = XML.toJSONObject(readFromUrl(url))
                .getJSONObject(TAG_RSS).getJSONObject(TAG_CHANNEL);
        if(provider == Provider.TUT) {
            setLastBuildDate(this.jsonObject.getString(TAG_TUT_LAST_BUILD_DATE));
        }
        else if(provider == Provider.ONLINER) {
            setLastBuildDate(this.jsonObject.getString(TAG_ONLINER_LAST_BUILD_DATE));
        }
        this.size = jsonObject.getJSONArray(TAG_ITEM).length();
    }

    private String readFromUrl(String url) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            inputLine = inputLine.replace("<![CDATA", "")
                    .replace("]>", "")
                    .replace("[", "")
                    .replace("]", "");
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

    public News getItem(int index) throws JSONException, ParseException {
        News news = parseJSONItem((JSONObject) jsonObject.getJSONArray(TAG_ITEM).get(index));
        return news;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    private void setLastBuildDate(String lastBuildDate) throws ParseException {
        this.lastBuildDate = lastBuildDate;
    }

    private News parseJSONItem(JSONObject jsonObject)
            throws JSONException, ParseException {
        Iterator it = jsonObject.keys();
        News news = new News();
        while (it.hasNext()) {
            String key = (String) it.next();
            switch (key) {
                case TAG_TITLE:
                    news.setTitle(jsonObject.getString(key));
                    break;
                case TAG_LINK:
                    news.setLink(jsonObject.getString(key));
                    break;
                /*case "description":
                    StringTokenizer st = new StringTokenizer(jsonObject.getString(key), "&#x3E;", false);
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (token.endsWith(".")) {
                            news.setTextDescription(token);
                        }
                    }
                    break;*/
                case TAG_PUB_DATE:
                    news.setDate(jsonObject.getString(key));
                    break;
                case TAG_MEDIA_CONTENT:
                    Object object = jsonObject.get(key);
                    if(object instanceof JSONObject)
                        news.setImageURL(((JSONObject) object).getString(TAG_URL));
                    else if(object instanceof JSONArray) {
                        for(int i = 0; i < ((JSONArray)object).length(); i++) {
                            String value = ((JSONArray)object).getJSONObject(i).getString(TAG_URL);
                            if(value.endsWith(".jpg") || value.endsWith(".png") || value.endsWith(".gif")) {
                                news.setImageURL(((JSONArray) object).getJSONObject(i).getString(TAG_URL));
                                break;
                            }
                        }
                    }
                    break;
                case TAG_MEDIA_THUMBNAIL:
                    news.setImageURL(jsonObject.getJSONObject(key).getString(TAG_URL));
                    break;
                default:break;
            }
        }
        news.setProvider(provider);
        return news;
    }

    public int size() {
        return size;
    }

}