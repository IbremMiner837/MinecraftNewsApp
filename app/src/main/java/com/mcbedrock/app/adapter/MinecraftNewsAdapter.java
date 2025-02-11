package com.mcbedrock.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.mcbedrock.app.Constants;
import com.mcbedrock.app.R;
import com.mcbedrock.app.model.NewsModel;
import com.mcbedrock.app.utils.CustomTabUtil;

import java.util.List;

public class MinecraftNewsAdapter extends RecyclerView.Adapter<MinecraftNewsAdapter.ViewHolder> {
    private final List<NewsModel> models;

    public MinecraftNewsAdapter(List<NewsModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Context context = holder.image.getContext();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        holder.title.setText(models.get(position).getTitle());
        holder.textView.setText(models.get(position).getText());
        Glide.with(context)
                .load(Constants.CONTENT + models.get(position).getImage())
                .apply(requestOptions)
                .into(holder.image);

        holder.materialButton.setOnClickListener(view -> new CustomTabUtil()
                .OpenCustomTab(context, models.get(position).getReadMoreUrl(), context.getColor(R.color.md_theme_light_onSecondary)));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton materialButton;
        TextView title, textView;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            materialButton = itemView.findViewById(R.id.ActionButton);
            title = itemView.findViewById(R.id.TitleView);
            textView = itemView.findViewById(R.id.TextView);
            image = itemView.findViewById(R.id.ImageView);
        }
    }
}