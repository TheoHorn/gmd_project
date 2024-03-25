package telecom.projet.indexes.omim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import telecom.projet.indexes.stitch.FilePartitionerChemical;

public class OmimIndexerCSV {
    
    public static Directory indexDirectory;

    public static void runIndexing() {
        try {
            indexDirectory = new SimpleFSDirectory(Path.of("indexes/omimcsv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File omimCsv = new File("data/OMIM/omim_onto.csv");

        // Checks for the file
        if (!omimCsv.exists() || !omimCsv.canRead() || !omimCsv.isFile()) {
            System.out.println("File does not exist or is not readable.");
            System.exit(1);
        }

         // Start indexing
         try{
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);
            System.out.println("Indexing to directory '" + indexDirectory);

            indexDoc(indexWriter, omimCsv);
            System.out.println("Indexing done");

            indexWriter.commit();
            indexWriter.close();
        }catch(Exception e){
            System.out.println("Caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
       
    }


    private static void indexDoc(IndexWriter writer, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                Document doc = new Document();
                String[] parts = fields[0].split("/");
                fields[0] = parts[parts.length - 1];
                doc.add(new StringField("omim_id", fields[0], Field.Store.YES));
                doc.add(new TextField("name", fields[1], Field.Store.YES));
                if (!fields[2].isEmpty()){
                    doc.add(new TextField("synonyms", fields[2], Field.Store.NO));
                }
                if (fields.length > 5 && !fields[5].isEmpty()){
                    doc.add(new StringField("cui",fields[5],Field.Store.YES));
                }
                writer.addDocument(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(ArrayIndexOutOfBoundsException e){

            e.printStackTrace();
    
        }
    }


    public static void main(String[] args) {
        runIndexing();
    }
}
