package telecom.projet.model;

import java.util.ArrayList;

public class Disease {

    private String name;

    private String cui_code;

    private ArrayList<Symptom> symptoms = new ArrayList<>();

    public Disease(String name, String cui_code) {
        this.name = name;
        this.cui_code = cui_code;
    }

    public Disease(String cui_code) {
        this.name = "";
        this.cui_code = cui_code;
    }

    public Disease() {
        this.name = "";
        this.cui_code = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCui_code() {
        return cui_code;
    }

    public void setCui_code(String cui_code) {
        this.cui_code = cui_code;
    }

    public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<Symptom> symptoms) {
        this.symptoms = symptoms;
    }
}
