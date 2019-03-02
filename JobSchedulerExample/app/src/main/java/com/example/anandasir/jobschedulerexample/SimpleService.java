package com.example.anandasir.jobschedulerexample;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SimpleService extends JobService {
    private static final String TAG = "JobService";
    private boolean isJobCancelled = false;

    ArrayList<String> urlList = new ArrayList<String>() {{
        add("https://www.google.co.in");
        add("https://www.facebook.com");
        add("https://www.irctc.co.in");
        add("https://www.youtube.com");
        add("https://www.wikipedia.org/");
        add("https://in.yahoo.com/?p=us");
        add("https://www.amazon.com/");
        add("https://twitter.com/");
        add("https://www.instagram.com/");
        add("https://www.netflix.com/in/");
        add("https://www.linkedin.com/");
        add("https://www.whatsapp.com/");
        add("https://www.microsoft.com/en-in/");
        add("https://www.etest.com");
        add("http://ivymobility.com/");
    }};
    ArrayList<Integer> connectedList = new ArrayList<Integer>();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Started");
        doSomeService(jobParameters);
        return true;
    }

    private void doSomeService(final JobParameters parameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < urlList.size(); i++) {
                    Log.d(TAG, "Running " + i);
                    if (isJobCancelled)
                        return;
                    try {
                        boolean isConnected = isConnectedToServer(urlList.get(i), 50);
                        Thread.sleep(5000);
                        if(isConnected) {
                            connectedList.add(i);
                            clear();
                            addTask();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job Finished");

                jobFinished(parameters, false);
            }
        }).start();
    }

    //put this code in an asynctask and call it there
    public boolean isConnectedToServer(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void addTask() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(connectedList);
        editor.putString("Store", json);
        editor.commit();
    }

    private void clear(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Cancelled");
        isJobCancelled = true;
        addTask();
        return false;
    }
}
