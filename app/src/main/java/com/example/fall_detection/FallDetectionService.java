package com.example.fall_detection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FallDetectionService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer_sensor, gyroscope_sensor;

    private float[] accelerometerReading = new float[3];
    private float[] gyroscopeReading = new float[3];

    long accelerometerTime, gyroscopeTime;
    boolean isACCLessthanLFT = false;

    private float accLFT = (float) 0.26;
    private float accUFT = (float) 2.77;
    private float gyrUFT = (float) 0.2545;

    float acc = -1, gyro = -1;

    boolean fallDetected = false;
    long timeDetected;

    HistoryDBHelper db;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // handle database
        db = new HistoryDBHelper(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    private void detection() {
        // Save fall in database
        DetectedFall detectedFall = new DetectedFall();
        db.addFall(detectedFall);

        // TODO: Send SMS
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent == null)
            return;

        long time = System.currentTimeMillis();

        if (!isACCLessthanLFT) {
            if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            float _acc = (float) Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
            if (_acc < accLFT) {
                isACCLessthanLFT = true;
                accelerometerTime = time;
            }

        } else {
            if (time - accelerometerTime > 500) {
                isACCLessthanLFT = false;
                acc = -1;
                gyro = -1;
                return;
            }

            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    acc = (float) Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    gyro = (float) Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
                    break;
            }

            if (acc == -1 || gyro == -1)
                return;

            if (acc < accUFT) {
                acc = -1;
                return;
            }

            if (gyro < gyrUFT) {
                gyro = -1;
                return;
            }

            // First time fall detected, call detection function
            if (!fallDetected)  {
                timeDetected = time;
                fallDetected = true;
                acc = gyro = -1;
                isACCLessthanLFT = false;
                detection();
            }

            // After 10s, start fall detecting again
            if (time - timeDetected > 10000) {
                fallDetected = false;
                return;
            }

            acc = gyro = -1;
            isACCLessthanLFT = false;
//            Log.i("FALL", "Fall Detected");
//            Toast.makeText(getApplicationContext(),"fall detected",Toast.LENGTH_SHORT).show();
        }
    }

    private void initSensors() {
        sensorManager.registerListener(this,
                accelerometer_sensor,
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                gyroscope_sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        initSensors();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
