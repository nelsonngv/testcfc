package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.EmployeeListitemBinding;
import com.pbasolutions.android.model.MEmployee;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * Created by pbadell on 10/5/15.
 */
public class EmployeeRVA extends RecyclerView.Adapter<EmployeeRVA.MEmployeeVH> implements FastScrollRecyclerView.SectionedAdapter {
    private Context mContext;
    private ObservableArrayList<MEmployee> employeeList;
    private LayoutInflater inflater;

    public EmployeeRVA(Context mContext, ObservableArrayList<MEmployee> employeeList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.employeeList = employeeList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public EmployeeRVA.MEmployeeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        EmployeeListitemBinding binding = EmployeeListitemBinding.inflate(inflater);
        return new MEmployeeVH(binding);
    }

    @Override
    public void onBindViewHolder(MEmployeeVH holder, int position) {
        MEmployee employee = employeeList.get(position);
        holder.vBinding.setEmployee(employee);
        if (employeeList.size() == 1)
            holder.itemView.setPadding(0, 10, 0, 10);
        else {
            if (position == 0) holder.itemView.setPadding(0, 10, 0, 0);
            if (position == employeeList.size() - 1) holder.itemView.setPadding(0, 0, 0, 10);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (employeeList != null)
            return employeeList.size();
        else return 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return employeeList.get(position).getName().substring(0, 1);
    }

    /**
     * Employee View Holder.
     */
    public class MEmployeeVH extends RecyclerView.ViewHolder {
        EmployeeListitemBinding vBinding;
        public MEmployeeVH(EmployeeListitemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
            itemView.setClickable(true);
        }
    }
}
