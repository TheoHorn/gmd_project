package telecom.projet;

import static telecom.projet.indexes.sider.MeddraResearcher.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


import telecom.projet.model.Disease;
import telecom.projet.model.Record;
import telecom.projet.model.Treatment;
import static telecom.projet.indexes.hpo.HpoOboResearcherForSynonym.searchingSynonymsBySymptom;

import static telecom.projet.indexes.drugbank.DrugbankResearcher.getTreatmentByATC;
import static telecom.projet.indexes.hpo.HpoOboResearcher.searchingDiseaseBySymptom;
import static telecom.projet.indexes.stitch.ChemicalSourcesResearcher.getATCbyCID;

public class Data {
    

    private ArrayList<Record> records = new ArrayList<Record>();

    public Data(String query_symptom, Boolean side_effect) throws IOException {
        this.records.clear();
        this.records = run(query_symptom, side_effect);
    }

    
    public ArrayList<Record> run(String query_symptom, Boolean side_effect) throws IOException {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        ArrayList<Record> records = new ArrayList<>();
        if (query_symptom.contains(" OR ") || query_symptom.contains(" or ")) {
            String[] symptoms = query_symptom.split(" OR ");
            for (String symptom : symptoms) {
                records.addAll(run(symptom, side_effect));
            }
        }else if (query_symptom.contains(" AND ") || query_symptom.contains(" and ")) {

        String[] symptoms = query_symptom.split(" AND ");
        ArrayList<Record> tempRecords = new ArrayList<>();

        for (String symptom : symptoms) {
            ArrayList<String> symptomSynonyms = searchingSynonymsBySymptom(symptom);
            for (String synonym : symptomSynonyms) {
                if (side_effect) {
                    tempRecords.addAll(searchSideEffect(synonym));
                } else {
                    tempRecords.addAll(searchDisease(synonym));
                }
            }
        }

        // Keeping only the common records for AND operation
        for (Record record : new ArrayList<>(tempRecords)) {
            if (Collections.frequency(tempRecords, record) == symptoms.length) {
                records.add(record);
            }
        }


        }else{
            ArrayList<String> symptoms = searchingSynonymsBySymptom(query_symptom);
            for (String symptom : symptoms) {
                System.out.println(symptom);
                if (side_effect) {
                    records.addAll(searchSideEffect(symptom));
                } else {
                    records.addAll(searchDisease(symptom));
                }
            }
        }
        return recordsCleaning(records);
    }

    /**
     * Search the databases for the symptoms
     * @param query the symptoms to search for
     * @return an ArrayList of the records found
     */
    public ArrayList<Record> searchDisease(String query) throws IOException {
        ArrayList<Record> records = new ArrayList<>();
        ArrayList<Disease> diseases_possible = searchingDiseaseBySymptom(query);
//        for(Disease disease: diseases_possible){
//            disease.setScore(getScoreByDisease(disease));
//        }
        for (Disease disease : diseases_possible) {
            ArrayList<String> diseases_names = getIndicationByCUI_meddra_all_indications(disease.getCui_code());
            if (diseases_names.size() == 0) {
                continue;
            }
            String disease_name_meddra = diseases_names.get(0);
            ArrayList<String> cids = getCIDbyCUI_meddra_all_indication(disease.getCui_code());
            for (String cid : cids) {
                ArrayList<String> atc_codes = getATCbyCID(cid);
                ArrayList<Treatment> treatments = getTreatmentByATC(atc_codes);
                for (Treatment treatment : treatments) {
                    Record record = new Record(query, disease_name_meddra, treatment.getName(), "", 0);
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
        ArrayList<String> cids = getCIDbySideEffect_meddra_all_se(query);
        for (String cid : cids) {
            ArrayList<String> atc_codes = getATCbyCID(cid);
            ArrayList<Treatment> treatments = getTreatmentByATC(atc_codes);
            for (Treatment treatment : treatments) {
                Record record = new Record(query, cid, treatment.getName(), "", 0);
                records.add(record);
            }
        }
        return records;
    }

    private ArrayList<Record> recordsCleaning(ArrayList<Record> records) {
        /* Remove the duplicates in the records
         */
        ArrayList<Record> records_cleaned = new ArrayList<>();
        for (Record record : records) {
            if (!records_cleaned.contains(record)) {
                records_cleaned.add(record);
            }
        }
        return records_cleaned;
    }

    public ArrayList<Record> getRecords() {
        return this.records;
    }
}
