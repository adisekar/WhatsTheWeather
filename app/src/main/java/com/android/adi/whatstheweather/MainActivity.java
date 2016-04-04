package com.android.adi.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView weatherTextView;

    public void findWeather(View view) {
        String cityNameStr = cityName.getText().toString();

        // Hide the Keyboard after Button Click
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityNameStr, "UTF-8");

            DownloadTask task = new DownloadTask();
            String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=64521f3935acc0a0412955fb90c29912";
            task.execute(weatherURL);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }

                // Close Input Stream Reader
                reader.close();

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    //    Log.i("main", jsonPart.getString("main"));
                    //   Log.i("description", jsonPart.getString("description"));

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    String message = "";

                    if (main != "" && description != "") {
                        message += main + ": " + description + "\r\n";
                    }
                    weatherTextView.setText(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        cityName = (EditText) findViewById(R.id.cityName);

        weatherTextView = (TextView) findViewById(R.id.weatherTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
