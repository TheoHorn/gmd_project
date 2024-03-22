package telecom.projet.model;

public class Symptom {

    private String cui_code;
    private String name;

    public Symptom(String cui_code, String name) {
        this.cui_code = cui_code;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
