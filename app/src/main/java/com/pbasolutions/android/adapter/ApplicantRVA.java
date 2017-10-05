package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.ApplicantListitemBinding;
import com.pbasolutions.android.model.MApplicant;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * Created by pbadell on 10/5/15.
 */
public class ApplicantRVA extends RecyclerView.Adapter<ApplicantRVA.MApplicantVH> implements FastScrollRecyclerView.SectionedAdapter {
    private Context mContext;
    private ObservableArrayList<MApplicant> applicantList;
    private LayoutInflater inflater;

    public ApplicantRVA(Context mContext, ObservableArrayList<MApplicant> applicantList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.applicantList = applicantList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ApplicantRVA.MApplicantVH onCreateViewHolder(ViewGroup parent, int viewType) {
        ApplicantListitemBinding binding = ApplicantListitemBinding.inflate(inflater);
        return new MApplicantVH(binding);
    }

    @Override
    public void onBindViewHolder(MApplicantVH holder, int position) {
        MApplicant applicant = applicantList.get(position);
        holder.vBinding.setApplicant(applicant);
        if (applicantList.size() == 1)
            holder.itemView.setPadding(0, 10, 0, 10);
        else {
            if (position == 0) holder.itemView.setPadding(0, 10, 0, 0);
            if (position == applicantList.size() - 1) holder.itemView.setPadding(0, 0, 0, 10);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (applicantList != null)
            return applicantList.size();
        else return 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return applicantList.get(position).getName().substring(0, 1);
    }

    /**
     * Employee View Holder.
     */
    public class MApplicantVH extends RecyclerView.ViewHolder {
        ApplicantListitemBinding vBinding;
        public MApplicantVH(ApplicantListitemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
            itemView.setClickable(true);
        }
    }
}
