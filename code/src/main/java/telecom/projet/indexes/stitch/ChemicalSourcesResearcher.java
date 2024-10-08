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

    public static ArrayList<String> getATCbyCID(String query) throws IOException {
        query = "CIDm" + query.substring(4);
        ArrayList<Drug> drugs = searchingIndex("cid", query);
        ArrayList<String> atc_codes = new ArrayList<>();
        for (Drug drug : drugs) {
            if (drug.getDatabase().equals("ATC")){
                atc_codes.add(drug.getDatabase_code());
            }
        }
        return atc_codes;
    }

    public static ArrayList<Drug> searchingIndex(String field_to_research, String query) throws IOException {
        String index_directory = "indexes/stitch_chemical_sources";
        SimpleFSDirectory directory = new SimpleFSDirectory(Path.of(index_directory));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TermQuery term_query = new TermQuery(new Term(field_to_research, query));
        TopDocs topDocs = searcher.search(term_query, 100);

        ArrayList<Drug> drugs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String database_code = doc.get("database_code");
            String database = doc.get("database");
            Drug drug = new Drug(database_code);
            drug.setDatabase(database);
            drugs.add(drug);
        }
        return drugs;
    }

    public static void main(String[] args) {
        try {
            ArrayList<Drug> drugs = searchingIndex("database_code", "B01AE02");
            for (Drug drug : drugs) {
                System.out.println(drug);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
