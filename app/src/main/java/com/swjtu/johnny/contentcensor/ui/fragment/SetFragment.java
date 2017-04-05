package com.swjtu.johnny.contentcensor.ui.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.ui.activity.LoginRegisterActivity;

/**
 * Created by Johnny on 2017/3/13.
 */

public class SetFragment extends Fragment {
    private View view;
    private Button btnExit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set,container,false);
        initView();
        return view;
    }

    private void initView(){
        btnExit = (Button) view.findViewById(R.id.btn_set_exit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
