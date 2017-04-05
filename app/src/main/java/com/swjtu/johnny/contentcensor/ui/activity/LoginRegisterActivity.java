package com.swjtu.johnny.contentcensor.ui.activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.ui.fragment.LoginFragment;
import com.swjtu.johnny.contentcensor.ui.fragment.RegisterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny on 2017/3/11.
 */

public class LoginRegisterActivity extends FragmentActivity {
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";

    private TextView tvLoginToRegister,tvRegisterToLogin;
    private Button btnLogin,btnRegister;
    private FragmentManager fragmentManager;
    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragmentList = new ArrayList<>();
    private int currentIndex = 0;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        fragmentManager = getFragmentManager();
        initView();

        if (savedInstanceState != null){//内存重启时调用
            //获取内存重启时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT,0);

            fragmentList.removeAll(fragmentList);
            fragmentList.add(fragmentManager.findFragmentByTag(0+""));
            fragmentList.add(fragmentManager.findFragmentByTag(1+""));

            //恢复fragment页面
            restoreFragment();
        }else {//正常是调用
            LoginFragment loginFragment = new LoginFragment();
            RegisterFragment registerFragment = new RegisterFragment();
            fragmentList.add(loginFragment);
            fragmentList.add(registerFragment);

            switchFragment();
        }
    }

    //初始化控件
    private void initView(){
        tvLoginToRegister = (TextView) findViewById(R.id.tv_login_to_register);
        tvRegisterToLogin = (TextView) findViewById(R.id.tv_register_to_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);

//        loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(0+"");
//        registerFragment = (RegisterFragment) getFragmentManager().findFragmentByTag(1+"");

        //切换到注册界面点击事件
        tvLoginToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = 1;
                switchFragment();
            }
        });

        //切换到登录界面点击事件
        tvRegisterToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = 0;
                switchFragment();
            }
        });

        //登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFragment = (LoginFragment) currentFragment;
                String username = loginFragment.getEtLoginUsername().getText().toString();
                String password = loginFragment.getEtLoginPassword().getText().toString();

                if (username.equals("admin")&&password.equals("123456")){
                    Intent intent = new Intent(LoginRegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginRegisterActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginRegisterActivity.this,"username:"+username+"\n"+"password:"+password,Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFragment = (RegisterFragment) currentFragment;
                String username = registerFragment.getEtRegisterUsername().getText().toString();
                String password = registerFragment.getEtRegisterPassword().getText().toString();
                String passwordCheck = registerFragment.getEtRegisterPasswordCheck().getText().toString();
                Toast.makeText(LoginRegisterActivity.this,"username:"+username+"\n"+"password:"+password+"\n"+"passwordCheck:"+passwordCheck,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //内存重启时保存当前fragment名字
        outState.putInt(CURRENT_FRAGMENT,currentIndex);
        super.onSaveInstanceState(outState);
    }

    /**
     * 切换界面
     */
    private void switchFragment(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
//        transaction.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);

        if (!fragmentList.get(currentIndex).isAdded()){
            if (currentIndex == 1){
                transaction.setCustomAnimations(R.animator.fragment_slide_right_in,
                        R.animator.fragment_slide_left_out
                );
            }
            transaction.hide(currentFragment).add(R.id.fl_login_register,fragmentList.get(currentIndex),""+currentIndex);
        }else {
            if (currentIndex == 0){
                transaction.setCustomAnimations(R.animator.fragment_slide_left_in,
                        R.animator.fragment_slide_right_out
                );
            }else {
                transaction.setCustomAnimations(R.animator.fragment_slide_right_in,
                        R.animator.fragment_slide_left_out
                );
            }
            transaction.hide(currentFragment).show(fragmentList.get(currentIndex));
        }

        currentFragment = fragmentList.get(currentIndex);

        transaction.commit();
    }

    /**
     *恢复fragment
     */
    private void restoreFragment(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i<fragmentList.size(); i++){
            if (i == currentIndex){
                transaction.show(fragmentList.get(i));
            }else {
                transaction.hide(fragmentList.get(i));
            }
        }

        transaction.commit();

        //记录当前显示的fragment
        currentFragment = fragmentList.get(currentIndex);
    }

    //设置状态栏为透明，并取消状态栏原本所占的空间
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //获取各控件实例
    public TextView getTvLoginToRegister() {
        return tvLoginToRegister;
    }

    public TextView getTvRegisterToLogin() {
        return tvRegisterToLogin;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public Button getBtnRegister() {
        return btnRegister;
    }
}
