package telecom.projet;

import static telecom.projet.indexes.drugbank.DrugbankResearcher.*;
import static telecom.projet.indexes.hpo.HpoOboResearcher.*;
import static telecom.projet.indexes.hpo.HpoOboResearcherForSynonym.*;
import static telecom.projet.indexes.omim.OmimResearcherCSV.*;
import static telecom.projet.indexes.omim.OmimResearcherTXT.*;
import static telecom.projet.indexes.sider.MeddraResearcher.*;
import static telecom.projet.indexes.stitch.ChemicalSourcesResearcher.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import telecom.projet.model.Disease;
import telecom.projet.model.Record;
import telecom.projet.model.SideEffect;
import telecom.projet.model.Symptom;
import telecom.projet.model.Treatment;
import telecom.projet.sqliteQuestioner.HpoApp;

public class Data {


    private ArrayList<Record> records = new ArrayList<Record>();

    public Data(String query_symptom, Boolean side_effect) throws IOException {
        this.records.clear();
        this.records = run(query_symptom, side_effect);
        recordsCleaning(records, side_effect);
    }


    public ArrayList<Record> run(String query_symptom, Boolean side_effect) throws IOException {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        query_symptom = query_symptom.toLowerCase().trim();
        ArrayList<Record> records = new ArrayList<>();
        if (query_symptom.contains(" and ")) {
            String[] symptoms = query_symptom.split(" and ");
            records.addAll(run(symptoms[0], side_effect));
            for (int i = 1; i < symptoms.length; i++) {
                records.retainAll(run(symptoms[i], side_effect));
            }
        }else if (query_symptom.contains(" or ")) {
            String[] symptoms = query_symptom.split(" or ");
            for (String symptom : symptoms) {
                records.addAll(run(symptom, side_effect));
            }
        }else{
            ArrayList<String> symptoms = searchingSynonymsBySymptom(query_symptom);
            symptoms.add(query_symptom);
            for (String symptom : symptoms) {
                if (side_effect) {
                    records.addAll(searchSideEffect(symptom.toLowerCase()));
                } else {
                    records.addAll(searchDisease(symptom.toLowerCase()));
                }
            }
        }
        return records;
    }

    /**
     * Search the databases for the symptoms
     * @param query the symptoms to search for
     * @return an ArrayList of the records found
     */
    public ArrayList<Record> searchDisease(String query) throws IOException {
        HpoApp hpoApp = new HpoApp();
        ArrayList<Record> records = new ArrayList<>();
        ArrayList<Symptom> possible_symptoms = searchingSymptomByQuery(query);


        ArrayList<Disease> diseases_possible = searchingDiseaseBySymptom(possible_symptoms);
        diseases_possible.addAll(hpoApp.searchingDiseaseBySymptom(possible_symptoms));

        ArrayList<Disease> diseases_searchable = searchingCuiOmim(diseases_possible);
        diseases_searchable.addAll(searchingCuiMeddra(diseases_possible));
        diseases_possible.addAll(symptomsToDisease(possible_symptoms));
        for (Disease disease : diseases_possible) {
            ArrayList<String> cids = getCIDbyCUI_meddra_all_indication(disease.getCui_code());
            for (String cid : cids) {
                ArrayList<String> atc_codes = getATCbyCID(cid);
                ArrayList<Treatment> treatments = getTreatmentByATC(atc_codes);
                for (Treatment treatment : treatments) {

                    Record record = new Record(query, disease.getName().trim(), treatment.getName().trim(), disease.getFind_in(), 1);
                    records.add(record);
                }
            }
        }

        return records;
    }

    

    /**
     * Search the databases for the symptoms
     * @param query the symptoms to search for
     * @return an ArrayList of the records found
     */
    public ArrayList<Record> searchSideEffect(String query) throws IOException {
        ArrayList<Record> records = new ArrayList<>();
        ArrayList<SideEffect> sideEffects = getSideEffectByString(query);
        for (SideEffect se : sideEffects) {
            ArrayList<String> atc_cure_se = getATCbyCID(se.getCid());
            ArrayList<Treatment> cure_treatments = getTreatmentByATC(atc_cure_se);
            ArrayList<String> bad_cids = getCIDbyCUI_meddra_all_indication(se.getCui());
            for (String cid : bad_cids) {
                ArrayList<String> atc_bad_se = getATCbyCID(cid);
                ArrayList<Treatment> bad_treatments = getTreatmentByATC(atc_bad_se);
                for (Treatment cure : cure_treatments) {
                    for (Treatment bad : bad_treatments) {
                        //TODO : verify if they can be taken together

                        int score = getMeanFrequency(se.getCui(), se.getCid());
                        Record record = new Record(query, bad.getName(), cure.getName(),"", score);
                        records.add(record);
                    }
                }
            }
        }
        return records;
    }

    private ArrayList<Disease> symptomsToDisease(ArrayList<Symptom> possibleSymptoms) {
        ArrayList<Disease> diseases = new ArrayList<>();
        for (Symptom symptom : possibleSymptoms) {
            Disease disease = new Disease();
            disease.setHp_code(symptom.getHp_code());
            disease.setCui_code(symptom.getCui_code());
            disease.setName(symptom.getName());
            disease.setFind_in("SIDER");
            diseases.add(disease);
        }
        return diseases;
    }

    private ArrayList<Record> recordsCleaning(ArrayList<Record> records, Boolean side_effect) {
        /* Remove the duplicates in the records
         */
        HashMap <String, Integer> scores = new HashMap<>();
        ArrayList<Record> records_cleaned = new ArrayList<>();
        for (Record r : records){
            if (scores.containsKey(r.getProblem())){
                scores.put(r.getProblem(), scores.get(r.getProblem()) + r.getScore());
            }else{
                scores.put(r.getProblem(), r.getScore());
            }
            if (!records_cleaned.contains(r)) {
                records_cleaned.add(r);
            }
        }
        for (Record r : records_cleaned){
            r.setScore(scores.get(r.getProblem()));
        }
        return records_cleaned;
    }

    public void orderingByScore(){
        this.records.sort((Record r1, Record r2) -> r2.getScore() - r1.getScore());
    }

    public ArrayList<Record> getRecords() {
        orderingByScore();
        System.out.println("Records: " + this.records.size() + " found.");
        return this.records;
    }

    
}
