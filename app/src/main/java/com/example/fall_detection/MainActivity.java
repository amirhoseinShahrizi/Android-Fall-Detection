package com.example.fall_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView a_x_tv, a_y_tv, a_z_tv,g_x_tv, g_y_tv, g_z_tv;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;


    private SensorManager a_sensorManager;
    private Sensor accelerometer_sensor, gyroscope_sensor;

    protected final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a_x_tv.setText("A X = "+sensorEvent.values[0]);
            a_y_tv.setText("A Y = "+sensorEvent.values[1]);
            a_z_tv.setText("A Z = "+sensorEvent.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    protected final SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            g_x_tv.setText("G X = "+sensorEvent.values[0]);
            g_y_tv.setText("G Y = "+sensorEvent.values[1]);
            g_z_tv.setText("G Z = "+sensorEvent.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setPages();
//        setSensors();


    }



    private  void setPages(){
        tabLayout =  findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
//        fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
        fragmentAdapter = new FragmentAdapter(this);

        viewPager2.setAdapter(fragmentAdapter);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


    }



    private void setSensors(){

        a_x_tv = findViewById(R.id.a_x_tv);
        a_y_tv = findViewById(R.id.a_y_tv);
        a_z_tv = findViewById(R.id.a_z_tv);

        g_x_tv = findViewById(R.id.g_x_tv);
        g_y_tv = findViewById(R.id.g_y_tv);
        g_z_tv = findViewById(R.id.g_z_tv);

        a_sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer_sensor = a_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        a_sensorManager.registerListener(accelerometerListener, accelerometer_sensor, a_sensorManager.SENSOR_DELAY_UI);

        gyroscope_sensor = a_sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        a_sensorManager.registerListener(gyroscopeListener, gyroscope_sensor, a_sensorManager.SENSOR_DELAY_UI);
    }


}