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

public class RegisterFragment extends Fragment{
    private View view;
    private LoginRegisterActivity activity;
    private EditText etRegisterUsername,etRegisterPassword,etRegisterPasswordCheck;
    private ImageButton ibClearUsername,ibClearPassword,ibClearPasswordCheck;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register,container,false);
        activity = (LoginRegisterActivity) getActivity();
        initView();
        return view;
    }

    //初始化控件
    private void initView(){
        etRegisterUsername = (EditText) view.findViewById(R.id.et_register_username);
        etRegisterPassword = (EditText) view.findViewById(R.id.et_register_password);
        etRegisterPasswordCheck = (EditText) view.findViewById(R.id.et_register_password_check);
        ibClearUsername = (ImageButton) view.findViewById(R.id.ib_register_clear_username);
        ibClearPassword = (ImageButton) view.findViewById(R.id.ib_register_clear_password);
        ibClearPasswordCheck = (ImageButton) view.findViewById(R.id.ib_register_clear_password_check);

        //用户名输入框内容改变事件监听
        etRegisterUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //内容改变后
            @Override
            public void afterTextChanged(Editable s) {
                if (etRegisterUsername.getText().toString().isEmpty()){
                    ibClearUsername.setVisibility(View.GONE);
                    activity.getBtnRegister().setEnabled(false);
                }else {
                    ibClearUsername.setVisibility(View.VISIBLE);
                    if (etRegisterPassword.getText().toString().isEmpty()||
                            etRegisterPasswordCheck.getText().toString().isEmpty()){
                        activity.getBtnRegister().setEnabled(false);
                    }else {
                        activity.getBtnRegister().setEnabled(true);
                    }
                }
            }
        });

        //密码输入框内容改变事件监听
        etRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //内容改变后
            @Override
            public void afterTextChanged(Editable s) {
                if (etRegisterPassword.getText().toString().isEmpty()){
                    ibClearPassword.setVisibility(View.GONE);
                    activity.getBtnRegister().setEnabled(false);
                }else {
                    ibClearPassword.setVisibility(View.VISIBLE);
                    if (etRegisterUsername.getText().toString().isEmpty()||
                            etRegisterPasswordCheck.getText().toString().isEmpty()){
                        activity.getBtnRegister().setEnabled(false);
                    }else {
                        activity.getBtnRegister().setEnabled(true);
                    }
                }
            }
        });

        //确认密码输入框内容改变事件监听
        etRegisterPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //内容改变后
            @Override
            public void afterTextChanged(Editable s) {
                if (etRegisterPasswordCheck.getText().toString().isEmpty()){
                    ibClearPasswordCheck.setVisibility(View.GONE);
                    activity.getBtnRegister().setEnabled(false);
                }else {
                    ibClearPasswordCheck.setVisibility(View.VISIBLE);
                    if (etRegisterUsername.getText().toString().isEmpty()||
                            etRegisterPassword.getText().toString().isEmpty()){
                        activity.getBtnRegister().setEnabled(false);
                    }else {
                        activity.getBtnRegister().setEnabled(true);
                    }
                }
            }
        });

        //清空用户名输入框
        ibClearUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRegisterUsername.setText(null);
            }
        });

        //清空密码输入框
        ibClearPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRegisterPassword.setText(null);
            }
        });

        //清空确认密码输入框
        ibClearPasswordCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRegisterPasswordCheck.setText(null);
            }
        });
    }

    /**
     * fragment界面切换动画监听
     * @param transit
     * @param enter
     * @param nextAnim
     * @return
     */
    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator;
        try{
            animator =  AnimatorInflater.loadAnimator(getActivity(),nextAnim);
            if (enter){
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {//动画开始
//                        super.onAnimationStart(animation);
                        Log.e("register","start");
                        //隐藏登录相关组件
                        activity.getTvLoginToRegister().setVisibility(View.GONE);
                        activity.getBtnLogin().setVisibility(View.GONE);
                        //显示注册相关组件
                        activity.getTvRegisterToLogin().setVisibility(View.VISIBLE);
                        activity.getBtnRegister().setVisibility(View.VISIBLE);
                        //设置注册切换到登录为不可点击
                        activity.getTvRegisterToLogin().setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
                        Log.e("register","end");
                        //设置注册切换到登录为可点击
                        activity.getTvRegisterToLogin().setClickable(true);
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

    public EditText getEtRegisterUsername() {
        return etRegisterUsername;
    }

    public EditText getEtRegisterPassword() {
        return etRegisterPassword;
    }

    public EditText getEtRegisterPasswordCheck() {
        return etRegisterPasswordCheck;
    }
}
