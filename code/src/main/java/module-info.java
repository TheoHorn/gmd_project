module telecom.projet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    //requires lucene.analyzers.common;
    requires lucene.core;
    requires lucene.queryparser;
    requires lucene.queries;
    requires org.xerial.sqlitejdbc;
    requires transitive javafx.graphics;
    

    opens telecom.projet to javafx.fxml;
    exports telecom.projet;
}
