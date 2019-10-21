package com.hopesquad.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hopesquad.R;
import com.hopesquad.models.Workouts;

import java.util.List;

/**
 * Created by rohit on 9/9/17.
 */

public class WorkoutListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Workouts> _listDataWorkouts;

    public WorkoutListAdapter(Context context, List<Workouts> listDataWorkouts) {
        this._context = context;
        this._listDataWorkouts = listDataWorkouts;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataWorkouts.get(groupPosition).getUserExerciseList()
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = _listDataWorkouts.get(groupPosition)
                .getUserExerciseList().get(childPosition).getExerciseName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_workout_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataWorkouts.get(groupPosition).getUserExerciseList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataWorkouts.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataWorkouts.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = _listDataWorkouts.get(groupPosition).getDayName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_workout_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}