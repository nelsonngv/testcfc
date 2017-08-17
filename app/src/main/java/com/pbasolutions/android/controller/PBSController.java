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
 * Created by pbadell on 12/4/15.
 */
public class PBSController extends ContextWrapper implements PBSIController{

    ContentResolver cr;
    ExecutorService exec = Executors.newSingleThreadExecutor();
    Task task;

    FutureTask<Bundle> taskResult;

    public static final String ARG_CONTENTVALUES = "ARG_CONTENTVALUES";

    public PBSController(Context base) {
        super(base);
    }

    public Bundle runTask(Bundle input, Bundle result) {
        task.setInput(input);
        task.setOutput(result);
        taskResult = new FutureTask(task);
        exec.execute(taskResult);
        try {
            result = taskResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            taskResult.cancel(true);
        }
        return result;
    }

    public Bundle runTaskDirectly(Bundle input, Bundle result) {
        task.setInput(input);
        task.setOutput(result);
        try {
            result = task.call();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public Bundle triggerEvent(String eventName, Bundle input, Bundle output, Object object) {
        input.putString(ARG_TASK_EVENT, eventName);
        return runTask(input, output);
    }

    public Bundle callEventDirectly(String eventName, Bundle input, Bundle output, Object object) {
        input.putString(ARG_TASK_EVENT, eventName);
        return runTaskDirectly(input, output);
    }

}
