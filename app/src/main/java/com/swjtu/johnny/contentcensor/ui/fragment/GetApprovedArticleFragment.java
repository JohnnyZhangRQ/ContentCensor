package com.swjtu.johnny.contentcensor.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.swjtu.johnny.contentcensor.model.Article;
import com.swjtu.johnny.contentcensor.model.SwipeRefreshView;
import com.swjtu.johnny.contentcensor.ui.activity.ArticleDetailActivity;
import com.swjtu.johnny.contentcensor.util.BitmapBase64Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Johnny on 2017/3/13.
 */

public class GetApprovedArticleFragment extends Fragment {
    String TAG = getClass().getSimpleName();

    private View view;
    private TextView tvNoApprovedArticle;
    private ListView lvApprovedArticle;
    private static List<Article> articleList = new ArrayList<>();
    private ArticleAdapter adapter;
    private SwipeRefreshView srvApprovedArticle;

    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_approved_article,container,false);

        requestQueue = Volley.newRequestQueue(getActivity());

        initView();
        loadDate();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        if (articleList.isEmpty()){
            tvNoApprovedArticle.setVisibility(View.VISIBLE);
        }else {
            tvNoApprovedArticle.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化控件
     */
    private void initView(){
        adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);

        tvNoApprovedArticle = (TextView) view.findViewById(R.id.tv_no_approved_article);

        srvApprovedArticle = (SwipeRefreshView) view.findViewById(R.id.srv_approved_article);
        srvApprovedArticle.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        //下拉刷新
        srvApprovedArticle.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences pre = getActivity().getSharedPreferences("user",getActivity().MODE_PRIVATE);

                Map<String,String> params = new HashMap<>();
                params.put("username",pre.getString("username",""));
                if (articleList.isEmpty()){
                    params.put("article_censor_time","1970-01-01 00:00:00");
                }else {
                    params.put("article_censor_time",articleList.get(0).getTime());
                }
                JSONObject jsonObject = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        "http://120.76.125.231/content_censor/article/get_approved_article.php",jsonObject,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
//                                articleList.clear();
                                try {
                                    JSONObject param = response.getJSONObject("param");
                                    int count = response.getInt("censored_article_count");
                                    if (count == 0){
                                        Toast.makeText(getActivity(),"已是最新数据",Toast.LENGTH_SHORT).show();
                                        if (articleList.isEmpty()) {
                                            tvNoApprovedArticle.setVisibility(View.VISIBLE);
                                        }
                                    }else {
                                        for (int i = count - 1;i >= 0;i--){
                                            JSONObject articleJson = param.getJSONObject(String.valueOf(i));
                                            String articleId = articleJson.getString("article_id");
                                            Bitmap articleIcon = BitmapBase64Util.base64ToBitmap(articleJson.getString("article_icon"));
                                            String articleTitle = articleJson.getString("article_title");
                                            String articleCensorTime = articleJson.getString("article_censor_time");
                                            String articleCensorState = articleJson.getString("article_censor_state");

                                            Log.e(TAG,articleId);
//                                    Log.e(TAG,articleIcon);
                                            Log.e(TAG,articleTitle);
                                            Log.e(TAG,articleCensorTime);
                                            Log.e(TAG,""+articleCensorState);

                                            String state ;
                                            if (Integer.parseInt(articleCensorState) == 1){
                                                state = "已通过";
                                            }else {
                                                state = "未通过";
                                            }

                                            Article article = new Article(Integer.valueOf(articleId),articleIcon,articleTitle,state,articleCensorTime);
                                            articleList.add(0,article);
//                                            adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
                                            lvApprovedArticle.setAdapter(adapter);
                                        }
                                        tvNoApprovedArticle.setVisibility(View.GONE);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                                srvApprovedArticle.setRefreshing(false);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
                        srvApprovedArticle.setRefreshing(false);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));
                requestQueue.add(jsonObjectRequest);
            }
        });
        //上拉加载更多
        srvApprovedArticle.setOnLoadListener(new SwipeRefreshView.OnLoadListener() {
            @Override
            public void onLoad() {
                SharedPreferences pre = getActivity().getSharedPreferences("user",getActivity().MODE_PRIVATE);

                Map<String,String> params = new HashMap<>();
                params.put("username",pre.getString("username",""));
                params.put("article_censor_time",articleList.get(articleList.size()-1).getTime());
                JSONObject jsonObject = new JSONObject(params);

                Log.e(TAG,articleList.get(articleList.size()-1).getTime());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        "http://120.76.125.231/content_censor/article/load_more_approved_article.php",jsonObject,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
//                                articleList.clear();
                                try {
                                    JSONObject param = response.getJSONObject("param");
                                    int count = response.getInt("censored_article_count");
                                    if (count == 0){
                                        Toast.makeText(getActivity(),"已是所有数据",Toast.LENGTH_SHORT).show();
//                                        tvNoApprovedArticle.setVisibility(View.VISIBLE);
                                    }else {
                                        for (int i = 0;i<count;i++){
                                            JSONObject articleJson = param.getJSONObject(String.valueOf(i));
                                            String articleId = articleJson.getString("article_id");
                                            Bitmap articleIcon = BitmapBase64Util.base64ToBitmap(articleJson.getString("article_icon"));
                                            String articleTitle = articleJson.getString("article_title");
                                            String articleCensorTime = articleJson.getString("article_censor_time");
                                            String articleCensorState = articleJson.getString("article_censor_state");

                                            Log.e(TAG,articleId);
//                                    Log.e(TAG,articleIcon);
                                            Log.e(TAG,articleTitle);
                                            Log.e(TAG,articleCensorTime);
                                            Log.e(TAG,""+articleCensorState);

                                            String state ;
                                            if (Integer.parseInt(articleCensorState) == 1){
                                                state = "已通过";
                                            }else {
                                                state = "未通过";
                                            }

                                            Article article = new Article(Integer.valueOf(articleId),articleIcon,articleTitle,state,articleCensorTime);
                                            articleList.add(article);
//                                            adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
                                            lvApprovedArticle.setAdapter(adapter);
                                        }
                                        tvNoApprovedArticle.setVisibility(View.GONE);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                srvApprovedArticle.setLoading(false);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
                        srvApprovedArticle.setLoading(false);
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));
                requestQueue.add(jsonObjectRequest);
            }
        });

        lvApprovedArticle = (ListView) view.findViewById(R.id.lv_approved_article);
        lvApprovedArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,position+"");
                Article article = articleList.get(position);
                String articleCensorState = article.getState();
                int articleId = article.getId();
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_id",articleId);
                intent.putExtra("article_censor_state",articleCensorState);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

    }

    /**
     * 加载数据
     */
    private void loadDate(){
        articleList.clear();
        Log.e(TAG,"resume");

        SharedPreferences pre = getActivity().getSharedPreferences("user",getActivity().MODE_PRIVATE);
        //初始时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        GregorianCalendar calendar = new GregorianCalendar(1970,1,1,0,0,0);

        Map<String,String> params = new HashMap<>();
        params.put("username",pre.getString("username",""));
        params.put("article_censor_time",formatter.format(calendar.getTime()));
        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "http://120.76.125.231/content_censor/article/get_approved_article.php",jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        articleList.clear();
                        try {
                            JSONObject param = response.getJSONObject("param");
                            int count = response.getInt("censored_article_count");
                            if (count == 0){
//                                Toast.makeText(getActivity(),"暂无已审核文章",Toast.LENGTH_SHORT).show();
                                tvNoApprovedArticle.setVisibility(View.VISIBLE);
                            }else {
                                for (int i = 0;i<count;i++){
                                    JSONObject articleJson = param.getJSONObject(String.valueOf(i));
                                    String articleId = articleJson.getString("article_id");
                                    Bitmap articleIcon = BitmapBase64Util.base64ToBitmap(articleJson.getString("article_icon"));
                                    String articleTitle = articleJson.getString("article_title");
                                    String articleCensorTime = articleJson.getString("article_censor_time");
                                    String articleCensorState = articleJson.getString("article_censor_state");

                                    Log.e(TAG,articleId);
//                                    Log.e(TAG,articleIcon);
                                    Log.e(TAG,articleTitle);
                                    Log.e(TAG,articleCensorTime);
                                    Log.e(TAG,""+articleCensorState);

                                    String state ;
                                    if (Integer.parseInt(articleCensorState) == 1){
                                        state = "已通过";
                                    }else {
                                        state = "未通过";
                                    }

                                    Article article = new Article(Integer.valueOf(articleId),articleIcon,articleTitle,state,articleCensorTime);
                                    articleList.add(article);
//                                    adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
                                    lvApprovedArticle.setAdapter(adapter);
                                }
                                tvNoApprovedArticle.setVisibility(View.GONE);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));
        requestQueue.add(jsonObjectRequest);
    }

    public static List<Article> getArticleList() {
        return articleList;
    }

}
