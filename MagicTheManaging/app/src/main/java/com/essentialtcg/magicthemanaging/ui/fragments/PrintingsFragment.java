package com.essentialtcg.magicthemanaging.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.essentialtcg.magicthemanaging.R;

/**
 * Created by Shawn on 4/14/2016.
 */
public class PrintingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_printings, container, false);
    }
}
