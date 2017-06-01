package com.swjtu.johnny.contentcensor.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.johnny.contentcensor.R;
import com.swjtu.johnny.contentcensor.model.Article;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Johnny on 2017/4/5.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {
    private int resourceId;
    private Context context;

    public ArticleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Article> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Article article = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.ivArticle = (CircleImageView) view.findViewById(R.id.iv_article);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_article_title);
            viewHolder.tvState = (TextView) view.findViewById(R.id.tv_article_state);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_article_time);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivArticle.setImageBitmap(article.getIcon());
        viewHolder.tvTitle.setText(article.getTitle());
        viewHolder.tvState.setText(article.getState());
        viewHolder.tvTime.setText(article.getTime());
        return view;
    }

    class ViewHolder {   //对控件的实例进行缓存
        CircleImageView ivArticle;
        TextView tvTitle;
        TextView tvState;
        TextView tvTime;
    }
}
