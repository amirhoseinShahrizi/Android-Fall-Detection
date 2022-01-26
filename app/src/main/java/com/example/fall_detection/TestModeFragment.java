package com.example.fall_detection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TestModeFragment extends Fragment {

    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    private Button buttonBrowse;
    private EditText editTextPath;

    private static final String LOG_TAG = "AndroidExample";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        View A = inflater.inflate(R.layout.fragment_test_mode_2, container, false);
        setRV(A);
        return A;
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    try {
                        String content = getFileContentFromURI(uri);
                        launchTestFallDetector(content);

                    }catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @NonNull
                private String getFileContentFromURI(Uri uri) throws IOException {
                    InputStream in = getContext().getContentResolver().openInputStream(uri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();

                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }

                    return total.toString();
                }


            });

    private void launchTestFallDetector(String content) {
        try {
            JSONArray testData = new JSONArray(content);
            FallDetectorCore detector = new FallDetectorCore(getContext(), "09212422065", 1, testData);
            detector.simulateSensors();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "SHIIIIIIIIIIIIIIIIIT", Toast.LENGTH_LONG).show();
        }
    }

    private void setRV(View view){

        this.editTextPath = (EditText) view.findViewById(R.id.editText_path);
        this.buttonBrowse = (Button) view.findViewById(R.id.button_browse);

        this.buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("*/*");
            }
        });


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
}