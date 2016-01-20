package com.pbasolutions.android.controller;

import android.os.Bundle;

import java.util.concurrent.Callable;

/**
 * Created by pbadell on 12/4/15.
 */
public abstract class Task implements Callable<Bundle>, ITask {

    protected Bundle input;
    protected Bundle output;
    protected String event;

    @Override
    public Bundle getInput() {
        return input;
    }

    @Override
    public void setInput(Bundle input) {
        this.input = input;
        event = input.getString("ARG_TASK_EVENT");
    }

    @Override
    public Bundle getOutput() {
        return output;
    }

    @Override
    public void setOutput(Bundle output) {
        this.output = output;
    }
}
