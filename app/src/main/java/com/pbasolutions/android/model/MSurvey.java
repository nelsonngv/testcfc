package com.pbasolutions.android.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by tinker on 4/24/16.
 */
public class MSurvey extends PBSJson implements Parcelable {
    public static final String TABLENAME = "C_Survey";
    public static final String C_SURVEY_ID_COL = "C_Survey_ID";
    public static final String C_SURVEY_UUID_COL = "C_Survey_UUID";
    public static final String C_SURVEYRESPONSE_UUID_COL = "C_SurveyResponse_UUID";
    public static final String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static final String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static final String C_SURVEYTEMPLATE_ID_COL = "C_SurveyTemplate_ID";
    public static final String C_SURVEYTEMPLATE_UUID_COL = "C_SurveyTemplate_UUID";
    public static final String C_SURVEYTEMPLATEQUESTION_ID_COL = "C_SurveyTemplateQuestion_ID";
    public static final String C_SURVEYTEMPLATEQUESTION_UUID_COL = "C_SurveyTemplateQuestion_UUID";
    public static final String ISACTIVE_COL = "IsActive";
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
    public static final String DATETRX_COL = "DateTrx";

    private String _UUID;
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
    private int statusColor;

    public MSurvey() {

    }

    public MSurvey(Parcel in) {
        C_ProjectLocation_ID = in.readInt();
        C_ProjectLocation_UUID = in.readString();
        C_ProjectLocation_Name = in.readString();
        C_SurveyTemplate_ID = in.readInt();
        C_SurveyTemplate_UUID = in.readString();
        C_SurveyTemplate_Name = in.readString();
        C_SurveyTemplateQuestion_ID = in.readInt();
        C_SurveyTemplateQuestion_UUID = in.readString();
        value = in.readString();
        validTo = in.readString();
        dateDelivery = in.readString();
        email = in.readString();
        emailSent = in.readString();
        status = in.readString();
        name = in.readString();
        dateStart = in.readString();
        dateEnd = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        Attachment_Signature = in.readString();
        templatename = in.readString();
        description = in.readString();
        question = in.readString();
        questiondesc = in.readString();
        sectionno = in.readString();
        sectionname = in.readString();
        type = in.readString();
        remarks = in.readString();
        amt = in.readString();
    }

    public static final Creator<MSurvey> CREATOR = new Creator<MSurvey>() {
        @Override
        public MSurvey createFromParcel(Parcel in) {
            return new MSurvey(in);
        }

        @Override
        public MSurvey[] newArray(int size) {
            return new MSurvey[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(C_ProjectLocation_ID);
        parcel.writeString(C_ProjectLocation_UUID);
        parcel.writeString(C_ProjectLocation_Name);
        parcel.writeInt(C_SurveyTemplate_ID);
        parcel.writeString(C_SurveyTemplate_UUID);
        parcel.writeString(C_SurveyTemplate_Name);
        parcel.writeInt(C_SurveyTemplateQuestion_ID);
        parcel.writeString(C_SurveyTemplateQuestion_UUID);
        parcel.writeString(value);
        parcel.writeString(validTo);
        parcel.writeString(dateDelivery);
        parcel.writeString(email);
        parcel.writeString(emailSent);
        parcel.writeString(status);
        parcel.writeString(name);
        parcel.writeString(dateStart);
        parcel.writeString(dateEnd);
        parcel.writeString(longitude);
        parcel.writeString(latitude);
        parcel.writeString(Attachment_Signature);
        parcel.writeString(templatename);
        parcel.writeString(description);
        parcel.writeString(question);
        parcel.writeString(questiondesc);
        parcel.writeString(sectionno);
        parcel.writeString(sectionname);
        parcel.writeString(type);
        parcel.writeString(remarks);
        parcel.writeString(amt);
    }

    @Override
    public String get_UUID() {
        return _UUID;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

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
        switch(status){
            case "Synced": {
                setStatusColor(Color.GREEN);
                break;
            }
            default: {
                setStatusColor(Color.RED);
                break;
            }
        }
    }

    public int getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(int statusColor) {
        this.statusColor = statusColor;
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
