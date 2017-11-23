package com.example.pranayanand.shakeit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SeekBar.OnSeekBarChangeListener {

    private SeekBar bar; // seekbar for controlling sensitivity
    private TextView sensitivity;

    // MediaPlayer controls playing the mp3
    private MediaPlayer mp;
    private boolean playing = false;

    // Display the gif in a webview for simplicity
    private WebView wv;

    private SensorManager sm;

    private float xAccel, yAccel, zAccel;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private boolean isPlaying=false;
    private double Limit = 5.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bar = (SeekBar)findViewById(R.id.seekBar);
        bar.setOnSeekBarChangeListener(this);
        sensitivity = (TextView) findViewById(R.id.sensitivity);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        mp = MediaPlayer.create(this, R.raw.batman_on_drugs);
        mp.setVolume(1.0f, 1.0f);

        wv = (WebView) findViewById(R.id.web);
        String html = "<html><body style = 'background:black;'><img src = 'file:///android_res/raw/batman.gif' style = 'width:100%;'></body></html>";
        wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    private void playGif(){
        mp.start();
        mp.setLooping(true);
        wv.setVisibility(View.VISIBLE);
    }

    private void stopGif(){
        mp.pause();
        mp.seekTo(mp.getCurrentPosition());
        wv.setVisibility(View.INVISIBLE);
    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        xAccel = sensorEvent.values[0];
        yAccel = sensorEvent.values[1];
        zAccel = sensorEvent.values[2];

        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (xAccel*xAccel + yAccel*yAccel + zAccel*zAccel));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if (mAccel > Limit) {
            if (!mp.isPlaying()){
                playGif();
                isPlaying=true;
            }
            else if (mp.isPlaying()){

                stopGif();



                isPlaying=true;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        sensitivity.setText("Sensitivity (" + i + ")");
        Limit = (10-i);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
