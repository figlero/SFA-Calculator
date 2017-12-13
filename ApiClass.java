package apps.finance.simple.CryptoCalculator;

/**
 * Created by figlero on 08.12.2017.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;

public class ApiClass {

    public boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getPrice(final String coin, final String währung) {
        String price;
        final String[] registerAnswer = new String[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder result = new StringBuilder();
                URL url = null;
                try {
                    if(währung.equals("USD")) {
                        url = new URL("https://api.coinmarketcap.com/v1/ticker/" + coin + "/");
                    }else{
                        url = new URL("https://api.coinmarketcap.com/v1/ticker/" + coin + "/?convert="+währung);
                    }
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    rd.close();
                    registerAnswer[0] = result.toString();
                    latch.countDown();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject mainObject = null;
        try {
            final JSONArray array = new JSONArray(registerAnswer[0]);
            final JSONObject obj = array.getJSONObject(0);
            if(währung.equals("USD")) {
                return obj.getString("price_usd");
            }else{
                return obj.getString("price_"+währung.toLowerCase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


       return "no price found";
    }

    public String [] listCoins() {
        String price;
        final String[] registerAnswer = new String[100];
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder result = new StringBuilder();
                URL url = null;
                try {
                    url = new URL("https://api.coinmarketcap.com/v1/ticker/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    rd.close();
                    registerAnswer[0] = result.toString();
                    latch.countDown();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject mainObject = null;
        try {
            final JSONArray array = new JSONArray(registerAnswer[0]);
            for(int i=0; i<100; i++) {
                final JSONObject obj = array.getJSONObject(i);
                registerAnswer[i] = "("+obj.getString("symbol")+") "+obj.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return registerAnswer;
    }

    public String [] listFiat() {
        String [] array = new String[13];
        array[0] = "(USD) US-Dollar";
        array[1] = "(EUR) Euro";
        array[2] = "(JPY) Yen";
        array[3] = "(GBP) Pound";
        array[4] = "(AUD) Australian Dollar";
        array[5] = "(CHF) Swiss Franc";
        array[6] = "(CAD) Canadian Dollar";
        array[7] = "(MXN) Mexican Peso";
        array[8] = "(CNY) Chinese Renminbi";
        array[9] = "(NZD) New Zeeland Dollar";
        array[10] = "(SEK) Swedish Krona";
        array[11] = "(RUB) Russian ruble";
        array[12] = "(HKD) Hongkong Dollar";

        return array;
    }

}


