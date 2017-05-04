package com.swjtu.johnny.contentcensor.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class GetCensoredArticleFragment extends Fragment {
    private View view;
    private TextView tvNoCensoredArticle;
    private ListView lvCensoredArticle;
    private MyDatabaseHelper dbHelper;
    private List<Article> articleList = new ArrayList<>();
    private ArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_censored_article,container,false);
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
        Cursor cursor = db.query("Article",null,"state != ?",new String[]{"未审核"},null,null,null);
        if (cursor.moveToFirst()){
            tvNoCensoredArticle.setVisibility(View.GONE);
            do {
                //遍历cursor对象，取出数据并打印
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String state = cursor.getString(cursor.getColumnIndex("state"));

                Article article = new Article(id,title,state,"");
                articleList.add(article);
            }while (cursor.moveToNext());
        }else {
            tvNoCensoredArticle.setVisibility(View.VISIBLE);
        }
        adapter = new ArticleAdapter(getActivity(),R.layout.list_article,articleList);
        lvCensoredArticle.setAdapter(adapter);
        cursor.close();
    }

    private void initView(){
        tvNoCensoredArticle = (TextView) view.findViewById(R.id.tv_no_censored_article);
        lvCensoredArticle = (ListView) view.findViewById(R.id.lv_censored_article);

        lvCensoredArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
