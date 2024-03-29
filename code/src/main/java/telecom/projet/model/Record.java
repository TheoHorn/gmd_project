package telecom.projet.model;

import java.util.Objects;

public class Record {
    private String symptom;
    private String problem;
    private String treatment;
    private String data_source;
    private int score;

    public Record(String symptom, String problem, String treatment, String data_source, int score) {
        this.symptom = symptom;
        this.problem = problem;
        this.treatment = treatment;
        this.data_source = data_source;
        this.score = score;
    }

    //getters and setters
    public String getSymptom() {
        return symptom;
    }
    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }
    public String getProblem() {
        return problem;
    }
    public void setProblem(String problem) {
        this.problem = problem;
    }
    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    public String getData_source() {
        return data_source;
    }
    public void setData_source(String data_source) {
        this.data_source = data_source;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(symptom, record.symptom) && Objects.equals(problem, record.problem) && Objects.equals(treatment, record.treatment) && Objects.equals(data_source, record.data_source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symptom, problem, treatment, data_source, score);
    }
}
