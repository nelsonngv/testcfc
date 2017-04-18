package com.pbasolutions.android.controller;

import android.content.Context;

/**
 * Created by pbadell on 10/5/15.
 */
public class PBSSurveyController extends PBSController {
    /**
     * Event name : get survey list.
     */
    public static final String GET_SURVEYS_EVENT = "GET_SURVEYS_EVENT";
    /**
     * Event name : get survey detail.
     */
    public static final String GET_SURVEY_EVENT = "GET_SURVEY_EVENT";
    /**
     * Event name : insert survey.
     */
    public static final String INSERT_SURVEY_EVENT = "INSERT_SURVEY_EVENT";
    /**
     * Event name : get templates.
     */
    public static final String GET_TEMPLATES_EVENT = "GET_TEMPLATES_EVENT";
    /**
     * Event name : get questions.
     */
    public static final String GET_QUESTIONS_EVENT = "GET_QUESTIONS_EVENT";

    public static final String ARG_SURVEY_UUID = "ARG_SURVEY_UUID";
    public static final String ARG_TEMPLATE_UUID = "ARG_TEMPLATE_UUID";
    public static final String ARG_TEMPLATES = "ARG_TEMPLATES";
    public static final String ARG_SURVEYS = "ARG_SURVEYS";
    public static final String ARG_SURVEY = "ARG_SURVEY";
    public static final String ARG_QUESTIONS = "ARG_QUESTIONS";
    public static final String ARG_SECTIONS = "ARG_SECTIONS";
    public static final String SURVEY_VALUES = "SURVEY_VALUES";
    public static String name;
    public static String templateUUID;
    public static String dateStart;

    /**
     *
     * @param base
     */
    public PBSSurveyController(Context base) {
        super(base);
        task = new SurveyTask(getContentResolver());
    }
}
