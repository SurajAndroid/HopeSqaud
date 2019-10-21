package com.hopesquad.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hopesquad.R;
import com.hopesquad.models.TargetGoal;


public class TargetGoalBottomSheet extends BottomSheetDialogFragment {

    private static String POSITION_KEY = "ref_key";
    private ItemAdapter itemAdapter;

    public static TargetGoalBottomSheet newInstance(String key) {
        TargetGoalBottomSheet fragment = new TargetGoalBottomSheet();
        Bundle args = new Bundle();
        args.putString(POSITION_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemAdapter = new ItemAdapter(getResources().getStringArray(R.array.array_target_goal));
        recyclerView.setAdapter(itemAdapter);
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_item_list_dialog_item, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickInterfaceBottomSheet listener = (ClickInterfaceBottomSheet) getActivity();
                    switch (getAdapterPosition()) {
                        case 0:
                            // Weight Loss
                            listener.onClickBottomSheetItem(TargetGoal.WEIGHT_LOSS);
                            dismiss();
                            break;
                        case 1:
                            // Weight Gain
                            listener.onClickBottomSheetItem(TargetGoal.WEIGHT_GAIN);
                            dismiss();
                            break;
                        case 2:
                            // Body Fit
                            listener.onClickBottomSheetItem(TargetGoal.BODY_FIT);
                            dismiss();
                            break;
                    }
                }
            });
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<TargetGoalBottomSheet.ViewHolder> {

        private final String[] mItemCount;

        ItemAdapter(String[] itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public TargetGoalBottomSheet.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TargetGoalBottomSheet.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(TargetGoalBottomSheet.ViewHolder holder, int position) {
            holder.text.setText(mItemCount[position]);
        }

        @Override
        public int getItemCount() {
            return mItemCount.length;
        }

    }


}