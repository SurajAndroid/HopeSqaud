package com.hopesquad.helper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hopesquad.R;

/**
 * Created by rohit on 10/9/17.
 */

public class ProgressBarDialog extends DialogFragment {

    // Set cancelOnTouch
    private boolean canceledOnTouchOutside = true;

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public ProgressBarDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        getDialog().setCancelable(canceledOnTouchOutside);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return inflater.inflate(R.layout.loading_progress_bar, null, false);
    }

    @Override
    public void onDestroyView() {
        try {
            Dialog dialog = getDialog();
            if (dialog != null && getRetainInstance()) {
                dialog.setDismissMessage(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

}
