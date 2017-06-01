package com.swjtu.johnny.contentcensor.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.adapter.ArticleAdapter;
import com.swjtu.johnny.contentcensor.db.MyDatabaseHelper;
import com.swjtu.johnny.contentcensor.model.Article;
import com.swjtu.johnny.contentcensor.ui.fragment.GetUnapprovedArticleFragment;
import com.swjtu.johnny.contentcensor.ui.fragment.GetUncensoredArticleFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by Johnny on 2017/4/6.
 */

public class ArticleDetailActivity extends Activity {
    String TAG = getClass().getSimpleName();

    private Button btnApproved,btnUnapproved,btnEdit;
    private ImageButton ibBack;
    private TextView tvTitle,tvUnapprovedReason,tvCreateTime,tvCensorTime;
    private int articleId,position;
    private String articleCensorState;

    private RichEditor reArticleDetail;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    private boolean isEditable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_article_detail);

        Intent intent = getIntent();
        articleId = intent.getIntExtra("article_id",0);
        articleCensorState = intent.getStringExtra("article_censor_state");
        position = intent.getIntExtra("position",0);
        Log.e(TAG,articleId+"");
        Log.e(TAG,articleCensorState);
        Log.e(TAG,position+"");

        requestQueue = Volley.newRequestQueue(this);

        initView();
        loadData();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        ibBack = (ImageButton) findViewById(R.id.ib_article_detail_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTitle = (TextView) findViewById(R.id.tv_article_detail_title);
        tvUnapprovedReason = (TextView) findViewById(R.id.tv_unapproved_reason);
        tvCreateTime = (TextView) findViewById(R.id.tv_article_detail_create_time);
        tvCensorTime = (TextView) findViewById(R.id.tv_article_detail_censor_time);

        progressDialog = new ProgressDialog(ArticleDetailActivity.this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);

        reArticleDetail = (RichEditor) findViewById(R.id.re_article_detail);
        reArticleDetail.setPadding(10,10,10,10);
        reArticleDetail.setBackgroundColor(Color.TRANSPARENT);
        reArticleDetail.setEditorBackgroundColor(Color.TRANSPARENT);
        reArticleDetail.setInputEnabled(false);

        btnApproved = (Button) findViewById(R.id.btn_approved);
        btnUnapproved = (Button) findViewById(R.id.btn_unapproved);
        btnEdit = (Button) findViewById(R.id.btn_article_detail_edit);

        //通过
        btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articleCensorState.equals("已通过")){
                    Toast.makeText(ArticleDetailActivity.this,"已是通过审核的文章，无需再次提交", Toast.LENGTH_SHORT).show();
                }else {
                    //获取当前时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    GregorianCalendar nowCalendar = new GregorianCalendar();
                    //获取审核人用户名
                    SharedPreferences pre = getSharedPreferences("user",MODE_PRIVATE);

                    progressDialog.show();
                    Map<String,String> params = new HashMap<>();
                    params.put("article_id",String.valueOf(articleId));
                    params.put("article_censor_time",formatter.format(nowCalendar.getTime()));
                    params.put("article_censor_by",pre.getString("username",""));
                    params.put("article_unapproved_reason",null);
                    params.put("article_censor_state",String.valueOf(1));
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
                                            Toast.makeText(ArticleDetailActivity.this,"审核结果提交成功", Toast.LENGTH_SHORT).show();
                                            if (articleCensorState.equals("未审核")){
                                                GetUncensoredArticleFragment.getArticleList().clear();
                                            }else if (articleCensorState.equals("未通过")){
                                                GetUnapprovedArticleFragment.getArticleList().remove(position);
                                            }
                                            finish();
                                        }else if (result.equals("failed")){
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(ArticleDetailActivity.this,"审核结果提交失败", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArticleDetailActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));

                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        //不通过
        btnUnapproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articleCensorState.equals("未通过")){
                    Toast.makeText(ArticleDetailActivity.this,"已是未通过审核文章，无需再次提交", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(ArticleDetailActivity.this,UnapprovedReasonActivity.class);
                    intent.putExtra("article_id",articleId);
                    intent.putExtra("article_censor_state",articleCensorState);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            }
        });

        //编辑
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditable){
                    reArticleDetail.setInputEnabled(false);
                    btnEdit.setText("编辑");
                    isEditable = !isEditable;

                    progressDialog.show();
                    Map<String,String> params = new HashMap<>();
                    params.put("article_id",String.valueOf(articleId));
                    params.put("article_content",reArticleDetail.getHtml());
                    JSONObject jsonObject = new JSONObject(params);
                    Log.e(TAG,jsonObject.toString());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            "http://120.76.125.231/content_censor/article/edit_article_content.php",jsonObject,
                            new Response.Listener<JSONObject>(){
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String result = response.getString("result");
                                        if (result.equals("succeed")){
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(ArticleDetailActivity.this,"编辑成功", Toast.LENGTH_SHORT).show();
                                        }else if (result.equals("failed")){
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(ArticleDetailActivity.this,"编辑失败", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArticleDetailActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));

                    requestQueue.add(jsonObjectRequest);
                }else {
                    reArticleDetail.setInputEnabled(true);
                    btnEdit.setText("完成");
                    isEditable = !isEditable;
                }
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData(){
        Map<String,String> params = new HashMap<>();
        params.put("article_id",String.valueOf(articleId));
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://120.76.125.231/content_censor/article/get_article_by_id.php",jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e(TAG,response.getString("article_title"));
                            Log.e(TAG,response.getString("article_content"));
                            Log.e(TAG,response.getString("article_unapproved_reason"));
                            Log.e(TAG,response.getString("article_create_time"));
                            Log.e(TAG,response.getString("article_censor_time"));
                            Log.e(TAG,response.getString("article_censor_state"));

                            tvTitle.setText(response.getString("article_title"));
                            reArticleDetail.setHtml(response.getString("article_content"));
                            tvUnapprovedReason.setText(response.getString("article_unapproved_reason"));
                            tvCreateTime.setText(response.getString("article_create_time"));
                            tvCensorTime.setText("审核时间:"+response.getString("article_censor_time"));

                            switch (response.getInt("article_censor_state")){
                                case 0:
                                    tvUnapprovedReason.setVisibility(View.GONE);
                                    tvCensorTime.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    tvUnapprovedReason.setVisibility(View.GONE);
                                    tvCensorTime.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    tvUnapprovedReason.setVisibility(View.VISIBLE);
                                    tvCensorTime.setVisibility(View.VISIBLE);
                                    break;
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ArticleDetailActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));
        requestQueue.add(jsonObjectRequest);
    }
}
