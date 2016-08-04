package com.pbasolutions.android.model;

/**
 * Created by pbadell on 10/5/15.
 */

//C_BPARTNER

import android.databinding.BaseObservable;

import com.pbasolutions.android.json.PBSJson;

import java.io.Serializable;
import java.sql.Blob;

public class MEmployee extends PBSJson{

    public static final String C_BPARTNER_UUID_COL = "C_BPartner_UUID";
    public static final String JOB_TITLE_COL = "Job_Title";
    private String name;
    private String idNumber;
    private String phone;
    private String profPic;
    private String age;
    private String sex;
    private String jobTitle;
    private String _UUID;
    private String ICNo;
    private String WorkPermit;

    private String Name;
    private String Status;
    private String _ID;

    private boolean isEditable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.equalsIgnoreCase("null"))
            phone = "";
        this.phone = phone;
    }

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public boolean isEditable() {
        return isEditable;
    }

    @Override
    public String get_UUID() {
        return _UUID;
    }

    @Override
    public void set_ID(int _ID) {

    }

    @Override
    public int get_ID() {
        return 0;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

    public String getICNo() {
        return ICNo;
    }

    public void setICNo(String ICNo) {
        this.ICNo = ICNo;
    }

    public String getWorkPermit() {
        return WorkPermit;
    }

    public void setWorkPermit(String workPermit) {
        WorkPermit = workPermit;
    }
}
