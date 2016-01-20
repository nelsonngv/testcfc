package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by pbadell on 10/8/15.
 */
public class PBSDeployController extends ContextWrapper implements PBSIController {

    public static final String ARG_RESOURCEALLOC_UUID = "ARG_RESOURCEALLOC_UUID";
    public static final String ARG_NOTE = "ARG_NOTE";
    public static final String NOTE_DETAILS_EVENT =  "GET_NOTE_EVENT";
    public static final String GET_DEPLOYS_EVENT = "GET_DEPLOYS_EVENT";
    public static final String ARG_DEPLOYS = "ARG_DEPLOYS";
    public static final String ARG_PROJECTLOCATION_ID = "ARG_PROJECTLOCATION_ID ";
    public static final java.lang.String ARG_DEPLOYMENTDATE = "ARG_DEPLOYMENTDATE";
    ContentResolver cr;
    ExecutorService exec = Executors.newSingleThreadExecutor();
    DeployTask deployTask;
    FutureTask<Bundle> taskResult;

    public PBSDeployController(Context base) {
        super(base);
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object) {
        return null;
    }
}
