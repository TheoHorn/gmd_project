package telecom.projet.model;

import java.util.ArrayList;

public class Symptom {

    private String cui_code;
    private String name;
    private String hp_code;
    private ArrayList<String> synonyms = new ArrayList<>();

    public Symptom(String name, String synonym, String cui_code, String hp_code) {
        this.cui_code = cui_code;
        this.name = name;
        this.hp_code = hp_code;
        addSynonyms(synonym);
    }

    public Symptom(String name, String synonym, String cui_code) {
        this.cui_code = cui_code;
        this.name = name;
        addSynonyms(synonym);
    }

    public Symptom(String name, String synonym) {
        this.cui_code = "";
        this.name = name;
        addSynonyms(synonym);
    }

    public Symptom(String name) {
        this.cui_code = "";
        this.name = name;
    }

    public Symptom() {
        this.cui_code = "";
        this.name = "";
    }

    public String getCui_code() {
        return cui_code;
    }

    public void setCui_code(String cui_code) {
        this.cui_code = cui_code;
    }

    public String getHp_code() {
        return hp_code;
    }

    public void setHp_code(String hp_code) {
        this.hp_code = hp_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public void addSynonyms(String synonym) {
        this.synonyms.add(synonym);
    }
}
