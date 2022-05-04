module de.dargmuesli.spotitag {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.desktop;
    requires java.logging;


    opens de.dargmuesli.spotitag to javafx.fxml;
    exports de.dargmuesli.spotitag;
}