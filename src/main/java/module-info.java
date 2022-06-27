module de.dargmuesli.spotitag {
    requires com.google.gson;
    requires java.desktop;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires kotlin.stdlib.jdk7;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;
    requires kotlinx.coroutines.javafx;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires mp3agic;
    requires nv.i18n;
    requires org.apache.commons.text;
    requires org.apache.logging.log4j;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.javafx;
    requires se.michaelthelin.spotify;

    opens de.dargmuesli.spotitag.model.enums to kotlinx.serialization.core;
    opens de.dargmuesli.spotitag.model.filesystem to kotlinx.serialization.core;
    opens de.dargmuesli.spotitag.model.music to kotlinx.serialization.core;
    opens de.dargmuesli.spotitag.persistence to kotlinx.serialization.core;
    opens de.dargmuesli.spotitag.persistence.cache to kotlinx.serialization.core;
    opens de.dargmuesli.spotitag.ui.controller to javafx.fxml;
    opens de.dargmuesli.spotitag.util to kotlinx.serialization.core;

    exports de.dargmuesli.spotitag.persistence.cache;
    exports de.dargmuesli.spotitag.persistence.config;
    exports de.dargmuesli.spotitag.persistence.state;
    exports de.dargmuesli.spotitag.persistence;
    exports de.dargmuesli.spotitag;
}