package telecom.projet;

import java.util.ArrayList;

import telecom.projet.model.Disease;
import telecom.projet.model.Drug;
import telecom.projet.model.Medicine;
import telecom.projet.model.Symptom;

public class Data {

    private ArrayList<Symptom> symptoms_list = new ArrayList<Symptom>();

    private ArrayList<Disease> diseases = new ArrayList<Disease>();

    private ArrayList<Drug> drugs = new ArrayList<Drug>();

    private ArrayList<Medicine> medicines = new ArrayList<Medicine>();

    

    
    public Data(String symptoms, Boolean side_effect) {
        /* Does all the searching and processing in the databases
         * @param: symptoms: the symptoms to search for
         * @param: side_effect: if true, search for side effects too
         */
        
        //TODO: split, gérer les OU/ET, mettre tout dans symptoms_list

        this.symptoms_list.add(new Symptom(symptoms)); //only one symptom for now

        case1();
        if (side_effect){
            case2();
        }


        //tests affichage
        // Disease d = new Disease("disease1","cui_code");
        // this.symptoms_list.add(new Symptom("symptom1"));
        // drugs.add(new Drug("drug1","database_code","id","database",d));
        // System.out.println("symptoms: "+symptoms_list);
        // System.out.println("diseases: "+diseases);
        // System.out.println("drugs: "+drugs);
        // System.out.println("medicines: "+medicines);
        
        
        
    }


    private void case1() {
        //TODO: search for diseases and treatments
    }

    private void case2() {
        //TODO: search for drugs and treatments
    }



    //getters
    public ArrayList<Symptom> getSymptoms_list() {
        return symptoms_list;
    }

    public ArrayList<Disease> getDiseases() {
        return diseases;
    }

    public ArrayList<Drug> getDrugs() {
        return drugs;
    }

    public ArrayList<Medicine> getMedicines() {
        return medicines;
    }
}
