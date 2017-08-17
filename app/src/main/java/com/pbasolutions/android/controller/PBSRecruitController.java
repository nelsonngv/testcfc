package com.pbasolutions.android.controller;

import android.content.Context;

/**
 * Created by pbadell on 10/5/15.
 */
public class PBSRecruitController extends PBSController {

    /**
     * Event name : get employee list.
     */
    public static final String GET_EMPLOYEES_EVENT = "GET_EMPLOYEES_EVENT";
    /**
     * Event name : get applicant list.
     */
    public static final String GET_APPLICANTS_EVENT = "GET_APPLICANTS_EVENT";
    /**
     * Event name : insert applicant.
     */
    public static final String INSERT_APPLICANT_EVENT = "INSERT_APPLICANT_EVENT";
    /**
     * Event name : get project location.
     */
    public static final String GET_SHIFTS_EVENT = "GET_SHIFTS_EVENT";
    /**
     * Event name : get hr setup job list.
     */
    public static final String GET_SETUPJOB_EVENT = "GET_SETUPJOB_EVENT";
    /**
     *
     */
    public static final String GET_NATIONALITY_EVENT = "GET_NATIONALITY_EVENT";
    /**
     *
     */
    public static final String GET_STATUS_EVENT = "GET_STATUS_EVENT";
    /**
     *
     */
    public static final String GET_APPLICANT_EVENT = "GET_APPLICANT_EVENT";
    /**
     *
     */
    public static final String EMPLOYEE_LIST = "EMPLOYEE_LIST";
    public static final String ARG_PROJECT_LOCATION_UUID = "ARG_PROJECT_LOCATION_UUID";
    public static final String ARG_PROJECT_LOCATION_NAME = "ARG_PROJECT_LOCATION_NAME";
    public static final String SHIFT_LIST = "SHIFT_LIST";
    public static final String SETUPJOB_LIST = "SETUPJOB_LIST";
    public static final String NATIONALITY_LIST = "NATIONALITY_LIST";
    public static final String APPLICANT_VALUES = "APPLICANT_VALUES";
    public static final String APPLICANT_LIST = "APPLICANT_LIST";
    public static final String ARG_JOBAPP_UUID = "ARG_JOBAPP_UUID";
    public static final String ARG_STATUS = "ARG_STATUS";
    public static final String ARG_APPLICANT = "ARG_APPLICANT";


    public static final String ARG_EMP_UUID = "ARG_EMP_UUID";
    public static final String GET_EMPLOYEE_EVENT = "GET_EMPLOYEE_EVENT";
    public static final String ARG_EMPLOYEE = "ARG_EMPLOYEE";
    public static final String GET_IDTYPE_EVENT = "GET_IDTYPE_EVENT";
    public static final String IDTYPE_LIST = "IDTYPE_LIST";
    public static final String GET_EMPLOYEEBYTAG_EVENT = "GET_EMPLOYEEBYTAG_EVENT";
    /**
     *
     * @param base
     */
    public PBSRecruitController(Context base) {
        super(base);
        task = new RecruitTask(getContentResolver());
    }
}
