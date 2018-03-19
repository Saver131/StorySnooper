package com.example.saverbullets.senior;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.button;

/**
 * Created by SaverBullets on 2/17/2017.
 */

public class TrendFeedFragment extends AppCompatActivity {
    private String url;
    final Context context = this;
    ListView listview;
    ArrayList<HashMap<String, String>> trendingList;

    private static final String TAG_POST = "post_id";
    private static final String TAG_PAGE = "page_id";
    private static final String TAG_PAGENAME = "name";
    private static final String TAG_CONTENT = "post";
    private static final String TAG_REACTION = "reaction";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_SHARE = "share";
    private static final String TAG_DATE = "date";
    private static final String TAG_VERACITY = "veracity";
    private static final String TAG_SHAREDLINK = "sharedlink";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_one);

        String type = getIntent().getExtras().getString("type");

        url = getString(R.string.ip)+"trendingtype?type="+type;
        System.out.println(url);
        new GetPostList().execute();

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetPostList extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TrendFeedFragment.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GET);

            Log.d("Response: ", "> " + jsonStr);

            trendingList = ParseJSON(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            listview = (ListView) findViewById(R.id.trendingList);

            /**final ListAdapter adapter = new SimpleAdapter(
             PageSelect.this, pageList,
             R.layout.list_page, new String[]{TAG_NAME,TAG_TYPE}, new int[]{R.id.pageName,R.id.pageType});**/


            //click open link
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                    System.out.println("http://www.facebook.com/"+trendingList.get(i).get(TAG_PAGE)+"_"+trendingList.get(i).get(TAG_POST));
                    Uri uri = Uri.parse("http://www.facebook.com/"+trendingList.get(i).get(TAG_PAGE)+"_"+trendingList.get(i).get(TAG_POST));
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
            });

            listview.setAdapter(new postAdapter(context));

        }

    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {

        String pageID,postID,pageName,content,reaction,comment,share,date,veracity,shareLink;
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> trendingList = new ArrayList<HashMap<String, String>>();

                // Getting JSON Array node
                JSONArray pageAll = new JSONArray(json);

                // looping through All Students
                for (int i = 0; i < pageAll.length(); i++) {
                    JSONObject c = pageAll.getJSONObject(i);

                    pageID = c.getString(TAG_PAGE);
                    postID = c.getString(TAG_POST);
                    pageName = c.getString(TAG_PAGENAME);
                    content = c.getString(TAG_CONTENT);
                    reaction = c.getString(TAG_REACTION);
                    comment = c.getString(TAG_COMMENT);
                    share = c.getString(TAG_SHARE);
                    date = c.getString("date");
                    veracity = c.getString(TAG_VERACITY)+'%';
                    shareLink = c.getString(TAG_SHAREDLINK);

                    // tmp hashmap for single student
                    HashMap<String, String> post = new HashMap<String, String>();

                    //add each child node to hashmap key value
                    post.put(TAG_PAGE, pageID);
                    post.put(TAG_POST, postID);
                    post.put(TAG_PAGENAME,pageName);
                    post.put(TAG_CONTENT,content);
                    post.put(TAG_REACTION,reaction);
                    post.put(TAG_COMMENT,comment);
                    post.put(TAG_SHARE,share);
                    post.put(TAG_DATE,date);
                    post.put(TAG_VERACITY,veracity);
                    post.put(TAG_SHAREDLINK,shareLink);

                    // adding student to students list
                    trendingList.add(post);
                }
                return trendingList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            return null;
        }
    }

    public class postAdapter extends BaseAdapter{
        private Context context;

        public postAdapter(Context c){
            context = c;
        }

        public int getCount(){
            return trendingList.size();
        }

        public Object getItem(int position){
            return position;
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(convertView == null){
                convertView = inflater.inflate(R.layout.list_item,null);
            }

            Typeface myFont = Typeface.createFromAsset(context.getApplicationContext().getAssets(),"fonts/THSarabunNew Bold.ttf");

            TextView pageName = (TextView) convertView.findViewById(R.id.pageName);
            pageName.setTypeface(myFont);
            pageName.setText(trendingList.get(position).get(TAG_PAGENAME));

            ImageView image = (ImageView) convertView.findViewById(R.id.url);
            switch(trendingList.get(position).get(TAG_PAGENAME)){
                case "ไทยรัฐ":
                    image.setImageResource(R.mipmap.page_thairath);
                    break;
                case "Drama-Addict":
                    image.setImageResource(R.mipmap.page_drama_add);
                    break;
                case "ข่าวสด":
                    image.setImageResource(R.mipmap.page_khaosod);
                    break;
                case "Siamsport":
                    image.setImageResource(R.mipmap.page_siamsport);
                    break;
                case "YouLike":
                    image.setImageResource(R.mipmap.page_youlike);
                    break;
                case "เรื่องเล่าเช้านี้":
                    image.setImageResource(R.mipmap.page_morning_news);
                    break;
                default:
                    break;
            }

            TextView content = (TextView) convertView.findViewById(R.id.content);
            content.setTypeface(myFont);
            content.setText(trendingList.get(position).get(TAG_CONTENT));

            TextView reaction = (TextView) convertView.findViewById(R.id.reaction);
            reaction.setText(trendingList.get(position).get(TAG_REACTION));

            TextView comment = (TextView) convertView.findViewById(R.id.comment);
            comment.setText(trendingList.get(position).get(TAG_COMMENT));

            TextView share = (TextView) convertView.findViewById(R.id.share);
            share.setText(trendingList.get(position).get(TAG_SHARE));

            TextView dateTime = (TextView) convertView.findViewById(R.id.dateTime);
            dateTime.setText(trendingList.get(position).get(TAG_DATE));

            Button veracity = (Button) convertView.findViewById(R.id.checkVeracity);
            veracity.setText(trendingList.get(position).get(TAG_VERACITY));
            veracity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Insert load veracity Detail button
                    Intent intent = new Intent(context,PostDetail.class);
                    intent.putExtra("veracity",trendingList.get(position).get(TAG_VERACITY));
                    intent.putExtra("id",trendingList.get(position).get(TAG_PAGE));
                    startActivity(intent);
                }
            });

            return convertView;
        }

    }


}
