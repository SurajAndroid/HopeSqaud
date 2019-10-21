package com.hopesquad.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hopesquad.R;
import com.hopesquad.models.UserMediaList;

import java.util.ArrayList;

/**
 * Created by pc on 24/01/2018.
 */

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter<HorizontalRecycleViewAdapter.ViewHolder> {

    private ArrayList<UserMediaList> itemsList;
    private Context mContext;

    public HorizontalRecycleViewAdapter(Context context, ArrayList<UserMediaList> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
        ViewHolder mh = new ViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        UserMediaList item = itemsList.get(i);
//        holder.nameTxtView.setText(item.DisplayName);
        if(!item.getUserStatusFileUrl().equals("")){
            Glide.with(mContext).load(item.getUserStatusFileUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .thumbnail(0.5f)
                    .crossFade()
                    .error(R.mipmap.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxtView;
        ImageView profile_image;

        public ViewHolder(View view) {
            super(view);
            nameTxtView = (TextView) itemView.findViewById(R.id.nameTxtView);
            profile_image = (ImageView) itemView.findViewById(R.id.userLogoImg);
        }
    }
}