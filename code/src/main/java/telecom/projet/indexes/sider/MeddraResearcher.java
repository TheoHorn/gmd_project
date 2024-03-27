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
import telecom.projet.model.Disease;

public class MeddraResearcher {
    public static void main(String[] args) {
        
        //CID: code for a drug

        //print the CID associated to a CUI (CUI: code for a side effect)
        System.out.println(getCIDbyCUI_meddra_all_indication("C0001860"));

        //print the CID associated to a side effect
        System.out.println(getCIDbySideEffect_meddra_all_se("abdominal pain"));

        //print the CID associated to an indication
        System.out.println(getCIDbyIndication_meddra_all_indication("abdominal pain"));

        //print the indication associated to a CUI
        System.out.println(getIndicationByCUI_meddra_all_indications("C0015967"));

    }

    //public static int getScoreByDisease(String name_disease)

    public static ArrayList<Disease> searchingCuiMeddra (ArrayList<Disease> diseases) throws IOException {
        /*
         * This method is used to get the CUI by disease
         * @param diseases: the list of diseases to search for
         * @return: list of diseases with CUI
         */
        ArrayList<Disease> diseases_with_cui = new ArrayList<Disease>();
        for (Disease disease : diseases) {
            ArrayList<String> cui_founds = search("indexes/sider/meddra",disease.getName(), "symptom", "cui");
            if (cui_founds.size() > 0) {
                disease.setCui_code(cui_founds.get(0));
                diseases_with_cui.add(disease);
            }
        }
        return diseases_with_cui;
    }
    public static ArrayList<String> getIndicationByCUI_meddra_all_indications(String cui){
        /*
         * This method is used to get the indication by CUI
         * @param cui: the CUI to search for
         * @return: list of indication
         */
        ArrayList<String> indication = new ArrayList<String>();
        String indexDirectoryPath = "indexes/sider/meddra_all_indications";
        String type_of_query = "umls";//cid = UMLS
        String type_of_result = "indication";

        try {
            indication = search(indexDirectoryPath, cui, type_of_query, type_of_result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return indication;
        
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
        ArrayList<String> cid = new ArrayList<String>();
        String type_of_result = "stitch_id";//cid = stitch_id

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

            int maxHitsPerPage = 1000; // Maximum number of hits per page
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
