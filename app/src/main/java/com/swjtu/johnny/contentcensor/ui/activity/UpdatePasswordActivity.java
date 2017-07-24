package com.swjtu.johnny.contentcensor.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.model.ActivityCollector;
import com.swjtu.johnny.contentcensor.model.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Johnny on 2017/4/12.
 */

public class UpdatePasswordActivity extends BaseActivity {
    private String TAG = getClass().getSimpleName();

    private ProgressDialog progressDialog;

    private EditText etOldPassword,etNewPassword,etNewPasswordCheck;
    private Button btnUpdatePasswordSure;
    private ImageButton ibBack;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        requestQueue = Volley.newRequestQueue(this);

        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initView(){
        progressDialog = new ProgressDialog(UpdatePasswordActivity.this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);

        //返回按钮事件
        ibBack = (ImageButton) findViewById(R.id.ib_update_password_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //输入框事件监听，用于改变确认按钮的状态
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etNewPasswordCheck = (EditText) findViewById(R.id.et_new_password_check);
        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etOldPassword.getText().toString().isEmpty()||
                        etNewPassword.getText().toString().isEmpty()||
                        etNewPasswordCheck.getText().toString().isEmpty()){
                    btnUpdatePasswordSure.setEnabled(false);
                }else {
                    btnUpdatePasswordSure.setEnabled(true);
                }
            }
        });
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etOldPassword.getText().toString().isEmpty()||
                        etNewPassword.getText().toString().isEmpty()||
                        etNewPasswordCheck.getText().toString().isEmpty()){
                    btnUpdatePasswordSure.setEnabled(false);
                }else {
                    btnUpdatePasswordSure.setEnabled(true);
                }
            }
        });
        etNewPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etOldPassword.getText().toString().isEmpty()||
                        etNewPassword.getText().toString().isEmpty()||
                        etNewPasswordCheck.getText().toString().isEmpty()){
                    btnUpdatePasswordSure.setEnabled(false);
                }else {
                    btnUpdatePasswordSure.setEnabled(true);
                }
            }
        });

        //确定修改密码按钮事件
        btnUpdatePasswordSure = (Button) findViewById(R.id.btn_update_password_sure);
        btnUpdatePasswordSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取审核人用户名
                SharedPreferences pre = getSharedPreferences("user",MODE_PRIVATE);

                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String newPasswordCheck = etNewPasswordCheck.getText().toString();

                if (!oldPassword.equals(pre.getString("password",""))){
                    Toast.makeText(UpdatePasswordActivity.this,"原密码错误",Toast.LENGTH_SHORT).show();
                }else if (newPassword.length() < 8||newPasswordCheck.length() < 8){
                    Toast.makeText(UpdatePasswordActivity.this,"密码不能少于8位",Toast.LENGTH_SHORT).show();
                }else if (!newPassword.equals(newPasswordCheck)){
                    Toast.makeText(UpdatePasswordActivity.this,"两次输入密码不相同",Toast.LENGTH_SHORT).show();
                }else if (oldPassword.equals(newPassword)){
                    Toast.makeText(UpdatePasswordActivity.this,"新密码不能与原密码相同",Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    //提交到服务器
                    Map<String,String> params = new HashMap<>();
                    params.put("username",pre.getString("username",""));
                    params.put("password",newPassword);
                    JSONObject jsonObject = new JSONObject(params);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            "http://120.76.125.231/content_censor/login/update_password.php", jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String result = response.getString("result");
                                        if (result.equals("succeed")){
                                            Log.e(TAG,"修改密码成功");
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            ActivityCollector.finishAll();
                                            Intent intent = new Intent(UpdatePasswordActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(UpdatePasswordActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                                        }else if (result.equals("failed")){
                                            Log.e(TAG,"修改密码失败");
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(UpdatePasswordActivity.this,"修改密码失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,error.getMessage());
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(UpdatePasswordActivity.this,"网络连接出错",Toast.LENGTH_SHORT).show();
                        }
                    });

                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));

                    requestQueue.add(jsonObjectRequest);
                }
            }
        });
    }
}
