package com.example.myapplication;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetDataBiz extends AsyncTask<String, Integer, String> {

    APIListener listener;

    public GetDataBiz() {
    }


    public void setListener(APIListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
        String demoIdUrl = params[0];
        URL url = null;
        try {
            url = new URL(demoIdUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader read = new InputStreamReader(it);
                BufferedReader buff = new BufferedReader(read);
                StringBuilder dta = new StringBuilder();
                String chunks;
                while ((chunks = buff.readLine()) != null) {
                    dta.append(chunks);
                }
                System.out.println("Data = " + dta.toString());
                return dta.toString();
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null) {
            if (!s.equals("")) {
                listener.onRequestSuccess(s);
            } else {
                listener.onRequestFailure("Error", 101);
            }
        }
    }


}
