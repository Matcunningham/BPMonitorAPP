package cecs343.bpmontor;

/**
 * Created by Mat on 4/16/2018.
 */

// This class holds global variables
public class AppConfig {
    // Server user login/register url
    public static final String URL_LOGIN = "http://matcunningham.com/login.php";
    public static final String URL_REGISTER = "http://matcunningham.com/register.php";
    public static final String URL_ISDOCTOR = "http://matcunningham.com/checkDoctor.php";
    public static final String URL_BPHIST   = "http://matcunningham.com/bpHist.php";
    public static final String URL_MEDHIST = "http://matcunningham.com/medHist.php";
    public static final String URL_MEDSCHED = "http://matcunningham.com/medSched.php";
    public static final String URL_RECBP   = "http://matcunningham.com/recordBp.php";
    public static final String URL_RECMED   = "http://matcunningham.com/recordMed.php";
    public static final String URL_SELPAT   = "http://matcunningham.com/selectPatient.php";
    public static final String URL_UPDATESCHED   = "http://matcunningham.com/updateSched.php";
    public static final String URL_ADDMED   = "http://matcunningham.com/addMed.php";
    public static final String URL_DELMEDS   = "http://matcunningham.com/delMeds.php";
    public static final String URL_ADDDOC   = "http://matcunningham.com/addDoc.php";

    // JSON TAGS
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

    // TIMEOUT FOR HTTP CONNECTION
    public static final int HTTP_TIME_OUT = 8000;

    // SPLASH SCREEN TIMEOUT
    public static final int TIME_OUT = 2500;

}
