package telecom.projet.model;

import java.util.ArrayList;

public class Disease {

    private String name;

    private String cui_code;

    private String omim_code;

    private ArrayList<Symptom> symptoms = new ArrayList<>();

    private String hp_id;

    private int score;

    private String find_in;

    public Disease(String name, String cui_code, String hp_code) {
        this.name = name;
        this.cui_code = cui_code;
        this.hp_id = hp_code;
    }

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

    public void addSymptoms(Symptom symptom) {
        this.symptoms.add(symptom);
    }

    public String getHp_code() {
        return hp_id;
    }

    public void setHp_code(String hp_code) {
        this.hp_id = hp_code;
    }

    public String getOmim_code() {
        return omim_code;
    }

    public void setOmim_code(String omim_code) {
        this.omim_code = omim_code;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFind_in() {
        return find_in;
    }

    public void setFind_in(String find_in) {
        this.find_in = find_in;
    }

    public String toString() {
        return "Disease{" +
                "name='" + name + '\'' +
                ", cui_code='" + cui_code + '\'' +
                ", omim_code='" + omim_code + '\'' +
                ", symptoms=" + symptoms +
                ", hp_id='" + hp_id + '\'' +
                '}';
    }
}
