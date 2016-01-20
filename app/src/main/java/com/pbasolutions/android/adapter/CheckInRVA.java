package com.pbasolutions.android.adapter;

/**
 * Adapter for list view.
 * Created by pbadell on 7/21/15.
 */
import java.util.ArrayList;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.model.MCheckIn;
import com.pbasolutions.android.model.CheckInVH;
import com.pbasolutions.android.R;

public class CheckInRVA extends RecyclerView.Adapter<CheckInVH> {
    /**
     * Application context.
     */
    private Context context;
    /**
     * Item list in recycler view.
     */
    private ArrayList<MCheckIn> checkInList;

    /**
     * Adapter for list view.
     * @param context
     * @param itemsList
     */
    public CheckInRVA(Context context, ArrayList<MCheckIn> itemsList) {
        this.context = context;
        this.checkInList = itemsList;
    }

    @Override
    public int getItemCount() {
        if (checkInList == null) {
            return 0;
        } else {
            return checkInList.size();
        }
    }

    @Override
    public CheckInVH onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_holder, null);
        CheckInVH viewHolder = new CheckInVH(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CheckInVH rowViewHolder, int position) {
        MCheckIn checkIn = checkInList.get(position);
        rowViewHolder.getTextLocationView().setText(String.valueOf(checkIn.getProjectLocation()));
        rowViewHolder.getImageStatusView().setBackgroundResource(checkIn.getStatusIcon());
        rowViewHolder.getTextDateView().setText(String.valueOf(checkIn.getDate()));
        rowViewHolder.getTextCommentView().setText(String.valueOf(checkIn.getComment()));
        rowViewHolder.getTextCheckinView().setText(String.valueOf(checkIn.getUuid()));
    }
}
