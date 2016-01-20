package com.pbasolutions.android.json;

import java.io.Serializable;

/**
 * Created by pbadell on 6/25/15.
 */
public class PBSTableJSON implements Serializable{
    private String Name;
    private PBSColumnsJSON Columns[];

    public String getTableName() {
        return Name;
    }
    public void setTableName(String tableName) {
        this.Name = tableName;
    }

    public PBSColumnsJSON[] getColumns() {
        return Columns;
    }
    public void setColumns(PBSColumnsJSON[] columns) {
        this.Columns = columns;
    }

}
