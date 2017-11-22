package com.pbasolutions.android.database;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.account.PBSAccountInfo;


/**
 * Created by pbadell on 6/29/15.
 */
public class PBSDBHelper extends SQLiteOpenHelper {
    /**
     * Class tag name.
     */
    private static final String TAG = "PBSDBHelper";
    /**
     * Database name.
     */
    public static final String DATABASE_NAME = "cfc.db";
    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 27;
    /**
     *
     */
    private Context context;
    /**
     * Constructor.
     */
    public PBSDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tables
        try {
            //ad_client
            db.execSQL("CREATE TABLE AD_CLIENT(" +
                    //STANDARD
                    "AD_CLIENT_ID NUMBER(10, 0)," +
                    "AD_ORG_UUID TEXT," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "AD_CLIENT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "NAME NVARCHAR2(300) NOT NULL," +
                    "CONSTRAINT CLIENTID_UNIQCONS UNIQUE (AD_CLIENT_ID)" +
                    ");");

            //create index for AD_Client_ID
            db.execSQL("CREATE INDEX AD_CLIENT_ID_INDEX ON AD_CLIENT(AD_CLIENT_ID)");

            //ad_org
            db.execSQL("CREATE TABLE AD_ORG(" +
                    //STANDARD
                    "AD_ORG_ID NUMBER(10, 0)," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "AD_ORG_UUID TEXT PRIMARY KEY NOT NULL, " +
                    //FK
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    //OTHERS
                    "NAME NVARCHAR2(300) NOT NULL, " +
                    "CONSTRAINT ORGID_UNIQCONS UNIQUE (AD_ORG_ID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID));");

            //create index for AD_Org_ID
            db.execSQL("CREATE INDEX AD_ORG_ID_INDEX ON AD_ORG(AD_ORG_ID)");

            //ad_role
            db.execSQL("CREATE TABLE AD_ROLE(" +
                    //STANDARD
                    "AD_ROLE_ID NUMBER(10, 0)," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "AD_ROLE_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "AD_ORG_UUID TEXT NOT NULL," +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    //OTHERS
                    "NAME NVARCHAR2(300) NOT NULL," +
                    //--
                    "CONSTRAINT ROLEID_UNIQCONS UNIQUE (AD_ROLE_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID));");

            //create index for AD_Role_ID
            db.execSQL("CREATE INDEX AD_ROLE_ID_INDEX ON AD_ROLE(AD_ROLE_ID)");

            //ad_user
            db.execSQL("CREATE TABLE AD_USER(" +
                    //MASTER DATA
                    //STANDARD
                    "AD_USER_ID NUMBER(10, 0)," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N' NOT NULL," +
                    //PK
                    "AD_USER_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    //TODO : TEMPORARILY REMOVE "NOT NULL". BECAUSE SHOULD START WITH AD_CLIENT.
                    //TODO : EXPECTED THAT SNYC FIRST WILL ADD THE ORG AND CLIENT.
                    "AD_ORG_UUID TEXT ," +
                    "AD_CLIENT_UUID TEXT ," +
                    //OTHERS
                    "NAME NVARCHAR2(300) NOT NULL," +
                    "TITLE NVARCHAR(40)," +
                    "C_BPARTNER_UUID NUMBER(10, 0)," +
                    //--
                    "CONSTRAINT USERID_UNIQCONS UNIQUE (AD_USER_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)" +
                    ");");

            //create index for AD_User_ID
            db.execSQL("CREATE INDEX AD_USER_ID_INDEX ON AD_USER(AD_USER_ID)");

            db.execSQL("CREATE TABLE C_PROJECTLOCATION(" +
                    //STANDARD
                    "C_PROJECTLOCATION_ID NUMBER(10, 0)," +
                    "AD_ORG_UUID TEXT NOT NULL," +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N' NOT NULL," +
                    "HR_CLUSTER_UUID TEXT NULL," +
                    //PK
                    "C_PROJECTLOCATION_UUID TEXT PRIMARY KEY NOT NULL, " +
                    //OTHERS
                    "NAME NVARCHAR2(300) NOT NULL," +
                    "VALUE NVARCHAR2(60) NOT NULL," +
                    "ISKIOSKMODE CHAR(1) DEFAULT 'N' NOT NULL," +
                    "ISPHOTO CHAR(1) DEFAULT 'N' NOT NULL," +
                    "NFCTAG TEXT NULL," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for C_PROJECTLOCATION_ID
            db.execSQL("CREATE INDEX C_PROJECTLOCATION_INDEX ON" +
                    " C_PROJECTLOCATION(C_PROJECTLOCATION_ID)");

            db.execSQL("CREATE TABLE HR_CLUSTER(" +
                    //STANDARD
                    "HR_CLUSTER_ID NUMBER(10, 0)," +
                    //PK
                    "HR_CLUSTER_UUID TEXT PRIMARY KEY NOT NULL" +
                    ");");

            //create index for HR_CLUSTER_ID
            db.execSQL("CREATE INDEX HR_CLUSTER_INDEX ON" +
                    " HR_CLUSTER(HR_CLUSTER_ID)");

            db.execSQL("CREATE TABLE HR_CLUSTERMANAGEMENT(" +
                    //STANDARD
                    "HR_CLUSTERMANAGEMENT_ID NUMBER(10, 0)," +
                    "HR_CLUSTER_UUID TEXT NULL," +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL," +
                    "AD_USER_UUID TEXT NULL," +
                    "ISPRIMARY CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    //PK
                    "HR_CLUSTERMANAGEMENT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID), " +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_CLUSTERMANAGEMENT_ID
            db.execSQL("CREATE INDEX HR_CLUSTERMANAGEMENT_INDEX ON" +
                    " HR_CLUSTERMANAGEMENT(HR_CLUSTERMANAGEMENT_ID)");

            //m_checkpoint --added latitude n long and tag
            db.execSQL("CREATE TABLE M_CHECKPOINT(" +
                    //STANDARD
                    "M_CHECKPOINT_ID NUMBER(10, 0)," +
                    "AD_ORG_UUID TEXT NOT NULL," +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_CHECKPOINT_UUID TEXT PRIMARY KEY NOT NULL, " +
                    //FK
                    "C_PROJECTLOCATION_UUID NUMBER(10,0) NOT NULL," +
                    //OTHERS
                    "NAME NVARCHAR2(300) NOT NULL," +
                    //NFC TAG VALUE
                    "VALUE NVARCHAR2(60) NOT NULL," +
                    "DESCRIPTION NVARCHAR2(255), " +
                    "LATITUDE DOUBLE NOT NULL," +
                    "LONGITUDE DOUBLE NOT NULL," +
                    //SEQUENCE ROUTE
                    "SEQNO TEXT NOT NULL," +
                    "CONSTRAINT CHECKPOINT_UNIQCONS UNIQUE (M_CHECKPOINT_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES" +
                    " C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)" +
                    ");");

            //create index for M_Checkpoint_ID
            db.execSQL("CREATE INDEX M_CHECKPOINT_ID_INDEX ON M_CHECKPOINT(M_CHECKPOINT_ID)");

            //m_checkin -- added user uuid
            db.execSQL("CREATE TABLE M_CHECKIN(" +
                    //STANDARD
                    "M_CHECKIN_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "AD_USER_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'N'," +
                    "ISSYNCED BOOLEAN DEFAULT 'N'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_CHECKIN_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "M_CHECKPOINT_UUID TEXT NOT NULL," +
                    //OTHERS
                    "DATETRX DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "LATITUDE DOUBLE ," +
                    "LONGITUDE DOUBLE ," +
                    "DESCRIPTION NVARCHAR2(255)," +
                    "ATTACHMENT_TOURPICTURE VARCHAR2(100)," +
                    //--
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(M_CHECKPOINT_UUID) REFERENCES" +
                    " M_CHECKPOINT(M_CHECKPOINT_UUID)" +
                    ");");

            //create index for M_Checkin_ID
            db.execSQL("CREATE INDEX M_CHECKIN_ID_INDEX ON M_CHECKIN(M_CHECKIN_ID)");


            //HR_SETUP_JOB
            db.execSQL("CREATE TABLE HR_SETUP_JOB(" +
                    //STANDARD
                    "HR_SETUP_JOB_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_SETUP_JOB_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "VALUE VARCHAR2(300) ," +
                    //--
                    "CONSTRAINT SETUPJOB_UNIQCONS UNIQUE (HR_SETUP_JOB_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");


            //create index for HR_SETUP_JOB_ID_INDEX
            db.execSQL("CREATE INDEX HR_SETUP_JOB_ID_INDEX ON HR_SETUP_JOB(HR_SETUP_JOB_ID)");

            //HR_JOB_POSITION
            db.execSQL("CREATE TABLE HR_JOBPOSITION(" +
                    //STANDARD
                    "HR_JOBPOSITION_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_JOBPOSITION_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "HR_SETUP_JOB_UUID TEXT," +
                    "C_BPARTNER_UUID TEXT," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "JOB_TITLE VARCHAR2(60) ," +
                    //--
                    "CONSTRAINT JOBPOS_UNIQCONS UNIQUE (HR_JOBPOSITION_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(HR_SETUP_JOB_UUID) REFERENCES" +
                    " HR_SETUP_JOB(HR_SETUP_JOB_UUID)," +
                    "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_JOB_POSITION_INDEX
            db.execSQL("CREATE INDEX HR_JOBPOSITION_INDEX ON" +
                    " HR_JOBPOSITION(HR_JOBPOSITION_ID)");

            //HR_NATIONALITY
            db.execSQL("CREATE TABLE HR_NATIONALITY(" +
                    //STANDARD
                    "HR_NATIONALITY_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_NATIONALITY_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "VALUE VARCHAR2(300) ," +
                    //--
                    "CONSTRAINT NATIONALITY_UNIQCONS UNIQUE (HR_NATIONALITY_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_SETUP_JOB_ID_INDEX
            db.execSQL("CREATE INDEX HR_NATIONALITY_ID_INDEX ON" +
                    " HR_NATIONALITY(HR_NATIONALITY_ID)");

            //HR_SHIFT
            db.execSQL("CREATE TABLE HR_SHIFT(" +
                    //STANDARD
                    "HR_SHIFT_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_SHIFT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "TIMEFROM DATETIME ," +
                    "TIMETO DATETIME ," +
                    "NAME VARCHAR2(300) ," +
                    //--
                    "CONSTRAINT SHIFT_UNIQCONS UNIQUE (HR_SHIFT_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_SHIFT_ID_INDEX
            db.execSQL("CREATE INDEX HR_SHIFT_ID_INDEX ON HR_SHIFT(HR_SHIFT_ID)");

            //HR_PROJLOCATION_SHIFT
            db.execSQL("CREATE TABLE HR_PROJLOCATION_SHIFT(" +
                    //STANDARD
                    "HR_PROJLOCATION_SHIFT_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_PROJLOCATION_SHIFT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "HR_SHIFT_UUID TEXT NOT NULL," +
                    "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                    //--
//                    "CONSTRAINT PROJLOCSHIFT_UNIQCONS UNIQUE (HR_PROJLOCATION_SHIFT_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(HR_SHIFT_UUID) REFERENCES HR_SHIFT(HR_SHIFT_UUID)," +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES" +
                    " C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_PROJLOCATION_SHIFT_ID_INDEX
            db.execSQL("CREATE INDEX HR_PROJLOCATION_SHIFT_ID_INDEX ON" +
                    " HR_PROJLOCATION_SHIFT(HR_PROJLOCATION_SHIFT_ID)");

            //HR_JOBAPPLICATION **not a master data.!!
            db.execSQL("CREATE TABLE HR_JOBAPPLICATION(" +
                    //STANDARD
                    "HR_JOBAPPLICATION_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'N'," +
                    "ISSYNCED BOOLEAN DEFAULT 'N'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_JOBAPPLICATION_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "HR_SETUP_JOB_UUID TEXT NOT NULL," +
                    "HR_NATIONALITY_UUID TEXT NOT NULL," +
                    "HR_PROJLOCATION_SHIFT_UUID TEXT NOT NULL," +//////////
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "IDTYPE VARCHAR2(60) ," +
                    "IDNUMBER VARCHAR2(60) ," +
                    "AGE NUMBER(10,0) ," +
                    "SEX CHAR(1) ," +
                    "PHONE VARCHAR2(40) ," +
                    "MARITALSTATUS CHAR(1) ," +
                    "QUALIFICATION_HIGHEST VARCHAR2(60) ," +
                    "QUALIFICATION_OTHER VARCHAR(60) ," +
                    "STATUS VARCHAR2(20) DEFAULT 'APPLIED', " +/////////////////
                    "APPLICATIONDATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')) , " +/////////////
                    "EXPECTEDSALARY NUMBER, " +/////////////
                    "YEARSOFEXPERIENCE NUMBER(10,0), " +///////////////
                    "ATTACHMENT_APPLICANTPICTURE VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_1 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_2 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_3 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_4 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_5 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_6 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_7 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_8 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_9 VARCHAR2(100)," +
                    "ATTACHMENT_CERTPICTURE_10 VARCHAR2(100)," +
                    "ATTACHMENT_SIGNATURE VARCHAR2(100)," +
                    "INTERVIEWER_NOTES TEXT," +
                    //--
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
//                    "FOREIGN KEY(HR_PROJLOCATION_SHIFT_UUID) REFERENCES HR_PROJLOCATION_SHIFT(HR_PROJLOCATION_SHIFT_UUID), "+
                    "FOREIGN KEY(HR_NATIONALITY_UUID) REFERENCES HR_NATIONALITY(HR_NATIONALITY_UUID), "+
                    "FOREIGN KEY(HR_SETUP_JOB_UUID) REFERENCES HR_SETUP_JOB(HR_SETUP_JOB_UUID)" +
                    ");");

            //create index for HR_JOBAPPLICATION_ID_INDEX
            db.execSQL("CREATE INDEX HR_JOBAPPLICATION_ID_INDEX ON HR_JOBAPPLICATION(HR_JOBAPPLICATION_ID)");

            //C_BPARTNER
            db.execSQL("CREATE TABLE C_BPARTNER(" +
                    //STANDARD
                    "C_BPARTNER_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "C_BPARTNER_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    //Value column is referring to IC number.
                    "VALUE VARCHAR2(60) ," +
                    "ISEMPLOYEE CHAR(1) ," +
                    "ICNo VARCHAR2(20) ," +
                    "WorkPermit VARCHAR2(20) ," +
                    "NFCTAG TEXT NULL," +
                    //--
                    //--
                    "CONSTRAINT BIZPART_UNIQCONS UNIQUE (C_BPARTNER_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_NATIONALITY_INDEX
            db.execSQL("CREATE INDEX C_BPARTNER_ID_INDEX ON C_BPARTNER(C_BPARTNER_ID)");

            //C_BPARTNER_LOCATION
            db.execSQL("CREATE TABLE C_BPARTNER_LOCATION(" +
                    //STANDARD
                    "C_BPARTNER_LOCATION_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "C_BPARTNER_LOCATION_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "C_BPARTNER_UUID TEXT," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "PHONE NVARCHAR2(40) ," +
                    //--
                    "CONSTRAINT BIZPARTLOC_UNIQCONS UNIQUE (C_BPARTNER_LOCATION_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for C_BPARTNER_LOCATION
            db.execSQL("CREATE INDEX C_BPARTNER_LOCATION_ID_INDEX ON C_BPARTNER_LOCATION(C_BPARTNER_LOCATION_ID)");

            //M_PRODUCT_CATEGORY
            db.execSQL("CREATE TABLE M_PRODUCT_CATEGORY(" +
                    //STANDARD
                    "M_PRODUCT_CATEGORY_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_PRODUCT_CATEGORY_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "VALUE NVARCHAR2(300) ," +
                    //--
//                    "CONSTRAINT PROCAT_UNIQCONS UNIQUE (M_PRODUCT_CATEGORY_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for M_PRODUCT_CATEGORY
            db.execSQL("CREATE INDEX M_PRODUCT_CATEGORY_ID_INDEX ON M_PRODUCT_CATEGORY(M_PRODUCT_CATEGORY_ID)");

            //C_UOM
            db.execSQL("CREATE TABLE C_UOM(" +
                    //STANDARD
                    "C_UOM_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "C_UOM_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "UOMSYMBOL VARCHAR2(4)," +
                    "NAME VARCHAR2(300) ," +
                    //--
                    "CONSTRAINT UOMID_UNIQCONS UNIQUE (C_UOM_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for C_UOM_ID_INDEX
            db.execSQL("CREATE INDEX C_UOM_ID_INDEX ON C_UOM(C_UOM_ID)");

            //M_ATTRIBUTESETINSTANCE
            db.execSQL("CREATE TABLE M_ATTRIBUTESETINSTANCE(" +
                    //STANDARD
                    "M_ATTRIBUTESETINSTANCE_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_ATTRIBUTESETINSTANCE_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "DESCRIPTION NVARCHAR2(255) ," +
                    //--
//                    "CONSTRAINT ATTRIBUTESETINSTANCEID_UNIQCONS UNIQUE (M_ATTRIBUTESETINSTANCE_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for M_ATTRIBUTESETINSTANCE_ID_INDEX
            db.execSQL("CREATE INDEX M_ATTRIBUTESETINSTANCE_ID_INDEX ON M_ATTRIBUTESETINSTANCE(M_ATTRIBUTESETINSTANCE_ID)");

            //M_PURCHASEREQUEST ***not a master data
            db.execSQL("CREATE TABLE M_PURCHASEREQUEST(" +
                    //STANDARD
                    "M_PURCHASEREQUEST_ID NUMBER(10, 0) DEFAULT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_PURCHASEREQUEST_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "C_PROJECTLOCATION_UUID TEXT," +
                    "AD_USER_UUID TEXT," +
                    //OTHERS
                    "DOCUMENTNO NVARCHAR2(40),"+
                    "REQUESTDATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "ISAPPROVED CHAR(1) DEFAULT 'N'," +
                    //--
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                    "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for M_PURCHASEREQUEST_ID_INDEX
            db.execSQL("CREATE INDEX M_PURCHASEREQUEST_ID_INDEX ON M_PURCHASEREQUEST(M_PURCHASEREQUEST_ID)");

            //M_REQUISITIONLINE
            db.execSQL("CREATE TABLE M_PURCHASEREQUESTLINE(" +
                    //STANDARD
                    "M_PURCHASEREQUESTLINE_ID NUMBER(10, 0) DEFAULT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_PURCHASEREQUESTLINE_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "M_PURCHASEREQUEST_UUID TEXT NOT NULL," +
                    "M_PRODUCT_UUID TEXT," +
                    //OTHERS
                    "DATEREQUIRED DATETIME NOT NULL,"+
                    "LINE NUMBER(10,0)," +
                    "QTYREQUESTED NUMBER," +
                    "ISEMERGENCY CHAR(1) DEFAULT 'N' NOT NULL," +
                    "PURCHASEREASON NVARCHAR2(255)" +
                    //--
//                    "FOREIGN KEY(M_PURCHASEREQUEST_UUID) REFERENCES M_PURCHASEREQUEST(M_PURCHASEREQUEST_UUID)" +
//                    "FOREIGN KEY(M_PRODUCT_UUID) REFERENCES M_PRODUCT(M_PRODUCT_UUID)" +
                    ");");

            //create index for M_PURCHASEREQUESTLINE_ID_INDEX
            db.execSQL("CREATE INDEX M_PURCHASEREQUESTLINE_ID_INDEX ON M_PURCHASEREQUESTLINE(M_PURCHASEREQUESTLINE_ID)");

            //C_PROJECTTASK
            db.execSQL("CREATE TABLE C_PROJECTTASK(" +
                    //STANDARD
                    "C_PROJECTTASK_ID NUMBER(10, 0) DEFAULT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    "DATEASSIGNED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "DUEDATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    //PK
                    "C_PROJECTTASK_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                    //OTHERS
                    "PRIORITY NUMBER(10,0), " +
                    "NAME NVARCHAR2(300), " +
                    "ASSIGNEDTO NUMBER(10, 0)," + //AD_USER_ID
                    "SECASSIGNEDTO NUMBER(10, 0)," + //AD_USER_ID

                    "ISDONE CHAR(1) DEFAULT 'N' NOT NULL, " +
                    "DESCRIPTION NVARCHAR2(255), " +
                    "EQUIPMENT NVARCHAR2(255), " +
                    "CONTACT NVARCHAR2(100), " +
                    "CONTACTNO NUMBER(15, 0), " +
                    "COMMENTS NVARCHAR2(255)," +
                    "ATTACHMENT_BEFORETASKPICTURE_1 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_1 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_2 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_3 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_4 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_5 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_6 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_7 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_8 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_9 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_10 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_11 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_12 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_13 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_14 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_15 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_16 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_17 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_18 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_19 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_20 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_21 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_22 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_23 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_24 VARCHAR2(100) , " +
                    "ATTACHMENT_TASKPICTURE_25 VARCHAR2(100) , " +
                    "ATTACHMENT_SIGNATURE VARCHAR2(100) , " +
                    "LATITUDE NUMBER , " +
                    "LONGITUDE NUMBER , " +
                    "ISEXPIRINGNOTIFIED CHAR(1) DEFAULT 'N' NOT NULL, " +
                    "ISEXPIREDNOTIFIED CHAR(1) DEFAULT 'N' NOT NULL, " +
                    //--
//                    "CONSTRAINT PROJTASKID_UNIQCONS UNIQUE (C_PROJECTTASK_ID)," +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for C_PROJECTTASK_ID_INDEX
            db.execSQL("CREATE INDEX C_PROJECTTASK_ID_INDEX ON C_PROJECTTASK(C_PROJECTTASK_ID)");

            //M_PRODUCT
            db.execSQL("CREATE TABLE M_PRODUCT(" +
                    //STANDARD
                    "M_PRODUCT_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "M_PRODUCT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "C_UOM_UUID TEXT," +
                    "M_PRODUCT_CATEGORY_UUID TEXT," +
                    "M_ATTRIBUTESETINSTANCE_UUID TEXT," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "VALUE NVARCHAR2(300) ," +
                    //--
//                    "CONSTRAINT PRODUCTID_UNIQCONS UNIQUE (M_PRODUCT_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_UOM_UUID) REFERENCES C_UOM(C_UOM_UUID)," +
//                    "FOREIGN KEY(M_PRODUCT_CATEGORY_UUID) REFERENCES M_PRODUCT_CATEGORY(M_PRODUCT_CATEGORY_UUID)," +
                    "FOREIGN KEY(M_ATTRIBUTESETINSTANCE_UUID) REFERENCES M_ATTRIBUTESETINSTANCE(M_ATTRIBUTESETINSTANCE_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for M_PRODUCT_ID_INDEX
            db.execSQL("CREATE INDEX M_PRODUCT_ID_INDEX ON M_PRODUCT(M_PRODUCT_ID)");

            //A_ASSET
            db.execSQL("CREATE TABLE A_ASSET(" +
                    //STANDARD
                    "A_ASSET_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "A_ASSET_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "M_PRODUCT_UUID TEXT," +
                    //OTHERS
                    "NAME VARCHAR2(300) ," +
                    "VALUE NVARCHAR2(300)," +
                    //--
//                    "CONSTRAINT ASSETID_UNIQCONS UNIQUE (A_ASSET_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
//                    "FOREIGN KEY(M_PRODUCT_UUID) REFERENCES M_PRODUCT(M_PRODUCT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for A_ASSET_ID_INDEX
            db.execSQL("CREATE INDEX A_ASSET_ID_INDEX ON A_ASSET(A_ASSET_ID)");

            //AD_NOTE **NOT MASTER DATA.
            db.execSQL("CREATE TABLE AD_NOTE(" +
                    //STANDARD
                    "AD_NOTE_ID NUMBER(10, 0) DEFAULT NULL," +
                    //PK
                    "AD_NOTE_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "AD_USER_UUID TEXT," +
                    //OTHERS
                    "DATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "SENDER TEXT NOT NULL," +
                    "MESSAGE NVARCHAR2(5000)," +
                    //--
//                                "CONSTRAINT NOTEID_UNIQCONS UNIQUE (AD_NOTE_ID)," +
                    "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(SENDER) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for AD_NOTE_ID_INDEX
            db.execSQL("CREATE INDEX AD_NOTE_ID_INDEX ON AD_NOTE(AD_NOTE_ID)");

            //M_MOVEMENT
            db.execSQL("CREATE TABLE M_MOVEMENT(" +
                    //STANDARD
                    "M_MOVEMENT_ID NUMBER(10, 0) DEFAULT NULL," +
                    //PK
                    "M_MOVEMENT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "C_PROJECTLOCATION_UUID TEXT," +
                    "C_PROJECTLOCATIONTO_UUID TEXT," +
                    "AD_USER_UUID TEXT," +
                    //OTHERS
                    "ISAPPROVED CHAR(1)," +
                    "MOVEMENTDATE DATETIME NOT NULL," +
                    "DOCUMENTNO TEXT," +
                    //--
                    "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                    "FOREIGN KEY(C_PROJECTLOCATIONTO_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)" +
                    ");");

            //M_MOVEMENTline
            db.execSQL("CREATE TABLE M_MOVEMENTLINE(" +
                    //STANDARD
                    "M_MOVEMENTLINE_ID NUMBER(10, 0) DEFAULT NULL," +
                    //PK
                    "M_MOVEMENTLINE_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "M_MOVEMENT_UUID TEXT," +
                    "M_PRODUCT_UUID TEXT," +
                    "C_UOM_UUID TEXT, " +
                    "M_ATTRIBUTESETINSTANCE_ID NUMBER(10, 0)," +
                    "ASI_DESCRIPTION TEXT, "+
                    //OTHERS
                    "LINE VARCHAR2(60) ," +
                    "MOVEMENTQTY NUMBER ," +
                    //--
                    "FOREIGN KEY(M_PRODUCT_UUID) REFERENCES M_PRODUCT(M_PRODUCT_UUID)" +
                    ");");
            //create index for M_MOVEMENT_ID_INDEX
            db.execSQL("CREATE INDEX M_MOVEMENT_ID_INDEX ON M_MOVEMENT(M_MOVEMENT_ID)");
            db.execSQL("CREATE INDEX M_MOVEMENTLINE_ID_INDEX ON M_MOVEMENTLINE(M_MOVEMENTLINE_ID)");

            //HR_PROJECTASSIGNMENT
            db.execSQL("CREATE TABLE HR_PROJECTASSIGNMENT(" +
                    //STANDARD
                    "HR_PROJECTASSIGNMENT_ID NUMBER(10, 0) DEFAULT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL, " +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'N'," +
                    "ISSYNCED BOOLEAN DEFAULT 'N'," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +
                    //PK
                    "HR_PROJECTASSIGNMENT_UUID TEXT PRIMARY KEY NOT NULL," +
                    //FK
                    "C_BPARTNER_UUID TEXT," +
                    "C_PROJECTLOCATION_UUID TEXT," +
                    "HR_PROJLOCATION_SHIFT_UUID TEXT, " +
                    "HR_SHIFT_UUID TEXT," +
                    //OTHERS
                    "NAME NVARCHAR2(300)," +
                    "VALUE VARCHAR2(300) ," +
                    "ASSIGNMENT_TYPE VARCHAR2(20) ," +
                    "ISDEFAULT BOOLEAN," +
                    //--
//                    "CONSTRAINT HRPROJASSID_UNIQCONS UNIQUE (HR_PROJECTASSIGNMENT_ID)," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID)," +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
//                    "FOREIGN KEY(HR_PROJLOCATION_SHIFT_UUID) REFERENCES HR_PROJLOCATION_SHIFT(HR_PROJLOCATION_SHIFT_UUID)," +
//                    "FOREIGN KEY(HR_SHIFT_UUID) REFERENCES HR_SHIFT(HR_SHIFT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for HR_PROJECTASSIGNMENT_ID_INDEX
            db.execSQL("CREATE INDEX HR_PROJECTASSIGNMENT_ID_INDEX ON HR_PROJECTASSIGNMENT(HR_PROJECTASSIGNMENT_ID)");

            //pbs_synctable
            db.execSQL("CREATE TABLE PBS_SYNCTABLE(" +
                    //STANDARD
                    "PBS_SYNCTABLE_ID TEXT NOT NULL," +
                    //PK
                    "PBS_SYNCTABLE_UUID TEXT PRIMARY KEY NOT NULL," +
                    //OTHERS
                    "ISMASTERDATA BOOLEAN," +
                    "ISRETAIN BOOLEAN," +
                    "RETENTIONPERIOD INT," +
                    "AD_TABLE_UUID TEXT," +
                    "NAME TEXT," +
                    "SEQNO INT DEFAULT 0" +
                    ");");

            //JOBAPP_SHIFTS_VIEW (USED)
            db.execSQL("CREATE VIEW JOBAPP_SHIFTS_VIEW AS " +
                    "SELECT HR_PROJLOCATION_SHIFT_UUID, C_PROJECTLOCATION_UUID,  HR_SHIFT.HR_SHIFT_UUID, HR_SHIFT.NAME " +
                    "FROM HR_PROJLOCATION_SHIFT " +
                    "INNER JOIN HR_SHIFT ON (HR_PROJLOCATION_SHIFT.HR_SHIFT_UUID = HR_SHIFT.HR_SHIFT_UUID);");

            db.execSQL("CREATE VIEW C_BPARTNER_VIEW AS" +
                    " SELECT C_BPARTNER.C_BPARTNER_UUID, C_BPARTNER.NAME," +
                    " C_BPARTNER.ICNO AS IDNUMBER," +
                    " C_BPARTNER.WORKPERMIT AS WORKPERMIT," +
                    " C_BPARTNER_LOCATION.PHONE, IFNULL(HR_JOBPOSITION.JOB_TITLE, '-') AS JOB_TITLE" +
                    " FROM C_BPARTNER" +
                    " LEFT JOIN C_BPARTNER_LOCATION ON (C_BPARTNER.C_BPARTNER_UUID = C_BPARTNER_LOCATION.C_BPARTNER_UUID)" +
                    " LEFT JOIN HR_JOBPOSITION ON (HR_JOBPOSITION.C_BPARTNER_UUID = C_BPARTNER.C_BPARTNER_UUID)" +
                    " GROUP BY C_BPARTNER.C_BPARTNER_UUID;"
            );

            db.execSQL("CREATE VIEW JOBAPP_LIST_VIEW AS " +
                    "SELECT HR_JOBAPPLICATION_UUID, " +
                    "HR_SETUP_JOB_UUID, " +
                    "HR_NATIONALITY_UUID, " +
                    "HR_JOBAPPLICATION.HR_PROJLOCATION_SHIFT_UUID, " +
                    "HR_JOBAPPLICATION.NAME, " +
                    "HR_JOBAPPLICATION.IDNUMBER, " +
                    "HR_JOBAPPLICATION.IDTYPE," +
                    "AGE, " +
                    "SEX, " +
                    "PHONE, " +
                    "MARITALSTATUS, " +
                    "QUALIFICATION_HIGHEST, " +
                    "QUALIFICATION_OTHER, " +
                    "STATUS, " +
                    "APPLICATIONDATE, " +
                    "EXPECTEDSALARY, " +
                    "YEARSOFEXPERIENCE, " +
                    "ATTACHMENT_APPLICANTPICTURE, " +
                    "ATTACHMENT_CERTPICTURE_1, " +
                    "ATTACHMENT_CERTPICTURE_2, " +
                    "ATTACHMENT_CERTPICTURE_3, " +
                    "ATTACHMENT_CERTPICTURE_4, " +
                    "ATTACHMENT_CERTPICTURE_5, " +
                    "ATTACHMENT_CERTPICTURE_6, " +
                    "ATTACHMENT_CERTPICTURE_7, " +
                    "ATTACHMENT_CERTPICTURE_8, " +
                    "ATTACHMENT_CERTPICTURE_9, " +
                    "ATTACHMENT_CERTPICTURE_10, " +
                    "INTERVIEWER_NOTES," +
                    "C_PROJECTLOCATION_UUID " +
                    "FROM HR_JOBAPPLICATION " +
                    "INNER JOIN HR_PROJLOCATION_SHIFT ON " +
                    "(HR_JOBAPPLICATION.HR_PROJLOCATION_SHIFT_UUID=HR_PROJLOCATION_SHIFT.HR_PROJLOCATION_SHIFT_UUID) "
            );

            db.execSQL("CREATE TABLE HR_IDENTITY (" +
                    "HR_IDENTITY_ID NUMBER(10,0) DEFAULT NULL," +
                    "HR_IDENTITY_UUID TEXT PRIMARY KEY NOT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL," +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "CREATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT(DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL, " +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N' NOT NULL," +
                    "NAME CHAR(2) NOT NULL," +
                    "VALUE NVARCHAR2(60) NOT NULL," +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //HR_LeaveType
            db.execSQL("CREATE TABLE HR_LEAVETYPE(" +
                    //STANDARD
                    "HR_LEAVETYPE_ID NUMBER(10,0) DEFAULT NULL," +
                    "HR_LEAVETYPE_UUID TEXT PRIMARY KEY NOT NULL," +
                    "AD_CLIENT_UUID TEXT NOT NULL," +
                    "AD_ORG_UUID TEXT NOT NULL," +
                    "NAME NVARCHAR2(60)," +
                    "DESCRIPTION NVARCHAR2(1000)," +
                    "CREATEDBY TEXT NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "UPDATEDBY TEXT NOT NULL," +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                    "ISAL CHAR(1)," + // -- IS ANNUAL LEAVE
                    "ISTRAININGLEAVE CHAR(1)," +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                    "ISDELETED BOOLEAN DEFAULT 'N'," +

                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //M_Attendance
            db.execSQL("CREATE TABLE M_ATTENDANCE(" +
                    //STANDARD
                    "M_ATTENDANCE_ID NUMBER(10, 0) DEFAULT NULL," +
                    "M_ATTENDANCE_UUID TEXT PRIMARY KEY NOT NULL," +
                    "C_PROJECTLOCATION_ID NUMBER(10, 0) DEFAULT NULL," +
                    "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                    "HR_SHIFT_ID NUMBER(10, 0) DEFAULT NULL," +
                    "HR_SHIFT_UUID TEXT NOT NULL," +
                    //FK
                    "CREATED DEPLOYMENTDATE NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))" +

//                    "FOREIGN KEY(C_PROJECTLOCATION_ID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_ID)" +
//                    "FOREIGN KEY(HR_SHIFT_ID) REFERENCES HR_SHIFT(HR_SHIFT_ID)" +
                    ");");

            //M_Attendanceline
            db.execSQL("CREATE TABLE M_ATTENDANCELINE(" +
                    //PK
                    "M_ATTENDANCELINE_UUID TEXT PRIMARY KEY NOT NULL," +
                    "C_PROJECTLOCATION_ID TEXT NOT NULL," +
                    "HR_SHIFT_UUID TEXT NOT NULL," +

                    //FK
                    "C_BPARTNER_UUID TEXT DEFAULT NULL," +
                    "CHECKINDATE DATETIME," +
                    "CHECKOUTDATE DATETIME, " +
                    "ISABSENT CHAR(1)," +
                    "ISOFF CHAR(1)," +
                    "ISREST CHAR(1)," +
                    //OTHERS
                    "COMMENT TEXT ," +
                    "HR_LEAVETYPE_ID NUMBER(10,0) DEFAULT NULL" +
                    ");");

            //START added 06/04/2017 by yao
            //C_SURVEYTEMPLATE
            db.execSQL("CREATE TABLE C_SURVEYTEMPLATE(" +
                    "C_SURVEYTEMPLATE_ID NUMBER(10, 0) NOT NULL, " +
                    "C_SURVEYTEMPLATE_UUID TEXT PRIMARY KEY NOT NULL, " +
                    "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                    "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                    "VALIDFROM DATE DEFAULT SYSDATE NOT NULL, " +
                    "VALIDTO DATE DEFAULT NULL, " +
                    "VALUE TEXT NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "DESCRIPTION TEXT, " +
                    "ISSIGN CHAR(1) DEFAULT 'Y' NOT NULL, " +
                    "ISGEOLOCATE CHAR(1) DEFAULT 'Y' NOT NULL," +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                    "ISUPDATED BOOLEAN DEFAULT 'N', " +
                    "ISSYNCED BOOLEAN DEFAULT 'N', " +
                    "ISDELETED BOOLEAN DEFAULT 'N', " +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //C_SURVEYTEMPLATEQUESTION
            db.execSQL("CREATE TABLE C_SURVEYTEMPLATEQUESTION(" +
                    "C_SURVEYTEMPLATEQUESTION_ID NUMBER(10, 0) NOT NULL, " +
                    "C_SURVEYTEMPLATEQUESTION_UUID TEXT PRIMARY KEY NOT NULL, " +
                    "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                    "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                    "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "DESCRIPTION TEXT, " +
                    "TYPE CHAR(1) DEFAULT 'N' NOT NULL, " +
                    "SEQNO NUMBER(10, 0) NOT NULL, " +
                    "WEIGHT NUMBER DEFAULT 1 NOT NULL, " +
                    "SECTIONNAME TEXT, " +
                    "SECTIONNO NUMBER, " +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                    "ISUPDATED BOOLEAN DEFAULT 'N', " +
                    "ISSYNCED BOOLEAN DEFAULT 'N', " +
                    "ISDELETED BOOLEAN DEFAULT 'N', " +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //C_SURVEY
            db.execSQL("CREATE TABLE C_SURVEY(" +
                    "C_SURVEY_ID NUMBER(10, 0) DEFAULT NULL, " +
                    "C_SURVEY_UUID TEXT PRIMARY KEY NOT NULL, " +
                    "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                    "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                    "C_PROJECTLOCATION_UUID NUMBER(10, 0) NOT NULL, " +
                    "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                    "VALUE TEXT NOT NULL, " +
                    "VALIDTO DATE, " +
                    "EMAIL TEXT NOT NULL, " +
                    "DATEDELIVERY DATE NOT NULL, " +
                    "EMAILSENT CHAR(1) DEFAULT 'N' NOT NULL, " +
                    "STATUS CHAR(1) DEFAULT 'N' NOT NULL, " +
                    "NAME TEXT, " +
                    "DATESTART DATE, " +
                    "DATEEND DATE, " +
                    "REMARKS TEXT, " +
                    "DATETRX DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "LONGITUDE NUMBER, " +
                    "LATITUDE NUMBER, " +
                    "ATTACHMENT_SIGNATURE VARCHAR2(100), " +
                    "ATTACHMENT_STAFFSIGNATURE VARCHAR2(100), " +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                    "ISUPDATED BOOLEAN DEFAULT 'N', " +
                    "ISSYNCED BOOLEAN DEFAULT 'N', " +
                    "ISDELETED BOOLEAN DEFAULT 'N', " +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID), " +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for C_SURVEY_ID_INDEX
            db.execSQL("CREATE INDEX C_SURVEY_ID_INDEX ON C_SURVEY(C_SURVEY_ID)");

            //C_SURVEYRESPONSE
            db.execSQL("CREATE TABLE C_SURVEYRESPONSE(" +
                    "C_SURVEYRESPONSE_ID NUMBER(10, 0) DEFAULT NULL, " +
                    "C_SURVEYRESPONSE_UUID TEXT PRIMARY KEY NOT NULL, " +
                    "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                    "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                    "C_SURVEY_UUID NUMBER(10, 0) NOT NULL, " +
                    "C_SURVEYTEMPLATEQUESTION_UUID NUMBER(10, 0) NOT NULL, " +
                    "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                    "REMARKS TEXT, " +
                    "AMT NUMBER DEFAULT 0 NOT NULL, " +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                    "ISUPDATED BOOLEAN DEFAULT 'N', " +
                    "ISSYNCED BOOLEAN DEFAULT 'N', " +
                    "ISDELETED BOOLEAN DEFAULT 'N', " +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_SURVEY_UUID) REFERENCES C_SURVEY(C_SURVEY_UUID)," +
                    "FOREIGN KEY(C_SURVEYTEMPLATEQUESTION_UUID) REFERENCES C_SURVEYTEMPLATEQUESTION(C_SURVEYTEMPLATEQUESTION_UUID)," +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //HR_ENTRYLOG
            db.execSQL("CREATE TABLE HR_ENTRYLOG( " +
                    "HR_ENTRYLOG_ID NUMBER(10, 0) DEFAULT NULL, " +
                    "HR_ENTRYLOG_UUID TEXT PRIMARY KEY NOT NULL, " +
                    "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                    "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                    "C_PROJECTLOCATION_UUID TEXT NOT NULL, " +
                    "C_BPARTNER_UUID TEXT NOT NULL, " +
                    "DATETRX DATETIME NOT NULL, " +
                    "LONGITUDE NUMBER, " +
                    "LATITUDE NUMBER, " +
                    "NFCTAG TEXT, " +
                    "DEVICENO VARCHAR2(20), " +
                    "ATTACHMENT_IMAGE VARCHAR2(100), " +
                    "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                    "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                    "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                    "ISUPDATED BOOLEAN DEFAULT 'N', " +
                    "ISSYNCED BOOLEAN DEFAULT 'N', " +
                    "ISDELETED BOOLEAN DEFAULT 'N', " +
                    "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                    "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                    "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID), " +
                    "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID), " +
                    "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                    "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                    ");");

            //create index for C_SURVEYRESPONSE_ID_INDEX
            db.execSQL("CREATE INDEX C_SURVEYRESPONSE_ID_INDEX ON C_SURVEYRESPONSE(C_SURVEYRESPONSE_ID)");
            //END added 06/04/2017 by yao

            //create index for M_MOVEMENT_ID_INDEX
            db.execSQL("CREATE INDEX M_ATTENDANCE_ID_INDEX ON M_ATTENDANCE(M_ATTENDANCE_ID)");
            db.execSQL("CREATE INDEX M_ATTENDANCELINE_UUID_INDEX ON M_ATTENDANCELINE(M_ATTENDANCELINE_UUID)");

            db.execSQL("PRAGMA FOREIGN_KEYS=ON;");
            //due to key constraint issue, below rows created before syncing happens.
            String insertSQL = "insert into ad_client(ad_client_id, ad_org_uuid,  created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted, ad_client_uuid, name) values ('0','0','11-FEB-06','0','11-FEB-06','100','Y','Y','Y','N', '300', 'System')";
            db.execSQL(insertSQL);
            insertSQL = " insert into ad_org(ad_org_id, created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted,ad_org_uuid, ad_client_uuid, name) values ('0','12-NOV-99','0','02-JAN-00','0','Y','Y','Y','N','400','300','*')";
            db.execSQL(insertSQL);
            insertSQL = " insert into ad_user(ad_user_id, created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted,ad_user_uuid, ad_org_uuid, ad_client_uuid, name, title) values ('0','12-NOV-99','0','02-JAN-00','0','Y','Y','Y','N','500','300','400','System', 'System')";
            db.execSQL(insertSQL);
            insertSQL = " insert into m_attributesetinstance(m_attributesetinstance_id, created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted, m_attributesetinstance_uuid, ad_org_uuid, ad_client_uuid, description) values ('0','26-JAN-15','500','26-JAN-15','500','Y','Y','Y','N','600','300','400','-')";
            db.execSQL(insertSQL);
            insertSQL = "insert into HR_IDENTITY(hr_identity_uuid, ad_client_uuid, ad_org_uuid,  created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted, name, value) values ('1000','400','300','11-FEB-06','100','11-FEB-06','100','Y','Y','Y','N', 'Pink','P')";
            db.execSQL(insertSQL);
            insertSQL = "insert into HR_IDENTITY(hr_identity_uuid, ad_client_uuid, ad_org_uuid,  created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted, name, value) values ('1001','400','300','11-FEB-06','100','11-FEB-06','100','Y','Y','Y','N', 'Blue','B')";
            db.execSQL(insertSQL);
            insertSQL = "insert into HR_IDENTITY(hr_identity_uuid, ad_client_uuid, ad_org_uuid,  created, createdby, updated, updatedby, isactive, isupdated, issynced, isdeleted, name, value) values ('1002','400','300','11-FEB-06','100','11-FEB-06','100','Y','Y','Y','N', 'FIN','F')";
            db.execSQL(insertSQL);
            String addCol = "ALTER TABLE M_ATTENDANCELINE ADD DAYS NUMBER(1,1) DEFAULT NULL";
            db.execSQL(addCol);

//            insertSQL = "insert into M_PRODUCT_CATEGORY(" +
//                    "M_PRODUCT_CATEGORY_ID,AD_ORG_UUID,AD_CLIENT_UUID,CREATED,CREATEDBY,UPDATED,UPDATEDBY,ISACTIVE,ISUPDATED,ISSYNCED,ISDELETED,M_PRODUCT_CATEGORY_UUID,NAME,VALUE) " +
//                    "values ('1000264','502a1593-01b9-4d28-8739-57316b11f3c2','4884dc2a-1491-4dab-a394-0c7b7b20322e','11-FEB-06','e191a5bc-358d-4ac6-8e2d-6fc066656605','11-FEB-06','e191a5bc-358d-4ac6-8e2d-6fc066656605','Y','Y','Y','N','f36d7d43-876d-44e2-bab6-a549b9015da6','Trade - Repair / Training-NC','Trade - Repair / Training-NC')";
//            db.execSQL(insertSQL);
//            insertSQL = "insert into HR_PROJLOCATION_SHIFT(" +
//            "HR_PROJLOCATION_SHIFT_ID,AD_ORG_UUID,AD_CLIENT_UUID,CREATED,CREATEDBY,UPDATED,UPDATEDBY,ISACTIVE,ISUPDATED,ISSYNCED,ISDELETED,HR_PROJLOCATION_SHIFT_UUID,HR_SHIFT_UUID,C_PROJECTLOCATION_UUID) " +
//            "values ('1000021','502a1593-01b9-4d28-8739-57316b11f3c2','4884dc2a-1491-4dab-a394-0c7b7b20322e','2016-01-16T12:04:32.000+0800','e191a5bc-358d-4ac6-8e2d-6fc066656605','2016-01-16T12:04:32.000+0800','e191a5bc-358d-4ac6-8e2d-6fc066656605','Y','Y','Y','N','57602605-5d18-4252-bc41-6f3878d24004','5c93ea3d-7d08-4f98-a4b4-f44ad04aa44c','b044209d-ae8f-44dc-aa0e-1176d88631d4')";
//            db.execSQL(insertSQL);
        } catch (SQLException e) {
//                        PandoraHelper.showErrorMessage(context, "Error in initializing database");
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
//            if (oldVersion < DATABASE_VERSION) {
//                db.execSQL("insert into m_checkin(m_checkin_id, ad_org_uuid, ad_client_uuid," +
//                        "ad_user_uuid,created, createdby, updated, updatedby, isactive, " +
//                        "isupdated, issynced, isdeleted, m_checkin_uuid,  m_checkpoint_uuid," +
//                        " datetrx, latitude, longitude, description, attachment_tourpicture)" +
//                        " values ('333331','300','400','500', datetime('now','-8 days'),'500', " +
//                        "datetime('now','-8 days'),'500','Y','Y','Y','N','444441'," +
//                        "'e7dfd4b9-4937-4751-9860-ff8df204ff3c', datetime('now','-8 days'), " +
//                        "'000.000', '000000','loitering around', 'null');");
//
//                db.execSQL("insert into m_checkin(m_checkin_id, ad_org_uuid, ad_client_uuid," +
//                        "ad_user_uuid,created, createdby, updated, updatedby, isactive, " +
//                        "isupdated, issynced, isdeleted, m_checkin_uuid,  m_checkpoint_uuid," +
//                        " datetrx, latitude, longitude, description, attachment_tourpicture)" +
//                        " values ('333332','300','400','500', DATETIME('NOW', 'LOCALTIME'),'500', " +
//                        "DATETIME('NOW', 'LOCALTIME'),'500','Y','Y','Y','N','444442'," +
//                        "'e7dfd4b9-4937-4751-9860-ff8df204ff3c', DATETIME('NOW', 'LOCALTIME'), " +
//                        "'000.000', '000000','loitering around', 'null');");
//
//                db.execSQL("insert into m_checkin(m_checkin_id, ad_org_uuid, ad_client_uuid," +
//                        "ad_user_uuid,created, createdby, updated, updatedby, isactive, " +
//                        "isupdated, issynced, isdeleted, m_checkin_uuid,  m_checkpoint_uuid," +
//                        " datetrx, latitude, longitude, description, attachment_tourpicture)" +
//                        " values ('333333','300','400','500', DATETIME('NOW', 'LOCALTIME'),'500', " +
//                        "DATETIME('NOW', 'LOCALTIME'),'500','Y','Y','Y','N','444443'," +
//                        "'e7dfd4b9-4937-4751-9860-ff8df204ff3c', DATETIME('NOW', 'LOCALTIME'), " +
//                        "'000.000', '000000','loitering around', 'null');");
//
//                db.execSQL("insert into m_checkin(m_checkin_id, ad_org_uuid, ad_client_uuid," +
//                        "ad_user_uuid,created, createdby, updated, updatedby, isactive, " +
//                        "isupdated, issynced, isdeleted, m_checkin_uuid,  m_checkpoint_uuid," +
//                        " datetrx, latitude, longitude, description, attachment_tourpicture)" +
//                        " values ('333337','300','400','500', datetime('now','-8 days'),'500', " +
//                        "datetime('now','-8 days'),'500','Y','Y','Y','N','444447'," +
//                        "'e7dfd4b9-4937-4751-9860-ff8df204ff3c', datetime('now','-8 days'), " +
//                        "'000.000', '000000','loitering around', 'null');");
//            }

            if (oldVersion < 13) {
                //added 06/04/2017 by yao
                //C_SURVEYTEMPLATE
                db.execSQL("CREATE TABLE C_SURVEYTEMPLATE(" +
                        "C_SURVEYTEMPLATE_ID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "VALIDFROM DATE DEFAULT SYSDATE NOT NULL, " +
                        "VALIDTO DATE DEFAULT NULL, " +
                        "VALUE TEXT NOT NULL, " +
                        "NAME TEXT NOT NULL, " +
                        "DESCRIPTION TEXT, " +
                        "ISSIGN CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "ISGEOLOCATE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //C_SURVEYTEMPLATEQUESTION
                db.execSQL("CREATE TABLE C_SURVEYTEMPLATEQUESTION(" +
                        "C_SURVEYTEMPLATEQUESTION_ID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATEQUESTION_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "NAME TEXT NOT NULL, " +
                        "DESCRIPTION TEXT, " +
                        "TYPE CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "SEQNO NUMBER(10, 0) NOT NULL, " +
                        "WEIGHT NUMBER DEFAULT 1 NOT NULL, " +
                        "SECTIONNAME TEXT, " +
                        "SECTIONNO NUMBER, " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //C_SURVEY
                db.execSQL("CREATE TABLE C_SURVEY(" +
                        "C_SURVEY_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "C_SURVEY_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_PROJECTLOCATION_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "VALUE TEXT NOT NULL, " +
                        "VALIDTO DATE, " +
                        "EMAIL TEXT NOT NULL, " +
                        "DATEDELIVERY DATE NOT NULL, " +
                        "EMAILSENT CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "STATUS CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "NAME TEXT, " +
                        "DATESTART DATE, " +
                        "DATEEND DATE, " +
                        "LONGITUDE NUMBER, " +
                        "LATITUDE NUMBER, " +
                        "ATTACHMENT_SIGNATURE VARCHAR2(100), " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID), " +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_SURVEY_ID_INDEX
                db.execSQL("CREATE INDEX C_SURVEY_ID_INDEX ON C_SURVEY(C_SURVEY_ID)");

                //C_SURVEYRESPONSE
                db.execSQL("CREATE TABLE C_SURVEYRESPONSE(" +
                        "C_SURVEYRESPONSE_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "C_SURVEYRESPONSE_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEY_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATEQUESTION_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "REMARKS TEXT, " +
                        "AMT NUMBER DEFAULT 0 NOT NULL, " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEY_UUID) REFERENCES C_SURVEY(C_SURVEY_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATEQUESTION_UUID) REFERENCES C_SURVEYTEMPLATEQUESTION(C_SURVEYTEMPLATEQUESTION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_SURVEYRESPONSE_ID_INDEX
                db.execSQL("CREATE INDEX C_SURVEYRESPONSE_ID_INDEX ON C_SURVEYRESPONSE(C_SURVEYRESPONSE_ID)");
            }

            if (oldVersion < 14) {
                //M_PURCHASEREQUESTLINE
                db.execSQL("ALTER TABLE M_PURCHASEREQUESTLINE ADD ISEMERGENCY CHAR(1) DEFAULT 'N' NOT NULL");
                db.execSQL("ALTER TABLE M_PURCHASEREQUESTLINE ADD PURCHASEREASON NVARCHAR2(255) NULL");
            }

            if (oldVersion < 15) {
                db.execSQL("ALTER TABLE C_PROJECTLOCATION ADD ISKIOSKMODE CHAR(1) DEFAULT 'N' NOT NULL");
                db.execSQL("ALTER TABLE C_PROJECTLOCATION ADD ISPHOTO CHAR(1) DEFAULT 'N' NOT NULL");
                db.execSQL("ALTER TABLE C_PROJECTLOCATION ADD NFCTAG TEXT NULL");
                db.execSQL("ALTER TABLE C_BPARTNER ADD NFCTAG TEXT NULL");

                //HR_ENTRYLOG
                db.execSQL("CREATE TABLE HR_ENTRYLOG( " +
                        "HR_ENTRYLOG_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "HR_ENTRYLOG_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_PROJECTLOCATION_UUID TEXT NOT NULL, " +
                        "C_BPARTNER_UUID TEXT NOT NULL, " +
                        "DATETRX DATETIME NOT NULL, " +
                        "LONGITUDE NUMBER, " +
                        "LATITUDE NUMBER, " +
                        "NFCTAG TEXT, " +
                        "DEVICENO VARCHAR2(20), " +
                        "ATTACHMENT_IMAGE VARCHAR2(100), " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID), " +
                        "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID), " +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
            }

            if (oldVersion < 16) {
                //C_PROJECTTASK
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_6 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_7 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_8 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_9 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_10 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_11 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_12 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_13 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_14 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_15 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_16 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_17 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_18 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_19 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_20 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_21 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_22 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_23 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_24 VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_TASKPICTURE_25 VARCHAR2(100)");
            }

            if (oldVersion < 17) {
                //HR_JOBAPPLICATION
                db.execSQL("ALTER TABLE HR_JOBAPPLICATION ADD ATTACHMENT_SIGNATURE VARCHAR2(100)");
                //C_PROJECTTASK
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD LATITUDE NUMBER");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD LONGITUDE NUMBER");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ATTACHMENT_SIGNATURE VARCHAR2(100)");
            }

            if (oldVersion < 18) {
                db.execSQL("PRAGMA writable_schema = true;");
                db.execSQL("PRAGMA foreign_keys = false;");

                //HR_ENTRYLOG
                db.execSQL("ALTER TABLE HR_ENTRYLOG RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_ENTRYLOG( " +
                        "HR_ENTRYLOG_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "HR_ENTRYLOG_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_PROJECTLOCATION_UUID TEXT NOT NULL, " +
                        "C_BPARTNER_UUID TEXT NOT NULL, " +
                        "DATETRX DATETIME NOT NULL, " +
                        "LONGITUDE NUMBER, " +
                        "LATITUDE NUMBER, " +
                        "NFCTAG TEXT, " +
                        "DEVICENO VARCHAR2(20), " +
                        "ATTACHMENT_IMAGE VARCHAR2(100), " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID), " +
                        "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID), " +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
                db.execSQL("INSERT INTO HR_ENTRYLOG SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_SURVEYRESPONSE
                db.execSQL("ALTER TABLE C_SURVEYRESPONSE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_SURVEYRESPONSE(" +
                        "C_SURVEYRESPONSE_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "C_SURVEYRESPONSE_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEY_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATEQUESTION_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "REMARKS TEXT, " +
                        "AMT NUMBER DEFAULT 0 NOT NULL, " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEY_UUID) REFERENCES C_SURVEY(C_SURVEY_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATEQUESTION_UUID) REFERENCES C_SURVEYTEMPLATEQUESTION(C_SURVEYTEMPLATEQUESTION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
                //create index for C_SURVEYRESPONSE_ID_INDEX
                db.execSQL("DROP INDEX C_SURVEYRESPONSE_ID_INDEX");
                db.execSQL("CREATE INDEX C_SURVEYRESPONSE_ID_INDEX ON C_SURVEYRESPONSE(C_SURVEYRESPONSE_ID)");
                db.execSQL("INSERT INTO C_SURVEYRESPONSE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_SURVEY
                db.execSQL("ALTER TABLE C_SURVEY RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_SURVEY(" +
                        "C_SURVEY_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "C_SURVEY_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_PROJECTLOCATION_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "VALUE TEXT NOT NULL, " +
                        "VALIDTO DATE, " +
                        "EMAIL TEXT NOT NULL, " +
                        "DATEDELIVERY DATE NOT NULL, " +
                        "EMAILSENT CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "STATUS CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "NAME TEXT, " +
                        "DATESTART DATE, " +
                        "DATEEND DATE, " +
                        "LONGITUDE NUMBER, " +
                        "LATITUDE NUMBER, " +
                        "ATTACHMENT_SIGNATURE VARCHAR2(100), " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID), " +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_SURVEY_ID_INDEX
                db.execSQL("DROP INDEX C_SURVEY_ID_INDEX");
                db.execSQL("CREATE INDEX C_SURVEY_ID_INDEX ON C_SURVEY(C_SURVEY_ID)");
                db.execSQL("INSERT INTO C_SURVEY SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_SURVEYTEMPLATEQUESTION
                db.execSQL("ALTER TABLE C_SURVEYTEMPLATEQUESTION RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_SURVEYTEMPLATEQUESTION(" +
                        "C_SURVEYTEMPLATEQUESTION_ID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATEQUESTION_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "NAME TEXT NOT NULL, " +
                        "DESCRIPTION TEXT, " +
                        "TYPE CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "SEQNO NUMBER(10, 0) NOT NULL, " +
                        "WEIGHT NUMBER DEFAULT 1 NOT NULL, " +
                        "SECTIONNAME TEXT, " +
                        "SECTIONNO NUMBER, " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
                db.execSQL("INSERT INTO C_SURVEYTEMPLATEQUESTION SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_SURVEYTEMPLATE
                db.execSQL("ALTER TABLE C_SURVEYTEMPLATE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_SURVEYTEMPLATE(" +
                        "C_SURVEYTEMPLATE_ID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "VALIDFROM DATE DEFAULT SYSDATE NOT NULL, " +
                        "VALIDTO DATE DEFAULT NULL, " +
                        "VALUE TEXT NOT NULL, " +
                        "NAME TEXT NOT NULL, " +
                        "DESCRIPTION TEXT, " +
                        "ISSIGN CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "ISGEOLOCATE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
                db.execSQL("INSERT INTO C_SURVEYTEMPLATE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //M_Attendance
                db.execSQL("ALTER TABLE M_ATTENDANCE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_ATTENDANCE(" +
                        //STANDARD
                        "M_ATTENDANCE_ID NUMBER(10, 0) DEFAULT NULL," +
                        "M_ATTENDANCE_UUID TEXT PRIMARY KEY NOT NULL," +
                        "C_PROJECTLOCATION_ID NUMBER(10, 0) DEFAULT NULL," +
                        "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                        "HR_SHIFT_ID NUMBER(10, 0) DEFAULT NULL," +
                        "HR_SHIFT_UUID TEXT NOT NULL," +
                        //FK
                        "CREATED DEPLOYMENTDATE NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))" +

//                    "FOREIGN KEY(C_PROJECTLOCATION_ID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_ID)" +
//                    "FOREIGN KEY(HR_SHIFT_ID) REFERENCES HR_SHIFT(HR_SHIFT_ID)" +
                        ");");
                db.execSQL("DROP INDEX M_ATTENDANCE_ID_INDEX");
                db.execSQL("CREATE INDEX M_ATTENDANCE_ID_INDEX ON M_ATTENDANCE(M_ATTENDANCE_ID)");
                db.execSQL("INSERT INTO M_ATTENDANCE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_LeaveType
                db.execSQL("ALTER TABLE HR_LEAVETYPE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_LEAVETYPE(" +
                        //STANDARD
                        "HR_LEAVETYPE_ID NUMBER(10,0) DEFAULT NULL," +
                        "HR_LEAVETYPE_UUID TEXT PRIMARY KEY NOT NULL," +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL," +
                        "NAME NVARCHAR2(60)," +
                        "DESCRIPTION NVARCHAR2(1000)," +
                        "CREATEDBY TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "ISAL CHAR(1)," + // -- IS ANNUAL LEAVE
                        "ISTRAININGLEAVE CHAR(1)," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +

                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
                db.execSQL("INSERT INTO HR_LEAVETYPE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                db.execSQL("ALTER TABLE HR_IDENTITY RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_IDENTITY (" +
                        "HR_IDENTITY_ID NUMBER(10,0) DEFAULT NULL," +
                        "HR_IDENTITY_UUID TEXT PRIMARY KEY NOT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL," +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT(DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N' NOT NULL," +
                        "NAME CHAR(2) NOT NULL," +
                        "VALUE NVARCHAR2(60) NOT NULL," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");
                db.execSQL("INSERT INTO HR_IDENTITY SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_PROJECTASSIGNMENT
                db.execSQL("ALTER TABLE HR_PROJECTASSIGNMENT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_PROJECTASSIGNMENT(" +
                        //STANDARD
                        "HR_PROJECTASSIGNMENT_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'N'," +
                        "ISSYNCED BOOLEAN DEFAULT 'N'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_PROJECTASSIGNMENT_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "C_BPARTNER_UUID TEXT," +
                        "C_PROJECTLOCATION_UUID TEXT," +
                        "HR_PROJLOCATION_SHIFT_UUID TEXT, " +
                        "HR_SHIFT_UUID TEXT," +
                        //OTHERS
                        "NAME NVARCHAR2(300)," +
                        "VALUE VARCHAR2(300) ," +
                        "ASSIGNMENT_TYPE VARCHAR2(20) ," +
                        "ISDEFAULT BOOLEAN," +
                        //--
//                    "CONSTRAINT HRPROJASSID_UNIQCONS UNIQUE (HR_PROJECTASSIGNMENT_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
//                    "FOREIGN KEY(HR_PROJLOCATION_SHIFT_UUID) REFERENCES HR_PROJLOCATION_SHIFT(HR_PROJLOCATION_SHIFT_UUID)," +
//                    "FOREIGN KEY(HR_SHIFT_UUID) REFERENCES HR_SHIFT(HR_SHIFT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_PROJECTASSIGNMENT_ID_INDEX
                db.execSQL("DROP INDEX HR_PROJECTASSIGNMENT_ID_INDEX");
                db.execSQL("CREATE INDEX HR_PROJECTASSIGNMENT_ID_INDEX ON HR_PROJECTASSIGNMENT(HR_PROJECTASSIGNMENT_ID)");
                db.execSQL("INSERT INTO HR_PROJECTASSIGNMENT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //AD_NOTE **NOT MASTER DATA.
                db.execSQL("ALTER TABLE AD_NOTE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE AD_NOTE(" +
                        //STANDARD
                        "AD_NOTE_ID NUMBER(10, 0) DEFAULT NULL," +
                        //PK
                        "AD_NOTE_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "AD_USER_UUID TEXT," +
                        //OTHERS
                        "DATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "SENDER TEXT NOT NULL," +
                        "MESSAGE NVARCHAR2(5000)," +
                        //--
//                                "CONSTRAINT NOTEID_UNIQCONS UNIQUE (AD_NOTE_ID)," +
                        "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(SENDER) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for AD_NOTE_ID_INDEX
                db.execSQL("DROP INDEX AD_NOTE_ID_INDEX");
                db.execSQL("CREATE INDEX AD_NOTE_ID_INDEX ON AD_NOTE(AD_NOTE_ID)");
                db.execSQL("INSERT INTO AD_NOTE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //A_ASSET
                db.execSQL("ALTER TABLE A_ASSET RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE A_ASSET(" +
                        //STANDARD
                        "A_ASSET_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "A_ASSET_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "M_PRODUCT_UUID TEXT," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "VALUE NVARCHAR2(300)," +
                        //--
//                    "CONSTRAINT ASSETID_UNIQCONS UNIQUE (A_ASSET_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
//                    "FOREIGN KEY(M_PRODUCT_UUID) REFERENCES M_PRODUCT(M_PRODUCT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for A_ASSET_ID_INDEX
                db.execSQL("DROP INDEX A_ASSET_ID_INDEX");
                db.execSQL("CREATE INDEX A_ASSET_ID_INDEX ON A_ASSET(A_ASSET_ID)");
                db.execSQL("INSERT INTO A_ASSET SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //M_PRODUCT
                db.execSQL("ALTER TABLE M_PRODUCT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_PRODUCT(" +
                        //STANDARD
                        "M_PRODUCT_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "M_PRODUCT_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "C_UOM_UUID TEXT," +
                        "M_PRODUCT_CATEGORY_UUID TEXT," +
                        "M_ATTRIBUTESETINSTANCE_UUID TEXT," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "VALUE NVARCHAR2(300) ," +
                        //--
//                    "CONSTRAINT PRODUCTID_UNIQCONS UNIQUE (M_PRODUCT_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_UOM_UUID) REFERENCES C_UOM(C_UOM_UUID)," +
//                    "FOREIGN KEY(M_PRODUCT_CATEGORY_UUID) REFERENCES M_PRODUCT_CATEGORY(M_PRODUCT_CATEGORY_UUID)," +
                        "FOREIGN KEY(M_ATTRIBUTESETINSTANCE_UUID) REFERENCES M_ATTRIBUTESETINSTANCE(M_ATTRIBUTESETINSTANCE_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for M_PRODUCT_ID_INDEX
                db.execSQL("DROP INDEX M_PRODUCT_ID_INDEX");
                db.execSQL("CREATE INDEX M_PRODUCT_ID_INDEX ON M_PRODUCT(M_PRODUCT_ID)");
                db.execSQL("INSERT INTO M_PRODUCT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_PROJECTTASK
                db.execSQL("ALTER TABLE C_PROJECTTASK RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_PROJECTTASK(" +
                        //STANDARD
                        "C_PROJECTTASK_ID NUMBER(10, 0) DEFAULT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        "DUEDATE DATETIME," +
                        //PK
                        "C_PROJECTTASK_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                        //OTHERS
                        "PRIORITY NUMBER(10,0), " +
                        "NAME NVARCHAR2(300), " +
                        "ASSIGNEDTO NUMBER(10, 0)," + //AD_USER_ID

                        "ISDONE CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "DESCRIPTION NVARCHAR2(255), " +
                        "COMMENTS NVARCHAR2(255)," +
                        "ATTACHMENT_BEFORETASKPICTURE_1 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_1 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_2 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_3 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_4 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_5 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_6 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_7 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_8 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_9 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_10 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_11 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_12 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_13 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_14 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_15 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_16 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_17 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_18 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_19 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_20 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_21 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_22 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_23 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_24 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_25 VARCHAR2(100) , " +
                        "ATTACHMENT_SIGNATURE VARCHAR2(100) , " +
                        "LATITUDE NUMBER , " +
                        "LONGITUDE NUMBER , " +
                        //--
//                    "CONSTRAINT PROJTASKID_UNIQCONS UNIQUE (C_PROJECTTASK_ID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_PROJECTTASK_ID_INDEX
                db.execSQL("DROP INDEX C_PROJECTTASK_ID_INDEX");
                db.execSQL("CREATE INDEX C_PROJECTTASK_ID_INDEX ON C_PROJECTTASK(C_PROJECTTASK_ID)");
                db.execSQL("INSERT INTO C_PROJECTTASK SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //M_PURCHASEREQUEST ***not a master data
                db.execSQL("ALTER TABLE M_PURCHASEREQUEST RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_PURCHASEREQUEST(" +
                        //STANDARD
                        "M_PURCHASEREQUEST_ID NUMBER(10, 0) DEFAULT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "M_PURCHASEREQUEST_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "C_PROJECTLOCATION_UUID TEXT," +
                        "AD_USER_UUID TEXT," +
                        //OTHERS
                        "DOCUMENTNO NVARCHAR2(40),"+
                        "REQUESTDATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "ISAPPROVED CHAR(1) DEFAULT 'N'," +
                        //--
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for M_PURCHASEREQUEST_ID_INDEX
                db.execSQL("DROP INDEX M_PURCHASEREQUEST_ID_INDEX");
                db.execSQL("CREATE INDEX M_PURCHASEREQUEST_ID_INDEX ON M_PURCHASEREQUEST(M_PURCHASEREQUEST_ID)");
                db.execSQL("INSERT INTO M_PURCHASEREQUEST SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //M_ATTRIBUTESETINSTANCE
                db.execSQL("ALTER TABLE M_ATTRIBUTESETINSTANCE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_ATTRIBUTESETINSTANCE(" +
                        //STANDARD
                        "M_ATTRIBUTESETINSTANCE_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "M_ATTRIBUTESETINSTANCE_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "DESCRIPTION NVARCHAR2(255) ," +
                        //--
//                    "CONSTRAINT ATTRIBUTESETINSTANCEID_UNIQCONS UNIQUE (M_ATTRIBUTESETINSTANCE_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for M_ATTRIBUTESETINSTANCE_ID_INDEX
                db.execSQL("DROP INDEX M_ATTRIBUTESETINSTANCE_ID_INDEX");
                db.execSQL("CREATE INDEX M_ATTRIBUTESETINSTANCE_ID_INDEX ON M_ATTRIBUTESETINSTANCE(M_ATTRIBUTESETINSTANCE_ID)");
                db.execSQL("INSERT INTO M_ATTRIBUTESETINSTANCE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_UOM
                db.execSQL("ALTER TABLE C_UOM RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_UOM(" +
                        //STANDARD
                        "C_UOM_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "C_UOM_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "UOMSYMBOL VARCHAR2(4)," +
                        "NAME VARCHAR2(300) ," +
                        //--
                        "CONSTRAINT UOMID_UNIQCONS UNIQUE (C_UOM_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_UOM_ID_INDEX
                db.execSQL("DROP INDEX C_UOM_ID_INDEX");
                db.execSQL("CREATE INDEX C_UOM_ID_INDEX ON C_UOM(C_UOM_ID)");
                db.execSQL("INSERT INTO C_UOM SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //M_PRODUCT_CATEGORY
                db.execSQL("ALTER TABLE M_PRODUCT_CATEGORY RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_PRODUCT_CATEGORY(" +
                        //STANDARD
                        "M_PRODUCT_CATEGORY_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "M_PRODUCT_CATEGORY_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "VALUE NVARCHAR2(300) ," +
                        //--
//                    "CONSTRAINT PROCAT_UNIQCONS UNIQUE (M_PRODUCT_CATEGORY_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for M_PRODUCT_CATEGORY
                db.execSQL("DROP INDEX M_PRODUCT_CATEGORY_ID_INDEX");
                db.execSQL("CREATE INDEX M_PRODUCT_CATEGORY_ID_INDEX ON M_PRODUCT_CATEGORY(M_PRODUCT_CATEGORY_ID)");
                db.execSQL("INSERT INTO M_PRODUCT_CATEGORY SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_BPARTNER_LOCATION
                db.execSQL("ALTER TABLE C_BPARTNER_LOCATION RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_BPARTNER_LOCATION(" +
                        //STANDARD
                        "C_BPARTNER_LOCATION_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "C_BPARTNER_LOCATION_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "C_BPARTNER_UUID TEXT," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "PHONE NVARCHAR2(40) ," +
                        //--
                        "CONSTRAINT BIZPARTLOC_UNIQCONS UNIQUE (C_BPARTNER_LOCATION_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_BPARTNER_LOCATION
                db.execSQL("DROP INDEX C_BPARTNER_LOCATION_ID_INDEX");
                db.execSQL("CREATE INDEX C_BPARTNER_LOCATION_ID_INDEX ON C_BPARTNER_LOCATION(C_BPARTNER_LOCATION_ID)");
                db.execSQL("INSERT INTO C_BPARTNER_LOCATION SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_BPARTNER
                db.execSQL("ALTER TABLE C_BPARTNER RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_BPARTNER(" +
                        //STANDARD
                        "C_BPARTNER_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "C_BPARTNER_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        //Value column is referring to IC number.
                        "VALUE VARCHAR2(60) ," +
                        "ISEMPLOYEE CHAR(1) ," +
                        "ICNo VARCHAR2(20) ," +
                        "WorkPermit VARCHAR2(20) ," +
                        "NFCTAG TEXT NULL," +
                        //--
                        //--
                        "CONSTRAINT BIZPART_UNIQCONS UNIQUE (C_BPARTNER_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_NATIONALITY_INDEX
                db.execSQL("DROP INDEX C_BPARTNER_ID_INDEX");
                db.execSQL("CREATE INDEX C_BPARTNER_ID_INDEX ON C_BPARTNER(C_BPARTNER_ID)");
                db.execSQL("INSERT INTO C_BPARTNER SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_JOBAPPLICATION **not a master data.!!
                db.execSQL("ALTER TABLE HR_JOBAPPLICATION RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_JOBAPPLICATION(" +
                        //STANDARD
                        "HR_JOBAPPLICATION_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'N'," +
                        "ISSYNCED BOOLEAN DEFAULT 'N'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_JOBAPPLICATION_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "HR_SETUP_JOB_UUID TEXT NOT NULL," +
                        "HR_NATIONALITY_UUID TEXT NOT NULL," +
                        "HR_PROJLOCATION_SHIFT_UUID TEXT NOT NULL," +//////////
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "IDTYPE VARCHAR2(60) ," +
                        "IDNUMBER VARCHAR2(60) ," +
                        "AGE NUMBER(10,0) ," +
                        "SEX CHAR(1) ," +
                        "PHONE VARCHAR2(40) ," +
                        "MARITALSTATUS CHAR(1) ," +
                        "QUALIFICATION_HIGHEST VARCHAR2(60) ," +
                        "QUALIFICATION_OTHER VARCHAR(60) ," +
                        "STATUS VARCHAR2(20) DEFAULT 'APPLIED', " +/////////////////
                        "APPLICATIONDATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')) , " +/////////////
                        "EXPECTEDSALARY NUMBER, " +/////////////
                        "YEARSOFEXPERIENCE NUMBER(10,0), " +///////////////
                        "ATTACHMENT_APPLICANTPICTURE VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_1 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_2 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_3 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_4 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_5 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_6 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_7 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_8 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_9 VARCHAR2(100)," +
                        "ATTACHMENT_CERTPICTURE_10 VARCHAR2(100)," +
                        "ATTACHMENT_SIGNATURE VARCHAR2(100)," +
                        "INTERVIEWER_NOTES TEXT," +
                        //--
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
//                    "FOREIGN KEY(HR_PROJLOCATION_SHIFT_UUID) REFERENCES HR_PROJLOCATION_SHIFT(HR_PROJLOCATION_SHIFT_UUID), "+
                        "FOREIGN KEY(HR_NATIONALITY_UUID) REFERENCES HR_NATIONALITY(HR_NATIONALITY_UUID), "+
                        "FOREIGN KEY(HR_SETUP_JOB_UUID) REFERENCES HR_SETUP_JOB(HR_SETUP_JOB_UUID)" +
                        ");");

                //create index for HR_JOBAPPLICATION_ID_INDEX
                db.execSQL("DROP INDEX HR_JOBAPPLICATION_ID_INDEX");
                db.execSQL("CREATE INDEX HR_JOBAPPLICATION_ID_INDEX ON HR_JOBAPPLICATION(HR_JOBAPPLICATION_ID)");
                db.execSQL("INSERT INTO HR_JOBAPPLICATION SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_PROJLOCATION_SHIFT
                db.execSQL("ALTER TABLE HR_PROJLOCATION_SHIFT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_PROJLOCATION_SHIFT(" +
                        //STANDARD
                        "HR_PROJLOCATION_SHIFT_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_PROJLOCATION_SHIFT_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "HR_SHIFT_UUID TEXT NOT NULL," +
                        "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                        //--
//                    "CONSTRAINT PROJLOCSHIFT_UNIQCONS UNIQUE (HR_PROJLOCATION_SHIFT_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(HR_SHIFT_UUID) REFERENCES HR_SHIFT(HR_SHIFT_UUID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES" +
                        " C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_PROJLOCATION_SHIFT_ID_INDEX
                db.execSQL("DROP INDEX HR_PROJLOCATION_SHIFT_ID_INDEX");
                db.execSQL("CREATE INDEX HR_PROJLOCATION_SHIFT_ID_INDEX ON" +
                        " HR_PROJLOCATION_SHIFT(HR_PROJLOCATION_SHIFT_ID)");
                db.execSQL("INSERT INTO HR_PROJLOCATION_SHIFT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_SHIFT
                db.execSQL("ALTER TABLE HR_SHIFT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_SHIFT(" +
                        //STANDARD
                        "HR_SHIFT_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_SHIFT_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "TIMEFROM DATETIME ," +
                        "TIMETO DATETIME ," +
                        "NAME VARCHAR2(300) ," +
                        //--
                        "CONSTRAINT SHIFT_UNIQCONS UNIQUE (HR_SHIFT_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_SHIFT_ID_INDEX
                db.execSQL("DROP INDEX HR_SHIFT_ID_INDEX");
                db.execSQL("CREATE INDEX HR_SHIFT_ID_INDEX ON HR_SHIFT(HR_SHIFT_ID)");
                db.execSQL("INSERT INTO HR_SHIFT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_NATIONALITY
                db.execSQL("ALTER TABLE HR_NATIONALITY RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_NATIONALITY(" +
                        //STANDARD
                        "HR_NATIONALITY_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_NATIONALITY_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "VALUE VARCHAR2(300) ," +
                        //--
                        "CONSTRAINT NATIONALITY_UNIQCONS UNIQUE (HR_NATIONALITY_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_SETUP_JOB_ID_INDEX
                db.execSQL("DROP INDEX HR_NATIONALITY_ID_INDEX");
                db.execSQL("CREATE INDEX HR_NATIONALITY_ID_INDEX ON" +
                        " HR_NATIONALITY(HR_NATIONALITY_ID)");
                db.execSQL("INSERT INTO HR_NATIONALITY SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_JOB_POSITION
                db.execSQL("ALTER TABLE HR_JOBPOSITION RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_JOBPOSITION(" +
                        //STANDARD
                        "HR_JOBPOSITION_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_JOBPOSITION_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "HR_SETUP_JOB_UUID TEXT," +
                        "C_BPARTNER_UUID TEXT," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "JOB_TITLE VARCHAR2(60) ," +
                        //--
                        "CONSTRAINT JOBPOS_UNIQCONS UNIQUE (HR_JOBPOSITION_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(HR_SETUP_JOB_UUID) REFERENCES" +
                        " HR_SETUP_JOB(HR_SETUP_JOB_UUID)," +
                        "FOREIGN KEY(C_BPARTNER_UUID) REFERENCES C_BPARTNER(C_BPARTNER_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_JOB_POSITION_INDEX
                db.execSQL("DROP INDEX HR_JOBPOSITION_INDEX");
                db.execSQL("CREATE INDEX HR_JOBPOSITION_INDEX ON" +
                        " HR_JOBPOSITION(HR_JOBPOSITION_ID)");
                db.execSQL("INSERT INTO HR_JOBPOSITION SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //HR_SETUP_JOB
                db.execSQL("ALTER TABLE HR_SETUP_JOB RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_SETUP_JOB(" +
                        //STANDARD
                        "HR_SETUP_JOB_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "HR_SETUP_JOB_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "NAME VARCHAR2(300) ," +
                        "VALUE VARCHAR2(300) ," +
                        //--
                        "CONSTRAINT SETUPJOB_UNIQCONS UNIQUE (HR_SETUP_JOB_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");


                //create index for HR_SETUP_JOB_ID_INDEX
                db.execSQL("DROP INDEX HR_SETUP_JOB_ID_INDEX");
                db.execSQL("CREATE INDEX HR_SETUP_JOB_ID_INDEX ON HR_SETUP_JOB(HR_SETUP_JOB_ID)");
                db.execSQL("INSERT INTO HR_SETUP_JOB SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //m_checkin -- added user uuid
                db.execSQL("ALTER TABLE M_CHECKIN RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_CHECKIN(" +
                        //STANDARD
                        "M_CHECKIN_ID NUMBER(10, 0) DEFAULT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL, " +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "AD_USER_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'N'," +
                        "ISSYNCED BOOLEAN DEFAULT 'N'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "M_CHECKIN_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "M_CHECKPOINT_UUID TEXT NOT NULL," +
                        //OTHERS
                        "DATETRX DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "LATITUDE DOUBLE ," +
                        "LONGITUDE DOUBLE ," +
                        "DESCRIPTION NVARCHAR2(255)," +
                        "ATTACHMENT_TOURPICTURE VARCHAR2(100)," +
                        //--
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(AD_USER_UUID) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(M_CHECKPOINT_UUID) REFERENCES" +
                        " M_CHECKPOINT(M_CHECKPOINT_UUID)" +
                        ");");

                //create index for M_Checkin_ID
                db.execSQL("DROP INDEX M_CHECKIN_ID_INDEX");
                db.execSQL("CREATE INDEX M_CHECKIN_ID_INDEX ON M_CHECKIN(M_CHECKIN_ID)");
                db.execSQL("INSERT INTO M_CHECKIN SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //m_checkpoint --added latitude n long and tag
                db.execSQL("ALTER TABLE M_CHECKPOINT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE M_CHECKPOINT(" +
                        //STANDARD
                        "M_CHECKPOINT_ID NUMBER(10, 0)," +
                        "AD_ORG_UUID TEXT NOT NULL," +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "M_CHECKPOINT_UUID TEXT PRIMARY KEY NOT NULL, " +
                        //FK
                        "C_PROJECTLOCATION_UUID NUMBER(10,0) NOT NULL," +
                        //OTHERS
                        "NAME NVARCHAR2(300) NOT NULL," +
                        //NFC TAG VALUE
                        "VALUE NVARCHAR2(60) NOT NULL," +
                        "DESCRIPTION NVARCHAR2(255), " +
                        "LATITUDE DOUBLE NOT NULL," +
                        "LONGITUDE DOUBLE NOT NULL," +
                        //SEQUENCE ROUTE
                        "SEQNO TEXT NOT NULL," +
                        "CONSTRAINT CHECKPOINT_UNIQCONS UNIQUE (M_CHECKPOINT_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES" +
                        " C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)" +
                        ");");

                //create index for M_Checkpoint_ID
                db.execSQL("DROP INDEX M_CHECKPOINT_ID_INDEX");
                db.execSQL("CREATE INDEX M_CHECKPOINT_ID_INDEX ON M_CHECKPOINT(M_CHECKPOINT_ID)");
                db.execSQL("INSERT INTO M_CHECKPOINT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                db.execSQL("ALTER TABLE HR_CLUSTERMANAGEMENT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE HR_CLUSTERMANAGEMENT(" +
                        //STANDARD
                        "HR_CLUSTERMANAGEMENT_ID NUMBER(10, 0)," +
                        "HR_CLUSTER_UUID TEXT NULL," +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "AD_ORG_UUID TEXT NOT NULL," +
                        "AD_USER_UUID TEXT NULL," +
                        "ISPRIMARY CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        //PK
                        "HR_CLUSTERMANAGEMENT_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID), " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for HR_CLUSTERMANAGEMENT_ID
                db.execSQL("DROP INDEX HR_CLUSTERMANAGEMENT_INDEX");
                db.execSQL("CREATE INDEX HR_CLUSTERMANAGEMENT_INDEX ON" +
                        " HR_CLUSTERMANAGEMENT(HR_CLUSTERMANAGEMENT_ID)");
                db.execSQL("INSERT INTO HR_CLUSTERMANAGEMENT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                db.execSQL("ALTER TABLE C_PROJECTLOCATION RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_PROJECTLOCATION(" +
                        //STANDARD
                        "C_PROJECTLOCATION_ID NUMBER(10, 0)," +
                        "AD_ORG_UUID TEXT NOT NULL," +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N' NOT NULL," +
                        "HR_CLUSTER_UUID TEXT NULL," +
                        //PK
                        "C_PROJECTLOCATION_UUID TEXT PRIMARY KEY NOT NULL, " +
                        //OTHERS
                        "NAME NVARCHAR2(300) NOT NULL," +
                        "VALUE NVARCHAR2(60) NOT NULL," +
                        "ISKIOSKMODE CHAR(1) DEFAULT 'N' NOT NULL," +
                        "ISPHOTO CHAR(1) DEFAULT 'N' NOT NULL," +
                        "NFCTAG TEXT NULL," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_PROJECTLOCATION_ID
                db.execSQL("DROP INDEX C_PROJECTLOCATION_INDEX");
                db.execSQL("CREATE INDEX C_PROJECTLOCATION_INDEX ON" +
                        " C_PROJECTLOCATION(C_PROJECTLOCATION_ID)");
                db.execSQL("INSERT INTO C_PROJECTLOCATION SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //ad_user
                db.execSQL("ALTER TABLE AD_USER RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE AD_USER(" +
                        //MASTER DATA
                        //STANDARD
                        "AD_USER_ID NUMBER(10, 0)," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N' NOT NULL," +
                        //PK
                        "AD_USER_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        //TODO : TEMPORARILY REMOVE "NOT NULL". BECAUSE SHOULD START WITH AD_CLIENT.
                        //TODO : EXPECTED THAT SNYC FIRST WILL ADD THE ORG AND CLIENT.
                        "AD_ORG_UUID TEXT ," +
                        "AD_CLIENT_UUID TEXT ," +
                        //OTHERS
                        "NAME NVARCHAR2(300) NOT NULL," +
                        "TITLE NVARCHAR(40)," +
                        "C_BPARTNER_UUID NUMBER(10, 0)," +
                        //--
                        "CONSTRAINT USERID_UNIQCONS UNIQUE (AD_USER_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)" +
                        ");");

                //create index for AD_User_ID
                db.execSQL("DROP INDEX AD_USER_ID_INDEX");
                db.execSQL("CREATE INDEX AD_USER_ID_INDEX ON AD_USER(AD_USER_ID)");
                db.execSQL("INSERT INTO AD_USER SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //ad_role
                db.execSQL("ALTER TABLE AD_ROLE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE AD_ROLE(" +
                        //STANDARD
                        "AD_ROLE_ID NUMBER(10, 0)," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "AD_ROLE_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "AD_ORG_UUID TEXT NOT NULL," +
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        //OTHERS
                        "NAME NVARCHAR2(300) NOT NULL," +
                        //--
                        "CONSTRAINT ROLEID_UNIQCONS UNIQUE (AD_ROLE_ID)," +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID));");

                //create index for AD_Role_ID
                db.execSQL("DROP INDEX AD_ROLE_ID_INDEX");
                db.execSQL("CREATE INDEX AD_ROLE_ID_INDEX ON AD_ROLE(AD_ROLE_ID)");
                db.execSQL("INSERT INTO AD_ROLE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //ad_org
                db.execSQL("ALTER TABLE AD_ORG RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE AD_ORG(" +
                        //STANDARD
                        "AD_ORG_ID NUMBER(10, 0)," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "AD_ORG_UUID TEXT PRIMARY KEY NOT NULL, " +
                        //FK
                        "AD_CLIENT_UUID TEXT NOT NULL," +
                        //OTHERS
                        "NAME NVARCHAR2(300) NOT NULL, " +
                        "CONSTRAINT ORGID_UNIQCONS UNIQUE (AD_ORG_ID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID));");

                //create index for AD_Org_ID
                db.execSQL("DROP INDEX AD_ORG_ID_INDEX");
                db.execSQL("CREATE INDEX AD_ORG_ID_INDEX ON AD_ORG(AD_ORG_ID)");
                db.execSQL("INSERT INTO AD_ORG SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //ad_client
                db.execSQL("ALTER TABLE AD_CLIENT RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE AD_CLIENT(" +
                        //STANDARD
                        "AD_CLIENT_ID NUMBER(10, 0)," +
                        "AD_ORG_UUID TEXT," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "UPDATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y' NOT NULL," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        //PK
                        "AD_CLIENT_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "NAME NVARCHAR2(300) NOT NULL," +
                        "CONSTRAINT CLIENTID_UNIQCONS UNIQUE (AD_CLIENT_ID)" +
                        ");");

                //create index for AD_Client_ID
                db.execSQL("DROP INDEX AD_CLIENT_ID_INDEX");
                db.execSQL("CREATE INDEX AD_CLIENT_ID_INDEX ON AD_CLIENT(AD_CLIENT_ID)");
                db.execSQL("INSERT INTO AD_CLIENT SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                db.execSQL("PRAGMA foreign_keys = true;");
                db.execSQL("PRAGMA writable_schema = false;");
            }

            if (oldVersion < 19) {
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ISEXPIRINGNOTIFIED CHAR(1) DEFAULT 'N' NOT NULL");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD ISEXPIREDNOTIFIED CHAR(1) DEFAULT 'N' NOT NULL");
            }

            if (oldVersion < 20) {
                db.execSQL("ALTER TABLE C_SURVEY ADD ATTACHMENT_STAFFSIGNATURE VARCHAR2(100)");
            }

            if (oldVersion < 25) {
                db.execSQL("PRAGMA writable_schema = true;");
                db.execSQL("PRAGMA foreign_keys = false;");

                db.execSQL("DROP TABLE IF EXISTS TMP_TABLE;");
                //C_SURVEY
                db.execSQL("ALTER TABLE C_SURVEY RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_SURVEY(" +
                        "C_SURVEY_ID NUMBER(10, 0) DEFAULT NULL, " +
                        "C_SURVEY_UUID TEXT PRIMARY KEY NOT NULL, " +
                        "AD_CLIENT_UUID NUMBER(10, 0) NOT NULL, " +
                        "AD_ORG_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_PROJECTLOCATION_UUID NUMBER(10, 0) NOT NULL, " +
                        "C_SURVEYTEMPLATE_UUID NUMBER(10, 0) NOT NULL, " +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL, " +
                        "VALUE TEXT NOT NULL, " +
                        "VALIDTO DATE, " +
                        "EMAIL TEXT NOT NULL, " +
                        "DATEDELIVERY DATE NOT NULL, " +
                        "EMAILSENT CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "STATUS CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "NAME TEXT, " +
                        "DATESTART DATE, " +
                        "DATEEND DATE, " +
                        "REMARKS TEXT, " +
                        "DATETRX DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "LONGITUDE NUMBER, " +
                        "LATITUDE NUMBER, " +
                        "ATTACHMENT_SIGNATURE VARCHAR2(100), " +
                        "ATTACHMENT_STAFFSIGNATURE VARCHAR2(100), " +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "CREATEDBY NUMBER(10, 0) NOT NULL, " +
                        "UPDATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME')), " +
                        "UPDATEDBY NUMBER(10, 0) NOT NULL, " +
                        "ISUPDATED BOOLEAN DEFAULT 'N', " +
                        "ISSYNCED BOOLEAN DEFAULT 'N', " +
                        "ISDELETED BOOLEAN DEFAULT 'N', " +
                        "FOREIGN KEY(AD_ORG_UUID) REFERENCES AD_ORG(AD_ORG_UUID)," +
                        "FOREIGN KEY(AD_CLIENT_UUID) REFERENCES AD_CLIENT(AD_CLIENT_UUID)," +
                        "FOREIGN KEY(C_SURVEYTEMPLATE_UUID) REFERENCES C_SURVEYTEMPLATE(C_SURVEYTEMPLATE_UUID), " +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)," +
                        "FOREIGN KEY(UPDATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_SURVEY_ID_INDEX
                db.execSQL("DROP INDEX C_SURVEY_ID_INDEX");
                db.execSQL("CREATE INDEX C_SURVEY_ID_INDEX ON C_SURVEY(C_SURVEY_ID)");
                db.execSQL("INSERT INTO C_SURVEY SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                //C_PROJECTTASK
                db.execSQL("ALTER TABLE C_PROJECTTASK RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE C_PROJECTTASK(" +
                        //STANDARD
                        "C_PROJECTTASK_ID NUMBER(10, 0) DEFAULT NULL," +
                        "CREATED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "CREATEDBY TEXT NOT NULL," +
                        "ISACTIVE CHAR(1) DEFAULT 'Y' NOT NULL," +
                        "ISUPDATED BOOLEAN DEFAULT 'Y'," +
                        "ISSYNCED BOOLEAN DEFAULT 'Y'," +
                        "ISDELETED BOOLEAN DEFAULT 'N'," +
                        "DATEASSIGNED DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        "DUEDATE DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))," +
                        //PK
                        "C_PROJECTTASK_UUID TEXT PRIMARY KEY NOT NULL," +
                        //FK
                        "C_PROJECTLOCATION_UUID TEXT NOT NULL," +
                        //OTHERS
                        "PRIORITY NUMBER(10,0), " +
                        "NAME NVARCHAR2(300), " +
                        "ASSIGNEDTO NUMBER(10, 0)," + //AD_USER_ID

                        "ISDONE CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "DESCRIPTION NVARCHAR2(255), " +
                        "COMMENTS NVARCHAR2(255)," +
                        "ATTACHMENT_BEFORETASKPICTURE_1 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_1 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_2 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_3 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_4 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_5 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_6 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_7 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_8 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_9 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_10 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_11 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_12 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_13 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_14 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_15 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_16 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_17 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_18 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_19 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_20 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_21 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_22 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_23 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_24 VARCHAR2(100) , " +
                        "ATTACHMENT_TASKPICTURE_25 VARCHAR2(100) , " +
                        "ATTACHMENT_SIGNATURE VARCHAR2(100) , " +
                        "LATITUDE NUMBER , " +
                        "LONGITUDE NUMBER , " +
                        "ISEXPIRINGNOTIFIED CHAR(1) DEFAULT 'N' NOT NULL, " +
                        "ISEXPIREDNOTIFIED CHAR(1) DEFAULT 'N' NOT NULL, " +
                        //--
//                    "CONSTRAINT PROJTASKID_UNIQCONS UNIQUE (C_PROJECTTASK_ID)," +
                        "FOREIGN KEY(C_PROJECTLOCATION_UUID) REFERENCES C_PROJECTLOCATION(C_PROJECTLOCATION_UUID)," +
                        "FOREIGN KEY(CREATEDBY) REFERENCES AD_USER(AD_USER_UUID)" +
                        ");");

                //create index for C_PROJECTTASK_ID_INDEX
                db.execSQL("DROP INDEX C_PROJECTTASK_ID_INDEX");
                db.execSQL("CREATE INDEX C_PROJECTTASK_ID_INDEX ON C_PROJECTTASK(C_PROJECTTASK_ID)");
                db.execSQL("INSERT INTO C_PROJECTTASK SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                db.execSQL("PRAGMA foreign_keys = true;");
                db.execSQL("PRAGMA writable_schema = false;");

//                db.execSQL("ALTER TABLE C_SURVEY ADD REMARKS TEXT");
//                db.execSQL("ALTER TABLE C_SURVEY ADD DATETRX DATETIME NOT NULL DEFAULT (DATETIME('NOW', 'LOCALTIME'))");
            }

            if (oldVersion < 26) {
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD SECASSIGNEDTO NUMBER(10, 0)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD EQUIPMENT VARCHAR2(255)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD CONTACT VARCHAR2(100)");
                db.execSQL("ALTER TABLE C_PROJECTTASK ADD CONTACTNO NUMBER(15, 0)");
            }

            if (oldVersion < 27) {
                db.execSQL("PRAGMA writable_schema = true;");
                db.execSQL("PRAGMA foreign_keys = false;");

                db.execSQL("DROP TABLE IF EXISTS TMP_TABLE;");
                //PBS_SYNCTABLE
                db.execSQL("ALTER TABLE PBS_SYNCTABLE RENAME TO TMP_TABLE;");
                db.execSQL("CREATE TABLE PBS_SYNCTABLE(" +
                        //STANDARD
                        "PBS_SYNCTABLE_ID TEXT NOT NULL," +
                        //PK
                        "PBS_SYNCTABLE_UUID TEXT PRIMARY KEY NOT NULL," +
                        //OTHERS
                        "ISMASTERDATA BOOLEAN," +
                        "ISRETAIN BOOLEAN," +
                        "RETENTIONPERIOD INT," +
                        "AD_TABLE_UUID TEXT," +
                        "NAME TEXT," +
                        "SEQNO INT DEFAULT 0" +
                        ");");

                db.execSQL("INSERT INTO PBS_SYNCTABLE SELECT * FROM TMP_TABLE;");
                db.execSQL("DROP TABLE TMP_TABLE;");

                db.execSQL("PRAGMA foreign_keys = true;");
                db.execSQL("PRAGMA writable_schema = false;");
            }
        } catch (SQLException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE
                    + e.getMessage());
        }
    }

    /**
     * Delete database
     */
    public static void reCreateDatabase(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(PBSAccountInfo.ACCOUNT_AUTHORITY);

        assert client != null;
        PBSContentProvider provider = (PBSContentProvider) client.getLocalContentProvider();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            client.close();
        else
            client.release();

        assert provider != null;
        provider.resetDatabase();
    }

    public void removeDatabase(Context mContext) {
        boolean result = mContext.deleteDatabase(DATABASE_NAME);
        if (result)
            Log.d(TAG, "Database Deleted...");
        else Log.d(TAG, "Database Deletion failed.");
    }
}
