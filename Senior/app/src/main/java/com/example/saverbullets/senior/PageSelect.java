package com.example.saverbullets.senior;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
 * Created by SaverBullets on 1/13/2017.
 */

public class PageSelect extends AppCompatActivity {
    // URL to get contacts JSON
    private String url = getString(R.string.ip)+"pagelist/";
    private Button button;
    final Context context = this;
    ListView listview;
    ArrayList<HashMap<String, String>> pageList;
    String check;
    // JSON Node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "type";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_page);

        new GetPageList().execute();

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetPageList extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PageSelect.this);
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
            listview = (ListView) findViewById(R.id.pageList);

            /**final ListAdapter adapter = new SimpleAdapter(
                    PageSelect.this, pageList,
                    R.layout.list_page, new String[]{TAG_NAME,TAG_TYPE}, new int[]{R.id.pageName,R.id.pageType});**/

            listview.setAdapter(new PageAdapter(context));


            button = (Button) findViewById(R.id.toMain);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View V){
                    check = "";
                    int count = listview.getAdapter().getCount();
                    for(int i = 0; i<count;i++){
                        LinearLayout itemlayout = (LinearLayout) listview.getChildAt(i);
                        CheckBox checkbox = (CheckBox) itemlayout.findViewById(R.id.cbBox);
                        if(checkbox.isChecked())
                        {
                            check = check + "id=" + checkbox.getTag().toString() + "&";
                        }
                    }
                    if(check!=""){
                    check = check.substring(0,check.length()-1);

                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra("url",check);
                    startActivity(intent);
                    finish();
                    }
                }
            });
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

                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String type = c.getString(TAG_TYPE);

                    // tmp hashmap for single student
                    HashMap<String, String> page = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    page.put(TAG_ID, id);
                    page.put(TAG_NAME,name);
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

            Typeface myFont = Typeface.createFromAsset(context.getApplicationContext().getAssets(),"fonts/THSarabunNew Bold.ttf");
            if(convertView == null){
                convertView = inflater.inflate(R.layout.list_page,null);
            }

            TextView txtName = (TextView) convertView.findViewById(R.id.pageName);
            txtName.setTypeface(myFont);
            txtName.setText(pageList.get(position).get("name"));

            TextView txtType = (TextView) convertView.findViewById(R.id.pageType);
            txtType.setTypeface(myFont);
            txtName.setText(pageList.get(position).get("type").replaceAll("_"," "));

            CheckBox Chk = (CheckBox) convertView.findViewById(R.id.cbBox);
            Chk.setTag(pageList.get(position).get("id"));

            return convertView;

        }
    }

}
