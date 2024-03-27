package telecom.projet.indexes.omim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

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

public class OmimIndexerTXT {
    public static Directory indexDirectory;

    public static void runIndexing() {
        try {
            indexDirectory = new SimpleFSDirectory(Path.of("indexes/omimtxt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File omimTxt = new File("data/OMIM/omim.txt");

        // Checks for the file
        if (!omimTxt.exists() || !omimTxt.canRead() || !omimTxt.isFile()) {
            System.out.println("File does not exist or is not readable.");
            System.exit(1);
        }

        // Start indexing
        try{
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);
            System.out.println("Indexing to directory '" + indexDirectory);

            indexDoc(indexWriter, omimTxt);
            System.out.println("Indexing done");

            indexWriter.commit();
            indexWriter.close();
        }catch(Exception e){
            System.out.println("Caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }

    }


    private static void indexDoc(IndexWriter writer, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            Document doc = new Document();
            String omim_id = null;
            String name = null;
            String symptoms = null;
            while (line != null){
                if (line.startsWith("*RECORD*")) {
                    if (omim_id != null && name != null && symptoms != null){
                        doc.add(new StringField("omim_id", omim_id, Field.Store.YES));
                        doc.add(new TextField("name", name.toLowerCase(), Field.Store.YES));
                        doc.add(new TextField("symptoms", symptoms.trim().toLowerCase(), Field.Store.YES));
                        writer.addDocument(doc);
                    }
                    omim_id = null;
                    name = null;
                    symptoms = null;
                    doc = new Document();
                    line = reader.readLine();
                } else if (line.startsWith("*FIELD* NO")) {
                    line = reader.readLine();
                    omim_id = line;
                } else if (line.startsWith("*FIELD* TI")) {
                    line = reader.readLine();
                    String[] parts = line.split(";;");
                    int spaceIndex = parts[0].indexOf(' ');
                    if (spaceIndex != -1) { // If space is found
                        // Extract the substring after the first space
                        parts[0] = parts[0].substring(spaceIndex + 1);
                    }
                    name = parts[0];
                } else if (line.startsWith("*FIELD* CS")) {
                    while ((line = reader.readLine()) != null && !line.startsWith("*FIELD*")) {
                        if (symptoms == null) {
                            symptoms = line;
                        } else {
                            symptoms += " " + line;
                        }
                    }
                }else{
                    line = reader.readLine();
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        runIndexing();
    }
}
