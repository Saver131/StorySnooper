package com.example.saverbullets.senior;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SaverBullets on 12/5/2016.
 */

public class VeracityResult extends Activity{

    TextView txtView,txtReason;
    String link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.veracity_result);
        link = getIntent().getExtras().getString("url");
        new GetData().execute();
    }

    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                URL url = new URL(getString(R.string.ip)+"veracity?url="+link);
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if(code==200){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finally {
                urlConnection.disconnect();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            String value,reason;
            txtView = (TextView)findViewById(R.id.Veracity);
            txtReason = (TextView) findViewById(R.id.reason);
                try{
                    JSONArray veracity = new JSONArray(result);
                    value = veracity.getString(0);
                    reason = veracity.getString(1);
                    System.out.println("help"+value+reason);
                    reason = reason.replaceAll(",","\n");
                    value = value.replaceAll(",","\n");
                    txtView.setText(value);
                    txtReason.setText(reason);
                }
                catch (JSONException e){
                    txtView.setText("error");
                    txtReason.setText("please try again");
                }

                super.onPostExecute(result);

        }
    }
}
