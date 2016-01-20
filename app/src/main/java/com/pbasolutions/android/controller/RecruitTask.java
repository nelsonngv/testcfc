package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.net.Uri;
import android.os.Bundle;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MApplicant;
import com.pbasolutions.android.model.MEmployee;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;

/**
 * Created by pbadell on 12/30/15.
 */
public class RecruitTask extends Task {

        private static final String TAG = "RecruitTask";

        private ContentResolver cr;

        public RecruitTask(ContentResolver cr) {
            this.cr = cr;
        }
        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public Bundle call() throws Exception {
            switch (event) {
                case PBSRecruitController.GET_EMPLOYEES_EVENT: {
                    return getEmployees();
                }
                case PBSRecruitController.GET_APPLICANTS_EVENT: {
                    return getApplicants();
                }
                case PBSRecruitController.INSERT_APPLICANT_EVENT: {
                    return insertApplicant();
                }
                case PBSRecruitController.GET_SHIFTS_EVENT: {
                    return getHRShift();
                }
                case PBSRecruitController.GET_SETUPJOB_EVENT: {
                    return getSetupJob();
                }
                case PBSRecruitController.GET_NATIONALITY_EVENT: {
                    return getNationality();
                }
                case PBSRecruitController.GET_APPLICANT_EVENT: {
                    return getApplicant();
                }
                case PBSRecruitController.GET_EMPLOYEE_EVENT: {
                    return getEmployee();
                }
                case PBSRecruitController.GET_IDTYPE_EVENT: {
                    return getIdTypes();
                }
                default:
                    return null;
            }
        }

    public Bundle getApplicants() {
        //29 COLUMNS.
        String[] projection = {
                MApplicant.HR_JOBAPPLICATION_UUID_COL,
                MApplicant.HR_SETUP_JOB_UUID_COL,
                MApplicant.HR_NATIONALITY_UUID_COL,
                MApplicant.HR_PROJLOCATION_SHIFT_UUID_COL,
                MApplicant.NAME_COL,
                MApplicant.IDNUMBER_COL,
                MApplicant.IDTYPE_COL,
                MApplicant.AGE_COL,
                MApplicant.SEX_COL,
                MApplicant.PHONE_COL,
                MApplicant.MARITALSTATUS_COL,
                MApplicant.QUALIFICATION_HIGHEST_COL,
                MApplicant.QUALIFICATION_OTHER_COL,
                MApplicant.STATUS_COL,
                MApplicant.APPLICATIONDATE_COL,
                MApplicant.EXPECTEDSALARY_COL,
                MApplicant.YEARSOFEXPERIENCED_COL,
                MApplicant.ATTACHMENT_APPLICANTPICTURE_COL,
                MApplicant.ATTACHMENT_CERTPICTURE_1,
                MApplicant.ATTACHMENT_CERTPICTURE_2,
                MApplicant.ATTACHMENT_CERTPICTURE_3,
                MApplicant.ATTACHMENT_CERTPICTURE_4,
                MApplicant.ATTACHMENT_CERTPICTURE_5,
                MApplicant.ATTACHMENT_CERTPICTURE_6,
                MApplicant.ATTACHMENT_CERTPICTURE_7,
                MApplicant.ATTACHMENT_CERTPICTURE_8,
                MApplicant.ATTACHMENT_CERTPICTURE_9,
                MApplicant.ATTACHMENT_CERTPICTURE_10 };
        //grab the applicants from database.
        String selection = ModelConst.C_PROJECTLOCATION_UUID_COL + "=?";
        String[] selectArgs = {input.getString(PBSRecruitController.ARG_PROJECT_LOCATION_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.JOBAPP_LIST_VIEW),
                projection, selection, selectArgs, null);

        ObservableArrayList<MApplicant> applicantList = new ObservableArrayList<MApplicant>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                MApplicant applicant = new MApplicant();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    String columnName = cursor.getColumnName(x);
                    String rowValue =  cursor.getString(x);
                    if (MApplicant.HR_JOBAPPLICATION_UUID_COL.equalsIgnoreCase(columnName)){
                        applicant.set_UUID(rowValue);
                    } else if (MApplicant.HR_SETUP_JOB_UUID_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setHR_SETUP_JOB_UUID(rowValue);
                    } else if (MApplicant.HR_NATIONALITY_UUID_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setHR_NATIONALITY_UUID(rowValue);
                    } else if (MApplicant.HR_PROJLOCATION_SHIFT_UUID_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setHR_PROJLOCATION_SHIFT_UUID(rowValue);
                    } else if (MApplicant.NAME_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setName(rowValue);
                    } else if (MApplicant.IDNUMBER_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setIdNumber(rowValue);
                    } else if (MApplicant.IDTYPE_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setIdType(rowValue);
                    }
                    else if (MApplicant.AGE_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setAge(rowValue);
                    } else if (MApplicant.SEX_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setSex(rowValue);
                    } else if (MApplicant.PHONE_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setPhone(rowValue);
                    } else if (MApplicant.MARITALSTATUS_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setMaritalStat(rowValue);
                    } else if (MApplicant.QUALIFICATION_HIGHEST_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setQualHighest(rowValue);
                    } else if (MApplicant.QUALIFICATION_OTHER_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setQualOther(rowValue);
                    } else if (MApplicant.STATUS_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setStatus(rowValue);
                    } else if (MApplicant.APPLICATIONDATE_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setApplDate(rowValue);
                    } else if (MApplicant.EXPECTEDSALARY_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setExpSal(rowValue);
                    } else if (MApplicant.YEARSOFEXPERIENCED_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setNumOfExp(rowValue);
                    } else if (MApplicant.ATTACHMENT_APPLICANTPICTURE_COL
                            .equalsIgnoreCase(columnName)) {
                        applicant.setProfPic(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_1
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic1(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_2
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic2(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_3
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic3(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_4
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic4(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_5
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic5(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_6
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic6(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_7
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic7(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_8
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic8(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_9
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic9(rowValue);
                    } else if (MApplicant.ATTACHMENT_CERTPICTURE_10
                            .equalsIgnoreCase(columnName)) {
                        applicant.setCertPic10(rowValue);
                    }
                }
                applicantList.add(applicant);
            } while (cursor.moveToNext());
            cursor.close();
        }
        output.putSerializable(PBSRecruitController.APPLICANT_LIST, applicantList);
        return output;
    }

    /**
     *
     * @return
     */
    private Bundle insertApplicant() {
        ContentValues cv = input.getParcelable(PBSRecruitController.APPLICANT_VALUES);
        Uri uri = cr.insert(ModelConst.uriCustomBuilder(ModelConst.HR_JOBAPPLICATION_TABLE), cv);
        boolean result = ModelConst.getURIResult(uri);

        if (result) {
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfuly insert new applicant.");
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to insert new applicant.");
        }
        return output;
    }

    /**
     *
     * @return
     */
    private Bundle getEmployees() {
        String[] projection = {MEmployee.C_BPARTNER_UUID_COL, ModelConst.NAME_COL, ModelConst.IDNUMBER_COL, ModelConst.PHONE_COL, MEmployee.JOB_TITLE_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_BPARTNER_VIEW),
                projection, null, null, null);
        ObservableArrayList<MEmployee> employeeList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                MEmployee employee = new MEmployee();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    String columnName = cursor.getColumnName(x);
                    String rowValue =  cursor.getString(x);
                    if (MEmployee.C_BPARTNER_UUID_COL.equalsIgnoreCase(columnName)){
                        employee.set_UUID(rowValue);
                    } else if (ModelConst.NAME_COL
                            .equalsIgnoreCase(columnName)) {
                        employee.setName(rowValue);
                    } else if (ModelConst.IDNUMBER_COL
                            .equalsIgnoreCase(columnName)) {
                        employee.setIdNumber(rowValue);
                    } else if (ModelConst.PHONE_COL
                            .equalsIgnoreCase(columnName)) {
                        employee.setPhone(rowValue);
                    } else if (MEmployee.JOB_TITLE_COL
                            .equalsIgnoreCase(columnName)) {
                        employee.setJobTitle(rowValue);
                    }
                }
                employeeList.add(employee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSRecruitController.EMPLOYEE_LIST, employeeList);
        return output;
    }

    /**
     *
     * @return
     */
    private Bundle getIdTypes() {
        String[] projection = {ModelConst.NAME_COL, ModelConst.VALUE_COL};
        //grab the setup job names from database view.
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.HR_IDENTITY_TABLE),
                projection, null, null, null);

        ArrayList<SpinnerPair> identityList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    } else if (ModelConst.VALUE_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    }
                }
                identityList.add(pair);
            } while (cursor.moveToNext());
            cursor.close();
        }
        output.putParcelableArrayList(PBSRecruitController.IDTYPE_LIST, identityList);
        return output;
    }

    /**
     *
     * @return
     */
    private Bundle getHRShift(){
        String[] projection = {ModelConst.HR_PROJLOCATION_SHIFT_UUID_COL, ModelConst.NAME_COL};
        String[] selectionArg = {input.getString(PBSRecruitController.ARG_PROJECT_LOCATION_UUID)};
        //grab the shift names from database view.
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.JOBAPP_SHIFTS_VIEW),
                projection, ModelConst.C_PROJECTLOCATION_UUID_COL + "=?", selectionArg, null);

        ArrayList<SpinnerPair> shiftList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    } else if (ModelConst.HR_PROJLOCATION_SHIFT_UUID_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    }
                }
                shiftList.add(pair);
            } while (cursor.moveToNext());
            cursor.close();
        }
        output.putParcelableArrayList(PBSRecruitController.SHIFT_LIST, shiftList);
        return output;
    }


    /**
     *
     * @return
     */
    public Bundle getSetupJob() {
        String[] projection = {ModelConst.HR_SETUP_JOB_UUID_COL, ModelConst.NAME_COL};
        //grab the setup job names from database view.
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.HR_SETUP_JOB_TABLE),
                projection, null, null, null);

        ArrayList<SpinnerPair> setupJobList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    } else if (ModelConst.HR_SETUP_JOB_UUID_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    }
                }
                setupJobList.add(pair);
            } while (cursor.moveToNext());
            cursor.close();
        }
        output.putParcelableArrayList(PBSRecruitController.SETUPJOB_LIST, setupJobList);
        return output;
    }

    /**
     *
     * @return
     */
    public Bundle getNationality() {
        String[] projection = {ModelConst.HR_NATIONALITY_UUID_COL, ModelConst.NAME_COL};
        //grab the setup job names from database view.
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.HR_NATIONALITY_TABLE),
                projection, null, null, null);

        ArrayList<SpinnerPair> nationalityList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (ModelConst.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    } else if (ModelConst.HR_NATIONALITY_UUID_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    }
                }
                nationalityList.add(pair);
            } while (cursor.moveToNext());
            cursor.close();
        }
        output.putParcelableArrayList(PBSRecruitController.NATIONALITY_LIST, nationalityList);
        return output;
    }

    /**
     * Get applicant selected from applicant list.
     * @return
     */
    private Bundle getApplicant() {
        ObservableArrayList<IModel> applicantList = (ObservableArrayList)input
                .getSerializable(PBSRecruitController.APPLICANT_LIST);
        String inputUUID = input.getString(PBSRecruitController.ARG_JOBAPP_UUID);
        MApplicant applicant = (MApplicant) PandoraHelper.getModel(applicantList, inputUUID);
        output.putSerializable(PBSRecruitController.ARG_APPLICANT, applicant);
        return output;
    }

    /**
     * Get employee selected from employee list.
     * @return
     */
    private Bundle getEmployee() {
        ObservableArrayList<IModel> employeeList = (ObservableArrayList)input
                .getSerializable(PBSRecruitController.EMPLOYEE_LIST);
        String inputUUID = input.getString(PBSRecruitController.ARG_EMP_UUID);
        MEmployee emp = (MEmployee)PandoraHelper.getModel(employeeList, inputUUID);
        output.putSerializable(PBSRecruitController.ARG_EMPLOYEE, emp);
        return output;
    }

}
