module de.dargmuesli.spotitag {
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;
    requires kotlinx.coroutines.javafx;
    requires mp3agic;
    requires org.apache.logging.log4j;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.javafx;
    requires se.michaelthelin.spotify;

    opens de.dargmuesli.spotitag.persistence.state to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state.data to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state.data.providers.spotify to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state.settings to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state.settings.file_system to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state.settings.spotify to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.ui.controller to javafx.fxml;

    exports de.dargmuesli.spotitag;
}