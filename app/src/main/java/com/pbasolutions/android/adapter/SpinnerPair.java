package com.pbasolutions.android.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by pbadell on 10/19/15.
 */
public class SpinnerPair implements Parcelable {
    public String key;
    public String value;

    public SpinnerPair() {
    }

    public SpinnerPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    protected SpinnerPair(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public static final Creator<SpinnerPair> CREATOR = new Creator<SpinnerPair>() {
        @Override
        public SpinnerPair createFromParcel(Parcel in) {
            return new SpinnerPair(in);
        }

        @Override
        public SpinnerPair[] newArray(int size) {
            return new SpinnerPair[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }
}
