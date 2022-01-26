package com.example.fall_detection;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FallDetectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FallDetectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ArrayList<DetectedFall> falls_list;
    public static ArrayList<DetectedFall> falls_reversed_list;
    HistoryDBHelper db;
    public static FallCardAdapter fallCardAdapter_fd;

    public FallDetectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FallDetectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FallDetectionFragment newInstance(String param1, String param2) {
        FallDetectionFragment fragment = new FallDetectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View A = inflater.inflate(R.layout.fragment_fall_detection, container, false);
        setRV(A);
        setFallDetectionService(A);
        // Inflate the layout for this fragment
        return A;
    }
    public void setRV(View view){

//        falls_list.add(new DetectedFall("1-Jan-2022", "10:00 AM"));
//        falls_list.add(new DetectedFall("2-Jan-2022", "11:00 AM"));
//        falls_list.add(new DetectedFall("2-Jan-2022", "12:00 AM"));
//        falls_list.add(new DetectedFall("3-Jan-2022", "13:00 AM"));
        //setting recycler view
        //later get from database probably
        RecyclerView recyclerView_fd = (RecyclerView) view.findViewById(R.id.fall_history_recycler_view);
        recyclerView_fd.setLayoutManager( new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        db = new HistoryDBHelper(getContext());

        falls_list = new ArrayList<DetectedFall>();
        falls_reversed_list = new ArrayList<DetectedFall>();
        db.getAllFalls(0).forEach(fall -> falls_list.add(fall));

        Log.i("falls", db.getAllFalls(0).get(0).getCurrent_date());
        for(int i = falls_list.size()-1; i >= 0; i--){
            falls_reversed_list.add(falls_list.get(i));
        }


        fallCardAdapter_fd = new FallCardAdapter(falls_reversed_list);
        recyclerView_fd.setAdapter(fallCardAdapter_fd);
        recyclerView_fd.setItemAnimator(new DefaultItemAnimator());

    }


    private void setFallDetectionService(View view){
        // Start button
        ToggleButton toggle_btn = (ToggleButton) view.findViewById(R.id.toggle_btn);
        toggle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle_btn.isChecked()) {
                    Log.i("Start", "Start Clicked");
                    getActivity().startService(new Intent(getActivity(), FallDetectionService.class));
                }
                else {
                    Log.i("Stop", "Stop Clicked");
                    getActivity().stopService(new Intent(getActivity(), FallDetectionService.class));
                }
            }
        });
    }


}