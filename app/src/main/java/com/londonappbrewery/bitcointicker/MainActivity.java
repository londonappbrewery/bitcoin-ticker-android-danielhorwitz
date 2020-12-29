package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";
    private final String AUTH_KEY = "NWY0MTgxOGYwYjk5NDE1MWExOWY3YWM2Yzg2NTk2YjY";

    // Member Variables:
    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = findViewById(R.id.priceLabel);
        Spinner spinner = findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // The OnItemSelected listener for the spinner.
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Bitcoin", "+spinner.onItemSelected(,," + position + "," + id + ")");
                String selectedCur = (String) parent.getItemAtPosition(position);
                Log.d("Bitcoin", "" + selectedCur);
                String url = BASE_URL + selectedCur;
                letsDoSomeNetworking(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Bitcoin", "Nothing selected.");
            }
        });

    }

    // Query for BitCoin price.
    private void letsDoSomeNetworking(String url) {
        Log.d("Bitcoin", "+letsDoSomeNetworking(" + url + ")");
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("x-ba-key", AUTH_KEY);
        RequestParams params = new RequestParams();
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d("Bitcoin", "JSON: " + response.toString());
                try {
                    double lastPrice = response.getDouble("last");
                    Log.d("Bitcoin", "Last price: " + lastPrice);
                    String lastPriceString = String.format(Locale.getDefault(), "%,.2f", lastPrice);
                    Log.d("Bitcoin", "Last price string: " + lastPriceString);
                    mPriceTextView.setText(lastPriceString);
                } catch (JSONException jex) {
                    Log.d("Bitcoin", "Error parsing response: " + jex.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("Bitcoin", "Request fail! Status code: " + statusCode);
                Log.d("Bitcoin", "Fail response: " + response);
                Log.e("ERROR", e.toString());
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Bitcoin", "String/Throwable Request fail! Status code: " + statusCode);
                Log.d("Bitcoin", "Fail response: " + responseString);
                Log.e("ERROR", throwable.toString());
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }


}
