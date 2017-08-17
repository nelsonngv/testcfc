package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by pbadell on 10/8/15.
 */
public class PBSBroadcastController extends ContextWrapper implements PBSIController {

    public static final String ARG_NOTE_UUID = "ARG_NOTE_UUID";
    public static final String ARG_NOTE = "ARG_NOTE";
    public static final String GET_NOTE_EVENT =  "GET_NOTE_EVENT";
    public static final String GET_NOTES_EVENT=  "GET_NOTES_EVENT";
    public static final String ARG_USER_UUID=  "ARG_USER_UUID";
    public static final String ARG_SELECTION=  "ARG_SELECTION";
    public static final String ARG_USER_ID=  "ARG_USER_ID";
    public static final String NOTE_LIST = "NOTE_LIST";
    public static final String SYNC_NOTES_EVENT = "SYNC_NOTES_EVENT";
    public static final String DELETE_NOTES_EVENT = "DELETE_NOTES_EVENT";
    public static final String ARG_PROJLOC_UUID = "ARG_PROJLOC_UUID";

    ContentResolver cr;

    public PBSBroadcastController(Context base) {
        super(base);
        cr = getContentResolver();
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object) {
        switch (eventName) {
            case GET_NOTES_EVENT: {
                getNotes(input, result, object);
                break;
            }
            case SYNC_NOTES_EVENT: {
                syncNotes(input, result, object);
                break;
            }
            case DELETE_NOTES_EVENT: {
                deleteNotes(input, result, object);
                break;
            }
            case GET_NOTE_EVENT: {
                getNote(input, result, object);
                break;
            }
            default:break;
        }
        return result;
    }

    public Bundle getNotes(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_NOTES_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        BroadcastTask task = new BroadcastTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bundle syncNotes(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, SYNC_NOTES_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        BroadcastTask task = new BroadcastTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bundle deleteNotes(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, DELETE_NOTES_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        BroadcastTask task = new BroadcastTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle getNote(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_NOTE_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        BroadcastTask task = new BroadcastTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}