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


import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.databinding.AttendanceSearchlineItemBinding;
import com.pbasolutions.android.databinding.AttendancelineItemBinding;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.fragment.AttendanceLineDetailFragment;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MAttendance;
import com.pbasolutions.android.model.MAttendanceLine;
import com.pbasolutions.android.model.MAttendanceSearchItem;
import com.pbasolutions.android.model.MPurchaseRequestLine;

/**
 * Created by danny on 8/8/15.
 */
public class AttendanceSearchLineRVA extends RecyclerView.Adapter<AttendanceSearchLineRVA.AttendanceSearchLineVH> {
    private Context mContext;
    private ObservableArrayList<MAttendanceSearchItem> attLineList;
    private LayoutInflater inflater;

    private final String ABSENT = "On Leave";
    private final String OFFDAY = "Off Day";
    private final String RESTDAY = "Rest Day";

    public AttendanceSearchLineRVA(Context mContext, ObservableArrayList<MAttendanceSearchItem> attLineList) {
        this.mContext = mContext;
        this.attLineList = attLineList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public AttendanceSearchLineRVA.AttendanceSearchLineVH onCreateViewHolder(ViewGroup parent, int viewType) {
        AttendanceSearchlineItemBinding binding = AttendanceSearchlineItemBinding.inflate(inflater);
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.attendance_searchline_item, null);
        AttendanceSearchLineVH viewHolder = new AttendanceSearchLineVH(binding, view, new BroadcastRVA.IViewHolderOnClicks(){
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
    public void onBindViewHolder(AttendanceSearchLineVH holder, int position) {
        MAttendanceSearchItem atLine = attLineList.get(position);

        String strTag = atLine.get_UUID();

        holder.at_employee.setTag(strTag);
        holder.at_checkindate.setTag(strTag);
        holder.at_checkoutdate.setTag(strTag);
        holder.at_leavetype.setTag(strTag);
        holder.at_resourcealloc.setTag(strTag);

        holder.vBinding.setAttendancesearchitem(atLine);

        boolean isAbsent = atLine.getStatus().contains(ABSENT);
        boolean isOff = atLine.getStatus().contains(OFFDAY);
        boolean isRest = atLine.getStatus().contains(RESTDAY);
        boolean isWork = !(atLine.getCheckIn() == null || atLine.getCheckIn().equalsIgnoreCase("")
                || atLine.getCheckOut() == null || atLine.getCheckOut().equalsIgnoreCase("")) && !isAbsent;

//        if (isOff || isRest) {
            if (isOff)
                holder.at_type.setText(OFFDAY);
            else if (isRest)
                holder.at_type.setText(RESTDAY);
//            PandoraHelper.setVisibleView(holder.at_offrestdaydesc, !isWork);
//            PandoraHelper.setVisibleView(holder.at_offrestday, !isWork);
//        }
        PandoraHelper.setVisibleView(holder.at_rowType, isOff || isRest);
        PandoraHelper.setVisibleView(holder.at_rowLeaveType, isAbsent);
        PandoraHelper.setVisibleView(holder.at_rowCheckinDate, isWork);
        PandoraHelper.setVisibleView(holder.at_rowCheckoutDate, isWork);
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

    public class AttendanceSearchLineVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        AttendanceSearchlineItemBinding vBinding;
        TextView at_employee;
        TextView at_checkindate;
        TextView at_checkoutdate;
        TextView at_leavetype;
        TextView at_resourcealloc;
        TextView at_type;
        TextView at_offrestdaydesc;
        TextView at_offrestday;

        TableRow at_rowCheckinDate;
        TableRow at_rowCheckoutDate;
        TableRow at_rowLeaveType;
        TableRow at_rowType;

        private BroadcastRVA.IViewHolderOnClicks listeners;

        public AttendanceSearchLineVH(AttendanceSearchlineItemBinding binding, View view, BroadcastRVA.IViewHolderOnClicks listeners) {
            super(binding.getRoot());
            this.vBinding = binding;
            this.listeners = listeners;
            View bindView = binding.getRoot();

            at_employee = (TextView) bindView.findViewById(R.id.att_employeename);
            at_checkindate = (TextView) bindView.findViewById(R.id.att_checkin);
            at_checkoutdate = (TextView) bindView.findViewById(R.id.att_checkout);
            at_leavetype = (TextView) bindView.findViewById(R.id.att_leavetype);
            at_resourcealloc = (TextView) bindView.findViewById(R.id.att_resouecealloc);
            at_type = (TextView) bindView.findViewById(R.id.att_type);
//            at_offrestdaydesc = (TextView) bindView.findViewById(R.id.att_offrestdaydesc);
//            at_offrestday = (TextView) bindView.findViewById(R.id.att_offrestday);

            at_rowCheckinDate = (TableRow) bindView.findViewById(R.id.ats_row_checkin);
            at_rowCheckoutDate = (TableRow) bindView.findViewById(R.id.ats_row_checkout);
            at_rowLeaveType = (TableRow) bindView.findViewById(R.id.ats_row_leavetype);
            at_rowType = (TableRow) bindView.findViewById(R.id.ats_row_type);

            at_checkindate.setOnClickListener(this);
            at_checkoutdate.setOnClickListener(this);
            at_leavetype.setOnClickListener(this);
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

    public ObservableArrayList<MAttendanceSearchItem> getLines() {
        return attLineList;
    }
}
