package telecom.projet.indexes.omim;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.SimpleFSDirectory;

import telecom.projet.model.Disease;
import telecom.projet.model.Symptom;

public class OmimResearcherTXT {

    public static ArrayList<Disease> searchingDiseaseBySymptom(String query) throws IOException {
        return searchingIndex("symptoms", query);
    }

    public static ArrayList<Disease> searchingDiseaseBySymptom(ArrayList<Symptom> symptoms) throws IOException {
        ArrayList<Disease> diseases = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            diseases.addAll(searchingDiseaseBySymptom(symptom.getName()));
        }
        return diseases;
    }

    public static ArrayList<Disease> searchingIndex(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/omimtxt";
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
            topDocs = searcher.search(phrase_query, 10);
        }else{
            TermQuery term_query = new TermQuery(new Term(field_to_research, query));
            topDocs = searcher.search(term_query, 10);
        }

        ArrayList<Disease> diseases = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String omim_id = doc.get("omim_id");
            String name = doc.get("name");
            Disease disease = new Disease();
            disease.setName(name);
            disease.setOmim_code(omim_id);
            diseases.add(disease);
        }
        return diseases;
    }

    public static void main(String[] args) {
        try {
            ArrayList<Disease> Diseases = searchingIndex("symptoms", "Fever");
            for (Disease Disease : Diseases) {
                System.out.println(Disease);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
