package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;

import java.util.Calendar;
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

    public static String deployDate;
    public static String projectLocationId;
    public static String shiftUUID;
    public static String attType;

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
                getAttendances(input, result, object);
                break;
            }
            case SEARCH_ATTENDANCE_EVENT: {
                searchAttendances(input, result, object);
                break;
            }
            case GET_SHIFTS_EVENT: {
                getShifts(input, result, object);
                break;
            }
            case GET_EMPLOYEES_EVENT: {
                getEmploys(input, result, object);
                break;
            }
            case GET_LEAVETYPES_EVENT: {
                getLeaveTypes(input, result, object);
                break;
            }
            case SAVE_ATTENDANCELINE_EVENT: {
                saveAttendanceLine(input, result, object);
                break;
            }
            case REMOVE_ATTDLINES_EVENT: {
                removeLines(input, result, object);
                break;
            }
            case CREATE_ATTENDANCE_EVENT: {
                createAttendance(input, result, object);
                break;
            }
            case GET_PROJECTLOCATIONS_EVENT: {
                getProjectLocations(input, result, object);
                break;
            }
        }
        return result;
    }

    private Bundle getAttendances(Bundle input, Bundle result, Object object) {
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

    private Bundle searchAttendances(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, SEARCH_ATTENDANCE_EVENT);
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

    private Bundle getEmploys(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_EMPLOYEES_EVENT);
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

    private Bundle getLeaveTypes(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_LEAVETYPES_EVENT);
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

    private Bundle saveAttendanceLine(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, SAVE_ATTENDANCELINE_EVENT);
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

    private Bundle removeLines(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, REMOVE_ATTDLINES_EVENT);
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

    private Bundle createAttendance(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, CREATE_ATTENDANCE_EVENT);
        attendanceTask.setInput(input);
        attendanceTask.setOutput(result);
        taskResult = new FutureTask(attendanceTask);
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

    private Bundle getProjectLocations(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_PROJECTLOCATIONS_EVENT);
        attendanceTask.setInput(input);
        attendanceTask.setOutput(result);
        taskResult = new FutureTask(attendanceTask);
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
