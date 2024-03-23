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
        String dir_meddra_all_indications = "indexes/sider/meddra_all_indications";
        String dir_meddra_all_se = "indexes/sider/meddra_all_se";
        String dir_meddra_freq = "indexes/sider/meddra_freq";
        String dir_meddra = "indexes/sider/meddra";

        //What we are looking for
        String querystr = "syndrome";
        String type_of_query = "side_effect";  //indication, umls, stitch_id

        try {
            search(dir_meddra, querystr, type_of_query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void search(String indexDirectoryPath, String querystr, String type_of_query) throws IOException {
        try {
            System.out.println("Searching for: " + querystr);
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(type_of_query, analyzer);
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
                //String stitch_id = doc.get("stitch_id");
                String indication = doc.get("side_effect");
                //System.out.println(umls +"\t"+ stitch_id +"\t"+ indication);
                
                System.out.println(umls +"\t"+ indication);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
