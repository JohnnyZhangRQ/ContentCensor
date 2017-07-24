package com.swjtu.johnny.contentcensor.ui.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
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
import com.swjtu.johnny.contentcensor.ui.activity.ArticleDetailActivity;
import com.swjtu.johnny.contentcensor.util.BitmapBase64Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny on 2017/3/13.
 */

public class GetUncensoredArticleFragment extends Fragment {
    String TAG = getClass().getSimpleName();

    private View view;
    private TextView tvNoUncensoredArticle;
    private ListView lvUncensoredArticle;
    private static List<Article>  articleList = new ArrayList<>();
    private SwipeRefreshLayout srlUncensoredArticle;
    private ArticleAdapter adapter;

    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_uncensored_article,container,false);

        requestQueue = Volley.newRequestQueue(getActivity());

        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
        lvUncensoredArticle.setAdapter(adapter);
        if (articleList.isEmpty()){
            tvNoUncensoredArticle.setVisibility(View.VISIBLE);
        }else {
            tvNoUncensoredArticle.setVisibility(View.GONE);
        }
    }

    private void initView(){
        tvNoUncensoredArticle = (TextView) view.findViewById(R.id.tv_no_uncensored_article);

        //下拉刷新
        srlUncensoredArticle = (SwipeRefreshLayout) view.findViewById(R.id.srl_uncensored_article);
        srlUncensoredArticle.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        srlUncensoredArticle.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (articleList.isEmpty()){
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://120.76.125.231/content_censor/article/get_uncensored_article.php",null,
                            new Response.Listener<JSONObject>(){
                                @Override
                                public void onResponse(JSONObject response) {
//                                    articleList.clear();
                                    try {
                                        JSONObject param = response.getJSONObject("param");
                                        int count = response.getInt("uncensored_article_count");
                                        if (count == 0){
                                            Toast.makeText(getActivity(),"暂无待审核文章",Toast.LENGTH_SHORT).show();
                                            tvNoUncensoredArticle.setVisibility(View.VISIBLE);
                                        }else {
//                                            for (int i = 0;i<count;i++){
                                                JSONObject articleJson = param.getJSONObject(String.valueOf(0));
                                                String articleId = articleJson.getString("article_id");
                                                Bitmap articleIcon = BitmapBase64Util.base64ToBitmap(articleJson.getString("article_icon"));
                                                String articleTitle = articleJson.getString("article_title");
//                                                String articleContent = articleJson.getString("article_content");
                                                String articleCreateTime = articleJson.getString("article_create_time");

                                                Log.e(TAG,articleId);
//                                                Log.e(TAG,articleIcon);
                                                Log.e(TAG,articleTitle);
//                                                Log.e(TAG,articleContent);
                                                Log.e(TAG,articleCreateTime);

                                                Article article = new Article(Integer.valueOf(articleId),articleIcon,articleTitle,"未审核",articleCreateTime);
                                                articleList.add(article);
                                                adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
                                                lvUncensoredArticle.setAdapter(adapter);
//                                            }
                                            tvNoUncensoredArticle.setVisibility(View.GONE);
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    srlUncensoredArticle.setRefreshing(false);
                                }

                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
                                    srlUncensoredArticle.setRefreshing(false);
                                }
                            });

                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5*1000,1,1.0f));
                    requestQueue.add(jsonObjectRequest);
                }else {
                    Toast.makeText(getActivity(),"您还有未审核的文章",Toast.LENGTH_SHORT).show();
                    srlUncensoredArticle.setRefreshing(false);
                }

            }
        });

        //listView点击事件
        lvUncensoredArticle = (ListView) view.findViewById(R.id.lv_uncensored_article);
        lvUncensoredArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = articleList.get(position);
                int articleId = article.getId();
                String articleCensorState = article.getState();
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_id",articleId);
                intent.putExtra("article_censor_state",articleCensorState);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取articleList
     */
    public static List<Article> getArticleList() {
        return articleList;
    }
}
