package telecom.projet.indexes.omim;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.SimpleFSDirectory;

import telecom.projet.model.Disease;

public class OmimResearcherCSV {
    public static ArrayList<Disease> searchingIndex(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/omimcsv";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TermQuery term_query = new TermQuery(new Term(field_to_research, query));
        TopDocs topDocs = searcher.search(term_query, 10);

        ArrayList<Disease> diseases = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String omim_id = doc.get("omim_id");
            String name = doc.get("name");
            String cui_code = doc.get("cui_code");
            Disease disease = new Disease(cui_code);
            disease.setName(name);
            disease.setOmim_code(omim_id);
            diseases.add(disease);
        }
        return diseases;
    }

    public static void main(String[] args) {
        try {
            ArrayList<Disease> Diseases = searchingIndex("cui", "C1412749");
            for (Disease Disease : Diseases) {
                System.out.println(Disease);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
