package com.swjtu.johnny.contentcensor.ui.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.adapter.ArticleAdapter;
import com.swjtu.johnny.contentcensor.db.MyDatabaseHelper;
import com.swjtu.johnny.contentcensor.model.Article;
import com.swjtu.johnny.contentcensor.ui.activity.ArticleDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny on 2017/3/13.
 */

public class GetUncensoredArticleFragment extends Fragment {
    private View view;
    private TextView tvNoUncensoredArticle;
    private ListView lvUncensoredArticle;
    private MyDatabaseHelper dbHelper;
    private List<Article> articleList = new ArrayList<>();
    private SwipeRefreshLayout srlUncensoredArticle;
    private ArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_uncensored_article,container,false);
        dbHelper = new MyDatabaseHelper(getActivity(),"ContentCensor.db",null,1);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        articleList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //查询Book表中的所有数据
        Cursor cursor = db.query("Article",null,"state = ?",new String[]{"未审核"},null,null,null);
        if (cursor.moveToFirst()){
            tvNoUncensoredArticle.setVisibility(View.GONE);
            do {
                //遍历cursor对象，取出数据并打印
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String state = cursor.getString(cursor.getColumnIndex("state"));

                Article article = new Article(id,title,state,"");
                articleList.add(article);
            }while (cursor.moveToNext());
        }else {
            tvNoUncensoredArticle.setVisibility(View.VISIBLE);
        }
        adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
        lvUncensoredArticle.setAdapter(adapter);
        cursor.close();
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
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    //插入数据
                    values.put("title","测试标题");
                    values.put("content", "测试正文");
                    values.put("state", "未审核");
                    values.put("time", "");
                    db.insert("Article", null, values);
                    values.clear();
                }else {
                    Toast.makeText(getActivity(),"您还有未审核的文章",Toast.LENGTH_SHORT).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                     public void run() {
                        onResume();
                        srlUncensoredArticle.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        //listView点击事件
        lvUncensoredArticle = (ListView) view.findViewById(R.id.lv_uncensored_article);
        lvUncensoredArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = articleList.get(position);
                int articleId = article.getId();
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("article_id",articleId);
                startActivity(intent);
            }
        });
    }
}
