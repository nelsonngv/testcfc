package com.pbasolutions.android.controller;

import android.os.Bundle;

/**
 * Created by pbadell on 8/14/15.
 */
public interface PBSIController {
    public final String ARG_TASK_EVENT = "ARG_TASK_EVENT";
    Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object);
}
