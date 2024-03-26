package telecom.projet.indexes.sider;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

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
        
        //CID: code for a drug

        //print the CID associated to a CUI (CUI: code for a side effect)
        System.out.println(getCIDbyCUI_meddra_all_indication("C0001860"));

        //print the CID associated to a side effect
        System.out.println(getCIDbySideEffect_meddra_all_se("abdominal pain"));

        //print the CID associated to an indication
        System.out.println(getCIDbyIndication_meddra_all_indication("abdominal pain"));

    }

    public static ArrayList<String> getCIDbySideEffect_meddra_all_se(String query){
        /*
         * This method is used to get the CID by side effect
         * @param se: the side effect to search for
         * @return: list of CID
         */
        String indexDirectoryPath = "indexes/sider/meddra_all_se";
        String type_of_query = "side_effect";
        //results
        ArrayList<String> cid = new ArrayList<String>(); //cid = UMLS
        String type_of_result = "umls";//cid = UMLS

        System.out.println("Searching for CID associated to side effect " + query);
        try {
            cid = search(indexDirectoryPath, query, type_of_query, type_of_result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cid;
    }

    public static ArrayList<String> getCIDbyIndication_meddra_all_indication(String query){
        /*
         * This method is used to get the CID by indication
         * @param indication: the indication to search for
         * @return: list of CID
         */
        String indexDirectoryPath = "indexes/sider/meddra_all_indications";
        String type_of_query = "indication";
        //results
        ArrayList<String> cid = new ArrayList<String>(); //cid = UMLS
        String type_of_result = "umls";//cid = UMLS

        System.out.println("Searching for CID associated to indication " + query);
        try {
            cid = search(indexDirectoryPath, query, type_of_query, type_of_result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cid;
    }


    public static ArrayList<String> getCIDbyCUI_meddra_all_indication(String query){
        /*
         * This method is used to get the CID by CUI
         * @param cui: the CUI to search for
         * @return: list of CID
         */

        ArrayList<String> cid = new ArrayList<String>(); //cid = UMLS
        String indexDirectoryPath = "indexes/sider/meddra_all_indications";
        String type_of_query = "umls";//cid = UMLS
        String type_of_result = "stitch_id";//cui = STITCH ID

        System.out.println("Searching for CID associated to CUI " + query);
        try {
            cid = search(indexDirectoryPath, query, type_of_query, type_of_result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cid;
    }

    public static ArrayList<String> search(String indexDirectoryPath, String querystr, String type_of_query, String type_of_result) throws IOException {
        try {
            ArrayList<String> result = new ArrayList<String>();
            
            //System.out.println("Searching for: " + querystr);
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(type_of_query, analyzer);
            Query query = parser.parse(querystr);

            int maxHitsPerPage = 100; // Maximum number of hits per page
            TopDocs results = searcher.search(query, maxHitsPerPage);
            ScoreDoc[] hits = results.scoreDocs;

            //System.out.println("Number of hits: " + hits.length + "  (Maximum: "+ maxHitsPerPage +")");
            
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                String res = doc.get(type_of_result);// Get the value of the field
                if (!result.contains(res)){
                    result.add(res);
                }    
            }
            reader.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
