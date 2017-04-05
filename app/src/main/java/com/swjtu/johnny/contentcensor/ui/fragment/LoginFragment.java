package com.swjtu.johnny.contentcensor.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.ui.activity.LoginRegisterActivity;

/**
 * Created by Johnny on 2017/3/13.
 */

public class LoginFragment extends Fragment {
    private View view;
    private EditText etLoginUsername,etLoginPassword;
    private LoginRegisterActivity activity;
    private ImageButton ibClearUsername,ibClearPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login,container,false);
        activity  = (LoginRegisterActivity) getActivity();
        initView();
        return view;
    }

    //初始化控件
    private void initView(){
        etLoginUsername = (EditText) view.findViewById(R.id.et_login_username);
        etLoginPassword = (EditText) view.findViewById(R.id.et_login_password);
        ibClearUsername = (ImageButton) view.findViewById(R.id.ib_login_clear_username);
        ibClearPassword = (ImageButton) view.findViewById(R.id.ib_login_clear_password);

        //用户名输入框内容改变时间监听
        etLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //内容改变后
            @Override
            public void afterTextChanged(Editable s) {
                if(etLoginUsername.getText().toString().isEmpty()){
                    ibClearUsername.setVisibility(View.GONE);
                    activity.getBtnLogin().setEnabled(false);
                }else {
                    ibClearUsername.setVisibility(View.VISIBLE);
                    if (!etLoginPassword.getText().toString().isEmpty()){
                        activity.getBtnLogin().setEnabled(true);
                    }else {
                        activity.getBtnLogin().setEnabled(false);
                    }
                }
            }
        });

        //密码输入框内容改变事件监听
        etLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //内容改变后
            @Override
            public void afterTextChanged(Editable s) {
                if (etLoginPassword.getText().toString().isEmpty()){
                    ibClearPassword.setVisibility(View.GONE);
                    activity.getBtnLogin().setEnabled(false);
                }else {
                    ibClearPassword.setVisibility(View.VISIBLE);
                    if (!etLoginUsername.getText().toString().isEmpty()){
                        activity.getBtnLogin().setEnabled(true);
                    }else {
                        activity.getBtnLogin().setEnabled(false);
                    }
                }
            }
        });

        //清空用户名输入框
        ibClearUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLoginUsername.setText(null);
            }
        });

        //清空密码输入框
        ibClearPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLoginPassword.setText(null);
            }
        });
    }

    /**
     *fragment界面切换动画监听
     * @param transit
     * @param enter
     * @param nextAnim
     * @return
     */
    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator;
        try {
            animator = AnimatorInflater.loadAnimator(getActivity(),nextAnim);
            if (enter){
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {//动画开始
//                    super.onAnimationStart(animation);
                        Log.e("login","start");
                        //隐藏注册相关组件
                        activity.getTvRegisterToLogin().setVisibility(View.GONE);
                        activity.getBtnRegister().setVisibility(View.GONE);
                        //显示登录相关组件
                        activity.getTvLoginToRegister().setVisibility(View.VISIBLE);
                        activity.getBtnLogin().setVisibility(View.VISIBLE);
                        //设置登录切换到注册为不可点击
                        activity.getTvLoginToRegister().setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {//动画结束
//                    super.onAnimationEnd(animation);
                        Log.e("login","end");
                        //设置登录切换到注册为可点击
                        activity.getTvLoginToRegister().setClickable(true);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return animator;
    }

    //获取EditText实例
    public EditText getEtLoginUsername() {
        return etLoginUsername;
    }

    public EditText getEtLoginPassword() {
        return etLoginPassword;
    }
}
