package com.pbasolutions.android.json;

/**
 * Created by pbadell on 7/1/15.
 */
public class PBSResultJSON extends PBSJson {

    String GUID;
    String Record;
    String DocStatus;
    String error;
    String Version;

    public static final String TRUE_TEXT = "TRUE";
    public static final String FALSE_TEXT = "FALSE";

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getRecord() {
        return Record;
    }

    public void setRecord(String record) {
        Record = record;
    }

    public String getDocStatus() {
        return DocStatus;
    }

    public void setDocStatus(String docStatus) {
        DocStatus = docStatus;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        this.Version = version;
    }
}
