package telecom.projet.model;

public class Treatment {

    private String name;
    private String atc_code;
    private String drugbank_id;
    private String database;

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

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "name='" + name + '\'' +
                ", atc_code='" + atc_code + '\'' +
                ", drugbank_id='" + drugbank_id + '\'' +
                ", database='" + database + '\'' +
                '}';
    }
}
