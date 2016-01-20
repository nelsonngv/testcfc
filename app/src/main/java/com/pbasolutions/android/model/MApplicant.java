package com.pbasolutions.android.model;

/**
 * Created by pbadell on 10/5/15.
 */

//HR_JOBAPPLICATION

import android.databinding.Bindable;
import android.view.View;

/**
 * Application model class, main purpose is to store and retrieve application info.
 */
public class MApplicant extends MEmployee{
  //  private MEmployee employee;
    private String _UUID;
    private String HR_SETUP_JOB_UUID;
    private String HR_NATIONALITY_UUID;
    private String HR_PROJLOCATION_SHIFT_UUID;

    private String nationality;
    private String idType;
    private String maritalStat;
    private String qualHighest;
    private String qualOther;

    private String applDate;
    private String expSal;
    private String isExp;
    private String status;
    private String numOfExp;
    private String projLoc;
    private String prefShift;
    private String certPic1;
    private String certPic2;
    private String certPic3;
    private String certPic4;
    private String certPic5;
    private String certPic6;
    private String certPic7;
    private String certPic8;
    private String certPic9;
    private String certPic10;
    private int empty;

    public static String HR_JOBAPPLICATION_UUID_COL = "HR_JobApplication_UUID";
    public static String HR_SETUP_JOB_UUID_COL = "HR_Setup_Job_UUID";
    public static String HR_NATIONALITY_UUID_COL = "HR_Nationality_UUID";
    public static String HR_PROJLOCATION_SHIFT_UUID_COL = "HR_ProjLocation_Shift_UUID";
    public static String NAME_COL = "Name";
    public static String IDTYPE_COL = "IDType";
    public static String IDNUMBER_COL = "IDNumber";
    public static String AGE_COL = "Age";
    public static String SEX_COL = "Sex";
    public static String PHONE_COL = "Phone";
    public static String MARITALSTATUS_COL = "MaritalStatus";
    public static String QUALIFICATION_HIGHEST_COL = "Qualification_Highest";
    public static String QUALIFICATION_OTHER_COL = "Qualification_Other";
    public static String STATUS_COL = "Status";
    public static String APPLICATIONDATE_COL = "ApplicationDate";
    public static String EXPECTEDSALARY_COL = "ExpectedSalary";
    public static String ISEXPERIENCED_COL = "IsExperienced";
    public static String YEARSOFEXPERIENCED_COL = "YearsOfExperience";
    public static String ATTACHMENT_APPLICANTPICTURE_COL = "Attachment_ApplicantPicture";
    public static String ATTACHMENT_CERTPICTURE_1 = "Attachment_CertPicture_1";
    public static String ATTACHMENT_CERTPICTURE_2 = "Attachment_CertPicture_2";
    public static String ATTACHMENT_CERTPICTURE_3 = "Attachment_CertPicture_3";
    public static String ATTACHMENT_CERTPICTURE_4 = "Attachment_CertPicture_4";
    public static String ATTACHMENT_CERTPICTURE_5 = "Attachment_CertPicture_5";
    public static String ATTACHMENT_CERTPICTURE_6 = "Attachment_CertPicture_6";
    public static String ATTACHMENT_CERTPICTURE_7 = "Attachment_CertPicture_7";
    public static String ATTACHMENT_CERTPICTURE_8 = "Attachment_CertPicture_8";
    public static String ATTACHMENT_CERTPICTURE_9 = "Attachment_CertPicture_9";
    public static String ATTACHMENT_CERTPICTURE_10 = "Attachment_CertPicture_10";

    @Bindable
    public int getEmpty() {
        return View.VISIBLE;
    }

    public void setEmpty(int empty) {
        this.empty = empty;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getMaritalStat() {
        return maritalStat;
    }

    public void setMaritalStat(String maritalStat) {
        this.maritalStat = maritalStat;
    }

    public String getQualHighest() {
        return qualHighest;
    }

    public void setQualHighest(String qualHighest) {
        this.qualHighest = qualHighest;
    }

    public String getQualOther() {
        return qualOther;
    }

    public void setQualOther(String qualOther) {
        this.qualOther = qualOther;
    }
    public String getApplDate() {
        return applDate;
    }

    public void setApplDate(String applDate) {
        this.applDate = applDate;
    }

    public String getExpSal() {
        return expSal;
    }

    public void setExpSal(String expSal) {
        this.expSal = expSal;
    }

    public String getIsExp() {
        return isExp;
    }

    public void setIsExp(String isExp) {
        this.isExp = isExp;
    }

    public String getNumOfExp() {
        return numOfExp;
    }

    public void setNumOfExp(String numOfExp) {
        this.numOfExp = numOfExp;
    }

    public String getProjLoc() {
        return projLoc;
    }

    public void setProjLoc(String projLoc) {
        this.projLoc = projLoc;
    }

    public String getPrefShift() {
        return prefShift;
    }

    public void setPrefShift(String prefShift) {
        this.prefShift = prefShift;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getCertPic1() {
        return certPic1;
    }

    public void setCertPic1(String certPic1) {
        this.certPic1 = certPic1;
    }

    public String getCertPic2() {
        return certPic2;
    }

    public void setCertPic2(String certPic2) {
        this.certPic2 = certPic2;
    }

    public String getCertPic3() {
        return certPic3;
    }

    public void setCertPic3(String certPic3) {
        this.certPic3 = certPic3;
    }

    public String getCertPic4() {
        return certPic4;
    }

    public void setCertPic4(String certPic4) {
        this.certPic4 = certPic4;
    }

    public String getCertPic5() {
        return certPic5;
    }

    public void setCertPic5(String certPic5) {
        this.certPic5 = certPic5;
    }

    public String getCertPic6() {
        return certPic6;
    }

    public void setCertPic6(String certPic6) {
        this.certPic6 = certPic6;
    }

    public String getCertPic7() {
        return certPic7;
    }

    public void setCertPic7(String certPic7) {
        this.certPic7 = certPic7;
    }

    public String getCertPic8() {
        return certPic8;
    }

    public void setCertPic8(String certPic8) {
        this.certPic8 = certPic8;
    }

    public String getCertPic9() {
        return certPic9;
    }

    public void setCertPic9(String certPic9) {
        this.certPic9 = certPic9;
    }

    public String getCertPic10() {
        return certPic10;
    }

    public void setCertPic10(String certPic10) {
        this.certPic10 = certPic10;
    }

    @Override
    public String get_UUID() {
        return _UUID;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }


    public String getHR_SETUP_JOB_UUID() {
        return HR_SETUP_JOB_UUID;
    }

    public void setHR_SETUP_JOB_UUID(String HR_SETUP_JOB_UUID) {
        this.HR_SETUP_JOB_UUID = HR_SETUP_JOB_UUID;
    }

    public String getHR_NATIONALITY_UUID() {
        return HR_NATIONALITY_UUID;
    }

    public void setHR_NATIONALITY_UUID(String HR_NATIONALITY_UUID) {
        this.HR_NATIONALITY_UUID = HR_NATIONALITY_UUID;
    }

    public String getHR_PROJLOCATION_SHIFT_UUID() {
        return HR_PROJLOCATION_SHIFT_UUID;
    }

    public void setHR_PROJLOCATION_SHIFT_UUID(String HR_PROJLOCATION_SHIFT_UUID) {
        this.HR_PROJLOCATION_SHIFT_UUID = HR_PROJLOCATION_SHIFT_UUID;
    }
}
