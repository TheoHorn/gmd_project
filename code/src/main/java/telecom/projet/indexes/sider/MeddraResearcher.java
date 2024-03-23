package telecom.projet.indexes.sider;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MeddraResearcher {
    public static void main(String[] args) {
        String indexDirectoryPath = "indexes/sider";

        //What you are looking for
        String querystr = "Headache";

        try {
            search(indexDirectoryPath, querystr);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void search(String indexDirectoryPath, String querystr) throws IOException {
        try {
            System.out.println("Searching for: " + querystr);
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("indication", analyzer);
            Query query = parser.parse(querystr);

            int maxHitsPerPage = 100; // Maximum number of hits per page
            TopDocs results = searcher.search(query, maxHitsPerPage);
            ScoreDoc[] hits = results.scoreDocs;
            System.out.println("Number of hits: " + hits.length + "  (Maximum: "+ maxHitsPerPage +")");
            if (hits.length == 0) {
                System.out.println("No results found.");
            }else{
                System.out.println("UMLS\t\tSTITCH ID\tIndication");
            }
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                // Get the value of the field
                String umls = doc.get("umls");
                String stitch_id = doc.get("stitch_id");
                String indication = doc.get("indication");
                
                System.out.println(umls +"\t"+ stitch_id +"\t"+ indication);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
