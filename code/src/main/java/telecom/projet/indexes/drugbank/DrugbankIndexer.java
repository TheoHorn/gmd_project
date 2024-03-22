package telecom.projet.indexes.drugbank;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
public class DrugbankIndexer {

    // Index directory
    public static Directory index_directory;

    /**
     * Run the indexing of the drugbank.xml file
     */
    public static void runIndexing(){
        try {
            index_directory = new SimpleFSDirectory(Path.of("indexes/drugbank"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File dbkxml = new File("data/DRUGBANK/drugbank.xml");

        // Checks for the file

        if (!dbkxml.exists() || !dbkxml.canRead() || !dbkxml.isFile()) {
            System.out.println("File does not exist or is not readable.");
            System.exit(1);
        }

        // Start indexing
        try{
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(index_directory, iwc);
            System.out.println("Indexing to directory '" + index_directory);

            indexDoc(indexWriter, dbkxml);
            System.out.println("Indexing done");

            indexWriter.commit();
            indexWriter.close();
        }catch(Exception e){
            System.out.println("Caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    private static void indexDoc(IndexWriter writer, File file) throws Exception {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            // Variables to store the drug data
            DefaultHandler handler = new DefaultHandler() {

                StringBuilder data = new StringBuilder();

                String id,name,toxicity,indication,state,pharmacodynamics;

                TextField nameField, toxicityField, indicationField, stateField, pharmacodynamicsField;
                StringField idField;

                ArrayList<String> atc_codes = new ArrayList<String>();

                boolean inDrug, isPrimary = false;

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("drug")) {
                        data.setLength(0);
                        inDrug = true;
                    }else if (qName.equalsIgnoreCase("drugbank-id")) {
                        String primaryAttribute = attributes.getValue("primary");
                        if (primaryAttribute != null && primaryAttribute.equalsIgnoreCase("true")) {
                            isPrimary = true;
                        }
                    }else if (qName.equalsIgnoreCase("atc-code")) {
                        String code = attributes.getValue("code");
                        atc_codes.add(code);
                    }
                    else if (qName.equalsIgnoreCase("products")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("mixtures")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("packagers")){
                        inDrug = false;
                    }else if(qName.equalsIgnoreCase("drug-interactions")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("international-brands")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("targets")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("pfams")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("polypeptide")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("enzyme")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("reaction")){
                        inDrug = false;
                    }else if (qName.equalsIgnoreCase("pathways")){
                        inDrug = false;
                    }
                }
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equalsIgnoreCase("drug")) {
                        idField = new StringField("id", id, Field.Store.YES);
                        nameField = new TextField("name", name, Field.Store.YES);
                        toxicityField = new TextField("toxicity", toxicity, Field.Store.NO);
                        indicationField = new TextField("indication", indication, Field.Store.NO);
                        stateField = new TextField("state", state, Field.Store.NO);
                        pharmacodynamicsField = new TextField("pharmacodynamics", pharmacodynamics, Field.Store.NO);
                        for (String atc_code : atc_codes) {
                            Document luceneDoc = new Document();
                            luceneDoc.add(idField);
                            luceneDoc.add(nameField);
                            luceneDoc.add(toxicityField);
                            luceneDoc.add(indicationField);
                            luceneDoc.add(stateField);
                            luceneDoc.add(pharmacodynamicsField);
                            luceneDoc.add(new StringField("atc_code", atc_code, Field.Store.YES));
                            try {
                                writer.addDocument(luceneDoc);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        atc_codes.clear();
                    } else if (inDrug) {
                        if (qName.equalsIgnoreCase("name")) {
                            name = data.toString();
                        } else if (isPrimary && qName.equalsIgnoreCase("drugbank-id")) {
                            id = data.toString();
                            isPrimary = false;
                        } else if (qName.equalsIgnoreCase("toxicity")) {
                            toxicity = data.toString();
                        } else if (qName.equalsIgnoreCase("indication")) {
                            indication = data.toString();
                        } else if (qName.equalsIgnoreCase("state")) {
                            state = data.toString();
                        }else if (qName.equalsIgnoreCase("pharmacodynamics")) {
                            pharmacodynamics = data.toString();
                        }
                        data.setLength(0);
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    data.append(ch, start, length);
                }
            };

            saxParser.parse(file, handler);

        } catch (Exception e) {
            System.out.println("Caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        runIndexing();
    }
}
