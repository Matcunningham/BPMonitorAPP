package cecs343.bpmontor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ViewBPHistory extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BpRvAdapter mAdapter;
    private SessionManager sesh;
    private int patientId;
    private String currPatientName;
    private boolean isDoc;
    private int selectedPatient;
    private View mProgressView;
    private Button retry; // Only used when http request times out
    private TextView heading; // Label for data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bphistory);
        sesh = new SessionManager(getApplicationContext());
        sesh.checkLogin();
        patientId = sesh.getPid();
        isDoc = sesh.getIsDoc();
        if(isDoc)
        {
            selectedPatient = sesh.getCurrentPat();
            currPatientName = sesh.getCurrentPatName();
            setTitle("Current Patient: " + currPatientName);
        }

        heading = findViewById(R.id.bphist_heading);
        mProgressView = findViewById(R.id.bphist_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.bp);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        retry = (Button) findViewById(R.id.retry);

        mAdapter = new BpRvAdapter(R.layout.bp_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        if(isDoc)
        {
            new BpQueryTask(selectedPatient).execute();
        }
        else
        {
            new BpQueryTask(patientId).execute();
        }

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
                if(isDoc)
                {
                    new BpQueryTask(selectedPatient).execute();
                }
                else
                {
                    new BpQueryTask(patientId).execute();
                }
            }
        });
    }

    // Returns to parent activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class BpQueryTask extends AsyncTask<Void, Void, String>{

        private final int id;

        BpQueryTask(int pid)
        {
            id = pid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // HTTP POST Request, returns JSON String for parsing.
                URL url = new URL(AppConfig.URL_BPHIST);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(AppConfig.HTTP_TIME_OUT);

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outStream = httpURLConnection.getOutputStream();
                BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(id), "UTF-8");
                bfWriter.write(postData);
                bfWriter.flush();
                bfWriter.close();
                outStream.close();

                InputStream inStream = httpURLConnection.getInputStream();
                httpURLConnection.setReadTimeout(AppConfig.HTTP_TIME_OUT);
                BufferedReader bfReader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bfReader.readLine()) != null) {
                    result += line;
                }
                bfReader.close();
                inStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch(java.net.SocketTimeoutException tOut)
            {
                this.cancel(true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(final String result) {
            mProgressView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonOb = new JSONObject(result);
                boolean status = jsonOb.getBoolean(AppConfig.SUCCESS);
                if(status)
                {
                    String headingFormat = String.format("%-14s %-10s %-8s"," DATE"," TIME"," BP");
                    heading.setText(headingFormat);
                    JSONArray json = jsonOb.getJSONArray("data");
                    String[] data = new String[json.length()];
                    for(int i = 0; i < json.length(); i++)
                    {
                        JSONObject row = json.getJSONObject(i);
                        String date =  row.getString(AppConfig.dateTag);
                        String time =  row.getString(AppConfig.timeTag);
                        String bp = row.getString(AppConfig.sysTag) + "\t/\t" + row.getString(AppConfig.diaTag);
                        time = time.substring(0, time.length()-3);
                        String lineFormat = String.format("%-12s %-10s %-8s",date,time,bp);
                        data[i] = lineFormat;

                    }
                    mAdapter.setBpData(data); // Sets row value for recycler view

                }
                else {
                    String message = jsonOb.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressView.setVisibility(View.GONE);
            retry.setVisibility(View.VISIBLE); // Displays retry button
            Toast.makeText(getApplicationContext(), "Time Out... TRY AGAIN", Toast.LENGTH_LONG).show();
        }
    }
}
