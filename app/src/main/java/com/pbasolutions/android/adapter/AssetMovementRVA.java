package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.AssetMovementListItemBinding;
import com.pbasolutions.android.model.MMovement;

/**
 * Created by pbadell on 10/8/15.
 */
public class AssetMovementRVA extends RecyclerView.Adapter<AssetMovementRVA.AssetMovementVH>{
    private Context mContext;
    private ObservableArrayList<MMovement> movements;
    private LayoutInflater inflater;

    public AssetMovementRVA(Context mContext, ObservableArrayList<MMovement> name, LayoutInflater inflater) {
        this.mContext = mContext;
        this.movements = name;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public AssetMovementRVA.AssetMovementVH onCreateViewHolder(ViewGroup parent, int viewType) {
        AssetMovementListItemBinding binding = AssetMovementListItemBinding.inflate(inflater);
        AssetMovementVH viewHolder = new AssetMovementVH(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AssetMovementVH holder, int position) {
        MMovement movement = movements.get(position);
        holder.vBinding.setMovement(movement);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (movements != null)
            return movements.size();
        else return 0;
    }

    public class AssetMovementVH extends RecyclerView.ViewHolder {
        AssetMovementListItemBinding vBinding;

        public AssetMovementVH(AssetMovementListItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
        }
    }
}
