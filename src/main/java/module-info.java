module de.dargmuesli.spotitag {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens de.dargmuesli.spotitag to javafx.fxml;
    exports de.dargmuesli.spotitag;
}