package com.murach.timertask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView messageTextView;
    private Button startButton;
    private Button stopButton;
    private TextView downloadTextView;
    Timer timer;
    private int counter;
    private final String URL_STRING = "http://rss.cnn.com/rss/cnn_tech.rss";
    private final String FILENAME = "news_feed.xml";
    private RSSFeed feed;
    private FileIO io;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        startButton = (Button) findViewById(R.id.startTimerButton);
        stopButton = (Button) findViewById(R.id.stopTimerButton);
        downloadTextView = (TextView) findViewById(R.id.downloadTextView);

        startButton.setEnabled(false);
        counter = 0;
        timer = new Timer();
        startTimer();
    }
    
    private void startTimer() {
        timer = new Timer();
        final long startMillis = System.currentTimeMillis();
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startMillis;
                updateView(elapsedMillis);
            }
        };
        timer.schedule(task, 0, 10000);
        io = new FileIO(this.getApplicationContext());
        new DownloadFeed().execute();
        counter++;
        downloadTextView.setText("File downloaded: "+ counter + " times");
    }


    public void startClick(View view) {
        startTimer();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void stopClick(View view) {
        timer.cancel();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        messageTextView.setText("Seconds: 0");
        counter++;
        downloadTextView.setText("File downloaded: "+ counter + " times");
    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis / 1000;

            @Override
            public void run() {
                messageTextView.setText("Seconds: " + elapsedSeconds);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        timer.cancel();
    }

    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed downloaded");
            new ReadFeed().execute();
        }
    }

    class ReadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            feed = io.readFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed read");
        }
    }
}