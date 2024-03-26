package telecom.projet;

import java.util.ArrayList;

import telecom.projet.model.Record;

public class Data {
    

    private ArrayList<Record> records = new ArrayList<Record>();
    
    public Data(String symptoms, Boolean side_effect) {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        
        //TODO: split, gérer les OU/ET, mettre tout dans symptoms_list

        search();
        

        //tests affichage
        // Disease d = new Disease("disease1","cui_code");
        // this.symptoms_list.add(new Symptom("symptom1"));
        // drugs.add(new Drug("drug1","database_code","id","database",d));
        // System.out.println("symptoms: "+symptoms_list);
        // System.out.println("diseases: "+diseases);
        // System.out.println("drugs: "+drugs);
        // System.out.println("medicines: "+medicines);
        
        //tests affichage

        Record record1 = new Record("symptom","disease", "treatment","hpo", 3);
        Record record2 = new Record("mal de tete", "angine", "doliprane", "doctolib", 0);
        this.records.add(record1);
        this.records.add(record2);
    }
    public void search() {
        //TODO: main function
    }

    public ArrayList<Record> getRecords() {
        return this.records;
    }
}
