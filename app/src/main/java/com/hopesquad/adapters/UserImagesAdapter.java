package com.hopesquad.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hopesquad.R;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.UserImages;

import java.util.ArrayList;


public class UserImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = UserImagesAdapter.class.getSimpleName();
    private final Point displaySize;

    private Context mContext;
    private ArrayList<UserImages> imagesArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) itemView.findViewById(R.id.img_android);
        }
    }


    public UserImagesAdapter(Context mContext, ArrayList<UserImages> chatImagesArrayList) {
        this.mContext = mContext;
        this.imagesArrayList = chatImagesArrayList;
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        displaySize = Utils.getDisplaySize(wm);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_images_item, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(imagesArrayList.get(position).getUserImageFileURL())
                .override((displaySize.x / 2), Utils.dpToPx(200))
                .centerCrop()
                .into(((ViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return imagesArrayList.size();
    }


}