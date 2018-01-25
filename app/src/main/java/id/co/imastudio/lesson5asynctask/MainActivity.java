package id.co.imastudio.lesson5asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView textQuote;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textQuote = findViewById(R.id.text_quote);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        //cara 2
        pref = getSharedPreferences("SIMPANAN", MODE_PRIVATE);

        //cek koneksi
        if (cekKoneksi()) {
            // kalau konek
            new ambilDataQuote().execute();
        } else {
            //kalau nggak konek
            ambilDataPreference();
        }


    }



    public class ambilDataQuote extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            textQuote.setText("Loading....");
        }

        @Override
        protected String doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://116.206.197.190:5089/qod.json")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject contents = jsonObject.getJSONObject("contents");
                    JSONArray quotes = contents.getJSONArray("quotes");
                    String quote = quotes.getJSONObject(0).getString("quote");
                    return quote;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "Gagal dapat dia";
        }

        @Override
        protected void onPostExecute(String quote) {
            textQuote.setText(quote);
            simpanData(quote);
        }
    }

    private void simpanData(String quote) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("DATA_QUOTE", quote);
        editor.commit();
    }

    private void ambilDataPreference() {
        String dataQuote = pref.getString("DATA_QUOTE", "Data masih kosong");
        textQuote.setText(dataQuote);
    }

    private boolean cekKoneksi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }
}
