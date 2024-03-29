package telecom.projet.indexes.hpo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HpoOboIndexer {

    public static Directory index_directory;
    static int batchSize = 10000;

    /**
     * Run the indexing of the Hpo.obo file
     */
    public static void runIndexing(){
        try {
            index_directory = new SimpleFSDirectory(Path.of("indexes/hpo_obo"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File hpo_obo = new File("data/HPO/hpo.obo");

        // Checks for the file
        if (!hpo_obo.exists() || !hpo_obo.canRead() || !hpo_obo.isFile()) {
            System.out.println("File does not exist or is not readable.");
            System.exit(1);
        }

        // Start indexing
        try{
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(index_directory, iwc);
            System.out.println("Indexing to directory '" + index_directory);

            indexDoc(indexWriter, hpo_obo);
            System.out.println("Indexing done");

            indexWriter.commit();
            indexWriter.close();
        }catch(Exception e){
            System.out.println("Caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    private static void indexDoc(IndexWriter writer, File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        // Skip the first 28 lines
        for (int i = 0; i < 28; i++) {
            reader.readLine();
        }
        int count = 0;
        //Document doc = new Document();

        ArrayList<String> cui_codes = new ArrayList<String>();
        String hp_id = null;
        String name = null;
        String cui_code ;
        StringField idField, nameField;
        
        while ((line = reader.readLine()) != null) {

            if (line.isEmpty()) {

                idField = new StringField("hp_id", hp_id, Field.Store.YES);
                name = name.replace("/", " ");
                nameField = new StringField("name", name.toLowerCase(), Field.Store.YES);
                for (String cuiCode : cui_codes) {
                    Document doc = new Document();        
                    doc.add(idField);
                    doc.add(nameField);
                    doc.add(new StringField("cui_code", cuiCode, Field.Store.YES));
                    try {
                        writer.addDocument(doc);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                cui_codes.clear();

            } else if (line.startsWith("id:")) {
                hp_id = line.substring(4).trim();             
            } else if (line.startsWith("name:")) {
                name = line.substring(6).trim(); 
            } else if (line.startsWith("xref: UMLS:")) {
                cui_code = line.substring(11).trim();
                cui_codes.add(cui_code); 
            }
            count++;
        }
        writer.commit();
        reader.close();
    }

    public static void main(String[] args) {
        runIndexing();
    }
}



