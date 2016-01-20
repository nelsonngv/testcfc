package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSRecruitController;
import com.pbasolutions.android.databinding.EmployeeDetailsBinding;
import com.pbasolutions.android.model.MEmployee;

/**
 * Created by pbadell on 10/29/15.
 */
public class EmployeeDetailsFragment extends PBSDetailsFragment {
    /**
     * PBSRecruitment controller.
     */
    private PBSRecruitController recCont;
    /**
     * EmployeeDetailsBinding. **auto generated class based on the xml name.
     */
    private EmployeeDetailsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recCont = new PBSRecruitController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.employee_details, container, false);
        Bundle inputBundle = new Bundle();
        inputBundle.putString(recCont.ARG_EMP_UUID, _UUID);
        inputBundle.putSerializable(recCont.EMPLOYEE_LIST, modelList);
        Bundle resultBundle = new Bundle();
        resultBundle = recCont.triggerEvent(recCont.GET_EMPLOYEE_EVENT, inputBundle, resultBundle, null);
        final MEmployee employee= (MEmployee)resultBundle.getSerializable(recCont.ARG_EMPLOYEE);
        binding =  EmployeeDetailsBinding.bind(rootView);
        binding.setEmployee(employee);
        return rootView;
    }

    @Override
    protected void setUI(View view) {

    }

    @Override
    protected void setUIListener() {

    }

    @Override
    protected void setValues() {

    }
}
