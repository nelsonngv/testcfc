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
 * Created by pbadell on 10/12/15.
 */
public class PBSAttendanceController extends ContextWrapper implements PBSIController {

    public static final String ARG_RESOURCEALLOC_UUID = "ARG_RESOURCEALLOC_UUID";
    public static final String ARG_NOTE = "ARG_NOTE";
    public static final String NOTE_DETAILS_EVENT =  "GET_NOTE_EVENT";
    public static final String GET_ATTENDANCES_EVENT = "GET_ATTENDANCES_EVENT";
    public static final String ARG_ATTENDANCES = "ARG_ATTENDANCES";
    public static final String ARG_PROJECTLOCATION_ID = "ARG_PROJECTLOCATION_ID ";
    public static final String ARG_PROJECTLOCATION_UUID = "ARG_PROJECTLOCATION_UUID ";
    public static final String ARG_DEPLOYMENTDATE = "ARG_DEPLOYMENTDATE";

    public static final String SHIFT_LIST = "SHIFT_LIST";
    public static final String GET_SHIFTS_EVENT = "GET_SHIFTS_EVENT";

    public static final String ARG_CONTENTVALUES = "ARG_CONTENTVALUES";
    public static final String INSERT_REQLINE_EVENT = "INSERT_REQLINE_EVENT";


    ContentResolver cr;
    ExecutorService exec = Executors.newSingleThreadExecutor();
    AttendanceTask attendanceTask;
    FutureTask<Bundle> taskResult;

    public PBSAttendanceController(Context base) {
        super(base);
        cr = getContentResolver();
        attendanceTask = new AttendanceTask(cr);
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object) {
        switch (eventName) {
            case GET_ATTENDANCES_EVENT: {
                getDeploys(input, result, object);
                break;
            }
            case GET_SHIFTS_EVENT: {
                getShifts(input, result, object);
                break;
            }
        }
        return result;
    }

    private Bundle getDeploys(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_ATTENDANCES_EVENT);
        attendanceTask.setInput(input);
        attendanceTask.setOutput(result);
        taskResult = new FutureTask (attendanceTask);
        exec.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bundle getShifts(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_SHIFTS_EVENT);
        attendanceTask.setInput(input);
        attendanceTask.setOutput(result);
        taskResult = new FutureTask (attendanceTask);
        exec.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}
