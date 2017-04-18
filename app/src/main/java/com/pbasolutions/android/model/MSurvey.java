package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MSurvey extends PBSJson {
    public static final String TABLENAME = "C_Survey";
    public static final String C_SURVEY_ID_COL = "C_Survey_ID";
    public static final String C_SURVEY_UUID_COL = "C_Survey_UUID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String C_SURVEYTEMPLATE_ID_COL = "C_SurveyTemplate_ID";
    public static final String C_SURVEYTEMPLATE_UUID_COL = "C_SurveyTemplate_UUID";
    public static final String C_SURVEYTEMPLATEQUESTION_ID_COL = "C_SurveyTemplateQuestion_ID";
    public static final String C_SURVEYTEMPLATEQUESTION_UUID_COL = "C_SurveyTemplateQuestion_UUID";
    public static final String VALUE_COL = "Value";
    public static final String VALIDTO_COL = "ValidTo";
    public static final String EMAIL_COL = "Email";
    public static final String DATEDELIVERY_COL = "DateDelivery";
    public static final String EMAILSENT_COL = "EmailSent";
    public static final String STATUS_COL = "Status";
    public static final String NAME_COL = "Name";
    public static final String DATESTART_COL = "DateStart";
    public static final String DATEEND_COL = "DateEnd";
    public static final String LONGITUDE_COL = "Longitude";
    public static final String LATITUDE_COL = "Latitude";
    public static final String ATTACHMENT_SIGNATURE_COL = "Attachment_Signature";
    public static final String DESCRIPTION_COL = "Description";
    public static final String SECTIONNO_COL = "SectionNo";
    public static final String SECTIONNAME_COL = "SectionName";
    public static final String TYPE_COL = "Type";
    public static final String REMARKS_COL = "Remarks";
    public static final String AMT_COL = "Amt";

    private int C_ProjectLocation_ID;
    private String C_ProjectLocation_UUID;
    private String C_ProjectLocation_Name;
    private int C_SurveyTemplate_ID;
    private String C_SurveyTemplate_UUID;
    private String C_SurveyTemplate_Name;
    private int C_SurveyTemplateQuestion_ID;
    private String C_SurveyTemplateQuestion_UUID;
    private String value;
    private String validTo;
    private String dateDelivery;
    private String email;
    private String emailSent;
    private String status;
    private String name;
    private String dateStart;
    private String dateEnd;
    private String longitude;
    private String latitude;
    private String Attachment_Signature;
    private String templatename;
    private String description;
    private String question;
    private String questiondesc;
    private String sectionno;
    private String sectionname;
    private String type;
    private String remarks;
    private String amt;

    public int getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(int c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public String getC_ProjectLocation_UUID() {
        return C_ProjectLocation_UUID;
    }

    public void setC_ProjectLocation_UUID(String c_ProjectLocation_UUID) {
        C_ProjectLocation_UUID = c_ProjectLocation_UUID;
    }

    public String getC_ProjectLocation_Name() {
        return C_ProjectLocation_Name;
    }

    public void setC_ProjectLocation_Name(String c_ProjectLocation_Name) {
        C_ProjectLocation_Name = c_ProjectLocation_Name;
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

    public String getC_SurveyTemplate_Name() {
        return C_SurveyTemplate_Name;
    }

    public void setC_SurveyTemplate_Name(String c_SurveyTemplate_Name) {
        C_SurveyTemplate_Name = c_SurveyTemplate_Name;
    }

    public int getC_SurveyTemplateQuestion_ID() {
        return C_SurveyTemplateQuestion_ID;
    }

    public void setC_SurveyTemplateQuestion_ID(int c_SurveyTemplateQuestion_ID) {
        C_SurveyTemplateQuestion_ID = c_SurveyTemplateQuestion_ID;
    }

    public String getC_SurveyTemplateQuestion_UUID() {
        return C_SurveyTemplateQuestion_UUID;
    }

    public void setC_SurveyTemplateQuestion_UUID(String c_SurveyTemplateQuestion_UUID) {
        C_SurveyTemplateQuestion_UUID = c_SurveyTemplateQuestion_UUID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateDelivery() {
        return dateDelivery;
    }

    public void setDateDelivery(String dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public String getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(String emailSent) {
        this.emailSent = emailSent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAttachment_Signature() {
        return Attachment_Signature;
    }

    public void setAttachment_Signature(String signature) {
        this.Attachment_Signature = signature;
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

    public String getTemplateName() {
        return templatename;
    }

    public void setTemplateName(String templatename) {
        this.templatename = templatename;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionDesc() {
        return questiondesc;
    }

    public void setQuestionDesc(String questiondesc) {
        this.questiondesc = questiondesc;
    }
}
