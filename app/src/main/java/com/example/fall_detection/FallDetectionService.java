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

    private boolean testMod = false;

    private SensorManager sensorManager;
    private Sensor accelerometer_sensor, gyroscope_sensor;

    private JSONArray sensorTestData = null;

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

    private static String global_number = "";
    public static int sensorAccuracy = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // handle database
        db = new HistoryDBHelper(this);

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

    private void detection() {
        // Save fall in database
        DetectedFall detectedFall = new DetectedFall();
        db.addFall(detectedFall);
        int size = db.getAllFalls().size()-1;
//        Log.i("fall detected", db.getAllFalls().get(size).getCurrent_date() + db.getAllFalls().get(size).getCurrent_time());
        FallDetectionFragment.falls_reversed_list.add(0, detectedFall);
        FallDetectionFragment.fallCardAdapter_fd.notifyItemInserted(size);

        String number = "09172622474";
        // Sending SMS
        if (global_number != ""){
            number = getGlobal_number();
        }
        String msg = "Fall detected";

        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(number, null, msg, null,null);
        Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                Toast.LENGTH_LONG).show();
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

    private void run(int sensorType, float[] sensorData) {

        long time = System.currentTimeMillis();

        if (!isACCLessthanLFT) {
            if (sensorType != Sensor.TYPE_ACCELEROMETER)
                return;

            float _acc = (float) Math.sqrt(sensorData[0] * sensorData[0] + sensorData[1] * sensorData[1] + sensorData[2] * sensorData[2]);
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

            switch (sensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    acc = (float) Math.sqrt(sensorData[0] * sensorData[0] + sensorData[1] * sensorData[1] + sensorData[2] * sensorData[2]);
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    gyro = (float) Math.sqrt(sensorData[0] * sensorData[0] + sensorData[1] * sensorData[1] + sensorData[2] * sensorData[2]);
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
            if (!fallDetected) {
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
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent == null)
            return;
        if (!testMod)
            run(sensorEvent.sensor.getType(), sensorEvent.values);
    }

    private void initSensors() {

        if (!testMod) {
            sensorManager.registerListener(this,
                    accelerometer_sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);

            sensorManager.registerListener(this,
                    gyroscope_sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            simulateSensors();
        }
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

    private void loadSensorTestData() {
        try {
            sensorTestData = new JSONArray(fall);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void simulateSensors() {
        if (sensorTestData == null)
            return;

        for (int i = 0; i < sensorTestData.length(); i++) {
            JSONObject sensorDataElement = sensorTestData.optJSONObject(i);
            try {
                int type = sensorDataElement.getInt("type");
                float x = (float) sensorDataElement.getDouble("x");
                float y = (float) sensorDataElement.getDouble("y");
                float z = (float) sensorDataElement.getDouble("z");
                run(type, new float[]{x, y, z});

                if (i <= sensorDataElement.length() - 2) {
                    long time = sensorDataElement.getLong("time");
                    JSONObject sensorNextDataElement = sensorTestData.optJSONObject(i + 1);
                    long nextTime = sensorNextDataElement.getLong("time");
                    TimeUnit.MILLISECONDS.sleep(nextTime - time);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    String fall = "[{\"time\":1643188284044,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284054,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284064,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284076,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284084,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284094,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284105,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284114,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284126,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284134,\"type\":1,\"x\":0.07999999821186066,\"y\":6.228013038635254,\"z\":6.961212158203125},{\"time\":1643188284135,\"type\":4,\"x\":0,\"y\":0,\"z\":0},{\"time\":1643188284136,\"type\":1,\"x\":-0.07999999821186066,\"y\":6.213071823120117,\"z\":6.940082550048828},{\"time\":1643188284143,\"type\":1,\"x\":-0.03999999910593033,\"y\":6.190186500549316,\"z\":6.921594142913818},{\"time\":1643188284145,\"type\":1,\"x\":0,\"y\":6.165256023406982,\"z\":6.905416965484619},{\"time\":1643188284146,\"type\":1,\"x\":0.03999999910593033,\"y\":6.153253555297852,\"z\":6.896167755126953},{\"time\":1643188284148,\"type\":4,\"x\":0,\"y\":0,\"z\":0}]";

}
