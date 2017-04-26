package com.pbasolutions.android.controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableArrayList;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.database.PBSDBHelper;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MApplicant;
import com.pbasolutions.android.model.MEmployee;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.model.ModelConst;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by pbadell on 12/30/15.
 */
public class SurveyTask extends Task {

    private static final String TAG = "SurveyTask";

    private ContentResolver cr;

    public SurveyTask(ContentResolver cr) {
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
            case PBSSurveyController.GET_SURVEYS_EVENT: {
                return getSurveys();
            }
            case PBSSurveyController.GET_SURVEY_EVENT: {
                return getSurvey();
            }
            case PBSSurveyController.INSERT_SURVEY_EVENT: {
                return insertSurvey();
            }
            case PBSSurveyController.GET_TEMPLATES_EVENT: {
                return getTemplates();
            }
            case PBSSurveyController.GET_QUESTIONS_EVENT: {
                return getTemplateQuestions();
            }
            default:
                return null;
        }
    }


    public Bundle getSurveys() {
        String projection[] = {
                MSurvey.C_SURVEY_UUID_COL,
                MSurvey.C_PROJECTLOCATION_UUID_COL,
                MSurvey.NAME_COL,
                MSurvey.DATEDELIVERY_COL,
                ModelConst.IS_SYNCED_COL
        };
        String sortOrder = MSurvey.STATUS_COL + " DESC, " + MSurvey.DATEDELIVERY_COL + " DESC";
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEY_TABLE),
                projection, null, null, sortOrder);
        ObservableArrayList<MSurvey> surveysList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                surveysList.add(populateProjectTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSSurveyController.ARG_SURVEYS, surveysList);
        return output;
    }


    private Bundle getSurvey() {
        String templateUUID = "";
        String[] surveyProjection = {
                ModelConst.C_SURVEY_TABLE + "." + MSurvey.NAME_COL,
                ModelConst.C_SURVEYTEMPLATE_TABLE + "." + MSurvey.C_SURVEYTEMPLATE_UUID_COL,
                ModelConst.C_SURVEYTEMPLATE_TABLE + "." + MSurvey.NAME_COL + " AS TemplateName",
                ModelConst.C_SURVEYTEMPLATE_TABLE + "." + MSurvey.DESCRIPTION_COL
        };
        String selection = ModelConst.C_SURVEY_TABLE + "." + MSurvey.C_SURVEY_UUID_COL + "=?";
        String[] selectionArgs = {input.getString(PBSSurveyController.ARG_SURVEY_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEY_JOIN_TEMPLATE_TABLE),
                surveyProjection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            templateUUID = cursor.getString(1);
            output.putSerializable(PBSSurveyController.ARG_SURVEY, populateProjectTask(cursor));
            cursor.close();
        }
        else return output;

        String[] sectionProjection = { "DISTINCT " + MSurvey.SECTIONNO_COL, MSurvey.SECTIONNAME_COL };
        String sectionSelection = MSurvey.C_SURVEYTEMPLATE_UUID_COL + "=?";
        String[] sectionSelectionArgs = {templateUUID};
        String sortOrder = MSurvey.SECTIONNO_COL + " ASC";
        cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE),
                sectionProjection, sectionSelection, sectionSelectionArgs, sortOrder);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            ArrayList<String> sections = new ArrayList<>();
            do {
                sections.add(cursor.getString(1));
            } while (cursor.moveToNext());
            output.putSerializable(PBSSurveyController.ARG_SECTIONS, sections);
        }
        if (cursor != null)
            cursor.close();

        String[] questionProjection = {
                ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.C_SURVEYTEMPLATEQUESTION_UUID_COL,
                ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.NAME_COL + " AS Question",
                ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.DESCRIPTION_COL + " AS QuestionDesc",
                ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.SECTIONNO_COL,
                ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.SECTIONNAME_COL,
                ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.TYPE_COL,
                ModelConst.C_SURVEYRESPONSE_TABLE + "." + MSurvey.REMARKS_COL,
                ModelConst.C_SURVEYRESPONSE_TABLE + "." + MSurvey.AMT_COL
        };
        sortOrder = ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.SECTIONNO_COL + " ASC, " + ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE + "." + MSurvey.C_SURVEYTEMPLATEQUESTION_UUID_COL + " ASC";
        cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEY_JOIN_TEMPLATE_JOIN_QUESTION_JOIN_RESPONSE_TABLE),
                questionProjection, selection, selectionArgs, sortOrder);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            ArrayList<MSurvey> questions = new ArrayList<>();
            do {
                questions.add(populateProjectTask(cursor));
            } while (cursor.moveToNext());
            output.putSerializable(PBSSurveyController.ARG_QUESTIONS, questions);
        }
        cursor.close();
        return output;
    }

    private MSurvey populateProjectTask(Cursor cursor) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        MSurvey survey = new MSurvey();
        try {
            for (int x = 0; x < cursor.getColumnNames().length; x++) {
                String columnName = cursor.getColumnName(x);
                String rowValue =  cursor.getString(x);
                if (MSurvey.C_SURVEY_UUID_COL.equalsIgnoreCase(columnName)){
                    if (rowValue != null)
                        survey.set_UUID(rowValue);
                } else if (MSurvey.C_SURVEY_ID_COL.equalsIgnoreCase(columnName)) {
                    if (rowValue != null)
                        survey.set_ID(Integer.parseInt(rowValue));
                } else if (MSurvey.C_SURVEYTEMPLATE_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    if (rowValue != null) {
                        if (!rowValue.isEmpty()) {
                            String C_SURVEYTEMPLATE_ID = ModelConst.mapUUIDtoColumn(ModelConst.C_SURVEYTEMPLATE_TABLE, MSurvey.C_SURVEYTEMPLATE_UUID_COL,
                                    rowValue, MSurvey.C_SURVEYTEMPLATE_ID_COL, cr);
                            if (!C_SURVEYTEMPLATE_ID.equals("null"))
                                survey.setC_SurveyTemplate_ID(Integer.valueOf(C_SURVEYTEMPLATE_ID));
                        }
                    }
                    survey.setC_SurveyTemplate_UUID(rowValue);
                } else if (MSurvey.C_SURVEYTEMPLATEQUESTION_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    if (rowValue != null) {
                        if (!rowValue.isEmpty()) {
                            String C_SURVEYTEMPLATEQUESTION_ID = ModelConst.mapUUIDtoColumn(ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE, MSurvey.C_SURVEYTEMPLATEQUESTION_UUID_COL,
                                    rowValue, MSurvey.C_SURVEYTEMPLATEQUESTION_ID_COL, cr);
                            if (!C_SURVEYTEMPLATEQUESTION_ID.equals("null"))
                                survey.setC_SurveyTemplateQuestion_ID(Integer.valueOf(C_SURVEYTEMPLATEQUESTION_ID));
                        }
                    }
                    survey.setC_SurveyTemplateQuestion_UUID(rowValue);
                } else if (MSurvey.C_PROJECTLOCATION_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    if (rowValue != null) {
                        if (!rowValue.isEmpty()) {
                            String projLocName = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_UUID_COL,
                                    rowValue, ModelConst.NAME_COL, cr);
                            if (!projLocName.equals("null"))
                                survey.setC_ProjectLocation_Name(projLocName);
                        }
                    }
                    survey.setC_ProjectLocation_UUID(rowValue);
                } else if (MSurvey.NAME_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setName(rowValue);
                } else if (ModelConst.IS_SYNCED_COL
                        .equalsIgnoreCase(columnName)) {
                    if ("N".equalsIgnoreCase(rowValue)) {
                        survey.setStatus("Pending to sync");
                    } else {
                        survey.setStatus("Synced");
                    }
                } else if (MSurvey.DATEDELIVERY_COL
                        .equalsIgnoreCase(columnName)) {
                    Date date = df.parse(rowValue);
                    survey.setDateDelivery(sdf.format(date));
                } else if ("TemplateName"
                        .equalsIgnoreCase(columnName)) {
                    survey.setTemplateName(rowValue);
                } else if (MSurvey.DESCRIPTION_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setDescription(rowValue);
                } else if ("Question"
                        .equalsIgnoreCase(columnName)) {
                    survey.setQuestion(rowValue);
                } else if ("QuestionDesc"
                        .equalsIgnoreCase(columnName)) {
                    survey.setQuestionDesc(rowValue);
                } else if (MSurvey.SECTIONNO_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setSectionno(rowValue);
                } else if (MSurvey.SECTIONNAME_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setSectionname(rowValue);
                } else if (MSurvey.TYPE_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setType(rowValue);
                } else if (MSurvey.REMARKS_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setRemarks(rowValue);
                } else if (MSurvey.AMT_COL
                        .equalsIgnoreCase(columnName)) {
                    survey.setAmt(rowValue);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return survey;
    }


    private Bundle insertSurvey() {
        ContentValues[] answerCV = (ContentValues[]) input.getParcelableArray(PBSSurveyController.SURVEY_ANSWERS);
        ContentValues surveyCV = input.getParcelable(PBSSurveyController.SURVEY_VALUES);
        Uri uri = cr.insert(ModelConst.uriCustomBuilder(ModelConst.C_SURVEY_TABLE), surveyCV);
        boolean result = ModelConst.getURIResult(uri);

        if (result) {
            long id = ContentUris.parseId(uri);
            String surveyuuid = ModelConst.mapUUIDtoColumn(ModelConst.C_SURVEY_TABLE, "rowid", String.valueOf(id), MSurvey.C_SURVEY_UUID_COL, cr);
            for (int i = 0; i < answerCV.length; i++) {
                answerCV[i].put(MSurvey.C_SURVEY_UUID_COL, surveyuuid);
            }
            int insertedCount = cr.bulkInsert(ModelConst.uriCustomBuilder(ModelConst.C_SURVEYRESPONSE_TABLE), answerCV);
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully insert new survey.");
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to insert new survey.");
        }
        return output;
    }

    private Bundle getTemplates() {
        String[] projection = {MSurvey.C_SURVEYTEMPLATE_UUID_COL, MSurvey.NAME_COL};
        //grab the template names from database view.
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEYTEMPLATE_TABLE),
                projection, null, null, null);

        ArrayList<SpinnerPair> templateList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                SpinnerPair pair = new SpinnerPair();
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    if (MSurvey.NAME_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setValue(cursor.getString(x));
                    } else if (MSurvey.C_SURVEYTEMPLATE_UUID_COL
                            .equalsIgnoreCase(cursor.getColumnName(x))) {
                        pair.setKey(cursor.getString(x));
                    }
                }
                templateList.add(pair);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        output.putParcelableArrayList(PBSSurveyController.ARG_TEMPLATES, templateList);
        return output;
    }

    private Bundle getTemplateQuestions() {
        String[] templateProjection = {
                MSurvey.NAME_COL + " AS TemplateName",
                MSurvey.DESCRIPTION_COL
        };
        String selection = MSurvey.C_SURVEYTEMPLATE_UUID_COL + "=?";
        String[] selectionArgs = {input.getString(PBSSurveyController.ARG_TEMPLATE_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEYTEMPLATE_TABLE),
                templateProjection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            output.putSerializable(PBSSurveyController.ARG_SURVEY, populateProjectTask(cursor));
            cursor.close();
        }
        else return output;

        String[] sectionProjection = { "DISTINCT " + MSurvey.SECTIONNO_COL, MSurvey.SECTIONNAME_COL };
        String sortOrder = MSurvey.SECTIONNO_COL + " ASC";
        cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE),
                sectionProjection, selection, selectionArgs, sortOrder);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            ArrayList<String> sections = new ArrayList<>();
            do {
                sections.add(cursor.getString(1));
            } while (cursor.moveToNext());
            output.putSerializable(PBSSurveyController.ARG_SECTIONS, sections);
        }
        if (cursor != null)
            cursor.close();

        String[] questionProjection = {
                MSurvey.C_SURVEYTEMPLATEQUESTION_UUID_COL,
                MSurvey.NAME_COL + " AS Question",
                MSurvey.DESCRIPTION_COL + " AS QuestionDesc",
                MSurvey.SECTIONNO_COL,
                MSurvey.SECTIONNAME_COL,
                MSurvey.TYPE_COL
        };
        //grab the template questions from database view.
        sortOrder = MSurvey.SECTIONNO_COL + " ASC, " + MSurvey.C_SURVEYTEMPLATEQUESTION_ID_COL + " ASC";
        cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_SURVEYTEMPLATEQUESTION_TABLE),
                questionProjection, selection, selectionArgs, sortOrder);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            ArrayList<MSurvey> questions = new ArrayList<>();
            do {
                questions.add(populateProjectTask(cursor));
            } while (cursor.moveToNext());
            output.putSerializable(PBSSurveyController.ARG_QUESTIONS, questions);
        }
        if (cursor != null)
            cursor.close();
        return output;
    }

}
