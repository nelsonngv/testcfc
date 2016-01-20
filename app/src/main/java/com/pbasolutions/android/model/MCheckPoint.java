package com.pbasolutions.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import java.io.Serializable;

/**
 * Created by pbadell on 9/22/15.
 */
public class MCheckPoint extends BaseObservable implements Serializable {

    String name;
    String seqNo;
    String description;

    @Bindable
    public ObservableArrayList<MCheckPoint> getCheckPointSeqList() {
        return checkPointSeqList;
    }

    public void setCheckPointSeqList(ObservableArrayList<MCheckPoint> checkPointSeqList) {
        this.checkPointSeqList = checkPointSeqList;
    }

    public ObservableArrayList<MCheckPoint> checkPointSeqList;

    @Bindable
    public String getName() {
        return name;
    }

    public MCheckPoint setName(String name) {
        this.name = name;
        return this;
    }
    @Bindable
    public String getSeqNo() {
        return seqNo;
    }

    public MCheckPoint setSeqNo(String seqNo) {
        this.seqNo = seqNo;
        return this;
    }
    @Bindable
    public String getDescription() {
        return description;
    }

    public MCheckPoint setDescription(String description) {
        this.description = description;
        return this;
    }
}
