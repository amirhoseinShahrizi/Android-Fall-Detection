package com.example.fall_detection;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private String supervisor_no;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View A = inflater.inflate(R.layout.fragment_settings, container, false);
        setRV(A);
        setThreshold(A);
        setPhoneNumber(A);
        return A;
    }

    public void setThreshold(View view){
        TextView threshold_tv;
        SeekBar threshold_sb;

        threshold_sb = view.findViewById(R.id.threshold_seekBar);
        threshold_tv = view.findViewById(R.id.threshold_percentage);

        threshold_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                threshold_tv.setText(i + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setRV(View view) {
        EditText editText_supervisor_no;
        Button button_save;

        editText_supervisor_no = (EditText) view.findViewById(R.id.supervisor_no_ed);
        button_save = (Button) view.findViewById(R.id.save_btn);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supervisor_no = editText_supervisor_no.getText().toString();
            }
        });
    }

    private void setPhoneNumber(View view){
        EditText supervisor_pn = view.findViewById(R.id.supervisor_no_ed);
        Button save = view.findViewById(R.id.save_btn);

        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FallDetectionService.setGlobal_number(supervisor_pn.getText().toString());
                hideKeyboard(getActivity());
                if(supervisor_pn.getText().toString().compareTo("") != 0){
                    Toast.makeText(getActivity(), "Saved !", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}