package cecs343.bpmontor;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import org.w3c.dom.Text;

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

public class ViewMedHistory extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private BpRvAdapter mAdapter;
    private SessionManager sesh;
    private int patientId;
    private String currPatientName;
    private boolean isDoc;
    private int selectedPatient;
    private View mProgressView;
    private TextView heading;
    private Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_med_history);


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

        retry = (Button) findViewById(R.id.retry_medhist);
        heading = findViewById(R.id.medhist_heading);
        mProgressView = findViewById(R.id.medhist_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.medhist_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new BpRvAdapter(R.layout.bp_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        if(isDoc)
        {
            new MedHistQueryTask(selectedPatient).execute();
        }
        else
        {
            new MedHistQueryTask(patientId).execute();
        }

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
                if(isDoc)
                {
                    new MedHistQueryTask(selectedPatient).execute();
                }
                else
                {
                    new MedHistQueryTask(patientId).execute();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MedHistQueryTask extends AsyncTask<Void, Void, String> {

        private final int id;

        MedHistQueryTask(int pid)
        {
            id = pid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // HTTP POST Request, returns JSON String for parsing.
            try {
                URL url = new URL(AppConfig.URL_MEDHIST);
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
                    String headingFormat = String.format("%-12s %-6s %-10s"," Name"," TIME"," DATE");
                    heading.setText(headingFormat);
                    JSONArray json = jsonOb.getJSONArray("data");
                    String[] data = new String[json.length()];
                    for(int i = 0; i < json.length(); i++)
                    {
                        JSONObject row = json.getJSONObject(i);
                        String drugName =  row.getString(AppConfig.nameTag);
                        String time = row.getString(AppConfig.timeTag);
                        time = time.substring(0, time.length()-3);
                        String date = row.getString(AppConfig.dateTag);
                        String lineFormat = String.format("%-18s %-6s %-10s",drugName,time,date);
                        data[i] = lineFormat;

                    }
                    mAdapter.setBpData(data);

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
            retry.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Time Out... TRY AGAIN", Toast.LENGTH_LONG).show();
        }
    }
}
