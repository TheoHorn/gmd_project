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
            Document doc = null;
            boolean insideRecord = false;
            String fieldName = null;
            StringBuilder fieldValue = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("*RECORD*")) {
                    // Start of a new document
                    if (doc != null) {
                        addFieldToDoc(doc, fieldName, fieldValue);
                        writer.addDocument(doc);
                    }
                    doc = new Document();
                    insideRecord = true;
                    fieldName = null;
                    fieldValue.setLength(0);
                } else if (insideRecord && line.startsWith("*FIELD*")) {
                    // Start of a new field
                    if (fieldName != null) {
                        // If we have previously started reading a field, add it to the document
                        addFieldToDoc(doc, fieldName, fieldValue);
                    }
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length == 2) {
                        fieldName = parts[1];
                        fieldValue.setLength(0);
                    }
                } else if (insideRecord) {
                    // If we are inside a record and not at the start of a new field or record, append to the field value
                    fieldValue.append(line).append(" ");
                }
            }

            // Add the last document
            if (doc != null) {
                addFieldToDoc(doc, fieldName, fieldValue);
                writer.addDocument(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFieldToDoc(Document doc, String fieldName, StringBuilder fieldValue) {
        if (fieldName != null && fieldValue.length() > 0) {
            // Add the field to the document
            if (fieldName.equals("NO")) {
                doc.add(new StringField("omim_id", fieldValue.toString().trim(), Field.Store.YES));
            } else if (fieldName.equals("TI")) {
                doc.add(new TextField("name", fieldValue.toString().trim(), Field.Store.YES));
            } else if (fieldName.equals("CS")) {
                doc.add(new TextField("symptoms", fieldValue.toString().trim(), Field.Store.YES));
            }
        }
    }

    public static void main(String[] args) {
        runIndexing();
    }
}
