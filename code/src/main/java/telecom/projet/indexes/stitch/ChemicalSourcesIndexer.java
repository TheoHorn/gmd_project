package telecom.projet.indexes.stitch;

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

public class ChemicalSourcesIndexer {

    public static Directory index_directory;
    static int batchSize = 10000;

    /**
     * Run the indexing of the chemical.sources.tsv file
     */
    public static void runIndexing(){
        try {
            index_directory = new SimpleFSDirectory(Path.of("indexes/stitch_chemical_sources"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File chemical_tsv = new File("data/STITCH - ATC/chemical.sources.V5.0.tsv");

        // Checks for the file
        if (!chemical_tsv.exists() || !chemical_tsv.canRead() || !chemical_tsv.isFile()) {
            System.out.println("File does not exist or is not readable.");
            System.exit(1);
        }

        // Start indexing
        try{
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(index_directory, iwc);
            System.out.println("Indexing to directory '" + index_directory);

            indexDoc(indexWriter, chemical_tsv);
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
        // Skip the first 9 lines
        for (int i = 0; i < 9; i++) {
            reader.readLine();
        }
        int count = 0;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split("\t");

            Document doc = new Document();

            doc.add(new StringField("cid_1", fields[0], Field.Store.YES));
            //doc.add(new TextField("cid_2", fields[1], Field.Store.NO));
            doc.add(new TextField("database", fields[2], Field.Store.YES));
            doc.add(new StringField("database_code", fields[3], Field.Store.YES));
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


