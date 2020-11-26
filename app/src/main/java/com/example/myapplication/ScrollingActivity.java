package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class ScrollingActivity extends AppCompatActivity {
    public static final String FILE_PATH = "mytextfile.txt";
    static final int READ_BLOCK_SIZE = 100;
    static String error = ""; // string field
    TextView textView = null;

    boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        textView = (TextView) findViewById(R.id.textView1);
        String content = null;
        try {
            content = this.ReadData();
            System.out.println("content = " + content);
            textView.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if(!isRunning){
                    clickButtonRefresh();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Đang đồng bộ nhé bạn!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void clickButtonRefresh() {
        Log.d("TAG", "clickButtonRefresh: " + isRunning);
        String url = "http://intelligentidea.biz:8080/crawler";


        GetDataBiz getDataBiz = new GetDataBiz();
        getDataBiz.setListener(new APIListener() {
            @Override
            public void onPreExecute() {
                isRunning = true;
            }

            @Override
            public void onRequestSuccess(Object object) {
                String data = (String) object;
                writeDataToFile(data);

                String x = null;
                try {
                    x = ReadData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textView.setText(x);
                isRunning = false;
            }

            @Override
            public void onRequestFailure(String message, int errorCode) {

            }
        });
        getDataBiz.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeDataToFile(String data) {
        // add-write text into file
        OutputStreamWriter outputWriter = null;

        try {
            System.out.println("DANG GHI FILE NHE DONG CHI");
            FileOutputStream fileout = openFileOutput("mytextfile.txt", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            //String data = this.fletchData();
            if (data == null || data.isEmpty()) {
                Toast.makeText(getBaseContext(), "Sync data error!", Toast.LENGTH_SHORT).show();
            } else {
                outputWriter.write(data);
            }
            // display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "File saved failure!", Toast.LENGTH_SHORT).show();
        } finally {
            if (null != outputWriter) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Read text from file
    public String ReadData() throws IOException {
        InputStreamReader InputRead = null;
        try {
            FileInputStream fileIn = openFileInput(FILE_PATH);
            InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            StringBuilder s = new StringBuilder();
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s.append(readstring);
            }
            System.out.println("s = " + s);
            return s.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != InputRead) {
                InputRead.close();
            }
        }
        return null;
    }

    public String fletchData() {
        // send request to server to get data
        return null;
    }

    public static void process() {
        String demoIdUrl = "http://intelligentidea.biz:8080/crawler";

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    public static void sendGet() throws Exception {
        String demoIdUrl = "http://intelligentidea.biz:8080/crawler";
        HttpGet request = new HttpGet(demoIdUrl);

        // add request headers
        request.addHeader("custom-key", "mkyong");
        request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                System.out.println(result);
            }

        }

    }


    public static String getDataPage1() throws IOException {
        String demoIdUrl = "http://intelligentidea.biz:8080/crawler";
        URL url = new URL(demoIdUrl);
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
            System.out.println("dta = " + dta.toString());
            return dta.toString();
        } else {
            //Handle else
            return null;
        }
    }
}