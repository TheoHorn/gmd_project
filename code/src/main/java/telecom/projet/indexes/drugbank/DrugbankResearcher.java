package telecom.projet.indexes.drugbank;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.SimpleFSDirectory;
import telecom.projet.model.Disease;
import telecom.projet.model.Drug;
import telecom.projet.model.Treatment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;


public class DrugbankResearcher {


    /**
     * Get the disease by the symptoms
     * @param query_symptoms the symptoms to search for
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Disease> getDiseaseBySymptom(String query_symptoms) throws IOException {
        return searchingForDisease("indication", query_symptoms);
    }

    /**
     * Get the disease by the symptoms
     * @param query_symptoms the symptoms to search for
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Disease> getDiseaseBySymptom(ArrayList<String> query_symptoms) throws IOException {
        ArrayList<Disease> diseases = new ArrayList<>();
        for (String query_symptom : query_symptoms) {
            diseases.addAll(searchingForDisease("indication", query_symptom));
        }
        return diseases;
    }

    /**
     * Get the treatment by the atc code
     * @param query_atc_code the atc code to search for
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Treatment> getTreatmentByATC(String query_atc_code) throws IOException {
        return searchingForTreatment("atc_code", query_atc_code);
    }


    /**
     * Get the treatment by the atc codes
     * @param query_atc_codes the atc codes to search for
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Treatment> getTreatmentByATC(ArrayList<String> query_atc_codes) throws IOException {
        ArrayList<Treatment> treatments = new ArrayList<>();
        for (String query_atc_code : query_atc_codes) {
            treatments.addAll(searchingForTreatment("atc_code", query_atc_code));
        }
        return treatments;
    }


    /**
     * Search the index for a disease in a specific field and query
     * @param field_to_research the field to search in the index
     * @param query the query to search in the index
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Disease> searchingForDisease(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/drugbank";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs topDocs;
        if (query.contains(" ")) {
            String[] words = query.split(" ");
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            for (String word : words) {
                builder.add(new Term(field_to_research, word));
            }
            PhraseQuery phrase_query = builder.build();
            Term[] terms = phrase_query.getTerms();
            for (Term term : terms) {
                System.out.println("Term: " + term.text());
            }
            topDocs = searcher.search(phrase_query, 10);
        }else{
            TermQuery term_query = new TermQuery(new Term(field_to_research, query));
            topDocs = searcher.search(term_query, 10);
        }

        System.out.println("Total hits: " + topDocs.totalHits);

        ArrayList<Disease> diseases = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String dbk_id = doc.get("id");
            String database_code = doc.get("atc_code");
            //TODO : add the field to disease class
        }
        return diseases;
    }

    /**
     * Search the index for a treatment in a specific field and query
     * @param field_to_research, the field to search in the index
     * @param query, the query to search in the index
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Treatment> searchingForTreatment(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/drugbank";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        //BooleanQuery.Builder bool_query = new BooleanQuery.Builder(); -> will be useful for multiple fields
        //PhraseQuery phrase_query = new PhraseQuery(); -> will be useful for multiple words in query
        //TopDocsCollector, TopScoreDocCollector -> will be useful for sorting ? 
        TopDocs topDocs;
        if (query.contains(" ")) {
            String[] words = query.split(" ");
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            for (String word : words) {
                builder.add(new Term(field_to_research, word));
            }
            PhraseQuery phrase_query = builder.build();
            Term[] terms = phrase_query.getTerms();
            for (Term term : terms) {
                System.out.println("Term: " + term.text());
            }
            topDocs = searcher.search(phrase_query, 10);
        }else{
            TermQuery term_query = new TermQuery(new Term(field_to_research, query));
            topDocs = searcher.search(term_query, 10);
        }

        System.out.println("Total hits: " + topDocs.totalHits);

        ArrayList<Treatment> treatments = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String dbk_id = doc.get("id");
            String database_code = doc.get("atc_code");
            //TODO : add the field to treatment class
        }
        return treatments;
    }

    public static void main(String[] args) throws IOException {

    }
}
