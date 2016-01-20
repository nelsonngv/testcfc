package com.pbasolutions.android.model;

import android.databinding.BaseObservable;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Created by pbadell on 11/30/15.
 */
public class BaseObservableInstanceCreator implements InstanceCreator<BaseObservable> {
    /**
     * Gson invokes this call-back method during deserialization to create an instance of the
     * specified type. The fields of the returned instance are overwritten with the data present
     * in the Json. Since the prior contents of the object are destroyed and overwritten, do not
     * return an instance that is useful elsewhere. In particular, do not return a common instance,
     * always use {@code new} to create a new instance.
     *
     * @param type the parameterized T represented as a {@link Type}.
     * @return a default object instance of type T.
     */
    @Override
    public BaseObservable createInstance(Type type) {
        return new BaseObservable();
    }
}
