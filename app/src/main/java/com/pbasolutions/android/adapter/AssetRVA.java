package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.AssetListItemBinding;
import com.pbasolutions.android.model.MStorage;

/**
 * Created by pbadell on 10/12/15.
 */
public class AssetRVA  extends RecyclerView.Adapter<AssetRVA.AssetVH>{
    private Context mContext;
    private ObservableArrayList<MStorage> storageList;
    private LayoutInflater inflater;

    public AssetRVA(Context mContext_, ObservableArrayList<MStorage> name_, LayoutInflater inflater_) {
        this.mContext = mContext_;
        this.storageList = name_;
        this.inflater = LayoutInflater.from(mContext_);
    }

    @Override
    public AssetRVA.AssetVH onCreateViewHolder(ViewGroup parent, int viewType) {
        AssetListItemBinding binding = AssetListItemBinding.inflate(inflater);
        return new AssetVH(binding);
    }

    @Override
    public void onBindViewHolder(AssetVH holder, int position) {
        if (storageList != null) {
            MStorage asset = storageList.get(position);
            holder.vBinding.setStorage(asset);
            if (storageList.size() == 1)
                holder.itemView.setPadding(0, 10, 0, 10);
            else {
                if (position == 0) holder.itemView.setPadding(0, 10, 0, 0);
                if (position == storageList.size() - 1) holder.itemView.setPadding(0, 0, 0, 10);
            }
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (storageList != null)
            return storageList.size();
        else return 0;
    }

    public class AssetVH extends RecyclerView.ViewHolder {
        AssetListItemBinding vBinding;

        public AssetVH(AssetListItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
            itemView.setClickable(true);
        }
    }

}
