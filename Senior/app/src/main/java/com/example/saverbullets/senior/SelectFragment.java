package com.example.saverbullets.senior;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SaverBullets on 2/16/2017.
 */

public class SelectFragment extends Fragment {
    // URL to get contacts JSON
    private String getPage = "http://52.187.174.94/pagelist/";
    private Button button;
    ListView listview;
    public MainActivity main;
    ArrayList<HashMap<String, String>> pageList;
    String check;
    // JSON Node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "type";
    SetUrlListener mCallback;
    public SelectFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new GetPageList().execute();
        View view = inflater.inflate(R.layout.fragment_select,
                container, false);
        // Inflate the layout for this fragment

        return view;
    }
    public void onStart() {
        super.onStart();
    }

    public interface SetUrlListener {
        public void SetUrl(String url);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (SetUrlListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    private class GetPageList extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
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
            listview = (ListView) getView().findViewById(R.id.pageList);

            /**final ListAdapter adapter = new SimpleAdapter(
             PageSelect.this, pageList,
             R.layout.list_page, new String[]{TAG_NAME,TAG_TYPE}, new int[]{R.id.pageName,R.id.pageType});**/

            listview.setAdapter(new PageAdapter(getActivity()));


            button = (Button) getView().findViewById(R.id.submit);
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

                        mCallback.SetUrl(check);
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
            txtType.setText(pageList.get(position).get("type").replaceAll("_"," "));

            CheckBox Chk = (CheckBox) convertView.findViewById(R.id.cbBox);
            Chk.setTag(pageList.get(position).get("id"));

            return convertView;

        }
    }
}
