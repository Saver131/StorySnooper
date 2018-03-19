package com.example.saverbullets.senior;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SaverBullets on 3/29/2017.
 */

public class ConfigActivity extends AppCompatActivity {
    // URL to get contacts JSON
    private String getPage = "http://52.187.174.94/pagetypelist/";
    ListView listview;
    ArrayList<HashMap<String, String>> pageList;
    final Context context = this;
    // JSON Node names
    private static final String TAG_TYPE = "type";
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        new GetPageList().execute();
    }

    public void onStart() {
        super.onStart();
        Button button = (Button) findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }

        });
    }

    private class GetPageList extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ConfigActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(getPage, WebRequest.GET);

            Log.d("Response: ", "> " + jsonStr);

            pageList = ParseJSON(jsonStr);

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
            listview = (ListView) findViewById(R.id.pageListConfig);

            /**final ListAdapter adapter = new SimpleAdapter(
             PageSelect.this, pageList,
             R.layout.list_page, new String[]{TAG_NAME,TAG_TYPE}, new int[]{R.id.pageName,R.id.pageType});**/

            listview.setAdapter(new PageAdapter(context));

        }

    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> pageList = new ArrayList<HashMap<String, String>>();

                // Getting JSON Array node
                JSONArray pageAll = new JSONArray(json);

                // looping through All Students
                for (int i = 0; i < pageAll.length(); i++) {
                    JSONObject c = pageAll.getJSONObject(i);

                    String type = c.getString(TAG_TYPE);

                    // tmp hashmap for single student
                    HashMap<String, String> page = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    page.put(TAG_TYPE,type);

                    // adding student to students list
                    pageList.add(page);
                }
                return pageList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            return null;
        }
    }

    public class PageAdapter extends BaseAdapter
    {
        private Context context;

        public PageAdapter(Context c){
            context = c;
        }

        public int getCount(){
            return pageList.size();
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
                convertView = inflater.inflate(R.layout.list_pagetypeconfig,null);
            }
            TextView txtName = (TextView) convertView.findViewById(R.id.pageType);
            txtName.setText(pageList.get(position).get("type").replaceAll("_"," "));

            return convertView;

        }
    }
}
