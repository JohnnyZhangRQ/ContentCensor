package com.swjtu.johnny.contentcensor.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swjtu.johnny.contentcensor.R;

/**
 * Created by Johnny on 2017/3/13.
 */

public class GetCensoredArticleFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_censored_article,container,false);
        return view;
    }
}
