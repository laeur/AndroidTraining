package com.laeur.differentdevices.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.laeur.differentdevices.R;
import com.laeur.differentdevices.model.CountryInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView ivCountryFlag;
    private TextView tvCapitalName, tvCountryName, tvInfoText;
    private ProgressBar pbLoadingInfoProgress, pbLoadingImageProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivCountryFlag = (ImageView)findViewById(R.id.activity_main_iv_flagimage);
        tvCountryName = (TextView)findViewById(R.id.activity_main_tv_countryname);
        tvCapitalName = (TextView)findViewById(R.id.activity_main_tv_capitalname);
        tvInfoText = (TextView)findViewById(R.id.activity_main_tv_infotext);

        pbLoadingImageProgress = (ProgressBar) findViewById(R.id.acitvity_main_pb_loadingimageprogress);
        pbLoadingInfoProgress = (ProgressBar) findViewById(R.id.acitvity_main_pb_loadininfoprogress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String countryCode = Locale.getDefault().getCountry();
        // Loading info
        new RetriveInfoTask().execute(countryCode);

        // Loading image
        pbLoadingImageProgress.setVisibility(View.VISIBLE);
        Uri flagImageUri = Uri.parse("http://www.geognos.com/api/en/countries/flag/"+ countryCode +".png");
        Glide.with(this).load(flagImageUri).listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                pbLoadingImageProgress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                pbLoadingImageProgress.setVisibility(View.GONE);
                return false;
            }
        }).into(ivCountryFlag);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class RetriveInfoTask extends AsyncTask<String, Void, CountryInfo> {

        private String BASE_API_URL = "http://www.geognos.com/api/en/countries/info/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingInfoProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected CountryInfo doInBackground(String... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader;
            String resultJson = "";

            StringBuilder resultingURL = new StringBuilder(BASE_API_URL);
            resultingURL
                    .append(params[0])  // Country
                    .append(".json");   // Format
            try {
                URL url = new URL(resultingURL.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONParser parser = new JSONParser(resultJson);
            CountryInfo info = parser.parse();
            return info;
        }

        @Override
        protected void onPostExecute(CountryInfo info) {
            super.onPostExecute(info);
            // Show interface, stop progress;
            pbLoadingInfoProgress.setVisibility(View.GONE);
            tvCapitalName.setText(info.getCapitalName());
            tvCountryName.setText(info.getCountryName());
            tvInfoText.setText(info.getAdditionalInfo());
        }
    }

    private class JSONParser {

        private String jsonString;

        public JSONParser(String jsonString) {
            this.jsonString = jsonString;
        }

        public CountryInfo parse() {

            String unknownInfo = getString(R.string.unknown);
            CountryInfo countryInfo = new CountryInfo(unknownInfo);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject resultsObject = jsonObject.optJSONObject("Results");
                if (resultsObject != null) {
                    JSONObject captialObject = resultsObject.optJSONObject("Capital");
                    if (captialObject != null) {
                        String capitalName = captialObject.optString("Name", unknownInfo);
                        countryInfo.setCapitalName(capitalName);
                    }
                    String countryName = resultsObject.optString("Name", unknownInfo);
                    countryInfo.setCountryName(countryName);

                    String info = resultsObject.optString("CountryInfo", unknownInfo);
                    countryInfo.setAdditionalInfo(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return countryInfo;
        }
    }

}
