package telecom.projet.indexes.sider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MeddraIndexer {
    
    public static void main(String[] args) {
        
        String indexDirectoryPath ="indexes/sider";
        String tsvFilePath = "data/SIDER/meddra_all_indications.tsv";
        
        indexTSV(tsvFilePath, indexDirectoryPath);
    }

public static void indexTSV(String tsvFilePath, String indexDirectoryPath) {
        try {
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

                // Create a new document and add the fields
                Document doc = new Document();

                //for meddra_all_inidications.tsv specifically

                //[0]: stitch_id (C........)
                //[1]: umls/CID
                //[3]: indications
                StringField stitch_id, umls;
                TextField indication;

                if ("PT".equals(fields[4])){
                    stitch_id = new StringField("stitch_id", fields[0], Field.Store.YES);
                    umls = new StringField("umls", fields[1], Field.Store.YES);
                    indication = new TextField("indication", fields[3], Field.Store.YES);
    
                    doc.add(stitch_id);
                    doc.add(umls);
                    doc.add(indication);

                    writer.addDocument(doc);
                }

            }

            writer.close();
            reader.close();
            System.out.println("Indexing of "+ tsvFilePath +" done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
