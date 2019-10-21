package com.hopesquad.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hopesquad.R;
import com.hopesquad.activity.chat.ChatActivity;
import com.hopesquad.models.UserListData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rohit on 1/10/17.
 */

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<UserListData> listData;
    private Context ctx;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxtView, lastMessageTxtView;
        RelativeLayout topRelay;
        CircleImageView userLogoImg;

        public ViewHolder(View view) {
            super(view);
            nameTxtView = (TextView) itemView.findViewById(R.id.nameTxtView);
            lastMessageTxtView = (TextView) itemView.findViewById(R.id.lastMessageTxtView);
            topRelay = (RelativeLayout) itemView.findViewById(R.id.topRelay);
            userLogoImg = (CircleImageView) itemView.findViewById(R.id.userLogoImg);
        }
    }

    public UserListAdapter(ArrayList<UserListData> datas, Context context) {
        this.listData = datas;
        this.ctx = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new UserListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            ((ViewHolder) holder).nameTxtView.setText(listData.get(position).getFirstName()+ " " +listData.get(position).getLastName());
            ((ViewHolder) holder).lastMessageTxtView.setText(listData.get(position).getLastMessage());

            ((ViewHolder) holder).topRelay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, ChatActivity.class);
                    intent.putExtra("anotherUserID", listData.get(position).getUserID());
                    ctx.startActivity(intent);
                }
            });

            if(!listData.get(position).getProfilePhoto().equals("")){
                Glide.with(ctx).load(listData.get(position).getProfilePhoto())
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
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into( ((ViewHolder) holder).userLogoImg);
            }

            /*if (listData.get(position).getIsLastMessageRead().equalsIgnoreCase("true")) {
                ((ViewHolder) holder).topRelay.setBackgroundColor(Color.WHITE);
            } else {
                ((ViewHolder) holder).topRelay.setBackgroundColor(Color.parseColor("#e1dfe1"));
            }*/

            ((ViewHolder) holder).topRelay.setBackgroundColor(Color.parseColor("#e1dfe1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
