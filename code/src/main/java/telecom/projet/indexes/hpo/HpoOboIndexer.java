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
        while ((line = reader.readLine()) != null) {
           
            Document doc = new Document();

            if (line.startsWith("[Term]")) {
                writer.addDocument(doc);
                doc = new Document();
            } else if (line.startsWith("id:")) {
                doc.add(new StringField("hp_id", line.substring(4).trim(), Field.Store.YES));
            } else if (line.startsWith("name:")) {
                doc.add(new TextField("name", line.substring(6).trim(), Field.Store.YES));
            } else if (line.startsWith("xref:")) {
                doc.add(new StringField("cui_code", line.substring(5).trim(), Field.Store.YES));
            }
            writer.addDocument(doc);

            count++;
            if (count % batchSize == 0) {
                writer.commit(); // Commit after each batch
                writer.deleteUnusedFiles(); // Delete unused index files
            }
        }
        writer.commit();
        writer.close();
        reader.close();
    }

    public static void main(String[] args) {
        runIndexing();
    }
}



