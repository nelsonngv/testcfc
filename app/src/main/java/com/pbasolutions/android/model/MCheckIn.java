package com.pbasolutions.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by pbadell on 7/27/15.
 */
public class MCheckIn extends BaseObservable implements Serializable {

    String date;
    String time;
    String user;
    String checkpoint;
    String projectLocation;
    String comment;
    String uuid;
    private int statusIcon;

    @Bindable
    public String getDate() {
        return date;
    }

    public MCheckIn setDate(String date) {
        this.date = date;
        return this;
    }
    @Bindable
    public String getTime() {
        return time;
    }

    public MCheckIn setTime(String time) {
        this.time = time;
        return this;
    }
    @Bindable
    public String getUser() {
        return user;
    }

    public MCheckIn setUser(String user) {
        this.user = user;
        return this;
    }
    @Bindable
    public String getCheckpoint() {
        return checkpoint;
    }

    public MCheckIn setCheckpoint(String checkpoint) {
        this.checkpoint = checkpoint;
        return this;
    }
    @Bindable
    public String getProjectLocation() {
        return projectLocation;
    }

    public MCheckIn setProjectLocation(String location) {
        this.projectLocation = location;
        return this;
    }
    @Bindable
    public String getComment() {
        return comment;
    }

    public MCheckIn setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public MCheckIn setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public int getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(int statusIcon) {
        this.statusIcon = statusIcon;
    }
}
