package telecom.projet.indexes.hpo;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.SimpleFSDirectory;

import telecom.projet.model.Disease;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;


public class HpoOboResearcher {


    public static ArrayList<Disease> searchingDiseaseBySymptom(String query_symptoms) throws IOException {
        return searchingIndexObo("name", query_symptoms);
    }

    public static ArrayList<Disease> searchingDiseaseBySymptom(ArrayList<String> query_symptoms) throws IOException {
        ArrayList<Disease> diseases = new ArrayList<>();
        for (String query_symptom : query_symptoms) {
            diseases.addAll(searchingIndexObo("name", query_symptom));
        }
        return diseases;
    }
    /**
     * Search the index for a specific field and query
     * @param field_to_research, the field to search in the index
     * @param query, the query to search in the index
     * @return an ArrayList of the diseases found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Disease> searchingIndexObo(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/hpo_obo";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs topDocs;
        TermQuery termQuery = new TermQuery(new Term(field_to_research, query));
        System.out.println("Term: " + termQuery);
        topDocs = searcher.search(termQuery, 10);

        System.out.println("Total hits: " + topDocs.totalHits);

        ArrayList<Disease> diseases = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String cui_code = doc.get("cui_code");
            String hp_id = doc.get("hp_id");
            diseases.add(new Disease(name, cui_code, hp_id));
        }
        return diseases;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<Disease> diseases = searchingIndexObo("name", "Abnormality of the temporomandibular joint");
        //ArrayList<Disease> diseases = searchingIndexObo("name", "Cutaneous myxoma");
        //ArrayList<Disease> diseases = searchingIndexObo("hp_id", "HP:0030431");
        for (Disease Disease : diseases) {
            System.out.println(Disease.getName()+" "+Disease.getCui_code()+" "+Disease.getHp_code());
        }
    }
}

