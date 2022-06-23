package de.dargmuesli.spotitag.ui.controller

import com.mpatric.mp3agic.EncodedText
import com.mpatric.mp3agic.ID3v23Tag
import com.mpatric.mp3agic.Mp3File
import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.model.music.Track
import de.dargmuesli.spotitag.persistence.state.SpotitagState
import de.dargmuesli.spotitag.persistence.state.data.SpotitagData
import de.dargmuesli.spotitag.persistence.state.settings.SpotitagSettings
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
        directoryTextField.text = SpotitagSettings.fileSystem.sourceDirectory
        isSubdirectoryIncludedCheckBox.isSelected = SpotitagSettings.fileSystem.isSubDirectoryIncluded ?: false

        arrayOf(
            Pair(SpotitagData.fileSystemData.filesFound, filesFoundLabel),
            Pair(SpotitagData.fileSystemData.filesFoundWithSpotifyId, filesFoundWithSpotifyIdLabel),
            Pair(SpotitagData.fileSystemData.filesFoundWithSpotitagVersion, filesFoundWithSpotitagVersionLabel),
            Pair(
                SpotitagData.fileSystemData.filesFoundWithSpotitagVersionNewest,
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
        SpotitagSettings.fileSystem.sourceDirectory?.let {
            val sourceDirectory = File(it)
            directoryChooser.initialDirectory = if (sourceDirectory.exists()) sourceDirectory else null
        }

        val file: File = directoryChooser.showDialog((actionEvent.source as Node).scene.window as Stage)

        directoryTextField.text = file.absolutePath
        SpotitagSettings.fileSystem.sourceDirectory = file.absolutePath
    }

    @FXML
    fun onDirectoryInput() {
        SpotitagSettings.fileSystem.sourceDirectory = directoryTextField.text
    }

    @FXML
    fun onSubdirectoryInclusionToggle() {
        SpotitagSettings.fileSystem.isSubDirectoryIncluded = isSubdirectoryIncludedCheckBox.isSelected
    }

    @FXML
    fun onScan() {
        SpotitagSettings.fileSystem.sourceDirectory?.let {
            val file = File(it)

            if (!file.exists()) {
                SpotitagNotification.error("Path does not exist!")
                return@let
            }

            if (!file.isDirectory) {
                SpotitagNotification.error("Path is not a directory!")
                return@let
            }

            launch(Dispatchers.IO) {
                scanFiles(file = file, fileCountTotal = countFiles(file))
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
                } else if (currentFile.isDirectory && SpotitagSettings.fileSystem.isSubDirectoryIncluded == true) {
                    count += countFiles(currentFile)
                }
            }
        }

        return count
    }

    private fun scanFiles(file: File, fileCountCurrent: Int = 0, fileCountTotal: Int? = null) {
        val listOfFiles = file.listFiles()

        listOfFiles?.forEachIndexed { index, currentFile ->
//            for (currentFile in listOfFiles) {
            if (currentFile.isFile && currentFile.extension == "mp3") {
                println("File " + currentFile.name)

                val mp3File = Mp3File(currentFile)

                if (!mp3File.hasId3v1Tag()) {
                    LOGGER.warn("File \"${mp3File.filename}\" does not have Id3v1 Tag!")
                }

                if (!mp3File.hasId3v2Tag()) {
                    mp3File.id3v2Tag = ID3v23Tag()
                }

                val id3v2Tag = mp3File.id3v2Tag

                if (id3v2Tag.version != "4.0") {
                    LOGGER.warn("File \"${mp3File.filename}\" does not have the preferred Id3v2 version! (${id3v2Tag.version} instead of 4.0)")
                }

                val version: EncodedText? = ID3v2TXXXFrameData.extract(
                    id3v2Tag.frameSets,
                    IS_SYNCHRONIZED,
                    "Version"
                )?.value

                SpotitagState.data.fileSystemData.trackData[currentFile.absolutePath] =
                    MusicFile(
                        Track(
                            album = Album(
                                artists = id3v2Tag.albumArtist?.let { listOf(Artist(name = id3v2Tag.albumArtist)) },
                                name = id3v2Tag.album
                            ),
                            artists = id3v2Tag.artist?.let { listOf(Artist(name = id3v2Tag.artist)) },
                            durationMs = mp3File.lengthInMilliseconds,
                            id = id3v2Tag.audioSourceUrl,
                            name = id3v2Tag.title
                        ),
                        version?.toString()
                    )
            } else if (currentFile.isDirectory && SpotitagSettings.fileSystem.isSubDirectoryIncluded == true) {
                scanFiles(currentFile, fileCountCurrent + index + 1, fileCountTotal)
            }

            if (fileCountTotal != null) {
                progressBar.progress = ((fileCountCurrent + index + 1) / fileCountTotal).toDouble()
            }
        }
    }
}