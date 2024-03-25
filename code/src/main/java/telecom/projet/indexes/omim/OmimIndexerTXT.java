package telecom.projet.indexes.omim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

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
            String line;
            Document doc = new Document();
            boolean hasFields = false; // Flag to check if the document has any fields
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("*RECORD*")) {
                    // Start of a new document
                    if (hasFields) {
                        writer.addDocument(doc);
                        doc = new Document(); // Reset the document for the next one
                        hasFields = false; // Reset the flag
                    }
                } else if (line.startsWith("*FIELD*")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length == 2) {
                        String fieldName = parts[1];
                        StringBuilder fieldValue = new StringBuilder();
                        // Read lines until the next *FIELD* or *RECORD*
                        while ((line = reader.readLine()) != null && !line.startsWith("*FIELD*") && !line.equals("*RECORD*")) {
                            if (fieldName.equals("NO") || fieldName.equals("TI") || fieldName.equals("CS")) {
                                fieldValue.append(line).append(" ");
                            }
                        }
                        // Add the field to the document
                        if (fieldName.equals("NO") || fieldName.equals("TI") || fieldName.equals("CS")) {
                            switch (fieldName) {
                                case "NO":
                                    doc.add(new StringField("omim_id", fieldValue.toString().trim(), Field.Store.YES));
                                    break;
                                case "TI":
                                    doc.add(new TextField("name", fieldValue.toString().trim(), Field.Store.YES));
                                    break;
                                case "CS":
                                    doc.add(new TextField("symptoms", fieldValue.toString().trim(), Field.Store.YES));
                                    break;
                            }
                            hasFields = true; // Set the flag to indicate that the document has fields
                        }
                    }
                }
            }
            // Add the last document
            if (hasFields) {
                writer.addDocument(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        runIndexing();
    }
}
