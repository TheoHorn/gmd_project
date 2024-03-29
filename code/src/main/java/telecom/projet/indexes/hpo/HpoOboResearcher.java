package telecom.projet.indexes.hpo;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.SimpleFSDirectory;

import telecom.projet.model.Disease;
import telecom.projet.model.Symptom;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;


public class HpoOboResearcher {


    public static ArrayList<Symptom> searchingSymptomByQuery(String query_symptoms) throws IOException {
        return searchingIndexOboRegex("name", query_symptoms);
    }

    public static ArrayList<Symptom> searchingSymptomByQuery(ArrayList<String> query_symptoms) throws IOException {
        ArrayList<Symptom> diseases = new ArrayList<>();
        for (String query_symptom : query_symptoms) {
            diseases.addAll(searchingIndexOboRegex("name", query_symptom));
        }
        return diseases;
    }

    //public static ArrayList<Symptom> searchingSymptomByQueryRegex(String query_regex){
    //    return searchingIndexObo("name", query_regex);
    //}

    
    /**
     * Search the index for a specific field and query with regexp
     * @param field_to_research, the field to search in the index
     * @param query, the query to search in the index
     * @return an ArrayList of the diseases found
     * @throws IOException if there is an error while reading the index
     */

    public static ArrayList<Symptom> searchingIndexOboRegex(String field_to_research, String regex) throws IOException {
        String index_directory = "indexes/hpo_obo";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
    
        RegexpQuery query = new RegexpQuery(new Term(field_to_research, regex));
    
        TopDocs topDocs = searcher.search(query, 10000);
    
        ArrayList<Symptom> symptoms = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String cui_code = doc.get("cui_code");
            String hp_id = doc.get("hp_id");
            Symptom symptom = new Symptom();
            symptom.setCui_code(cui_code);
            symptom.setHp_code(hp_id);
            symptom.setName(name);
            symptoms.add(symptom);
        }
        return symptoms;
    }

    /**
     * Search the index for a specific field and query
     * @param field_to_research, the field to search in the index
     * @param query, the query to search in the index
     * @return an ArrayList of the diseases found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Symptom> searchingIndexObo(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/hpo_obo";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs topDocs;
        TermQuery termQuery = new TermQuery(new Term(field_to_research, query));
        topDocs = searcher.search(termQuery, 10000);
        


        ArrayList<Symptom> symptoms = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String cui_code = doc.get("cui_code");
            String hp_id = doc.get("hp_id");
            Symptom symptom = new Symptom();
            symptom.setCui_code(cui_code);
            symptom.setHp_code(hp_id);
            symptom.setName(name);
            System.out.println("Adding a Symptom.....");
            symptoms.add(symptom);
        }
        System.out.println(symptoms.size());
        return symptoms;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<Symptom> ss = searchingIndexOboRegex("name", "leukoencephalopathy");
        System.out.println("Size of symptoms : ");
        System.out.println(ss.size());
        //ArrayList<Disease> diseases = searchingIndexObo("name", "Cutaneous myxoma");
        //ArrayList<Disease> diseases = searchingIndexObo("hp_id", "HP:0030431");
        ArrayList<Symptom> ss2 = searchingIndexOboRegex("name", ".*pain.*");
        ArrayList<Symptom> ss3 = searchingIndexOboRegex("name", "[a-z]{4}");
        for (Symptom symptom : ss) {
            System.out.println(symptom.getName());
        }
    }
}

