package telecom.projet.indexes;

import telecom.projet.indexes.drugbank.DrugbankIndexer;
import telecom.projet.indexes.hpo.HpoOboIndexer;
import telecom.projet.indexes.omim.OmimIndexerTXT;
import telecom.projet.indexes.sider.MeddraIndexer;
import telecom.projet.indexes.stitch.ChemicalSourcesIndexer;
import telecom.projet.indexes.omim.OmimIndexerCSV;
import  java.nio.file.Files;
import  java.nio.file.Paths;

public class Indexer {

    public static void runAllIndexes() {
        if (!Files.exists(Paths.get("indexes"))) {
            System.out.println("Creating indexes directory...");
            System.out.println("Indexing all data sources");
            System.out.println("Indexing Drugbank...");
            DrugbankIndexer.runIndexing();
            System.out.println("Indexing Chemical Sources... (STITCH)... this may take a while. (around ~15-20 minutes)");
            ChemicalSourcesIndexer.runIndexing();
            System.out.println("Indexing Sider Meddra data...");
            MeddraIndexer.runIndexing();
            System.out.println("Indexing Omim data...");
            OmimIndexerCSV.runIndexing();
            OmimIndexerTXT.runIndexing();
            System.out.println("Indexing HPO data...");
            HpoOboIndexer.runIndexing();
            System.out.println("All Indexing done");
        }else{
            System.out.println("Indexes directory already exists, skipping indexing");
        }
    }
}
