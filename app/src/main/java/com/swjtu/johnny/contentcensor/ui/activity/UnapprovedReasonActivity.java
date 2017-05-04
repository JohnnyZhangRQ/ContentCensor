package com.swjtu.johnny.contentcensor.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.db.MyDatabaseHelper;

/**
 * Created by Johnny on 2017/4/9.
 */

public class UnapprovedReasonActivity extends Activity {
    private CheckBox cbPoliticallySensitive,cbRumor,cbPornographic,cbOtherReason;
    private EditText etOtherReason;
    private Button btnUnapprovedReasonSure;
    private int articleId;

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unapproved_reason);

        dbHelper = new MyDatabaseHelper(UnapprovedReasonActivity.this,"ContentCensor.db",null,1);


        Intent intent = getIntent();
        articleId = intent.getIntExtra("article_id",0);

        initView();
    }

    private void initView(){
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
                        unapprovedReason = unapprovedReason+"\n"+cbRumor.getText().toString();
                    }
                    if (cbPornographic.isChecked()){
                        unapprovedReason = unapprovedReason+"\n"+cbPornographic.getText().toString();
                    }
                    if (cbOtherReason.isChecked()){
                        unapprovedReason = unapprovedReason+"\n"+etOtherReason.getText().toString();
                    }

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("state","未通过");
                    db.update("Article",values,"id = ?",new String[]{""+articleId});
                    Toast.makeText(UnapprovedReasonActivity.this,"审核结果已提交，未通过"+"\n"+"理由:"+unapprovedReason,Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UnapprovedReasonActivity.this, ArticleDetailActivity.class);
        intent.putExtra("article_id",articleId);
        startActivity(intent);
        finish();
    }
}
