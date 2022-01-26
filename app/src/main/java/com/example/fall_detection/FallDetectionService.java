package com.example.fall_detection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class FallDetectionService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer_sensor, gyroscope_sensor;

    private FallDetectorCore detector;

    private static String global_number = "09212422065";
    public static int sensorAccuracy = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        detector = new FallDetectorCore(
                getApplicationContext(),
                getGlobal_number(),
                false,
                null);
        Log.i("Service", "FallDetectionService started");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        initSensors();
//        changeSamplingRate(sensorAccuracy);
    }

    private void writeToFile(String data, Context context){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("testLog.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(String file, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent == null)
            return;
        detector.run(sensorEvent.sensor.getType(), sensorEvent.values);
    }

    private void initSensors() {
        sensorManager.registerListener(this,
                accelerometer_sensor,
                SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this,
                gyroscope_sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void changeSamplingRate(int i) {

        switch (i){
            case 3:
                sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case 2:
                sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_UI);
                sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_UI);
                break;
            case 1:
                sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_GAME);
                sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_GAME);
                break;
            case 0:
                sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setGlobal_number(String global_number) {
        FallDetectionService.global_number = global_number;
    }

    public static String getGlobal_number() {
        return global_number;
    }
}
