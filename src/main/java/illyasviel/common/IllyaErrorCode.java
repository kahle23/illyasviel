package illyasviel.common;

import artoria.exception.ErrorCode;

/**
 * Illyasviel error code.
 * @author Kahle
 */
public enum IllyaErrorCode implements ErrorCode {
    GLB_PARAM_MAP_NOT_NULL("GLB001", "Parameter \"param\" must not null. "),

    WRM_WORK_REPORT_PARAM_NOT_NULL("WRM001", "Parameter \"workReportParam\" must not null. "),
    WRM_SAVE_FILE_SUFFIX_NOT_BLANK("WRM002", "Parameter \"suffix\" must not blank. "),
    WRM_BEGIN_TIME_NOT_BLANK("WRM003", "Parameter \"beginTime\" must not blank. "),
    WRM_END_TIME_NOT_BLANK("WRM004", "Parameter \"endTime\" must not blank. "),
    WRM_BEGIN_TIME_BEFORE_END("WRM005", "Begin time must before end time. "),
    WRM_SOURCE_NAMES_NOT_BLANK("WRM006", "Parameter \"sourceNames\" must not blank. "),
    WRM_RESULT_MAP_NOT_NULL("WRM007", "Parameter \"resMap\" must not null. "),
    WRM_FILE_ENCODING_NOT_BLANK("WRM008", "Parameter \"fileEncoding\" must not blank. "),
    WRM_RESULT_LIST_NOT_NULL("WRM009", "Parameter \"resList\" must not null. "),
    WRM_GITLAB_ADDRESS_NOT_BLANK("WRM010", "Parameter \"gitlabAddress\" must not blank. "),
    WRM_GITLAB_PROJECTS_NOT_BLANK("WRM011", "Parameter \"gitlabProjects\" must not blank. "),
    WRM_PROJECT_STRING_NOT_BLANK("WRM012", "Parameter \"projectString\" must not blank. "),
    WRM_PROJECT_STRING_NOT_WRONG("WRM013", "Parameter \"projectString\" must not wrong. "),
    WRM_ZENTAO_ADDRESS_NOT_BLANK("WRM014", "Parameter \"zentaoAddress\" must not blank. "),
    WRM_MUST_FIND_SPECIFIED_TAG("WRM015", "The specified tag cannot be found in the \"historyItem\". "),
    WRM_TIME_FORMAT_MATCH("WRM016", "The time format in the \"historyItem\" has changed. "),
    WRM_BUG_TD_ELEMENTS_SIZE_ERROR("WRM017", "Incompatible Zentao, \"td\" size need equal 10. "),
    WRM_TASK_TD_ELEMENTS_SIZE_ERROR("WRM018", "Incompatible Zentao, \"td\" size need equal 13. "),
    WRM_DETAIL_ADDRESS_NOT_BLANK("WRM019", "Parameter \"detailAddress\" must not blank. "),
    WRM_KEYWORD_NOT_BLANK("WRM020", "Parameter \"keyword\" must not blank. "),

    AGD_AUTO_GET_DAILY_BONUS_PARAM_NOT_NULL("AGD001", "Parameter \"autoGetDailyBonusParam\" must not null. "),
    AGD_WEBSITE_NAMES_NOT_BLANK("AGD002", "Parameter \"websiteNames\" must not blank. "),
    AGD_V2EX_COOKIES_NOT_BLANK("AGD003", "Parameter \"v2exCookies\" must not blank. "),
    AGD_V2EX_COOKIES_NOT_EMPTY("AGD004", "Parameter \"v2exCookies\" must not empty. "),
    AGD_NOT_PROCESS_ONCLICK("AGD005", "Unable to resolve the daily bonus address when processing \"onclick\". "),
    AGD_NOT_TIME_TO_GET_OR_WEBSITE_CHANGED("AGD006", "It is not time to get it or the website has been changed. "),
    AGD_DAILY_BONUS_ADDRESS_NOT_BLANK("AGD007", "The daily bonus address cannot be found in the thread local. "),
    AGD_GRAY_ELEMENTS_NOT_EMPTY("AGD008", "The \"gray\" element is not found. "),
    AGD_CELL_ELEMENTS_ENOUGH("AGD009", "The \"cell\" element is not enough in \"Main\" element. "),
    AGD_TABLE_ELEMENTS_SIZE_ERROR("AGD010", "Expect too few \"table\" labels. "),
    AGD_TR_ELEMENTS_SIZE_ERROR_IN_TABLE("AGD011", "No balance gets record. "),

    TTS_TABLE_PATH_NOT_BLANK("TTS001", "Parameter \"tablePath\" must not blank. "),
    TTS_SQL_TEMPLATE_NOT_BLANK("TTS002", "Parameter \"sqlTemplate\" must not blank. "),
    TTS_SQL_SAVE_PATH_NOT_BLANK("TTS003", "Parameter \"sqlSavePath\" must not blank. "),
    TTS_TABLE_PATH_MUST_BE_TABLE("TTS004", "Parameter \"tablePath\" must be a table file. "),
    ;

    private String code;
    private String description;

    IllyaErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {

        return this.code;
    }

    @Override
    public String getDescription() {

        return this.description;
    }

}
