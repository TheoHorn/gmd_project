package telecom.projet.indexes.sider;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import telecom.projet.model.Disease;
import telecom.projet.model.SideEffect;

public class MeddraResearcher {
    public static void main(String[] args) {
        
        //CID: code for a drug

        // //print the CID associated to a CUI (CUI: code for a side effect)
        // System.out.println(getCIDbyCUI_meddra_all_indication("C0015967"));

        // //print the CID associated to a side effect
        // //System.out.println(getCIDbySideEffect_meddra_all_se("abdominal pain"));

        // //print the CID associated to an indication
        // System.out.println(getCIDbyIndication_meddra_all_indication("abdominal pain"));

        // //print the indication associated to a CUI
        // System.out.println(getIndicationByCUI_meddra_all_indications("C0015967"));

        //print the frequency of apparition of a side effect (cui) fo a drug (cid)
        System.out.println(getFrequencyByCIDAndCUI_meddra_freq("CID100000085", "C0020437"));
        
        System.out.println(getFrequencyByCIDAndCUI_meddra_freq("CID100000444", "C0002418"));
        System.out.println(getMeanFrequency("C0002418", "CID100000444"));
        System.out.println(getMeanFrequency("C0022575", "CID100004634"));
    }


    public static int getMeanFrequency(String cui, String cid){
        int score = 0;
        ArrayList<Double> freqNb = new ArrayList<>();
        ArrayList<String> freq = getFrequencyByCIDAndCUI_meddra_freq(cid, cui);
        for (String f : freq) {
            if (f.contains("%")){
                freqNb.add(Double.parseDouble(f.substring(0, f.length()-1)));
            }
            else if (f.equals("common")){ freqNb.add(25.); }
            else if (f.equals("uncommon")){ freqNb.add(10.); }
            else if (f.equals("rare")){ freqNb.add(5.); }
            else if (f.equals("very rare")){ freqNb.add(3.); }
            else if (f.equals("frequent")){ freqNb.add(25.); }
            else if (f.equals("infrequent")){ freqNb.add(5.); }
            else if (f.equals("postmarketing")){ freqNb.add(0.); }
            }
        //score : mean of the frequencies
        for (double f : freqNb) {
            score += f;
        }
        if (freqNb.size() == 0){
            return 0;
        }
        score = score/freqNb.size();
        return score;
    }



    public static ArrayList<String> getFrequencyByCIDAndCUI_meddra_freq(String cid, String cui){
        /*
         * This method is used to get the frequency of apparition of a side effect (cui) fo a drug (cid)
         * @param cid: the CID to search for
         * @param cui: the CUI to search for
         * @return: list of frequency
         */
        ArrayList<String> frequency = new ArrayList<String>();
        String indexDirectoryPath = "indexes/sider/meddra_freq";
        String type_of_query = "stitch_id";
        String type_of_secondQuery = "umls";
        String type_of_result = "frequency";

        try {
            frequency = doubleSearch(indexDirectoryPath, cid, type_of_query, cui, type_of_secondQuery, type_of_result);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return frequency;
    }


    public static ArrayList<Disease> searchingCuiMeddra (ArrayList<Disease> diseases) throws IOException {
        /*
         * This method is used to get the CUI by disease
         * @param diseases: the list of diseases to search for
         * @return: list of diseases with CUI
         */
        ArrayList<Disease> diseases_with_cui = new ArrayList<Disease>();
        for (Disease disease : diseases) {
            ArrayList<String> cui_founds = search("indexes/sider/meddra",disease.getName(), "symptom", "cui");
            if (!cui_founds.isEmpty()) {
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
        String type_of_query = "umls";
        String type_of_result = "indication";

        try {
            indication = search(indexDirectoryPath, cui, type_of_query, type_of_result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return indication;
        
    }

    public static ArrayList<SideEffect> getSideEffectByString(String string_query){
        try {
            ArrayList<String> result = new ArrayList<String>();
            String indexDirectoryPath = "indexes/sider/meddra_all_se";
            String type_of_query = "side_effect";
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(type_of_query, analyzer);
            Query query = parser.parse(string_query);

            int maxHitsPerPage = 50; // Maximum number of hits per page
            TopDocs results = searcher.search(query, maxHitsPerPage);
            ScoreDoc[] hits = results.scoreDocs;


            ArrayList<SideEffect> se = new ArrayList<SideEffect>();
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                String name = doc.get("side_effect");
                String cui = doc.get("umls");
                String cid = doc.get("stitch_id");
                SideEffect side_effect = new SideEffect();
                side_effect.setName(name);
                side_effect.setCid(cid);
                side_effect.setCui(cui);
                if (!se.contains(side_effect)){
                    se.add(side_effect);
                }
            }
            reader.close();
            return se;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        ArrayList<String> result = new ArrayList<String>();
        try {
            //System.out.println("Searching for: " + querystr);
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(type_of_query, analyzer);
            Query query;
            if (!querystr.isEmpty()){
               query = parser.parse(querystr);
            }else{
                return result;
            }


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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> doubleSearch(String indexDirectoryPath, String firstQuery, String type_of_firstQuery, String secondQuery, String  type_of_secondQuery, String type_of_result) throws IOException {
        try {
            ArrayList<String> result = new ArrayList<String>();
            
            //System.out.println("Searching for: " + querystr);
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            // QueryParser parser = new QueryParser(type_of_query, analyzer);
            // Query query = parser.parse(querystr);
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {type_of_firstQuery, type_of_secondQuery}, analyzer);



            Query query1 = parser.parse(firstQuery);
            Query query2 = parser.parse(secondQuery);

            // Création d'une requête booléenne pour combiner les deux requêtes
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(new BooleanClause(query1, BooleanClause.Occur.MUST));
            builder.add(new BooleanClause(query2, BooleanClause.Occur.MUST));
            


            int maxHitsPerPage = 100; // Maximum number of hits per page
            //TopDocs results = searcher.search(query, maxHitsPerPage);
            TopDocs results = searcher.search(builder.build(), maxHitsPerPage);

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
