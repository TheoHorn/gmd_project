package telecom.projet.indexes.drugbank;

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


public class DrugbankResearcher {

    /**
     * Search the index for a specific field and query
     * @param field_to_research, the field to search in the index
     * @param query, the query to search in the index
     * @return an ArrayList of the drugs found
     * @throws IOException if there is an error while reading the index
     */
    public static ArrayList<Drug> searchingIndex(String field_to_research, String query) throws IOException {
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

        ArrayList<Drug> drugs = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String dbk_id = doc.get("id");
            String database_code = doc.get("atc_code");
            Drug drug = new Drug();
            drug.setName(name);
            drug.setDatabase_code(database_code);
            drug.setDrugbank_id(dbk_id);
            drug.setDatabase("ATC");
            drugs.add(drug);
        }
        return drugs;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<Drug> drugs = searchingIndex("indication", "compensated liver Drug");
        for (Drug Drug : drugs) {
            System.out.println(Drug.getName()+" "+Drug.getDatabase_code()+" "+Drug.getDrugbank_id());
        }
    }
}
