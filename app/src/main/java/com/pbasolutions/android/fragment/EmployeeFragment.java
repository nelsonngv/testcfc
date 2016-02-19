package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.EmployeeRVA;
import com.pbasolutions.android.controller.PBSRecruitController;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MEmployee;

/**
 * Created by pbadell on 10/5/15.
 */
public class EmployeeFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "EmployeeFragment";

    /**
     * Recruitment controller.
     */
    PBSRecruitController recCont;
    /**
     *
     */
    ObservableArrayList<MEmployee> employees;
    /**
     * Constructor method.
     */
    public EmployeeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recCont = new PBSRecruitController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.employee, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.employee_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        employees = getEmployeeList();
        EmployeeRVA viewAdapter = new EmployeeRVA(getActivity(),employees, inflater);
        addRecyclerViewListener(recyclerView);
        recyclerView.setAdapter(viewAdapter);
        return rootView;
    }

    /**
     * Get list of employee based on project location selection.
     * @return
     */
    private ObservableArrayList<MEmployee> getEmployeeList() {
        PandoraContext globalVar = ((PandoraMain)getActivity()).globalVariable;
        if (globalVar != null) {
            String projectLocationUUID = globalVar.getC_projectlocation_uuid();
            if (projectLocationUUID != null) {
                Bundle input = new Bundle();
                Bundle result = recCont.triggerEvent(recCont.GET_EMPLOYEES_EVENT, input, new Bundle(), null);
                return (ObservableArrayList<MEmployee>) result.getSerializable(recCont.EMPLOYEE_LIST);
            } else {
                PandoraHelper.showErrorMessage((PandoraMain) getActivity(), getString(R.string.text_projectloc_na));
            }
        }
        return null;
    }

    /**
     * Add click listener on the recycler view. when ever user clicks the list item. it will navigate them to the item details.
     * @param rv
     */
    private void addRecyclerViewListener(RecyclerView rv) {
        //TODO: refactor method to be shareable.
        ObservableArrayList<IModel> modelList = (ObservableArrayList) employees;
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new FragmentListOnItemClickListener(modelList, new EmployeeDetailsFragment(), getActivity(), getString(R.string.title_employee_details)))
//                {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        TextView _uuid = (TextView) view.findViewById(R.id.employee_uuid);
//                        Fragment fragment = new EmployeeDetailsFragment();
//                        ((EmployeeDetailsFragment) fragment).set_UUID((String) _uuid.getText());
//                        ((EmployeeDetailsFragment) fragment).setList(employees);
//                        if (fragment != null) {
//                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragment.setRetainInstance(true);
//                            fragmentTransaction.replace(R.id.container_body, fragment);
//                            fragmentTransaction.addToBackStack(fragment.getClass().getName());
//                            fragmentTransaction.commit();
//                            ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_employee_details));
//                        }
//                    }
//                })
        );

    }
}
