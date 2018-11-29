package com.cudocomm.troubleticket.util;

/**
 * Created by adsxg on 4/11/2017.
 */

public class Constants {

    public static final String EXTRA_MESSAGE = "message";

    public static final boolean LOGS = true;
    public static float C_GPS_ACC = 100;
    public static long C_VALID_GPS_TIME = 4 * 60 * 60 * 10000;


    public static final String LOG_APP = "TTS";
    public static final String IS_LEARNED = "islearned";
    public static final String DEFAULT_FILE_NAME = "tts.dat";

    public static final String _VERSION = "2.4";

    /*public static final String BASE_URL = "http://202.58.124.112/TT_SCM_DEV/apiv6/";
    public static final String BASE_URL_IMAGE = "http://202.58.124.112/TT_SCM_DEV/";*/

//    public static final String BASE_URL = "http://tt.scm.co.id/apiv6/";
    public static final String BASE_URL_IMAGE = "http://tt.scm.co.id/";
//
    public static final String BASE_URL = "http://tt.scm.co.id/api/";

//    public static final String BASE_URL = "http://103.77.78.169/tt_scm_new_cr/api/";
//    public static final String BASE_URL_IMAGE = "http://103.77.78.169//tt_scm_new_cr/";

    /*public static final String BASE_URL = "http://202.58.124.112/TT_SCM_DEV/apiv5/";
    public static final String BASE_URL_IMAGE = "http://202.58.124.112/TT_SCM_DEV/";*/

    public static final String URL_LOGIN = "login2";
//    public static final String URL_LOGIN = "signin";
    public static final String URL_LOAD_MASTER = "load_all_master";
    public static final String REFRESH_TOKEN = "refresh_token";
//    public static final String URL_TICKET_HISTORY = "get_ticket_log";
    public static final String URL_TICKET_HISTORY = "ticketlogmobile";
//    public static final String MY_TICKET = "ticketbycreator";
    public static final String MY_TICKET = "ticketbystation";
    public static final String URL_GET_ENGINEER = "get_engineer";
    public static final String URL_GET_ASSIGN_TO = "get_assign_to";
    public static final String URL_GET_ASSIGN_TO_ME = "get_assign_to_me";
    public static final String URL_GET_ESCALATION_TICKET = "getescalationticket";
    public static final String URL_GET_ASSIGNMENT_TICKET = "getassignmentticket";
    public static final String URL_GET_REQUEST_VISIT = "getrequestvisit";
    public static final String URL_GET_SEND_REMINDER_KADIV = "remainder_open_ticket_by_kadiv";
    public static final String URL_REJECT_REQUEST_VISIT = "rejectrequestvisit";
    public static final String URL_APPROVE_REQUEST_VISIT = "approverequestvisit";
    public static final String URL_REQUEST_APPROVE_CLOSE = "getrequestapproval";
    public static final String URL_REQUEST_APPROVE_CLOSE_NEW = "getrequestapproval2";
//    public static final String URL_SUBMIT_ASSIGNMENT = "assignment_ticket";
    public static final String URL_SUBMIT_ASSIGNMENT = "new_assignment_ticket";
    public static final String URL_SUBMIT_ASSIGNMENTV2 = "new_assignment_ticketv2";
    public static final String URL_SUBMIT_ASSIGNMENT_TO = "new_assignment_ticket_auto_generate";

    public static final String SELECTED_STATION_LAT = "stationLatitude";
    public static final String SELECTED_STATION_LONG = "stationLongitude";
    public static final String COUNTER_NEED_APPROVAL = "needApprovalCounter";
    public static final String COUNTER_MY_TASK = "myTaskCounter";
    public static final String COUNTER_TOTAL = "totalTicket";

    public static String RESPONSE_STATUS = "status";
    public static String RESPONSE_SUCCESS = "success";
    public static String RESPONSE_OPEN_TICKET = "open_tickets";
    public static String RESPONSE_CONF_TICKET = "confirm_tickets";

    public static String RESPONSE_TICKETS = "tickets";
    public static String TICKET_LOGS = "ticketLogs";
    public static String SELECTED_TICKET_POSITION = "selectedTicketPosition";
    public static String SELECTED_TICKET = "selectedTicket";
    public static String SELECTED_ASSIGNMENT = "selectedAssignment";
    public static String PARAM_TICKET_ID = "ticket_id";
    public static String PARAM_TICKET_CONFIRM_BY = "ticket_confirmby";
    public static String PARAM_ID = "id";
    public static String PARAM_INFO = "info";
    public static String PARAM_TGLDAPATURE = "tgldepature";
    public static String PARAM_VESSELNO = "vesselno";

    public static String PARAM_DATE_VISIT = "date_visit";
    public static String PARAM_REASON = "reason";
    public static String PARAM_SECTION = "section";
    public static String PARAM_ENGINEERS = "engineers";
    public static String PARAM_STATION_ID = "station_id";
    public static String PARAM_TITLE = "title";
    public static String PARAM_MSG = "message";
    public static String ACTIVE_PAGE = "active_page";
    public static String HOME_PAGE = "Home";
    public static String TT_ESCALATION = "TT Escalation";
    public static String TT_TOP_TEN = "Top Ten List";
    public static String TT_STATISTICS = "Statistics";
    public static String TT_TO_DO_LIST = "To Do List";
    public static String TT_NEWEST_TICKET = "Newest Ticket";
    public static String TT_NON_AC_NIELASEN = "Non-AC Nielsen";
    public static String TT_AC_NIELASEN = "AC Nielsen";
    public static String TT_O_CHANNEL = "O Channel";
    public static String TT_NEXMEDIA = "Nexmedia";
    public static String TT_SEND_REMINDER = "Send Reminder";
    public static String TICKET_INFO_PAGE = "Ticket Information";
    public static String ASSIGNMENT_PAGE = "Job Assignment";
    public static String MY_TASK_PAGE = "My Task";
    public static String TT_ACTIVITY_PAGE = "TT Activity";
    public static String MY_TASK_DETAIL_PAGE = "My Task Detail";
    public static String MY_VISIT_PAGE = "On Site Visit";
    public static String MY_VISIT_DETAIL_PAGE = "On Site Visit Detail";
    public static String REQUEST_VISIT_PAGE = "Approval Request Visit";
    public static String REQUEST_VISIT_DETAIL_PAGE = "Approval Request Visit Detail";
//    KST
//    public static String MY_APPROVAL_PAGE = "Request Close Ticket";
    public static String MY_APPROVAL_PAGE = "Approval Ticket";
    public static String MY_APPROVAL_DETAIL_PAGE = "Request Close Ticket Info";

    public static final int TECHNICIAN = 1;
    public static final int KST = 2;
    public static final int KORWIL = 3;
    public static final int KADEP_WIL = 4;
    public static final int KADEP_TS = 5;
    public static final int KADEP_INFRA = 11;
    public static final int KADIV = 6;
    public static final int CBTO = 12;
    public static final int ENGINEER = 7;

    public static final int STATUS_CLOSED = 0;
    public static final int STATUS_OPEN = 1;
    public static final int STATUS_CONF = 2;

    public static final String USER_LOGIN_TAG = "userlogin";

    public static final String IS_LOGIN = "is_login";
    public static final String USER_LOCAL_INDEX = "user_local_index";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String POSITION_ID = "position_id";
    public static final String POSITION_NAME = "position_name";
    public static final String STATION_ID = "station_id";
    public static final String STATION_NAME = "station_name";
    public static final String REGION_ID = "region_id";
    public static final String REGION_NAME = "region_name";
    public static final String BRANCH_NAME = "branch_name";
    public static final String USER_PICTURE = "user_picture";
    public static final String DEPARTMENT_ID = "department_id";
    public static final String DEPARTMENT_NAME = "department_name";
    public static final String FCM_REG_ID = "fcm_registered_id";
    public static final String ID_UPDRS = "id_updrs";

    public static final String GANGGUAN_AV = "Gangguan AV";
    public static final String DOWN_TIME = "Down Time";
    public static final String KERUSAKAN = "Kerusakan";

    public static String CRITICAL = "critical";
    public static String MAJOR = "major";
    public static String MINOR = "minor";

    public static String GUIDANCE = "guidance";
    public static String ON_SITE_VISIT = "on site visit";

//    PARAM
    public static String PARAM_USERNAME = "username";
    public static String PARAM_PASSWORD = "password";
    public static String PARAM_TOKEN = "token";
    public static String PARAM_CREATOR = "creator_id";
    public static String PARAM_TICKET_NO = "ticket_no";
    public static final String IV_TARGET_DT = "iv_target_dt";

    //    popup

    public static String ARGS_TITLE = "argsTitle";
    public static String ARGS_INFO_LABEL = "argsLabelInfo";
    public static String ARGS_DESC = "argsDesc";
    public static String ARGS_BACK = "argsBack";
    public static String ARGS_PROCESS = "argsProcess";
    public static String ARGS_BACK_LISTENER = "argsBackListener";
    public static String ARGS_PROCESS_LISTENER = "argsProcessListener";

//    reuqest code
    public static final int REQUEST_NEW_TICKET = 100;
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_APPROVAL = 9;
    public static final int ENGINEER_ASSIGNMENT = 10;
    public static final int REQUEST_ENGINEER_ONSITE_CLOSE = 1000;
    public static final int REQUEST_VIEW_ASSIGNMENT = 10011;

    public static String NASIONAL = "Nasional";

    public static String DASHBOARD_OPEN_TICKET = "dashboard_open_ticket";
    public static String DASHBOARD_CLOSE_TICKET = "dashboard_close_ticket";
    public static String DASHBOARD_CRITICAL_TICKET = "dashboard_critical_ticket";
    public static String DASHBOARD_MAJOR_TICKET = "dashboard_major_ticket";
    public static String DASHBOARD_MINOR_TICKET = "dashboard_minor_ticket";
    public static String DASHBOARD_ACN_TICKET = "dashboard_acn_ticket";
    public static String DASHBOARD_NACN_TICKET = "dashboard_nacn_ticket";


    public static String MY_ACTIVE_TICKETS = "ACTIVE TICKET";
    public static String MY_OTHER_TICKETS = "OTHER TICKET";

    public static String AC_NIELSEN = "AC NIELSEN";
    public static String NON_AC_NIELSEN = "NON AC NIELSEN";
    public static String O_CHANNEL = "O CHANNEL";
    public static String NEXMEDIA = "NEXMEDIA";

    //    version
    public static final String URL_CEK_UPDATE_APK = BASE_URL + "cek_version";

    //    version
    public static final String URL_PROGRAM_ACARA = BASE_URL + "getprogramacara";

}
