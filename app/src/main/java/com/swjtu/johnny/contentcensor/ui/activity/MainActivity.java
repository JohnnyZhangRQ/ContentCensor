package com.swjtu.johnny.contentcensor.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.adapter.MainFragmentPagerAdapter;
import com.swjtu.johnny.contentcensor.ui.fragment.GetApprovedArticleFragment;
import com.swjtu.johnny.contentcensor.ui.fragment.GetUnapprovedArticleFragment;
import com.swjtu.johnny.contentcensor.ui.fragment.GetUncensoredArticleFragment;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/3/13.
 */

public class MainActivity extends FragmentActivity {
    private ViewPager vpMain;
    private RadioGroup rgTabMain;
    private RadioButton rbGetUncensoredArticle, rbGetApprovedArticle,rbGetUnapprovedArticle;
    private TextView tvMainTitle;
    private ArrayList<Fragment> fragmentList;
    private View paddingView;
    private ImageView ivSet;
    private Button btnUpdatePassword,btnExit;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        initView();
        initViewPager();
    }

    //初始化控件
    private void initView(){
        rgTabMain = (RadioGroup) findViewById(R.id.rg_tab_main);

        rbGetUncensoredArticle = (RadioButton) findViewById(R.id.rb_get_uncensored_article);
        rbGetApprovedArticle = (RadioButton) findViewById(R.id.rb_get_approved_article);
        rbGetUnapprovedArticle = (RadioButton) findViewById(R.id.rb_get_unapproved_article);

        tvMainTitle = (TextView) findViewById(R.id.tv_main_title);

        ivSet = (ImageView) findViewById(R.id.iv_set);
        btnUpdatePassword = (Button) findViewById(R.id.btn_set_update_password);
        btnExit = (Button) findViewById(R.id.btn_set_exit);

        ivSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);
                Intent intent = new Intent(MainActivity.this,UpdatePasswordActivity.class);
                startActivity(intent);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //获取顶部View，动态设置高度
        paddingView = findViewById(R.id.paddingView);
        ViewGroup.LayoutParams params = paddingView.getLayoutParams();
        params.height = getStatusBarHeight();

        rgTabMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                //获取当前被选中的RadioButton的ID，用于改变ViewPager的当前页
                int current = 0;
                switch (checkedId){
                    case R.id.rb_get_uncensored_article:
                        current = 0;
                        break;
                    case R.id.rb_get_approved_article:
                        current = 1;
                        break;
                    case R.id.rb_get_unapproved_article:
                        current = 2;
                        break;
                }

                if (vpMain.getCurrentItem() != current){
                    vpMain.setCurrentItem(current);
                }
            }
        });
    }

    //初始化ViewPager
    private void initViewPager(){
        vpMain = (ViewPager) findViewById(R.id.vp_main);

        fragmentList = new ArrayList<>();

        Fragment getUncensoredArticleFragment = new GetUncensoredArticleFragment();
        Fragment getApprovedArticleFragment = new GetApprovedArticleFragment();
        Fragment getUnapprovedArticleFragment = new GetUnapprovedArticleFragment();

        //将各Fragment加入到数组中
        fragmentList.add(getUncensoredArticleFragment);
        fragmentList.add(getApprovedArticleFragment);
        fragmentList.add(getUnapprovedArticleFragment);

        //设置ViewPager适配器
        vpMain.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(),fragmentList));

        //当前为第一个页面
        vpMain.setCurrentItem(0);

        //ViewPager页面改变监听
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //获取当前界面用于改变对应RadioButton的状态
                int current = vpMain.getCurrentItem();
                switch (current){
                    case 0:
                        rgTabMain.check(R.id.rb_get_uncensored_article);
                        tvMainTitle.setText("未审核");
                        break;
                    case 1:
                        rgTabMain.check(R.id.rb_get_approved_article);
                        tvMainTitle.setText("已通过");
                        break;
                    case 2:
                        rgTabMain.check(R.id.rb_get_unapproved_article);
                        tvMainTitle.setText("未通过");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    //获取当前设备状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
