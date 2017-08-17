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
import com.pbasolutions.android.databinding.AssetMovementlineItemBinding;
import com.pbasolutions.android.fragment.MovementLineDetails;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.fragment.RequisitionLineDetailsFragment;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MMovementLine;

/**
 * Created by pbadell on 12/4/15.
 */
public class MovementLineRVA extends RecyclerView.Adapter<MovementLineRVA.MovementLineVH> {
    private Context mContext;
    private ObservableArrayList<MMovementLine> lines;
    private LayoutInflater inflater;

    public MovementLineRVA(Context mContext, ObservableArrayList<MMovementLine> lines, LayoutInflater inflater) {
        this.mContext = mContext;
        this.lines = lines;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MovementLineRVA.MovementLineVH onCreateViewHolder(ViewGroup parent, int viewType) {
        AssetMovementlineItemBinding binding = AssetMovementlineItemBinding.inflate(inflater);
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.asset_movementline_item, null);
        return new MovementLineVH(binding, view, new BroadcastRVA.IViewHolderOnClicks(){
            @Override
            public void onCheckbox(CheckBox cb, int pos) {
                MMovementLine line = (MMovementLine) cb.getTag();
                line.setIsSelected(cb.isChecked());
                lines.get(pos).setIsSelected(cb.isChecked());
            }

            @Override
            public void onText(TextView tvCaller, int pos) {
                ObservableArrayList<IModel> modelList = (ObservableArrayList) lines;
                Fragment fragment = new MovementLineDetails();
                Object tag = tvCaller.getTag();

                if (tag!=null) {
                    String uuid = null;
                    int id = 0;
                    if (tag instanceof String) {
                        uuid = tag.toString();
                    } else if (tag instanceof Integer){
                        id = ((Integer) tag).intValue();
                    }

                    if ((uuid != null && !uuid.isEmpty()) || (id!=0)) {
                        ((PBSDetailsFragment) fragment).set_UUID(uuid);
                        ((PBSDetailsFragment) fragment).set_ID(id);
                        //TODO enhance. instead passing the whole list why not only pass current selected model
                        ((PBSDetailsFragment) fragment).setModelList(modelList);
                        if (fragment != null) {
                            FragmentManager fragmentManager = ((PandoraMain)mContext).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragment.setRetainInstance(true);
                            fragmentTransaction.replace(R.id.container_body, fragment);
                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
                            fragmentTransaction.commit();
                            ((PandoraMain) mContext).getSupportActionBar().setTitle(mContext.getString(R.string.title_movementline_details));
                        }
                    }
                }
                new FragmentListOnItemClickListener(modelList, new RequisitionLineDetailsFragment(),
                        (FragmentActivity)mContext, mContext.getString(R.string.title_requisitionline));
            }
        });
    }

    @Override
    public void onBindViewHolder(MovementLineVH holder, int position) {
        if (lines != null){
            MMovementLine line = lines.get(position);
            holder.cbox.setChecked(line.isSelected());
            holder.cbox.setTag(line);
            String value;
            String uuid = line.get_UUID();
            //if uuid is null, set ID to the get. **special case for movement as movement data
            //that are coming from server will never be stored in local db.
            if ( uuid != null){
                holder.prodName.setTag(uuid);
            } else {
                holder.prodName.setTag(line.get_ID());
            }
            holder.vBinding.setLine(line);
        }

    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (lines != null)
            return lines.size();
        else return 0;
    }

    public class MovementLineVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        AssetMovementlineItemBinding vBinding;
        CheckBox cbox;
        TextView prodName;
        private BroadcastRVA.IViewHolderOnClicks listeners;

        public MovementLineVH(AssetMovementlineItemBinding binding, View view, BroadcastRVA.IViewHolderOnClicks listeners) {
            super(binding.getRoot());
            this.vBinding = binding;
            this.listeners = listeners;
            View bindView = binding.getRoot();
            this.cbox = (CheckBox) bindView.findViewById(R.id.removeLine);
            this.prodName = (TextView) bindView.findViewById(R.id.prodName);
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

    public ObservableArrayList<MMovementLine> getLines() {
        return lines;
    }
}