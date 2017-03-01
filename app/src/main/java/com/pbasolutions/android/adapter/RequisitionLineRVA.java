package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.databinding.RequisitionlineItemBinding;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.fragment.RequisitionLineDetailsFragment;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MPurchaseRequestLine;

/**
 * Created by pbadell on 11/20/15.
 */
public class RequisitionLineRVA extends RecyclerView.Adapter<RequisitionLineRVA.RequisitionLineVH> {
    private Context mContext;
    private ObservableArrayList<MPurchaseRequestLine> reqLineList;
    private LayoutInflater inflater;

    public RequisitionLineRVA(Context mContext, ObservableArrayList<MPurchaseRequestLine> reqLineList) {
        this.mContext = mContext;
        this.reqLineList = reqLineList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RequisitionLineRVA.RequisitionLineVH onCreateViewHolder(ViewGroup parent, int viewType) {
        RequisitionlineItemBinding binding = RequisitionlineItemBinding.inflate(inflater);
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.requisitionline_item, null);
        RequisitionLineVH viewHolder = new RequisitionLineVH(binding, view, new BroadcastRVA.IViewHolderOnClicks(){
            @Override
            public void onCheckbox(CheckBox cb, int pos) {
                MPurchaseRequestLine prline = (MPurchaseRequestLine) cb.getTag();
                prline.setIsSelected(cb.isChecked());
                reqLineList.get(pos).setIsSelected(cb.isChecked());
            }

            @Override
            public void onText(TextView tvCaller, int pos) {
                //    TextView _uuid = (TextView) view.findViewById(R.id._UUID);
                ObservableArrayList<IModel> modelList = (ObservableArrayList) reqLineList;
                Fragment fragment = new RequisitionLineDetailsFragment();
                Object tag = tvCaller.getTag();
                if (tvCaller!=null) {
                    String _UUID = tvCaller.getTag().toString();
                    if (_UUID != null && !_UUID.isEmpty()) {
                        ((PBSDetailsFragment) fragment).set_UUID(_UUID);
                        ((PBSDetailsFragment) fragment).setModelList(modelList);
                        if (fragment != null) {
                            FragmentManager fragmentManager = ((PandoraMain)mContext).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragment.setRetainInstance(true);
                            fragmentTransaction.replace(R.id.container_body, fragment);
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                            fragmentTransaction.commit();
                            ((PandoraMain) mContext).getSupportActionBar().setTitle(mContext.getString(R.string.title_requisitionline));
                        }
                    }
                }
                new FragmentListOnItemClickListener(modelList, new RequisitionLineDetailsFragment(),
                        (FragmentActivity)mContext, mContext.getString(R.string.title_requisitionline));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RequisitionLineVH holder, int position) {
        MPurchaseRequestLine prLine = reqLineList.get(position);
        holder.cbox.setChecked(prLine.isSelected());
        holder.cbox.setTag(prLine);
        holder.prodName.setTag(prLine.get_UUID());
        holder.vBinding.setPrLine(prLine);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (reqLineList != null)
            return reqLineList.size();
        else return 0;
    }

    public class RequisitionLineVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        RequisitionlineItemBinding vBinding;
        CheckBox cbox;
        TextView prodName;
        private BroadcastRVA.IViewHolderOnClicks listeners;

        public RequisitionLineVH(RequisitionlineItemBinding binding, View view, BroadcastRVA.IViewHolderOnClicks listeners) {
            super(binding.getRoot());
            this.vBinding = binding;
            this.listeners = listeners;
            View bindView = binding.getRoot();
            this.cbox = (CheckBox) bindView.findViewById(R.id.removePrl);
            this.prodName = (TextView) bindView.findViewById(R.id.prlProdName);
            cbox.setOnClickListener(this);
            prodName.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (v instanceof CheckBox){
                listeners.onCheckbox((CheckBox)v, getPosition());
            } else {
                listeners.onText((TextView)v, getPosition());
            }
        }
    }

    public ObservableArrayList<MPurchaseRequestLine> getLines() {
        return reqLineList;
    }
}
