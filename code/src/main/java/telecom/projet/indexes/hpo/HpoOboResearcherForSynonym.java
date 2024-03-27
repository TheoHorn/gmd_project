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


public class HpoOboResearcherForSynonym {

    public static ArrayList<Symptom> searchingSynonymsBySymptom(String query_symptoms) throws IOException {
        return searchingIndexObo("name", query_symptoms);
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
        //if (query.contains(" ")) {
        //    String[] words = query.split(" ");
        //    BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        //    for (String word : words) {
        //        booleanQueryBuilder.add(new TermQuery(new Term(field_to_research, word)), BooleanClause.Occur.SHOULD);
        //    }
        //    BooleanQuery booleanQuery = booleanQueryBuilder.build();
        //    System.out.println("Term: " + booleanQuery);
        //    topDocs = searcher.search(booleanQuery, 10);
        //} else {
        //    TermQuery termQuery = new TermQuery(new Term(field_to_research, query));
        //    System.out.println("Term: " + termQuery);
        //    topDocs = searcher.search(termQuery, 10);
        //}

        TermQuery termQuery = new TermQuery(new Term(field_to_research, query));
        System.out.println("Term: " + termQuery);
        topDocs = searcher.search(termQuery, 10);

        System.out.println("Total hits: " + topDocs.totalHits);

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
        ArrayList<Symptom> symptoms = searchingIndexObo("name", "Abnormality of the temporomandibular joint");
        //ArrayList<Disease> diseases = searchingIndexObo("name", "Cutaneous myxoma");
        //ArrayList<Disease> diseases = searchingIndexObo("hp_id", "HP:0030431");
        ArrayList<String> synonyms = new ArrayList<>();
        String name = "";
        for (Symptom Symptom : symptoms) {
            synonyms.add(Symptom.getSynonyms().get(0));
            //System.out.println(Symptom.getName()+" "+Symptom.getCui_code()+" "+Symptom.getHp_code());
        }
        System.out.println(name+" "+"synonyms : ");
        for (String synonym : synonyms){
            System.out.println(synonym+" ");
        }
        System.out.println("cui_code : " + symptoms.get(0).getCui_code());
        System.out.println("END");
    }
}

