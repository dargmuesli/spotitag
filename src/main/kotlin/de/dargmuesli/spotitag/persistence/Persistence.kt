package de.dargmuesli.spotitag.persistence

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.persistence.state.SpotitagStateWrapper
import de.dargmuesli.spotitag.ui.SpotitagNotification
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

object Persistence {
    var isInitialized = false

    private val appDataDirectory: Path
        get() {
            val os = System.getProperty("os.name").lowercase()

            return if (os.contains("win")) {
                Paths.get(System.getenv("AppData"), MainApp.APPLICATION_TITLE)
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                Paths.get(System.getProperty("user.home"), ".config", MainApp.APPLICATION_TITLE)
            } else {
                Paths.get("")
            }
        }
    private val jackson: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    private val stateFile = appDataDirectory.resolve("state.json")
    private val versionProperties = Properties()

    init {
        versionProperties.load(this.javaClass.getResourceAsStream("/version.properties"))
    }

    fun getVersion(): String = versionProperties.getProperty("version")

    fun stateLoad() {
        if (Files.exists(stateFile)) {
            try {
                SpotitagStateWrapper.state =
                    jackson.readValue(String(Files.readAllBytes(stateFile)), SpotitagStateWrapper.javaClass).state
            } catch (e: Exception) {
                SpotitagNotification.error("Loading application settings failed!", e)
                exitProcess(0)
            }
        }

        isInitialized = true
    }

    fun stateSave() {
        if (!isInitialized) return

        if (!Files.exists(stateFile.parent)) {
            Files.createDirectories(stateFile.parent)
        }

        Files.writeString(stateFile, jackson.writeValueAsString(SpotitagStateWrapper))
    }

    fun getCacheDirectory(): Path {
        return Paths.get(System.getProperty("java.io.tmpdir"), MainApp.APPLICATION_TITLE)
    }
}
