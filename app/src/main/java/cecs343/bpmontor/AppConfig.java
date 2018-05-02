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
    public static final String URL_SELPAT   = "http://bpmon.heliohost.org/selectPatient.php";
    public static final String URL_UPDATESCHED   = "http://bpmon.heliohost.org/updateSched.php";
    public static final String URL_ADDMED   = "http://bpmon.heliohost.org/addMed.php";
    public static final String URL_DELMEDS   = "http://bpmon.heliohost.org/delMeds.php";
    public static final String URL_ADDDOC   = "http://bpmon.heliohost.org/addDoc.php";

    //JSON TAGS
    public static final String pidTag = "pid";
    public static final String errorTag = "error";
    public static final String errorMessageTag = "error_msg";
    public static final String emailTag = "email";
    public static final String dateTag = "dte";
    public static final String timeTag = "tim";
    public static final String mednameTag = "medName";
    public static final String nameTag = "name";
    public static final String sysTag = "sys";
    public static final String diaTag = "dia";
    public static final String SUCCESS = "success";
    public static final String IS_DOC = "isDoctor";
    public static final String FULL_NAME = "fullname";

}
