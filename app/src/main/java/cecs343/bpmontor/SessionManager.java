package cecs343.bpmontor;

/**
 * Created by Mat on 4/19/2018.
 */

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
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    static Editor editor;

    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "userPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    private static final String KEY_PID = "pid";

    private static final String CURR_PAT = "CurrentPatient";
    private static final String CURR_PAT_NAME = "CurrentPatientName";


    static final String IS_DOC = "isDoctor";
    static boolean isDoc;

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(int pid){
        // Storing login value
        editor.putBoolean(IS_LOGIN, true);

        // Storing id in pref
        editor.putInt(KEY_PID, pid);

        editor.commit();
    }

    public void setCurrentPat(int pid)
    {
        editor.putInt(CURR_PAT, pid);
        editor.commit();
    }

    public int getCurrentPat()
    {
        int pid = pref.getInt(CURR_PAT, -1);
        return pid;
    }

    public void setCurrentPatName(String name)
    {
        editor.putString(CURR_PAT_NAME, name);
        editor.commit();
    }

    public String getCurrentPatName()
    {
        String name = pref.getString(CURR_PAT_NAME, "ERROR ");
        return name;
    }
    /**
     * If false it will redirect user to login page
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){

            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    public int getPid(){
        // user ID
        int pid = pref.getInt(KEY_PID, 0);
        return pid;
    }

    public boolean getIsDoc()
    {
        boolean doc = pref.getBoolean(IS_DOC, false);
        return doc;
    }


    //Clear session details
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }


    //Quick check for login
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    // Check if user is doctor
    public static class CheckDoctorTask extends AsyncTask<Void, Void, String> {

        private final int mPid;

        // Constructor
        CheckDoctorTask(int pid) {
            mPid = pid;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(AppConfig.URL_ISDOCTOR);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outStream = httpURLConnection.getOutputStream();
                BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
                String postData = URLEncoder.encode("pid", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mPid), "UTF-8");
                bfWriter.write(postData);
                bfWriter.flush();
                bfWriter.close();
                outStream.close();

                InputStream inStream = httpURLConnection.getInputStream();
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "ERROR";
        }

        @Override
        protected void onPostExecute(final String result) {
            try {
                JSONObject json = new JSONObject(result);
                Boolean status = json.getBoolean(AppConfig.IS_DOC);
                isDoc = status;
                editor.putBoolean(IS_DOC, isDoc);
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
