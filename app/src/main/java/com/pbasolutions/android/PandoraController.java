package com.pbasolutions.android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;

import com.pbasolutions.android.controller.BroadcastTask;
import com.pbasolutions.android.controller.PBSIController;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.model.ModelConst;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by pbadell on 11/5/15.
 */
public class PandoraController extends ContextWrapper implements PBSIController {
    /**
     * Param project location
     */
    public static final String ARG_PROJECT_LOCATION_JSON = "ARG_PROJECT_LOCATION_JSON";

    /**
     *
     */
    public static final String ARG_PROJECT_LOCATION_UUID = "ARG_PROJECT_LOCATION_UUID";

    /**
     *
     */
    public static final String GET_PROJECT_LOCATION_DATA = "SET_PROJECT_LOCATION_DATA";

    /**
     *
     */
    public static final String GET_PROJLOC_EVENT = "GET_PROJLOC_EVENT";

    private ContentResolver cr;

    public PandoraController(Context base) {
        super(base);
        cr = getContentResolver();
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle result, Object object) {
        switch (eventName) {
            case GET_PROJLOC_EVENT:{
                getProjLocs(input, result, object);
                break;
            }
            default:
                break;
        }
        return result;
    }

    private Bundle getProjLocs(Bundle input, Bundle output, Object object) {
        input.putString(ARG_TASK_EVENT, GET_PROJLOC_EVENT);
        ExecutorService es = Executors.newSingleThreadExecutor();
        PandoraTask task = new PandoraTask(input, output, cr);
        FutureTask<Bundle> taskResult = new FutureTask<Bundle> (task);
        es.execute(taskResult);
        try {
            output = taskResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }
}
