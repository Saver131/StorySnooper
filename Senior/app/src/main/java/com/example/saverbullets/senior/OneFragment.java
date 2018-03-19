package com.example.saverbullets.senior;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneFragment extends Fragment {
    ListView listview;
    public MainActivity main;
    ArrayList<HashMap<String, String>> trendingList;

    private String url;

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

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (MainActivity) getActivity();
        url = getString(R.string.ip)+"trending?" + main.url;
        System.out.println(url);
        new getTrending().execute();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    private class getTrending extends AsyncTask<Void,Void,Void>{
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            WebRequest webreq = new WebRequest();

            String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GET);

            Log.d("Response: ", ">" + jsonStr);

            trendingList = ParseJson(jsonStr);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            listview = (ListView) getView().findViewById(R.id.trendingList);
            //updating parsed JSON data into ListView
            /**ListAdapter adapter = new SimpleAdapter(
                    getActivity(),trendingList,
                    R.layout.list_item,new String[]{TAG_PAGENAME,TAG_CONTENT,TAG_DATETIME,TAG_REACTION,TAG_COMMENT,TAG_SHARE,TAG_VERACITY}, new int[]{R.id.pageName,R.id.content,R.id.dateTime,R.id.reaction,R.id.comment,R.id.share,R.id.checkVeracity});
            **/
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
            listview.setAdapter(new postAdapter(getActivity()));



            mProgressDialog.dismiss();
        }
    }

    private ArrayList<HashMap<String, String>> ParseJson(String json) {
        String pageID,postID,pageName,content,reaction,comment,share,date,veracity,shareLink;
        if (json != null) {
            try {
                ArrayList<HashMap<String, String>> trendingList = new ArrayList<HashMap<String, String>>();

                //Getting json array node
                JSONArray trending = new JSONArray(json);

                for (int i = 0; i < trending.length(); i++) {
                    JSONObject c = trending.getJSONObject(i);

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

                    //tmp hashmap for single post
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

                    trendingList.add(post);
                }
                return trendingList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from url");
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
