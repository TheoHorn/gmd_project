package telecom.projet.model;

import java.util.ArrayList;

public class Drug {
    private String name;
    private String atc_code;
    private String drugbank_id;

    private ArrayList<Drug> drugs = new ArrayList<>();

    public Drug(String name, String atc_code, String drugbank_id) {
        this.name = name;
        this.atc_code = atc_code;
        this.drugbank_id = drugbank_id;
    }

    public Drug(String name) {
        this.name = name;
        this.atc_code = "";
        this.drugbank_id = "";
    }

    public Drug() {
        this.name = "";
        this.atc_code = "";
        this.drugbank_id = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAtc_code() {
        return atc_code;
    }

    public void setAtc_code(String atc_code) {
        this.atc_code = atc_code;
    }

    public String getDrugbank_id() {
        return drugbank_id;
    }

    public void setDrugbank_id(String drugbank_id) {
        this.drugbank_id = drugbank_id;
    }
}
