package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.CheckpointSeqItemBinding;
import com.pbasolutions.android.model.MCheckPoint;

/**
 * Created by pbadell on 9/22/15.
 * Check Point Sequence Recycler View Adapter.
 */
public class CheckPointRVA extends RecyclerView.Adapter<CheckPointRVA.CheckPointSeqVH> {

    private Context mContext;
    private ObservableArrayList<MCheckPoint> checkPointSeqs;
    private LayoutInflater inflater;

    public CheckPointRVA(Context mContext, ObservableArrayList<MCheckPoint> checkPointSeqs, LayoutInflater inflater) {
        this.mContext = mContext;
        this.checkPointSeqs = checkPointSeqs;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public CheckPointRVA.CheckPointSeqVH onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckpointSeqItemBinding binding = CheckpointSeqItemBinding.inflate(inflater);

        return new CheckPointSeqVH(binding);
    }

    @Override
    public void onBindViewHolder(CheckPointSeqVH holder, int position) {
        MCheckPoint checkPointSeq = checkPointSeqs.get(position);
        holder.vBinding.setCheckPointSeq(checkPointSeq);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (checkPointSeqs != null)
            return checkPointSeqs.size();
        else return 0;
    }

    public class CheckPointSeqVH extends RecyclerView.ViewHolder {
        CheckpointSeqItemBinding vBinding;
        public CheckPointSeqVH(CheckpointSeqItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
        }
    }
}
