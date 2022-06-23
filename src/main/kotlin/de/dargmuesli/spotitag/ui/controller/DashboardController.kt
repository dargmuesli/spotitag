package de.dargmuesli.spotitag.ui.controller

import com.mpatric.mp3agic.EncodedText
import com.mpatric.mp3agic.ID3v23Tag
import com.mpatric.mp3agic.Mp3File
import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.model.music.Track
import de.dargmuesli.spotitag.persistence.cache.providers.FileSystemCache
import de.dargmuesli.spotitag.persistence.cache.providers.SpotifyCache
import de.dargmuesli.spotitag.persistence.config.providers.FileSystemConfig
import de.dargmuesli.spotitag.persistence.state.providers.FileSystemState
import de.dargmuesli.spotitag.provider.FileSystemProvider
import de.dargmuesli.spotitag.provider.SpotifyProvider
import de.dargmuesli.spotitag.ui.SpotitagNotification
import de.dargmuesli.spotitag.ui.SpotitagStage
import de.dargmuesli.spotitag.util.ID3v2TXXXFrameData
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
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
            Pair(FileSystemState.filesFound, filesFoundLabel),
            Pair(FileSystemState.filesFoundWithSpotifyId, filesFoundWithSpotifyIdLabel),
            Pair(FileSystemState.filesFoundWithSpotitagVersion, filesFoundWithSpotitagVersionLabel),
            Pair(
                FileSystemState.filesFoundWithSpotitagVersionNewest,
                filesFoundWithSpotitagVersionNewestLabel
            )
        ).forEach {
            it.first.addListener { _ ->
                launch(Dispatchers.JavaFx) {
                    it.second.text = it.first.value.toString()
                }
            }
        }
    }

    @FXML
    fun menuFileSettingsAction() {
        SpotitagStage(
            "/de/dargmuesli/spotitag/fxml/settings.fxml",
            Modality.WINDOW_MODAL,
            MainApp.resources.getString("settings")
        ).show()
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
        FileSystemConfig.sourceDirectory?.let {
            val file = File(it)

            if (!file.exists()) {
                SpotitagNotification.error("Path does not exist!")
                return@let
            }

            if (!file.isDirectory) {
                SpotitagNotification.error("Path is not a directory!")
                return@let
            }

//            launch(Dispatchers.IO) {
            scanFiles(file = file, fileCountTotal = countFiles(file))
//            }
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

    private fun scanFiles(file: File, fileCountCurrent: Int = 0, fileCountTotal: Int? = null) {
        SpotifyProvider.authorize()

        val listOfFiles = file.listFiles()

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

            if (fileCountTotal != null) {
                progressBar.progress = ((fileCountCurrent + index + 1) / fileCountTotal).toDouble()
            }
        }
    }
}