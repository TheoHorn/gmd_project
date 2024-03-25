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
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChemicalSourcesIndexer {

    public static Directory indexDirectory;
    static int batchSize = 500000;
    static int documentBatchSize = 20000;

    static int numThreads = 8;

    public static void runIndexing() {
        try {
            indexDirectory = new SimpleFSDirectory(Path.of("indexes/stitch_chemical_sources"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File chemicalTsv = new File("data/STITCH - ATC/chemical.sources.V5.0.tsv");

        // Checks for the file
        if (!chemicalTsv.exists() || !chemicalTsv.canRead() || !chemicalTsv.isFile()) {
            System.out.println("File does not exist or is not readable.");
            System.exit(1);
        }

        String outputDir = "data/STITCH - ATC/chemical_partition";
        System.out.println("Partitioning file");
        FilePartitionerChemical.partitionFile(chemicalTsv, batchSize, outputDir);
        System.out.println("Partitioning done");
        System.out.println("Indexing partitioned files");
        indexPartitionedFiles(outputDir);
        System.out.println("Indexing done");
    }

    private static void indexPartitionedFiles(String outputDir) {
        File partitionDirectory = new File(outputDir);
        File[] partitionFiles = partitionDirectory.listFiles();
        if (partitionFiles != null) {
            try {
                Analyzer analyzer = new StandardAnalyzer();
                IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
                IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
                for (File partitionFile : partitionFiles) {
                    executorService.execute(() -> indexPartitionFile(indexWriter, partitionFile));
                }
                executorService.shutdown();
                while (!executorService.isTerminated()) {
                    // Waiting for all tasks to complete
                }
                System.out.println("Optimizing index ...");
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void indexPartitionFile(IndexWriter writer, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Document> batch = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                Document doc = new Document();
                doc.add(new StringField("cid", fields[0], Field.Store.NO));
                doc.add(new TextField("database", fields[2], Field.Store.YES));
                doc.add(new StringField("database_code", fields[3], Field.Store.YES));
                batch.add(doc);
                if (batch.size() >= documentBatchSize) {
                    writer.addDocuments(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                writer.addDocuments(batch);
            }
            writer.commit();
            batch.clear();
            System.out.println("Indexed " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runIndexing();
    }
}
