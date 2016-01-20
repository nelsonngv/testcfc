package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.EmployeeListitemBinding;
import com.pbasolutions.android.model.MEmployee;

/**
 * Created by pbadell on 10/5/15.
 */
public class EmployeeRVA extends RecyclerView.Adapter<EmployeeRVA.MEmployeeVH>{
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
        MEmployeeVH viewHolder = new MEmployeeVH(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MEmployeeVH holder, int position) {
        MEmployee employee = employeeList.get(position);
        holder.vBinding.setEmployee(employee);
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

    /**
     * Employee View Holder.
     */
    public class MEmployeeVH extends RecyclerView.ViewHolder {
        EmployeeListitemBinding vBinding;
        public MEmployeeVH(EmployeeListitemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
        }
    }
}
