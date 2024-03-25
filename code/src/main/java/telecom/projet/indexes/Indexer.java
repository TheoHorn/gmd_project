package telecom.projet.indexes;

import telecom.projet.indexes.drugbank.DrugbankIndexer;
import telecom.projet.indexes.sider.MeddraIndexer;
import telecom.projet.indexes.stitch.ChemicalSourcesIndexer;
import telecom.projet.indexes.omim.OmimIndexerCSV;
import telecom.projet.indexes.hpo.HpoOboIndexer;

public class Indexer {

    public static void runAllIndexes() {
        System.out.println("Indexing all data sources");
        System.out.println("Indexing Drugbank...");
        DrugbankIndexer.runIndexing();
        System.out.println("Indexing Chemical Sources... (STITCH)... this may take a while. (around ~15-20 minutes)");
        ChemicalSourcesIndexer.runIndexing();
        System.out.println("Indexing Sider Meddra data...");
        MeddraIndexer.runIndexing();
        System.out.println("Indexing Omim data...");
        OmimIndexerCSV.runIndexing();
        // OmimIndexerTXT.runIndexing();
        System.out.println("Indexing HPO data...");
        HpoOboIndexer.runIndexing();
        System.out.println("All Indexing done");
    }
}
