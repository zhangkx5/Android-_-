package com.example.kaixin.final_experiment;

import android.app.Service;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by kaixin on 2016/12/17.
 */

public class SharkActivity extends AppCompatActivity{
    private ImageButton ib_back;
    private TextView tv_show;
    private static final String DATABASE_NAME = "final.db";
    private SensorManager mSensorManager;
    private Vibrator vibrator = null;
    private long lastTime = 0;
    private myDB dbHelper;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        float[] accValues = null;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if (accValues != null) {
                        for (int i = 0; i < 3; i++) {
                            if (Math.abs(accValues[i] - event.values[i]) > 20) {
                                Calendar c = Calendar.getInstance();
                                if (c.getTimeInMillis() - lastTime > 3000) {
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    Cursor cursor = db.query("Shark_TABLE", null, null, null, null, null, "id desc");
                                    if (cursor.moveToFirst()) {
                                        Random rand = new Random();
                                        int randInt = rand.nextInt(cursor.getCount());
                                        cursor.moveToPosition(randInt);
                                        String joke = cursor.getString(cursor.getColumnIndex("joke"));
                                        tv_show.setText(joke);
                                        //Toast.makeText(SharkActivity.this, joke, Toast.LENGTH_SHORT).show();
                                    }
                                    cursor.close();
                                    db.close();
                                }
                                vibrator.vibrate(500);
                                lastTime = c.getTimeInMillis();
                                break;
                            }
                        }
                    }
                    accValues = event.values.clone();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shark);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        dbHelper = new myDB(this, DATABASE_NAME, null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        tv_show = (TextView) findViewById(R.id.joke);
        ib_back = (ImageButton) findViewById(R.id.shark_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }
}
