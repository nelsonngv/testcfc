package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MSurveyQnA extends PBSJson {
    public static final String TABLENAME = "C_SurveyResponse";
    public static final String C_SURVEYRESPONSE_ID_COL = "C_SurveyResponse_ID";
    public static final String C_SURVEYRESPONSE_UUID_COL = "C_SurveyResponse_UUID";
    public static final String C_SURVEY_ID_COL = "C_Survey_ID";
    public static final String C_SURVEY_UUID_COL = "C_Survey_UUID";
    public static final String C_SURVEYTEMPLATE_ID_COL = "C_SurveyTemplate_ID";
    public static final String C_SURVEYTEMPLATE_UUID_COL = "C_SurveyTemplate_UUID";
    public static final String C_SURVEYTEMPLATEQUESTION_UUID_COL = "C_SurveyTemplateQuestion_UUID";
    public static final String NAME_COL = "Name";
    public static final String DESCRIPTION_COL = "Description";
    public static final String SECTIONNO_COL = "SectionNo";
    public static final String SECTIONNAME_COL = "SectionName";
    public static final String TYPE_COL = "Type";
    public static final String REMARKS_COL = "Remarks";
    public static final String AMT_COL = "Amt";

    private int C_SurveyResponse_ID;
    private String C_SurveyResponse_UUID;
    private int C_Survey_ID;
    private String C_Survey_UUID;
    private int C_SurveyTemplate_ID;
    private String C_SurveyTemplate_UUID;
    private String templatename;
    private String description;
    private String question;
    private String sectionno;
    private String sectionname;
    private String type;
    private String remarks;
    private String amt;

    public int getC_SurveyResponse_ID() {
        return C_SurveyResponse_ID;
    }

    public void setC_SurveyResponse_ID(int c_SurveyResponse_ID) {
        C_SurveyResponse_ID = c_SurveyResponse_ID;
    }

    public String getC_SurveyResponse_UUID() {
        return C_SurveyResponse_UUID;
    }

    public void setC_SurveyResponse_UUID(String c_SurveyResponse_UUID) {
        C_SurveyResponse_UUID = c_SurveyResponse_UUID;
    }

    public int getC_Survey_ID() {
        return C_Survey_ID;
    }

    public void setC_Survey_ID(int c_Survey_ID) {
        C_Survey_ID = c_Survey_ID;
    }

    public String getC_Survey_UUID() {
        return C_Survey_UUID;
    }

    public void setC_Survey_UUID(String c_Survey_UUID) {
        C_Survey_UUID = c_Survey_UUID;
    }

    public int getC_SurveyTemplate_ID() {
        return C_SurveyTemplate_ID;
    }

    public void setC_SurveyTemplate_ID(int c_SurveyTemplate_ID) {
        C_SurveyTemplate_ID = c_SurveyTemplate_ID;
    }

    public String getC_SurveyTemplate_UUID() {
        return C_SurveyTemplate_UUID;
    }

    public void setC_SurveyTemplate_UUID(String c_SurveyTemplate_UUID) {
        C_SurveyTemplate_UUID = c_SurveyTemplate_UUID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSectionno() {
        return sectionno;
    }

    public void setSectionno(String sectionno) {
        this.sectionno = sectionno;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplateName() {
        return templatename;
    }

    public void setTemplateName(String surveyname) {
        this.templatename = templatename;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}
