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
import android.widget.TableRow;
import android.widget.TextView;


import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.databinding.AttendancelineItemBinding;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.fragment.AttendanceLineDetailFragment;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MAttendanceLine;
import com.pbasolutions.android.model.MPurchaseRequestLine;

/**
 * Created by pbadell on 11/20/15.
 */
public class AttendanceLineRVA extends RecyclerView.Adapter<AttendanceLineRVA.AttendanceLineVH> {
    private Context mContext;
    private ObservableArrayList<MAttendanceLine> attLineList;
    private LayoutInflater inflater;

    public AttendanceLineRVA(Context mContext, ObservableArrayList<MAttendanceLine> attLineList) {
        this.mContext = mContext;
        this.attLineList = attLineList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public AttendanceLineRVA.AttendanceLineVH onCreateViewHolder(ViewGroup parent, int viewType) {
        AttendancelineItemBinding binding = AttendancelineItemBinding.inflate(inflater);
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.attendanceline_item, null);
        AttendanceLineVH viewHolder = new AttendanceLineVH(binding, view, new BroadcastRVA.IViewHolderOnClicks(){
            @Override
            public void onCheckbox(CheckBox cb, int pos) {
                MAttendanceLine prline = (MAttendanceLine) cb.getTag();
                prline.setIsSelected(cb.isChecked());
                attLineList.get(pos).setIsSelected(cb.isChecked());
            }

            @Override
            public void onText(TextView tvCaller, int pos) {
                //    TextView _uuid = (TextView) view.findViewById(R.id._UUID);
//                ObservableArrayList<IModel> modelList = (ObservableArrayList) attLineList;
//                Fragment fragment = new AttendanceLineDetailFragment();
//                Object tag = tvCaller.getTag();
//                if (tvCaller!=null) {
//                    String _UUID = tvCaller.getTag().toString();
//                    if (_UUID != null && !_UUID.isEmpty()) {
//                        ((PBSDetailsFragment) fragment).set_UUID(_UUID);
//                        ((PBSDetailsFragment) fragment).setModelList(modelList);
//                        if (fragment != null) {
//                            FragmentManager fragmentManager = ((PandoraMain)mContext).getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragment.setRetainInstance(true);
//                            fragmentTransaction.replace(R.id.container_body, fragment);
//                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
//                            fragmentTransaction.commit();
//                            ((PandoraMain) mContext).getSupportActionBar().setTitle(mContext.getString(R.string.title_requisitionline));
//                        }
//                    }
//                }
//                new FragmentListOnItemClickListener(modelList, new AttendanceLineDetailFragment(),
//                        (FragmentActivity)mContext, mContext.getString(R.string.title_attendanceline));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AttendanceLineVH holder, int position) {
        MAttendanceLine atLine = attLineList.get(position);
        holder.cbox.setChecked(atLine.isSelected());
        holder.cbox.setTag(atLine);

        String strTag = atLine.get_UUID();

        holder.at_isabsent.setTag(strTag);
        holder.at_ispresent.setTag(strTag);
        holder.at_checkindate.setTag(strTag);
        holder.at_checkoutdate.setTag(strTag);
        holder.at_leavetype.setTag(strTag);
        holder.at_comments.setTag(strTag);

        holder.vBinding.setAtLine(atLine);

        boolean isAbsent = atLine.getIsAbsent().equalsIgnoreCase("Y") || atLine.getIsAbsent().equalsIgnoreCase("1");
        boolean isPresent = atLine.getIsPresent().equalsIgnoreCase("Y") || atLine.getIsPresent().equalsIgnoreCase("1");

        holder.at_rowLeaveType.setVisibility(isAbsent? View.VISIBLE : View.GONE);
        holder.at_rowCheckinDate.setVisibility(isAbsent? View.GONE : View.VISIBLE);
        holder.at_rowCheckoutDate.setVisibility(isAbsent? View.GONE : View.VISIBLE);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (attLineList != null)
            return attLineList.size();
        else return 0;
    }

    public class AttendanceLineVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        AttendancelineItemBinding vBinding;
        CheckBox cbox;
        TextView at_isabsent;
        TextView at_ispresent;
        TextView at_checkindate;
        TextView at_checkoutdate;
        TextView at_leavetype;
        TextView at_comments;

        TableRow at_rowCheckinDate;
        TableRow at_rowCheckoutDate;
        TableRow at_rowLeaveType;

        private BroadcastRVA.IViewHolderOnClicks listeners;

        public AttendanceLineVH(AttendancelineItemBinding binding, View view, BroadcastRVA.IViewHolderOnClicks listeners) {
            super(binding.getRoot());
            this.vBinding = binding;
            this.listeners = listeners;
            View bindView = binding.getRoot();
            this.cbox = (CheckBox) bindView.findViewById(R.id.removePrl);

            at_isabsent = (TextView) bindView.findViewById(R.id.at_isabsent);
            at_ispresent = (TextView) bindView.findViewById(R.id.at_ispresent);
            at_checkindate = (TextView) bindView.findViewById(R.id.at_checkindate);
            at_checkoutdate = (TextView) bindView.findViewById(R.id.at_checkoutdate);
            at_leavetype = (TextView) bindView.findViewById(R.id.at_leavetype);
            at_comments = (TextView) bindView.findViewById(R.id.at_comments);

            at_rowCheckinDate = (TableRow) bindView.findViewById(R.id.at_row_checkindate);
            at_rowCheckoutDate = (TableRow) bindView.findViewById(R.id.at_row_checkoutdate);
            at_rowLeaveType = (TableRow) bindView.findViewById(R.id.at_row_leavetype);

            cbox.setOnClickListener(this);

            at_isabsent.setOnClickListener(this);
            at_ispresent.setOnClickListener(this);
            at_checkindate.setOnClickListener(this);
            at_checkoutdate.setOnClickListener(this);
            at_leavetype.setOnClickListener(this);
            at_comments.setOnClickListener(this);
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

    public ObservableArrayList<MAttendanceLine> getLines() {
        return attLineList;
    }
}
