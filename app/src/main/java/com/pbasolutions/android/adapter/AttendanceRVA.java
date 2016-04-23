package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.RequisitionListItemBinding;
import com.pbasolutions.android.model.MPurchaseRequest;

/**
 * Created by pbadell on 10/13/15.
 */
public class AttendanceRVA extends RecyclerView.Adapter<AttendanceRVA.AttendanceVH>{
    private Context mContext;
    private ObservableArrayList<MPurchaseRequest> requisList;
    private LayoutInflater inflater;

    public AttendanceRVA(Context mContext, ObservableArrayList<MPurchaseRequest> requisList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.requisList = requisList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public AttendanceRVA.AttendanceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        RequisitionListItemBinding binding = RequisitionListItemBinding.inflate(inflater);
        AttendanceVH viewHolder = new AttendanceVH(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AttendanceVH holder, int position) {
        MPurchaseRequest requis = requisList.get(position);
        holder.vBinding.setRequisition(requis);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (requisList != null)
            return requisList.size();
        else return 0;
    }

    public class AttendanceVH extends RecyclerView.ViewHolder {
        RequisitionListItemBinding vBinding;
        public AttendanceVH(RequisitionListItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
        }
    }
}
