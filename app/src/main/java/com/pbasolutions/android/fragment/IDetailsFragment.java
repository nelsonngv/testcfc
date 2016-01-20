package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;

import com.pbasolutions.android.model.IModel;

/**
 * Created by pbadell on 10/29/15.
 */
public interface IDetailsFragment {

    public void set_UUID(String uuid);
    public void setModelList(ObservableArrayList<IModel> modelList);
    public void set_ID(int id);
}
