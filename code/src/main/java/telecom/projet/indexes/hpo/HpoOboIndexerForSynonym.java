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

public class HpoOboIndexerForSynonym {

    public static Directory index_directory;
    static int batchSize = 10000;

    /**
     * Run the indexing of the Hpo.obo file
     */
    public static void runIndexing(){
        try {
            index_directory = new SimpleFSDirectory(Path.of("indexes/hpo_obo_syn"));
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

        ArrayList<String> synonyms = new ArrayList<String>();
        String hp_id = null;
        String name = null;
        String cui_code = null;
        String synonym ;
        StringField idField, nameField, cuiCodeField;
        
        while ((line = reader.readLine()) != null) {

            if (line.isEmpty()) {

                idField = new StringField("hp_id", hp_id, Field.Store.YES);
                nameField = new StringField("name", name, Field.Store.YES);
                cuiCodeField = new StringField("cui_code", cui_code, Field.Store.YES);
                for (String synonymOf : synonyms) {
                    Document doc = new Document();        
                    doc.add(idField);
                    doc.add(nameField);
                    doc.add(cuiCodeField);
                    doc.add(new StringField("synonym", synonymOf, Field.Store.YES));
                    try {
                        writer.addDocument(doc);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                synonyms.clear();

            } else if (line.startsWith("id:")) {
                hp_id = line.substring(4).trim();             
            } else if (line.startsWith("name:")) {
                name = line.substring(6).trim(); 
            } else if (line.startsWith("xref: UMLS:")) {
                cui_code = line.substring(11).trim();
            } else if (line.startsWith("synonym:")) {
                
                String trimmedLine = line.substring(9).trim();
                int startIndex = trimmedLine.indexOf('"');
                int endIndex = trimmedLine.indexOf('"', startIndex + 1);
                String result = "";
                if (startIndex != -1 && endIndex != -1) {
                    result = trimmedLine.substring(startIndex + 1, endIndex);
                    //System.out.println("String between quotes: " + result);
                }
                synonym = result;
                synonyms.add(synonym);
            }
            count++;
        }
        writer.commit();
        writer.close();
        reader.close();
    }

    public static void main(String[] args) {
        runIndexing();
    }
}



