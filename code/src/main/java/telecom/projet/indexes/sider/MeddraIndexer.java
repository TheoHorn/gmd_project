package telecom.projet.indexes.sider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MeddraIndexer {
    /*
     * This class is used to index the SIDER database
     */
    
    public static void runIndexing() {
        
        String tsvFilePath = "data/SIDER/meddra_all_indications.tsv";
        indexTSV(tsvFilePath);

        tsvFilePath = "data/SIDER/meddra_all_se.tsv";
        indexTSV(tsvFilePath);

        tsvFilePath = "data/SIDER/meddra_freq.tsv";
        indexTSV(tsvFilePath);

        tsvFilePath = "data/SIDER/meddra.tsv";
        indexTSV(tsvFilePath);
    }

    public static void indexTSV(String tsvFilePath) {
    /*
     * This method is used to index a TSV file
     * 
     */
        try {
            String indexDirectoryPath = "";
            switch (tsvFilePath) {
                case "data/SIDER/meddra_all_indications.tsv":
                    indexDirectoryPath ="indexes/sider/meddra_all_indications";
                    break;
                case "data/SIDER/meddra_all_se.tsv":
                    indexDirectoryPath ="indexes/sider/meddra_all_se";
                    break;
                case "data/SIDER/meddra_freq.tsv":
                    indexDirectoryPath ="indexes/sider/meddra_freq";
                    break;
                case "data/SIDER/meddra.tsv":
                    indexDirectoryPath ="indexes/sider/meddra";
                    break;
                default:
                    break;
            }

            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(indexDirectory, config);

            File tsvFile = new File(tsvFilePath);

            // Checks for the file
            if (!tsvFile.exists() || !tsvFile.canRead() || !tsvFile.isFile()) {
                System.out.println("File does not exist or is not readable.");
                System.exit(1);
            }

            BufferedReader reader = new BufferedReader(new FileReader(tsvFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t"); // Use "\t" as separator
                Document doc;
                // Create a new document and add the fields
                switch(tsvFilePath){
                    case "data/SIDER/meddra_all_indications.tsv":
                        doc = meddra_all_indications(writer, fields);
                        writer.addDocument(doc);
                        break;
                    case "data/SIDER/meddra_all_se.tsv":
                        doc = meddra_all_se(writer, fields);
                        writer.addDocument(doc);
                        break;
                    case "data/SIDER/meddra_freq.tsv":
                        doc = meddra_freq(writer, fields);
                        writer.addDocument(doc);
                        break;
                    case "data/SIDER/meddra.tsv":
                        doc = meddra(writer, fields);
                        writer.addDocument(doc);
                        break;
                    default:
                        break;
                }
                
            }

            writer.close();
            reader.close();
            System.out.println("Indexing of "+ tsvFilePath +" done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Document meddra_all_indications(IndexWriter writer, String[] fields) {
        //for meddra_all_inidications.tsv
    
        Document doc = new Document();
        //[0]: stitch_id (C........)
        //[1]: umls/CID
        //[3]: indications
        TextField stitch_id, umls;
        TextField indication;

        if ("LLT".equals(fields[4])){
            stitch_id = new TextField("stitch_id", fields[0], Field.Store.YES);
            umls = new TextField("umls", fields[1], Field.Store.YES);
            indication = new TextField("indication", fields[3].toLowerCase(), Field.Store.YES);

            doc.add(stitch_id);
            doc.add(umls);
            doc.add(indication);
        }
        return doc;
    }

    private static Document meddra_all_se(IndexWriter writer, String[] fields) {
        //for meddra_all_se.tsv
    
        Document doc = new Document();
        //[0]: stitch_id (C........)
        //[2]: umls/CID
        //[5]: side effect
        TextField stitch_id, umls;
        TextField side_effect;

        if ("LLT".equals(fields[3])){
            stitch_id = new TextField("stitch_id", fields[0], Field.Store.YES);
            umls = new TextField("umls", fields[2], Field.Store.YES);
            side_effect = new TextField("side_effect", fields[5], Field.Store.YES);

            doc.add(stitch_id);
            doc.add(umls);
            doc.add(side_effect);
        }
        return doc;
    }

    private static Document meddra_freq(IndexWriter writer, String[] fields) {
        //for meddra_freq.tsv
    
        Document doc = new Document();
        //[0]: stitch_id (C........)
        //[2]: umls/CID
        //[4]: frequency
        //[9]: side effect
        TextField stitch_id, umls, side_effect, frequency;

        if ("LLT".equals(fields[7]) && !"placebo".equals(fields[3])){
            stitch_id = new TextField("stitch_id", fields[0], Field.Store.YES);
            umls = new TextField("umls", fields[2], Field.Store.YES);
            frequency = new TextField("frequency", fields[4], Field.Store.YES);
            side_effect = new TextField("side_effect", fields[9], Field.Store.YES);

            doc.add(stitch_id);
            doc.add(umls);
            doc.add(frequency);
            doc.add(side_effect);
        }
        return doc;
    }

    private static Document meddra(IndexWriter writer, String[] fields) {
        //for meddra.tsv
    
        Document doc = new Document();
        //[0]: stitch_id (C........)
        //[3]: side effect
        TextField umls, side_effect;

        if ("LT".equals(fields[1])){
            umls = new TextField("cui", fields[0], Field.Store.YES);
            side_effect = new TextField("symptom", fields[3], Field.Store.YES);
            doc.add(umls);
            doc.add(side_effect);
        }
        return doc;
    }

    public static void main(String[] args) {
        runIndexing();
    }
}
