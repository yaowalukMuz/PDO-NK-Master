package com.mist.it.pod_nk;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Yaowaluk on 20/07/2560.
 */

public class SomeFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saveInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.dialog_return, container, false);
       //ButterKnife.inject(this, rootView);
        return rootView;
    }

}
