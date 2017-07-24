package com.swjtu.johnny.contentcensor.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.swjtu.johnny.contentcensor.model.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Johnny on 2017/4/22.
 */

public class LoginActivity extends BaseActivity {
    private EditText etUsername,etPassword;
    private ImageButton ibClearUsername,ibClearPassword;
    private Button btnLogin;

    private RequestQueue requestQueue;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        initView();
    }

    private void initView(){
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);

        etUsername = (EditText) findViewById(R.id.et_login_username);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        ibClearUsername = (ImageButton) findViewById(R.id.ib_login_clear_username);
        ibClearPassword = (ImageButton) findViewById(R.id.ib_login_clear_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        //用户名、密码输入框内容改变事件监听
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(etUsername.getText().toString().isEmpty()){
                    ibClearUsername.setVisibility(View.GONE);
                    btnLogin.setEnabled(false);
                }else {
                    ibClearUsername.setVisibility(View.VISIBLE);
                    if (!etPassword.getText().toString().isEmpty()){
                        btnLogin.setEnabled(true);
                    }else {
                        btnLogin.setEnabled(false);
                    }
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPassword.getText().toString().isEmpty()){
                    ibClearPassword.setVisibility(View.GONE);
                    btnLogin.setEnabled(false);
                }else {
                    ibClearPassword.setVisibility(View.VISIBLE);
                    if (!etUsername.getText().toString().isEmpty()){
                        btnLogin.setEnabled(true);
                    }else {
                        btnLogin.setEnabled(false);
                    }
                }
            }
        });

        //输入框清空按钮事件监听
        ibClearUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setText(null);
            }
        });
        ibClearPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setText(null);
            }
        });

        //登录按钮事件监听
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                progressDialog.show();

                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);
                JSONObject jsonObject = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        "http://120.76.125.231/content_censor/login/login.php",jsonObject,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String result = response.getString("result");
                                    if (result.equals("succeed")){
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        SharedPreferences.Editor editor  = getSharedPreferences("user",MODE_PRIVATE).edit();
                                        editor.putString("username",username);
                                        editor.putString("password",password);
                                        editor.apply();
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (result.equals("failed")){
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(LoginActivity.this,"用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this,"网络连接出错", Toast.LENGTH_SHORT).show();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));
                requestQueue.add(jsonObjectRequest);
            }
        });
    }
}
