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
public class PBSRequisitionController extends ContextWrapper implements PBSIController {

    /**
     * Event name : get employee list.
     */
    public static final String GET_REQUISITIONS_EVENT = "GET_REQUISITIONS_EVENT";

    public static final String SYNC_REQUISITIONS_EVENT = "SYNC_ATTENDANCES_EVENT";

    public static final String CREATE_REQUISITION_EVENT = "CREATE_REQUISITION_EVENT";

    public static final String GET_REQUISITION_EVENT = "GET_REQUISITION_EVENT";
    /**
     *
     */
    public static final String ARG_REQUISITION_LIST = "ARG_ATTENDANCE_LIST";
    public static final String ARG_PURCHASEREQUEST_UUID = "ARG_PURCHASEREQUEST_UUID";
    public static final String ARG_REQUISITION = "ARG_REQUISITION";
    public static final String ARG_PROJECT_LOCATION_UUID = "ARG_PROJECT_LOCATION_UUID";
    public static final String ARG_PROJECT_LOCATION_ID = "ARG_PROJECT_LOCATION_ID";
    public static final String ARG_USER_ID = "ARG_USER_ID";
    public static final String ARG_PURCHASEREQUEST = "ARG_PURCHASEREQUEST";
    public static final String GET_REQUISITIONLINES_EVENT = "GET_REQUISITIONLINES_EVENT";
    public static final String ARG_PURCHASEREQUESTLINE_LIST = "ARG_PURCHASEREQUESTLINE_LIST";
    public static final String GET_PRODUCT_LIST_EVENT = "GET_PRODUCT_LIST_EVENT";
    public static final String ARG_PRODUCT_LIST = "ARG_PRODUCT_LIST";
    public static final String ARG_PURCHASEREQUESTLINE_UUID = "ARG_PURCHASEREQUESTLINE_UUID";
    public static final String ARG_PURCHASEREQUESTLINE = "ARG_PURCHASEREQUESTLINE";
    public static final String GET_REQUISITIONLINE_EVENT = "GET_REQUISITIONLINE_EVENT";
    public static final String ARG_PROD_COL_SELECTION = "ARG_PROD_COL_SELECTION";
    public static final String ARG_ORDERBY = "ARG_ORDERBY";
    public static final String ARG_PURCHASEREQUEST_VALUES = "ARG_PURCHASEREQUEST_VALUES";
    public static final String INSERT_REQLINE_EVENT = "INSERT_REQLINE_EVENT";
    public static final String ARG_TABLENAME = "ARG_TABLENAME";
    public static final String ARG_CONTENTVALUES = "ARG_CONTENTVALUES";
    public static final String INSERT_REQ_EVENT = "INSERT_REQ_EVENT";
    public static final String ARG_REQUESTDATE = "ARG_REQUESTDATE";
    public static final String GET_DATE_EVENT = "ARG_GET_DATE";
    public static final String REMOVE_REQ_EVENT = "REMOVE_REQ_EVENT";
    public static final String REMOVE_REQLINES_EVENT = "REMOVE_REQLINES_EVENT";
    public static final String DELETE_REQLINES_EVENT = "DELETE_REQLINES_EVENT";
    public static final String ARG_REQUISITIONLINE_LIST = "ARG_REQUISITIONLINE_LIST";
    public static final String CHECK_PR_IS_REQUESTED_EVENT = "CHECK_PR_IS_REQUESTED_EVENT";
    public static final String ARG_IS_PR_REQUESTED = "ARG_IS_PR_REQUESTED";
    public static final String UPDATE_PR_LINE_EVENT = "UPDATE_PR_LINE_EVENT";

    ContentResolver cr;
    ExecutorService exec = Executors.newSingleThreadExecutor();
    RequisitionTask reqTask;
    FutureTask<Bundle> taskResult;
    public static String requestedDate;
    /**
     *
     * @param base
     */
    public PBSRequisitionController(Context base) {
        super(base);
        cr = getContentResolver();
        reqTask = new RequisitionTask(cr);
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object) {
        switch (eventName) {
            case GET_REQUISITIONS_EVENT: {
                getRequisitions(input, result, object);
                break;
            }
            case SYNC_REQUISITIONS_EVENT: {
                syncRequisitions(input, result, object);
                break;
            }
            case GET_REQUISITION_EVENT: {
                getRequisition(input, result, object);
                break;
            }
            case GET_PRODUCT_LIST_EVENT: {
                getProductList(input, result, object);
                break;
            }
            case GET_REQUISITIONLINES_EVENT: {
                getRequisitionLines(input, result, object);
                break;
            }
            case GET_REQUISITIONLINE_EVENT: {
                getRequisitionLine(input, result, object);
                break;
            }
            case INSERT_REQ_EVENT: {
                insertReq(input, result, object);
                break;
            }
            case INSERT_REQLINE_EVENT: {
                insertReqLine(input, result, object);
                break;
            }
            case GET_DATE_EVENT: {
                getDate(input, result, object);
                break;
            }
            case REMOVE_REQ_EVENT: {
                removeReq(input, result, object);
                break;
            }
            case REMOVE_REQLINES_EVENT: {
                removeLines(input, result, object);
                break;
            }
            case CREATE_REQUISITION_EVENT : {
                createRequisition(input,result, object);
                break;
            }
            case CHECK_PR_IS_REQUESTED_EVENT : {
                isPRRequested(input, result, object);
                break;
            }
            case UPDATE_PR_LINE_EVENT: {
                updatePRLine(input,result, object);
            }

            default:
                break;
        }
        return result;
    }

    private Bundle updatePRLine(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, UPDATE_PR_LINE_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle isPRRequested(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, CHECK_PR_IS_REQUESTED_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle createRequisition(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, CREATE_REQUISITION_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

//    private Bundle deleteLines(Bundle input, Bundle result, Object object) {
//        input.putString(ARG_TASK_EVENT, DELETE_REQLINES_EVENT);
//        reqTask.setInput(input);
//        reqTask.setOutput(result);
//        taskResult = new FutureTask (reqTask);
//        exec.execute(taskResult);
//        try {
//            result = taskResult.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    private Bundle removeLines(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, REMOVE_REQLINES_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle getDate(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_DATE_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle insertReqLine(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, INSERT_REQLINE_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle insertReq(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, INSERT_REQ_EVENT);

        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle removeReq(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, REMOVE_REQ_EVENT);

        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle getRequisitionLine(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_REQUISITIONLINE_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle getRequisitionLines(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_REQUISITIONLINES_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle getProductList(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_PRODUCT_LIST_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle getRequisition(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_REQUISITION_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask (reqTask);
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

    private Bundle syncRequisitions(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, SYNC_REQUISITIONS_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask<Bundle> (reqTask);
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

    /**
     *
     * @param input
     * @param result
     * @param object
     */
    private Bundle getRequisitions(Bundle input, Bundle result, Object object) {
        input.putString(ARG_TASK_EVENT, GET_REQUISITIONS_EVENT);
        reqTask.setInput(input);
        reqTask.setOutput(result);
        taskResult = new FutureTask<Bundle> (reqTask);
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
