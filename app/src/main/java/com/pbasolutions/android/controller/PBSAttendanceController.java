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
    public static final String ARG_ATTENDANCESEARCHRES = "ARG_ATTENDANCESEARCHRES";

    public static final String SHIFT_LIST = "SHIFT_LIST";
    public static final String GET_SHIFTS_EVENT = "GET_SHIFTS_EVENT";

    public static final String ARG_CONTENTVALUES = "ARG_CONTENTVALUES";
    public static final String INSERT_REQLINE_EVENT = "INSERT_REQLINE_EVENT";

    public static final String EMPLOYEE_LIST = "EMPLOYEE_LIST";
    public static final String GET_EMPLOYEES_EVENT = "GET_EMPLOYEES_EVENT";
    public static final String ARG_EMPLOYEE = "ARG_EMPLOYEE";

    public static final String LEAVETYPE_LIST = "LEAVETYPE_LIST";
    public static final String GET_LEAVETYPES_EVENT = "GET_LEAVETYPES_EVENT";
    public static final String ARG_LEAVETYPE = "ARG_LEAVETYPE";
    public static final String SAVE_ATTENDANCELINE_EVENT = "SAVE_ATTENDANCELINE_EVENT";

    public static final String ARG_ATTENDANCELINE_LIST = "ARG_ATTENDANCELINE_LIST";
    public static final String REMOVE_ATTDLINES_EVENT = "REMOVE_ATTDLINES_EVENT";

    public static final String ARG_ATTENDANCE_REQUEST = "ARG_ATTENDANCE_REQUEST";
    public static final String CREATE_ATTENDANCE_EVENT = "CREATE_ATTENDANCE_EVENT";

    public static final String ARG_SEARCH_ATTENDANCE_REQUEST = "ARG_SEARCH_ATTENDANCE_REQUEST";
    public static final String SEARCH_ATTENDANCE_EVENT = "SEARCH_ATTENDANCE_EVENT";

    public static final String INSERT_ATTENDANCE_REQ_EVENT = "INSERT_ATTENDANCE_REQ_EVENT";

    public static final String GET_PROJECTLOCATIONS_EVENT = "GET_PROJECTLOCATIONS_EVENT";
    public static final String ARG_PROJECTLOCATIONS = "ARG_PROJECTLOCATIONS";

    public static final String ARG_NFCTAG = "ARG_NFCTAG";
    public static final String GET_PROJECTLOCATION_BY_TAG_EVENT = "GET_PROJECTLOCATION_BY_TAG_EVENT";

    public static final String CREATE_ATTENDANCETRACKING_EVENT = "CREATE_ATTENDANCETRACKING_EVENT";

    public static String deployDate;
    public static String projectLocationId;
    public static String projectLocationName;
    public static String shiftUUID;
    public static String attType;
    public static String empName;
    public static boolean isKioskMode;
    public static boolean isPhoto;

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
        callTask(input, result, eventName);
        return result;
    }

    private Bundle callTask(Bundle input, Bundle result, String event) {
        input.putString(ARG_TASK_EVENT, event);
        attendanceTask.setInput(input);
        attendanceTask.setOutput(result);
        taskResult = new FutureTask (attendanceTask);
        exec.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}
