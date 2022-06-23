package de.dargmuesli.spotitag.persistence

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.ui.SpotitagNotification
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

object Persistence {
    var isInitialized = false

    val configDirectory: Path
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
    val cacheDirectory: Path = Paths.get(System.getProperty("user.home"), ".cache", MainApp.APPLICATION_TITLE)
    val localDirectory: Path = Paths.get(System.getProperty("user.home"), ".local", "share", MainApp.APPLICATION_TITLE)
    val tmpDirectory: Path = Paths.get(System.getProperty("java.io.tmpdir"), MainApp.APPLICATION_TITLE)
    private val fileMap = hashMapOf(
        Pair(cacheDirectory, PersistenceTypes.CACHE),
        Pair(configDirectory, PersistenceTypes.CONFIG),
        Pair(localDirectory, PersistenceTypes.STATE)
    )
    private val jackson: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    private val versionProperties = Properties()

    init {
        versionProperties.load(this.javaClass.getResourceAsStream("/version.properties"))
    }

    fun getVersion(): String = versionProperties.getProperty("version")

    fun load() {
        for ((directory, type) in fileMap) {
            val filePath = directory.resolve("${type.type}.json")
            if (Files.exists(filePath)) {
                try {
                    PersistenceWrapper[type] =
                        jackson.readValue(
                            String(Files.readAllBytes(filePath)),
                            PersistenceWrapper[type].javaClass
                        )
                } catch (e: Exception) {
                    SpotitagNotification.error("Loading application data failed!", e)
                    exitProcess(0)
                }
            }
        }

        isInitialized = true
    }

    fun save() {
        if (!isInitialized) return

        for ((directory, type) in fileMap) {
            val filePath = directory.resolve("${type.type}.json")

            if (!Files.exists(filePath.parent)) {
                Files.createDirectories(filePath.parent)
            }

            Files.writeString(filePath, jackson.writeValueAsString(PersistenceWrapper[type]))
        }
    }
}
