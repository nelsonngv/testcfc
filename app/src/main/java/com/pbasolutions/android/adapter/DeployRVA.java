package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.DeploymentListitemBinding;
import com.pbasolutions.android.model.MDeploy;
import com.pbasolutions.android.model.MResourceAlloc;

/**
 * Created by pbadell on 10/8/15.
 */
public class DeployRVA extends RecyclerView.Adapter<DeployRVA.DeployVH>{

    private Context mContext;
    private ObservableArrayList<MDeploy> deployList;
    private LayoutInflater inflater;

    public DeployRVA(Context mContext, ObservableArrayList<MDeploy> deploys, LayoutInflater inflater) {
        this.mContext = mContext;
        this.deployList = deploys;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public DeployRVA.DeployVH onCreateViewHolder(ViewGroup parent, int viewType) {
        DeploymentListitemBinding binding = DeploymentListitemBinding.inflate(inflater);
        DeployVH viewHolder = new DeployVH(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeployVH holder, int position) {
        MDeploy deploy = deployList.get(position);
        holder.vBinding.setDeploy(deploy);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (deployList != null)
            return deployList.size();
        else return 0;
    }

    public class DeployVH extends RecyclerView.ViewHolder {
        DeploymentListitemBinding vBinding;
        public DeployVH(DeploymentListitemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
        }
    }
}
