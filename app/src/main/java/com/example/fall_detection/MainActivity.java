package com.example.fall_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
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

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setPages();
//        setSensors();

//        startService(new Intent( this, FallDetectionService.class));
    }


//    private void fallDetectionTestMode(ArrayList<String> records) {
//        // each record = time ax ay az gx gy gz
//        for (String record : records) {
//
//            float[] itemReading = new float[7];
//            int i = 0;
//
//            for (String item : record.split(" ")) {
//                itemReading[i] = Float.parseFloat(item);
//                i++;
//            }
//
//            // Detect fall for this record
//
//        }
//    }


    private void setPages(){
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
}