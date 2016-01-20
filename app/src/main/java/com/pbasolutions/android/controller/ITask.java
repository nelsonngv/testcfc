package com.pbasolutions.android.controller;

import android.os.Bundle;

/**
 * Created by pbadell on 12/4/15.
 */
public interface ITask {

    Bundle getInput();

    void setInput(Bundle input);

    Bundle getOutput();

    void setOutput(Bundle output);
}
