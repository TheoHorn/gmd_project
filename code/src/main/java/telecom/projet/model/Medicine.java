package telecom.projet.model;

import java.util.ArrayList;

public class Medicine {

    private String name;
    private String cid_code;

    private ArrayList<Drug> drugs = new ArrayList<>();

    public Medicine(String name, String cid_code) {
        this.name = name;
        this.cid_code = cid_code;
    }

    public Medicine(String cid_code) {
        this.name = "";
        this.cid_code = cid_code;
    }

    public Medicine() {
        this.name = "";
        this.cid_code = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getcid_code() {
        return cid_code;
    }

    public void setcid_code(String cid_code) {
        this.cid_code = cid_code;
    }

    public String getCid_code() {
        return cid_code;
    }

    public void setCid_code(String cid_code) {
        this.cid_code = cid_code;
    }

    public ArrayList<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(ArrayList<Drug> drugs) {
        this.drugs = drugs;
    }
}
