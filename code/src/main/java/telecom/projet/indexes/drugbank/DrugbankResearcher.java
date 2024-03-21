package telecom.projet.indexes.drugbank;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class DrugbankResearcher {

    public static void searchingIndex(String field_to_research, String query) throws IOException {
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

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Name: " + doc.get("name")+ " - " + doc.get("id"));
        }
    }

    public static void main(String[] args) throws IOException {
        searchingIndex("atc_code", "A16AX02");
    }
}
