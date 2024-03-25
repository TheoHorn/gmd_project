package telecom.projet.indexes.stitch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePartitionerChemical {

    public static void partitionFile(File inputFile, int batchSize, String outputDir) {
        // Create the output directory if it doesn't exist
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int partitionIndex = 1;
            List<String> batchLines = new ArrayList<>();
            //skip the first 9 lines for chemical.sources.tsv
            for (int i = 0; i < 9; i++) {
                reader.readLine();
            }
            while ((line = reader.readLine()) != null) {
                batchLines.add(line);
                if (batchLines.size() >= batchSize) {
                    writePartitionFile(batchLines, partitionIndex++, outputDir);
                    batchLines.clear();
                }
            }
            // Write remaining lines to the last partition
            if (!batchLines.isEmpty()) {
                writePartitionFile(batchLines, partitionIndex, outputDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writePartitionFile(List<String> lines, int partitionIndex, String outputDir) {
        String partitionFileName = outputDir + "/partition_" + partitionIndex + ".tsv";
        File partitionFile = new File(partitionFileName);
        // Delete the file if it already exists
        if (partitionFile.exists()) {
            partitionFile.delete();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(partitionFileName))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File inputFile = new File("data/STITCH - ATC/chemical.sources.V5.0.tsv");
        int batchSize = 500000; // Set your desired batch size
        String outputDir = "data/STITCH - ATC/"; // Set your desired output directory
        partitionFile(inputFile, batchSize, outputDir);
    }
}
