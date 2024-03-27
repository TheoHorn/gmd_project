package telecom.projet;

import static telecom.projet.indexes.hpo.HpoOboResearcher.searchingSymptomByQuery;

import static telecom.projet.indexes.omim.OmimResearcherCSV.searchingCuiOmim;
import static telecom.projet.indexes.omim.OmimResearcherTXT.searchingDiseaseBySymptom;
import static telecom.projet.indexes.sider.MeddraResearcher.*;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;


import telecom.projet.model.Disease;
import telecom.projet.model.Record;
import telecom.projet.model.Symptom;
import telecom.projet.model.Treatment;
import static telecom.projet.indexes.hpo.HpoOboResearcherForSynonym.searchingSynonymsBySymptom;

import static telecom.projet.indexes.drugbank.DrugbankResearcher.getTreatmentByATC;
import static telecom.projet.indexes.sider.MeddraResearcher.getCIDbyCUI_meddra_all_indication;
import static telecom.projet.indexes.sider.MeddraResearcher.getCIDbySideEffect_meddra_all_se;
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
        query_symptom = query_symptom.toLowerCase();
        ArrayList<Record> records = new ArrayList<>();
        if (query_symptom.contains(" AND ")) {
            String[] symptoms = query_symptom.split(" AND ");
            records.addAll(run(symptoms[0], side_effect));
            for (int i = 1; i < symptoms.length; i++) {
                records.retainAll(run(symptoms[i], side_effect));
            }
        }else if (query_symptom.contains(" OR ")) {
            String[] symptoms = query_symptom.split(" OR ");
            for (String symptom : symptoms) {
                records.addAll(run(symptom, side_effect));
            }
        }else{
            ArrayList<String> symptoms = searchingSynonymsBySymptom(query_symptom);
            for (String symptom : symptoms) {
                if (side_effect) {
                    records.addAll(searchSideEffect(symptom.toLowerCase()));
                } else {
                    records.addAll(searchDisease(symptom.toLowerCase()));
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
        ArrayList<Symptom> possible_symptoms = searchingSymptomByQuery(query);
        ArrayList<Disease> diseases_possible = searchingDiseaseBySymptom(possible_symptoms);
        ArrayList<Disease> diseases_searchable = searchingCuiOmim(diseases_possible);
        diseases_searchable.addAll(searchingCuiMeddra(diseases_possible));
        diseases_possible.addAll(symptomsToDisease(possible_symptoms));
        for (Disease disease : diseases_possible) {
            ArrayList<String> cids = getCIDbyCUI_meddra_all_indication(disease.getCui_code());
            for (String cid : cids) {
                ArrayList<String> atc_codes = getATCbyCID(cid);
                ArrayList<Treatment> treatments = getTreatmentByATC(atc_codes);
                for (Treatment treatment : treatments) {
                    Record record = new Record(query, disease.getName(), treatment.getName(), "", 0);
                    records.add(record);
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
            diseases.add(disease);
        }
        return diseases;
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
