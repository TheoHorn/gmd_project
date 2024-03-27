package telecom.projet;

import static telecom.projet.indexes.sider.MeddraResearcher.*;


import java.io.IOException;
import java.util.ArrayList;


import telecom.projet.model.Disease;
import telecom.projet.model.Record;
import telecom.projet.model.Symptom;
import telecom.projet.model.Treatment;
import static telecom.projet.indexes.hpo.HpoOboResearcherForSynonym.searchingSynonymsBySymptom;

import static telecom.projet.indexes.drugbank.DrugbankResearcher.getTreatmentByATC;
import static telecom.projet.indexes.hpo.HpoOboResearcher.searchingDiseaseBySymptom;
import static telecom.projet.indexes.sider.MeddraResearcher.getCIDbyCUI_meddra_all_indication;
import static telecom.projet.indexes.sider.MeddraResearcher.getCIDbySideEffect_meddra_all_se;
import static telecom.projet.indexes.stitch.ChemicalSourcesResearcher.getATCbyCID;

public class Data {
    

    private ArrayList<Record> records = new ArrayList<Record>();
    
    public Data(String query_symptom, Boolean side_effect) throws IOException {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        this.records.clear();
        if (query_symptom.contains(" OR ") || query_symptom.contains(" AND ")) {
            System.out.println("OR and AND are not supported yet");
            return;
        }else{
            ArrayList<Symptom> symptoms = searchingSynonymsBySymptom(query_symptom);
            for (Symptom symptom : symptoms) {
                if (side_effect) {
                    this.records.addAll(searchSideEffect(symptom.getName()));
                } else {
                    this.records.addAll(searchDisease(symptom.getName()));
                }
            }
        }
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

    public ArrayList<Record> getRecords() {
        return this.records;
    }
}
