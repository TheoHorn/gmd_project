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
import java.util.HashSet;
import java.util.Set;


public class HpoOboResearcherForSynonym {

    public static ArrayList<String> searchingSynonymsBySymptom(String query_symptoms) throws IOException {
        ArrayList<Symptom> symptoms = searchingIndexObo("synonym", query_symptoms);
        Set<String> uniqueSynonyms = new HashSet<>();

        for (Symptom symptom : symptoms) {
            ArrayList<Symptom> s = searchingIndexObo("name", symptom.getName());
            for (Symptom s1 : s) {
                uniqueSynonyms.addAll(s1.getSynonyms());
            }

            uniqueSynonyms.add(symptom.getName());
            uniqueSynonyms.addAll(symptom.getSynonyms());
        }
        return new ArrayList<>(uniqueSynonyms);
    }


    /**
     * Search the index for a specific field and query
     * @param field_to_research, the field to search in the index
     * @param query, the query to search in the index
     * @return an ArrayList of the diseases found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Symptom> searchingIndexObo(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/hpo_obo_syn";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        //BooleanQuery.Builder bool_query = new BooleanQuery.Builder(); -> will be useful for multiple fields
        //PhraseQuery phrase_query = new PhraseQuery(); -> will be useful for multiple words in query
        //TopDocsCollector, TopScoreDocCollector -> will be useful for sorting ?

        TopDocs topDocs;

        TermQuery termQuery = new TermQuery(new Term(field_to_research, query));
        topDocs = searcher.search(termQuery, 10);


        ArrayList<Symptom> symptoms = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String cui_code = doc.get("cui_code");
            String hp_id = doc.get("hp_id");
            String synonym = doc.get("synonym");
            symptoms.add(new Symptom(name, synonym, cui_code, hp_id));
        }

        return symptoms;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(searchingSynonymsBySymptom("fever"));
    }
}

