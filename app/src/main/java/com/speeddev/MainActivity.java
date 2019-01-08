package com.speeddev;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    // your sd card
    String storage = Environment.getExternalStorageDirectory().toString();
    // the file to be copied
    File srcFile = new File (storage + "/sample.txt");
    // make sure your target location folder exists!
    File targetFile = new File (storage + "/MyNewFolder/sample.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lets request external storage read/write permissions in a background thread
        (new Startup(this)).execute();
    }

    // we make our request in an AsyncTask to stay out of the main thread for improved performance
    private static class Startup extends AsyncTask<Void, Void, Void> {
        private WeakReference<MainActivity> activityReference;
        Startup(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected Void doInBackground(Void... params) {
            MainActivity activity = activityReference.get();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return null;
        }
    }

    // this is our button linked to layout file activity_main.xml
    public void copyFile(View view) {
        try {
            copyFiles(srcFile, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // when the button is pressed both source and destination file/directory are processed in this method
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyFiles(File sourceFile, File destinationFile) throws IOException {
        // check if source file exists. if not this creates it
        if (!sourceFile.exists()) {
            sourceFile.createNewFile();
        }

        // check if destination directory exists. if not this creates it
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }

        // finally copy the file to its destination
        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destinationFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
            sourceFile.delete();
            Toast.makeText(getApplicationContext(), "File Created!", Toast.LENGTH_SHORT).show();
        }
    }

}
