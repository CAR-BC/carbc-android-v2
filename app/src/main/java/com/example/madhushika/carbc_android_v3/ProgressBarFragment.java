package com.example.madhushika.carbc_android_v3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressBarFragment extends Fragment {

    public android.widget.ProgressBar progressBar;

    public ProgressBarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_bar, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_item);
        return view;
    }

}
