package telecom.projet;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.transformation.SortedList;
import telecom.projet.model.Disease;
import telecom.projet.model.Drug;
import telecom.projet.model.Record;
import telecom.projet.model.Treatment;
import telecom.projet.sqliteQuestioner.HpoApp;

import static telecom.projet.indexes.drugbank.DrugbankResearcher.getTreatmentByATC;
import static telecom.projet.indexes.hpo.HpoOboResearcher.searchingDiseaseBySymptom;
import static telecom.projet.indexes.sider.MeddraResearcher.getCIDbyCUI_meddra_all_indication;
import static telecom.projet.indexes.sider.MeddraResearcher.getCIDbySideEffect_meddra_all_se;
import static telecom.projet.indexes.stitch.ChemicalSourcesResearcher.getATCbyCID;

public class Data {
    

    private ArrayList<Record> records = new ArrayList<Record>();
    
    public Data(String symptoms, Boolean side_effect) throws IOException {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        
        //TODO: split, gérer les OU/ET, mettre tout dans symptoms_list
        if (side_effect) {
            this.records = searchSideEffect(symptoms);
        } else {
            this.records = searchDisease(symptoms);
        }
//        Record record1 = new Record("symptom","disease", "treatment","hpo", 3);
//        Record record2 = new Record("mal de tete", "angine", "doliprane", "doctolib", 0);
//        this.records.add(record1);
//        this.records.add(record2);
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
            //TODO : get the name disease by medra
            ArrayList<String> cids = getCIDbyCUI_meddra_all_indication(disease.getCui_code());
            for (String cid : cids) {
                ArrayList<String> atc_codes = getATCbyCID(cid);
                ArrayList<Treatment> treatments = getTreatmentByATC(atc_codes);
                for (Treatment treatment : treatments) {
                    Record record = new Record(query, cid, treatment.getName(), "", 0);
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
