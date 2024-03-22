package telecom.projet.indexes;

import telecom.projet.indexes.drugbank.DrugbankIndexer;
import telecom.projet.indexes.stitch.ChemicalSourcesIndexer;

public class Indexer {

    public static void runAllIndexes() {
        System.out.println("Indexing all data sources");
        System.out.println("Indexing Drugbank...");
        DrugbankIndexer.runIndexing();
        System.out.println("Indexing Chemical Sources... (STITCH)... this may take a while. (around ~5-10 minutes)");
        ChemicalSourcesIndexer.runIndexing();
    }
}
