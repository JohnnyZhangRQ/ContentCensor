package com.swjtu.johnny.contentcensor.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.adapter.ArticleAdapter;
import com.swjtu.johnny.contentcensor.db.MyDatabaseHelper;
import com.swjtu.johnny.contentcensor.model.Article;

/**
 * Created by Johnny on 2017/4/6.
 */

public class ArticleDetailActivity extends Activity {
    private TextView tvArticleTitle,tvArticleContent;
    private Button btnApproved,btnUnapproved;
    private int articleId;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_article_detail);

        dbHelper = new MyDatabaseHelper(ArticleDetailActivity.this,"ContentCensor.db",null,1);

        Intent intent = getIntent();
        articleId = intent.getIntExtra("article_id",0);

        initView();
    }

    private void initView(){
        tvArticleTitle = (TextView) findViewById(R.id.tv_article_detail_title);
        tvArticleContent = (TextView) findViewById(R.id.tv_article_detail_content);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //查询Book表中的所有数据
        Cursor cursor = db.query("Article",null,"id = ?",new String[]{""+articleId},null,null,null);
        if (cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));

                tvArticleTitle.setText(title);
                tvArticleContent.setText(content);
            }while (cursor.moveToNext());
        }
        cursor.close();

        btnApproved = (Button) findViewById(R.id.btn_approved);
        btnUnapproved = (Button) findViewById(R.id.btn_unapproved);

        btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("state","已通过");
                db.update("Article",values,"id = ?",new String[]{""+articleId});
                Toast.makeText(ArticleDetailActivity.this,"审核结果已提交，已通过",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnUnapproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleDetailActivity.this,UnapprovedReasonActivity.class);
                intent.putExtra("article_id",articleId);
                startActivity(intent);
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();
//                values.put("state","未通过");
//                db.update("Article",values,"id = ?",new String[]{""+articleId});
//                Toast.makeText(ArticleDetailActivity.this,"审核结果已提交，未通过",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
