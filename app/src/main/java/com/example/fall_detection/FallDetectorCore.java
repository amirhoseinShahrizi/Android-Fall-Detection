package com.example.fall_detection;

import android.content.Context;
import android.hardware.Sensor;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class FallDetectorCore {

    public FallDetectorCore(Context context, String phoneNumber, Integer testMod, JSONArray sensorTestData){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.db = new HistoryDBHelper(context);
        this.testMod = testMod;
        this.sensorTestData = sensorTestData;
    }

    private String phoneNumber;
    private Context context;
    private Integer testMod;
    long accelerometerTime;
    boolean isACCLessThanLFT = false;
    private JSONArray sensorTestData;
    private float accLFT = (float) 0.26;
    private float accUFT = (float) 2.77;
    private float gyrUFT = (float) 0.2545;
    float acc = -1, gyro = -1;
    boolean fallDetected = false;
    long timeDetected;
    HistoryDBHelper db;

    private void detection() {
        // Save fall in database
        DetectedFall detectedFall = new DetectedFall();
        db.addFall(detectedFall, this.testMod);

        // Sending SMS
        String msg = "Fall detected";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, msg, null,null);
        Toast.makeText(context, "Message Sent successfully!",
                Toast.LENGTH_LONG).show();
    }
    private float getGeoSize(float[] dataArray) {
        return (float) Math.sqrt(dataArray[0] * dataArray[0] + dataArray[1] * dataArray[1] + dataArray[2] * dataArray[2]);
    }
    public void run(int sensorType, @NonNull float[] sensorData) {

        long time = System.currentTimeMillis();
        Log.i("RUN INFO", "" + sensorType  + " " + sensorData[0] + " " + sensorData[1] + " " + sensorData[2]);

        if (!isACCLessThanLFT) {
            checkStateACCLessThanFT(sensorType, sensorData, time);
        } else {
            if (HalfASecondPassed(time)) return;
            updateAccAndGyro(sensorType, sensorData);
            if (!sensorDataReadyForFallDetection()) return;
            detectPossibleFall(time);

            // After 10s, start fall detecting again
            if (time - timeDetected > 10000) {
                fallDetected = false;
                return;
            }
            acc = gyro = -1;
            isACCLessThanLFT = false;
        }
    }

    private void detectPossibleFall(long time){
        // First time fall detected, call detection function
        if (!fallDetected) {
            timeDetected = time;
            fallDetected = true;
            acc = gyro = -1;
            isACCLessThanLFT = false;
            detection();
        }
    }
    private boolean sensorDataReadyForFallDetection(){
        if (acc == -1 || gyro == -1)
            return false;

        if (acc < accUFT) {
            acc = -1;
            return false;
        }

        if (gyro < gyrUFT) {
            gyro = -1;
            return false;
        }

        return true;
    }

    private void updateAccAndGyro(int sensorType, float [] sensorData) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                acc = getGeoSize(sensorData);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyro = getGeoSize(sensorData);
                break;
        }
    }

    private boolean HalfASecondPassed(long time) {
        if (time - accelerometerTime > 500) {
            isACCLessThanLFT = false;
            acc = -1;
            gyro = -1;
            return true;
        }
        return false;
    }

    private void checkStateACCLessThanFT(int sensorType, float [] sensorData, long time) {
        if (sensorType != Sensor.TYPE_ACCELEROMETER)
            return;
        float _acc = getGeoSize(sensorData);
        if (_acc < accLFT) {
            isACCLessThanLFT = true;
            accelerometerTime = time;
        }
    }

    public void simulateSensors() throws Exception {
        if (sensorTestData == null || testMod == 0)
            throw new Exception("No Test Data provided or not in test mode");

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

}
