package com.swjtu.johnny.contentcensor.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.swjtu.johnny.contentcensor.db.MyDatabaseHelper;
import com.swjtu.johnny.contentcensor.ui.fragment.GetApprovedArticleFragment;
import com.swjtu.johnny.contentcensor.ui.fragment.GetUncensoredArticleFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Johnny on 2017/4/9.
 */

public class UnapprovedReasonActivity extends Activity {
    String TAG = getClass().getSimpleName();

    private CheckBox cbPoliticallySensitive,cbRumor,cbPornographic,cbOtherReason;
    private EditText etOtherReason;
    private Button btnUnapprovedReasonSure;
    private ImageButton ibBack;
    private int articleId,position;
    private String articleCensorState;

//    private MyDatabaseHelper dbHelper;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unapproved_reason);

//        dbHelper = new MyDatabaseHelper(UnapprovedReasonActivity.this,"ContentCensor.db",null,1);

        Intent intent = getIntent();
        articleId = intent.getIntExtra("article_id",0);
        articleCensorState = intent.getStringExtra("article_censor_state");
        position = intent.getIntExtra("position",0);

        requestQueue = Volley.newRequestQueue(this);

        initView();
    }

    private void initView(){
        progressDialog = new ProgressDialog(UnapprovedReasonActivity.this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);

        ibBack = (ImageButton) findViewById(R.id.ib_unapproved_reason_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cbPoliticallySensitive = (CheckBox) findViewById(R.id.cb_politically_sensitive);
        cbRumor = (CheckBox) findViewById(R.id.cb_rumor);
        cbPornographic = (CheckBox) findViewById(R.id.cb_pornographic);
        cbOtherReason = (CheckBox) findViewById(R.id.cb_other_reason);

        cbOtherReason.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    etOtherReason.setEnabled(true);
                }else {
                    etOtherReason.setEnabled(false);
                }
            }
        });

        etOtherReason = (EditText) findViewById(R.id.et_other_reason);

        btnUnapprovedReasonSure = (Button) findViewById(R.id.btn_unapproved_reason_sure);
        btnUnapprovedReasonSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unapprovedReason = "";
                if (!(cbPoliticallySensitive.isChecked()||cbRumor.isChecked()||cbPornographic.isChecked()||cbOtherReason.isChecked())){
                    Toast.makeText(UnapprovedReasonActivity.this,"请至少选择一个理由",Toast.LENGTH_SHORT).show();
                }else if (cbOtherReason.isChecked()&&etOtherReason.getText().toString().isEmpty()) {
                    Toast.makeText(UnapprovedReasonActivity.this,"其他理由不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if (cbPoliticallySensitive.isChecked()){
                        unapprovedReason = unapprovedReason+cbPoliticallySensitive.getText().toString();
                    }
                    if (cbRumor.isChecked()){
                        if (unapprovedReason.equals("")){
                            unapprovedReason = unapprovedReason+cbRumor.getText().toString();
                        }else {
                            unapprovedReason = unapprovedReason+"\n"+cbRumor.getText().toString();
                        }
                    }
                    if (cbPornographic.isChecked()){
                        if (unapprovedReason.equals("")){
                            unapprovedReason = unapprovedReason+cbPornographic.getText().toString();
                        }else {
                            unapprovedReason = unapprovedReason+"\n"+cbPornographic.getText().toString();
                        }
                    }
                    if (cbOtherReason.isChecked()){
                        if (unapprovedReason.equals("")){
                            unapprovedReason = unapprovedReason+etOtherReason.getText().toString();
                        }else {
                            unapprovedReason = unapprovedReason+"\n"+etOtherReason.getText().toString();
                        }
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    GregorianCalendar nowCalendar = new GregorianCalendar();
                    //获取审核人用户名
                    SharedPreferences pre = getSharedPreferences("user",MODE_PRIVATE);

                    progressDialog.show();
                    Map<String,String> params = new HashMap<>();
                    params.put("article_id",String.valueOf(articleId));
                    params.put("article_censor_time",formatter.format(nowCalendar.getTime()));
                    params.put("article_censor_by",pre.getString("username",""));
                    params.put("article_unapproved_reason",unapprovedReason);
                    params.put("article_censor_state",String.valueOf(2));
                    JSONObject jsonObject = new JSONObject(params);
                    Log.e(TAG,jsonObject.toString());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            "http://120.76.125.231/content_censor/article/censor_article.php",jsonObject,
                            new Response.Listener<JSONObject>(){
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String result = response.getString("result");
                                        if (result.equals("succeed")){
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(UnapprovedReasonActivity.this,"审核结果提交成功", Toast.LENGTH_SHORT).show();
                                            if (articleCensorState.equals("未审核")){
                                                GetUncensoredArticleFragment.getArticleList().clear();
                                            }else if (articleCensorState.equals("已通过")){
                                                GetApprovedArticleFragment.getArticleList().remove(position);
//                                                GetApprovedArticleFragment.getAdapter().notifyDataSetChanged();
                                            }
                                            Intent intent = new Intent(UnapprovedReasonActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }else if (result.equals("failed")){
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(UnapprovedReasonActivity.this,"审核结果提交失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                            },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(UnapprovedReasonActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));

                    requestQueue.add(jsonObjectRequest);
                }
            }
        });
    }
}
