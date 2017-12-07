package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.SurveyListItemBinding;
import com.pbasolutions.android.model.MSurvey;

/**
 * Created by pbadell on 10/13/15.
 */
public class SurveyRVA extends RecyclerView.Adapter<SurveyRVA.SurveyVH> {
    private Context mContext;
    private ObservableArrayList<MSurvey> surveyList;
    private LayoutInflater inflater;

    public SurveyRVA(Context mContext, ObservableArrayList<MSurvey> surveyList) {
        this.mContext = mContext;
        this.surveyList = surveyList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public SurveyRVA.SurveyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        SurveyListItemBinding binding = SurveyListItemBinding.inflate(inflater);
        return new SurveyVH(binding);
    }

    @Override
    public void onBindViewHolder(SurveyVH holder, int position) {
        MSurvey survey = surveyList.get(position);
        int numOfSynced = survey.getNumOfSynced();
        int totalToSync = survey.getTotalToSync();
        if (numOfSynced == totalToSync)
            survey.setStatus("Synced");
        else survey.setStatus("Pending to sync (" + numOfSynced + "/" + totalToSync + ")");

        holder.vBinding.setSurvey(survey);
        if (surveyList.size() == 1)
            holder.itemView.setPadding(0, 10, 0, 10);
        else {
            if (position == 0) holder.itemView.setPadding(0, 10, 0, 0);
            if (position == surveyList.size() - 1) holder.itemView.setPadding(0, 0, 0, 10);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (surveyList != null)
            return surveyList.size();
        else return 0;
    }

    public class SurveyVH extends RecyclerView.ViewHolder {
        SurveyListItemBinding vBinding;
        public SurveyVH(SurveyListItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
            itemView.setClickable(true);
        }
    }
}
