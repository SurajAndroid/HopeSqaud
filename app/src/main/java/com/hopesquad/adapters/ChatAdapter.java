package com.hopesquad.adapters;

import android.content.Context;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daasuu.bl.BubbleLayout;
import com.hopesquad.R;
import com.hopesquad.activity.userimages.SlideshowDialogFragment;
import com.hopesquad.models.ChatMessage;
import com.hopesquad.models.ImageStatus;
import com.hopesquad.models.UserImages;

import java.util.ArrayList;
import java.util.Calendar;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;
    private int EMPTY = 101;
    private int SHOW_IMAGE_SELF = 102;
    private int SHOW_IMAGE_OTHERS = 103;
    private static String today;

    private Context mContext;
    private ArrayList<ChatMessage> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        TextView message, timestamp;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            message =  view.findViewById(R.id.messageTxt);
            imageView =  view.findViewById(R.id.thumbnail);
            progressBar =   view.findViewById(R.id.progress);
        }
    }


    public ChatAdapter(Context mContext, ArrayList<ChatMessage> messageArrayList, String userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SHOW_IMAGE_SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_image_item_self, parent, false);
        } else if (viewType == SHOW_IMAGE_OTHERS) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_image_item_others, parent, false);
        } else if (viewType == EMPTY) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_blank, parent, false);
        } else if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {

       if (!messageArrayList.get(position).getImageMessageUrl().equals("")  && messageArrayList.get(position).getSenderID().equals(""+userId)) {
            return SHOW_IMAGE_SELF;
        } else if (!messageArrayList.get(position).getImageMessageUrl().equals("") && !messageArrayList.get(position).getSenderID().equals(""+userId)) {
            return SHOW_IMAGE_OTHERS;
        } else if (messageArrayList.get(position).getMessage().trim().isEmpty()) {
            return EMPTY;
        } else if (messageArrayList.get(position).getSenderID().equals(""+userId)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ChatMessage message = messageArrayList.get(position);

        if (!message.getImageMessageUrl().equals("")) {
            try{
                Glide.with(mContext).load(message.getImageMessageUrl())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                ((ViewHolder) holder).progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                ((ViewHolder) holder).progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(((ViewHolder) holder).imageView);

                ((ViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        ArrayList<ImageStatus> userImagesList = new ArrayList<ImageStatus>();
                        ImageStatus userImage = new ImageStatus();
                        userImage.setUserStatusFileUrl(message.getImageMessageUrl());
                        userImagesList.add(userImage);
                        bundle.putSerializable("images", userImagesList);
                        bundle.putInt("position", 0);

                        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
                        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "slideshow");
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        } else if (message.getMessage().trim().isEmpty()) {
        } else {
            ((ViewHolder) holder).message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


    public void addMessage(ChatMessage chatMessage) {
        messageArrayList.add(chatMessage);
        notifyDataSetChanged();
    }
}