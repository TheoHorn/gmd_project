package telecom.projet;

import static telecom.projet.indexes.drugbank.DrugbankResearcher.*;
import static telecom.projet.indexes.hpo.HpoOboResearcher.*;
import static telecom.projet.indexes.sider.MeddraResearcher.*;
import static telecom.projet.indexes.stitch.ChemicalSourcesResearcher.*;

import java.io.IOException;
import java.util.ArrayList;

import telecom.projet.model.Disease;
import telecom.projet.model.Record;
import telecom.projet.model.Treatment;

public class Data {
    

    private ArrayList<Record> records = new ArrayList<Record>();
    
    public Data(String symptoms, Boolean side_effect) throws IOException {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        
        //TODO: split, gérer les OU/ET, mettre tout dans symptoms_list
        this.records = searchDisease(symptoms);
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
        for (Disease disease : diseases_possible) {
            //TODO : get the name disease by medra
            String disease_name_meddra = getIndicationByCUI_meddra_all_indications(disease.getCui_code()).get(0);
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
    public ArrayList<Record> searchSideEffect(String query) {
        ArrayList<Record> records = new ArrayList<>();

        return records;
    }

    public ArrayList<Record> getRecords() {
        return this.records;
    }
}
