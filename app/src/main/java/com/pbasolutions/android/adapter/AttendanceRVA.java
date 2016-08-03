package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.AttendanceItemBinding;
import com.pbasolutions.android.databinding.RequisitionListItemBinding;
import com.pbasolutions.android.model.MAttendance;

/**
 * Created by pbadell on 10/13/15.
 */
public class AttendanceRVA extends RecyclerView.Adapter<AttendanceRVA.AttendanceVH>{
    private Context mContext;
    private ObservableArrayList<MAttendance> attendanceList;
    private LayoutInflater inflater;

    public AttendanceRVA(Context mContext, ObservableArrayList<MAttendance> requisList) {
        this.mContext = mContext;
        this.attendanceList = requisList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public AttendanceRVA.AttendanceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        AttendanceItemBinding binding = AttendanceItemBinding.inflate(inflater);
        AttendanceVH viewHolder = new AttendanceVH(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AttendanceVH holder, int position) {
        MAttendance attendance = attendanceList.get(position);
        holder.vBinding.setAttendance(attendance);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (attendanceList != null)
            return attendanceList.size();
        else return 0;
    }

    public class AttendanceVH extends RecyclerView.ViewHolder {
        AttendanceItemBinding vBinding;
        public AttendanceVH(AttendanceItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
        }
    }
}
