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
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestModeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText records_ET;
    ImageButton clear_IB;

    public TestModeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestModeFragment newInstance(String param1, String param2) {
        TestModeFragment fragment = new TestModeFragment();
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
        // Inflate the layout for this fragment
        View A = inflater.inflate(R.layout.fragment_test_mode, container, false);
        setRV(A);

        records_ET = A.findViewById(R.id.test_mode_et);
        clear_IB = A.findViewById(R.id.clear_ib);
        clearClicked(clear_IB);


        return A;
    }

    private void setRV(View view){
        //setting recycler view
        //later get from database probably


        RecyclerView recyclerView_fd = (RecyclerView) view.findViewById(R.id.test_fall_history_recycler_view);
        recyclerView_fd.setLayoutManager( new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        ArrayList<DetectedFall> falls_list = new ArrayList<DetectedFall>();
        falls_list.add(new DetectedFall());
        falls_list.add(new DetectedFall("1/1/2022", "11:56 AM"));
        falls_list.add(new DetectedFall("1/1/2022", "11:56 AM"));
        falls_list.add(new DetectedFall("1/1/2022", "11:56 AM"));
        falls_list.add(new DetectedFall("1/1/2022", "11:56 AM"));
        falls_list.add(new DetectedFall("1/1/2022", "11:56 AM"));

        FallCardAdapter fallCardAdapter_fd = new FallCardAdapter(falls_list);
        recyclerView_fd.setAdapter(fallCardAdapter_fd);
        recyclerView_fd.setItemAnimator(new DefaultItemAnimator());
    }

    private void clearClicked(ImageButton ib){
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (toggle_btn.isChecked()) {
//                    Log.i("Start", "Start Clicked");
//                    getActivity().startService(new Intent(getActivity(), FallDetectionService.class));
//                }
//                else {
//                    Log.i("Stop", "Stop Clicked");
//                    getActivity().stopService(new Intent(getActivity(), FallDetectionService.class));
//                }
                records_ET.setText("");
            }
        });
    }
}