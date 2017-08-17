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
 * Created by pbadell on 10/13/15.
 */
public class PBSTaskController extends ContextWrapper implements PBSIController {

    public static final String ARG_C_PROJECTTASK_UUID = "ARG_C_PROJECTTASK_UUID";
    public static final String ARG_AD_USER_ID= "ARG_AD_USER_ID";
    public static final String ARG_TASK = "ARG_TASK";
    public static final String ARG_TASK_ID = "ARG_TASK_ID";
    public static final String ARG_TASK_UUID = "ARG_TASK_UUID";
    public static final String TASK_DETAILS_EVENT =  "TASK_DETAILS_EVENT";
    public static final String GET_PROJTASKS_EVENT =  "GET_PROJTASKS_EVENT";
    public static final String SYNC_PROJTASKS_EVENT =  "SYNC_PROJTASKS_EVENT";
    public static final String COMPLETE_PROJTASK_EVENT =  "COMPLETE_PROJTASKS_EVENT";
    public static final String GET_PROJTASK_EVENT =  "GET_PROJTASK_EVENT";
    public static final String CREATE_TASK_EVENT = "CREATE_TASK_EVENT" ;
    public static final String GET_USERS_EVENT = "GET_USERS_EVENT";
    public static final String GET_PROJECTLOCATIONS_EVENT = "GET_PROJECTLOCATIONS_EVENT";
    public static final String ARG_TASK_LIST = "ARG_TASK_LIST";
    public static final String ARG_PROJTASK= "ARG_PROJTASK";
    public static final String ARG_COMMENTS = "ARG_COMMENTS";
    public static final String ARG_PROJLOC_UUID = "ARG_PROJLOC_UUID";
    public static final String ARG_PROJLOC_ID = "ARG_PROJLOC_ID";
    public static final String ARG_DUEDATE = "ARG_DUEDATE";


    public static final String ARG_TASKPIC = "ARG_TASKPIC_";
    public static final String ARG_TASKPIC_1 = "ARG_TASKPIC_1";
    public static final String ARG_TASKPIC_2 = "ARG_TASKPIC_2";
    public static final String ARG_TASKPIC_3 = "ARG_TASKPIC_3";
    public static final String ARG_TASKPIC_4 = "ARG_TASKPIC_4";
    public static final String ARG_TASKPIC_5 = "ARG_TASKPIC_5";
    public static final String ARG_USERS = "ARG_USERS" ;
    public static final String ARG_PROJECTLOCATIONS = "ARG_PROJECTLOCATIONS";
    ContentResolver cr;

    public PBSTaskController(Context base) {
        super(base);
        cr = getContentResolver();
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object) {
        switch (eventName) {
            case GET_PROJTASKS_EVENT: {
                getTasks(input, result, object);
                break;
            }
            case SYNC_PROJTASKS_EVENT: {
                syncTasks(input, result, object);
                break;
            }
            case COMPLETE_PROJTASK_EVENT: {
                completeTask(input, result, object);
                break;
            }
            case TASK_DETAILS_EVENT: {
                break;
            }
            case GET_PROJTASK_EVENT: {
                getProjTask(input, result, object);
                break;
            }
            case GET_PROJECTLOCATIONS_EVENT: {
                getProjectLocations(input, result, object);
                break;
            }
            case GET_USERS_EVENT: {
                getUsers(input, result, object);
                break;
            }
            case CREATE_TASK_EVENT: {
                createTask(input, result, object);
                break;
            }
            default : break;
        }
        return result;
    }

    private Bundle getUsers(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_USERS_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle getProjectLocations(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_PROJECTLOCATIONS_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle getProjTask(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_PROJTASK_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle completeTask(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, COMPLETE_PROJTASK_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle getTasks(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_PROJTASKS_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle syncTasks(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, SYNC_PROJTASKS_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
        FutureTask<Bundle> taskResult = new FutureTask<>(task);
        es.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle createTask(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, CREATE_TASK_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ProjectTask task = new ProjectTask(input, result, cr);
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
