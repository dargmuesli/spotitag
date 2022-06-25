package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import de.dargmuesli.spotitag.persistence.cache.FileSystemCache
import de.dargmuesli.spotitag.persistence.cache.SpotifyCache
import de.dargmuesli.spotitag.persistence.config.FileSystemConfig
import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.provider.FileSystemProvider
import de.dargmuesli.spotitag.provider.SpotifyProvider
import de.dargmuesli.spotitag.ui.SpotitagNotification
import de.dargmuesli.spotitag.ui.SpotitagStage
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.stage.DirectoryChooser
import javafx.stage.Modality
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.JavaFxDispatcher
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File


class DashboardController : CoroutineScope {

    override val coroutineContext: JavaFxDispatcher
        get() = Dispatchers.JavaFx

    companion object {
        const val IS_SYNCHRONIZED = true
        val LOGGER: Logger = LogManager.getLogger()
    }

    private val directoryChooser = DirectoryChooser()

    @FXML
    private lateinit var container: ScrollPane

    @FXML
    private lateinit var directoryTextField: TextField

    @FXML
    private lateinit var isSubdirectoryIncludedCheckBox: CheckBox

    @FXML
    private lateinit var progressBar: ProgressBar

    @FXML
    private lateinit var filesFoundLabel: Label

    @FXML
    private lateinit var filesFoundWithSpotifyIdLabel: Label

    @FXML
    private lateinit var filesFoundWithSpotitagVersionLabel: Label

    @FXML
    private lateinit var filesFoundWithSpotitagVersionNewestLabel: Label

    @FXML
    fun initialize() {
        directoryTextField.text = FileSystemConfig.sourceDirectory
        isSubdirectoryIncludedCheckBox.isSelected = FileSystemConfig.isSubDirectoryIncluded ?: false

        arrayOf(
            FileSystemState.filesFound to filesFoundLabel,
            FileSystemState.filesFoundWithSpotifyId to filesFoundWithSpotifyIdLabel,
            FileSystemState.filesFoundWithSpotitagVersion to filesFoundWithSpotitagVersionLabel,
            FileSystemState.filesFoundWithSpotitagVersionNewest to filesFoundWithSpotitagVersionNewestLabel
        ).forEach {
            it.first.addListener { _ ->
                launch(Dispatchers.JavaFx) {
                    it.second.text = it.first.value.toString()
                }
            }
        }

        Persistence.isInitialized.addListener { x ->
            container.isDisable = !Persistence.isInitialized.value
        }
    }

    @FXML
    fun onClickSettings() {
        SpotitagStage(
            "/de/dargmuesli/spotitag/fxml/settings.fxml",
            Modality.WINDOW_MODAL,
            MainApp.resources.getString("settings")
        ).show()
    }

    @FXML
    fun onClickClearCache() {
        launch(Dispatchers.IO) {
            FileSystemCache.trackData.clear()
            SpotifyCache.trackData.clear()
            Persistence.save(PersistenceTypes.CACHE, PersistenceTypes.STATE)
        }
    }

    @FXML
    fun chooseDirectory(actionEvent: ActionEvent) {
        FileSystemConfig.sourceDirectory?.let {
            val sourceDirectory = File(it)
            directoryChooser.initialDirectory = if (sourceDirectory.exists()) sourceDirectory else null
        }

        val file: File = directoryChooser.showDialog((actionEvent.source as Node).scene.window as Stage)

        directoryTextField.text = file.absolutePath
        FileSystemConfig.sourceDirectory = file.absolutePath
    }

    @FXML
    fun onDirectoryInput() {
        FileSystemConfig.sourceDirectory = directoryTextField.text
    }

    @FXML
    fun onSubdirectoryInclusionToggle() {
        FileSystemConfig.isSubDirectoryIncluded = isSubdirectoryIncludedCheckBox.isSelected
    }

    @FXML
    fun onScan() {
        launch(Dispatchers.IO) {
            FileSystemConfig.sourceDirectory?.let {
                val directory = File(it)

                if (!directory.exists()) {
                    SpotitagNotification.error("Path does not exist!")
                    return@let
                }

                if (!directory.isDirectory) {
                    SpotitagNotification.error("Path is not a directory!")
                    return@let
                }

                SpotifyProvider.authorize()

                scanFiles(directory = directory, fileCountTotal = countFiles(directory))
            }
        }
    }

    private fun countFiles(file: File): Int {
        var count = 0

        val listOfFiles = file.listFiles()

        listOfFiles?.let {
            for (currentFile in listOfFiles) {
                if (currentFile.isFile && currentFile.extension == "mp3") {
                    count++
                } else if (currentFile.isDirectory && FileSystemConfig.isSubDirectoryIncluded == true) {
                    count += countFiles(currentFile)
                }
            }
        }

        return count
    }

    private fun scanFiles(directory: File, fileCountCurrent: Int = 0, fileCountTotal: Int? = null) {
        val listOfFiles = directory.listFiles()

        listOfFiles?.forEachIndexed { index, currentFile ->
            if (currentFile.isFile && currentFile.extension == "mp3") {
                val text = "file \"${currentFile.name}\""

                if (FileSystemCache.trackData.containsKey(currentFile.absolutePath)) {
                    LOGGER.debug("Skipping $text as it's cached.")
                    return@forEachIndexed
                } else {
                    LOGGER.info("Processing $text...")
                }

                val musicFile = FileSystemProvider.getMusicFile(currentFile).also {
                    FileSystemCache.trackData[it.path] = it
                }

                SpotifyProvider.getSpotifyTrack(musicFile)?.also {
                    SpotifyCache.trackData[it.id] = it
                }
            } else if (currentFile.isDirectory && FileSystemConfig.isSubDirectoryIncluded == true) {
                scanFiles(currentFile, fileCountCurrent + index + 1, fileCountTotal)
            }

            if (fileCountTotal != null && fileCountTotal != 0) {
                progressBar.progress = ((fileCountCurrent + index + 1) / fileCountTotal).toDouble()
            }
        }

        Persistence.save(PersistenceTypes.CACHE, PersistenceTypes.STATE)
    }
}