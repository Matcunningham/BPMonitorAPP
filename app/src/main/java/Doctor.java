/**
 * Created by Mat on 4/14/2018.
 */

import java.util.*;

public class Doctor extends User {
    private ArrayList patients = new ArrayList<Patient>();
    private Patient currentPatient;

    public ArrayList getPatients() {
        return patients;
    }

    public void setPatients(ArrayList patients) {
        this.patients = patients;
    }

    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }
}
