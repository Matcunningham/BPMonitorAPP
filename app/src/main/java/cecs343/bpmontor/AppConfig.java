package cecs343.bpmontor;

/**
 * Created by Mat on 4/16/2018.
 */

public class AppConfig {
    // Server user login/register url
    public static final String URL_LOGIN = "http://bpmon.heliohost.org/login.php";
    public static final String URL_REGISTER = "http://bpmon.heliohost.org/register.php";
    public static final String URL_ISDOCTOR = "http://bpmon.heliohost.org/checkDoctor.php";
    public static final String URL_BPHIST   = "http://bpmon.heliohost.org/bpHist.php";
    public static final String URL_MEDHIST = "http://bpmon.heliohost.org/medHist.php";
    public static final String URL_MEDSCHED = "http://bpmon.heliohost.org/medSched.php";
    public static final String URL_RECBP   = "http://bpmon.heliohost.org/recordBp.php";
    public static final String URL_RECMED   = "http://bpmon.heliohost.org/recordMed.php";

    //JSON TAGS
    public static final String pidTag = "pid";
    public static final String errorTag = "error";
    public static final String errorMessageTag = "error_msg";
    public static final String emailTag = "email";
    public static final String docidTag = "docid";
    public static final String dateTag = "dte";
    public static final String timeTag = "tim";
    public static final String mednameTag = "medName";
    public static final String nameTag = "name";
    public static final String sysTag = "sys";
    public static final String diaTag = "dia";

}
