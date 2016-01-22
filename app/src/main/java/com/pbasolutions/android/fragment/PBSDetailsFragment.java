package com.pbasolutions.android.fragment;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.model.IModel;

/**
 * Created by pbadell on 10/29/15.
 */
public abstract class PBSDetailsFragment extends Fragment implements IDetailsFragment {
    protected String _UUID;
    protected int _ID;

    protected ObservableArrayList<IModel> modelList;

    @Override
    public void set_UUID(String uuid) {
        this._UUID = uuid;
    }

    @Override
    public void set_ID(int id) {
        this._ID = id;
    }

    @Override
    public void setModelList(ObservableArrayList<IModel> modelList) {
        this.modelList = modelList;

    }

    public ObservableArrayList<IModel> getModelList() {
        return modelList;
    }

    protected void addRecyclerViewListener(RecyclerView rv){};

    /**
     *
     */
    protected void takePicture(int code, View view) {
        PandoraMain context = (PandoraMain)getActivity();
        context.dispatchTakePictureIntent(code, view);
    }

    public String get_UUID() {
        return _UUID;
    }

    public int get_ID() {
        return _ID;
    }

    abstract protected void setUI(View view);
    abstract protected void setUIListener();
    abstract protected void setValues();
}
