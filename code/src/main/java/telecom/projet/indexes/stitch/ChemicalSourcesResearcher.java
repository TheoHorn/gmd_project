package telecom.projet.indexes.stitch;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.SimpleFSDirectory;
import telecom.projet.model.Drug;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ChemicalSourcesResearcher {

    public static ArrayList<Drug> searchingIndex(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/stitch_chemical_sources";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TermQuery term_query = new TermQuery(new Term(field_to_research, query));
        TopDocs topDocs = searcher.search(term_query, 10);


        System.out.println("Total hits: " + topDocs.totalHits);

        ArrayList<Drug> drugs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String atc_code = doc.get("atc_code");
            drugs.add(new Drug(atc_code));
        }
        return drugs;
    }
}
