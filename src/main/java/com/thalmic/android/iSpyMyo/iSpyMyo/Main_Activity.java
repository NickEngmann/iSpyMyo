/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.iSpyMyo.iSpyMyo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.android.sample.helloworld.R;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;
import android.os.Vibrator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

public class Main_Activity extends Activity {

    private TextView mLockStateView;
    private TextView mTextView;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private int flag = 0;
    public int audio_saved = 0;
    public int text_saved = 0;
    //names for saved audio/text files
    String[] saved_audio = new String[]{"audio_1.3gp","audio_2.3gp","audio_3.3gp","audio_4.3gp","audio_5.3gp","audio_6.3gp","audio_7.3gp","audio_8.3gp","audio_9.3gp","audio_10.3gp"};
    String[] saved_text = new String[]{"text_1.txt","text_2.txt","text_3.txt","text_4.txt","text_5.txt","text_6.txt","text_7.txt","text_8.txt","text_9.txt","text_10.txt"};
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            mTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            mTextView.setText("MYO UNSYNC'D");
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.unlocked);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.locked);
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 1000, 0, 1000, 0, 1000, 0, 2000, 100};
            switch (pose) {
                case UNKNOWN:
                    mTextView.setText(getString(R.string.hello_world));
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    mTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    mTextView.setText(getString(R.string.pose_fist));
                    v.vibrate(pattern, -1);
                    myo.lock();
                    startActivity(new Intent("iSpyMyo.phonecall"));
                    break;
                case WAVE_IN:
                    mTextView.setText(getString(R.string.pose_wavein));
                    if (flag == 0) {
                        Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_LONG).show();
                        startRecording(saved_audio[audio_saved]);
                    }
                    flag = 1;
                    break;
                case WAVE_OUT:
                    myo.notifyUserAction();
                    mTextView.setText(getString(R.string.pose_waveout));
                    if (flag == 1) {
                        stopRecording();
                        Toast.makeText(getBaseContext(), "Finished Recording", Toast.LENGTH_LONG).show();
                        audio_saved += 1;
                        /*
                        try {
                            playrecording(saved_audio[audio_saved - 1]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */
                    }
                    flag = 0;
                    break;
                case FINGERS_SPREAD:
                    mTextView.setText(getString(R.string.pose_fingersspread));
                    speechtotext();
                    /*if(flag == 0 && datasaved ==1) {
                        Toast.makeText(getBaseContext(), "Text Saved", Toast.LENGTH_LONG).show();
                    }
                    datasaved=0;
                    */
                    break;
            }
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.TIMED);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextView = (TextView) findViewById(R.id.text);

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        audio_saved = 0;
        text_saved = 0;
        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        if (R.id.home ==id){
            returnhome();
        }
        if (R.id.saved_audio == id){
            savedaudio();
        }
        if (R.id.saved_text == id){
            savedtext();
        }
        if (R.id.how_to_use == id){
            howtouse();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void startRecording(String audiofile) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(getFilesDir() + "/" + audiofile);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            System.err.println("An IOException was caught again:" + e.getMessage());
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void playrecording(String audiofile) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        MediaPlayer mP = new MediaPlayer();
        mP.setDataSource(getFilesDir() + "/" + audiofile);
        mP.prepare();
        mP.start();
    }

    public void speechtotext() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify free form input
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, 2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            ArrayList<String> results;
            if (data != null) {
                results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Toast.makeText(this, results.get(0), Toast.LENGTH_SHORT).show();
                writeToFile(results.get(0), saved_text[text_saved]);
                text_saved += 1;
            }
        }
    }

    public void writeToFile(String data, String location) {
        try {
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(location, Context.MODE_PRIVATE));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(location, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Toast.makeText(this, "Text Saved", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(this, "Text Did Not Save", Toast.LENGTH_SHORT).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public void returnhome(){
            Intent intent = new Intent(this, Main_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
    }
    public void savedaudio(){
        startActivity(new Intent("iSpyMyo.Save_Audio"));
    }
    public void savedtext(){
        startActivity(new Intent("iSpyMyo.Save_Text"));
    }
    public void howtouse(){
        startActivity(new Intent("iSpyMyo.HowTo"));
    }
}