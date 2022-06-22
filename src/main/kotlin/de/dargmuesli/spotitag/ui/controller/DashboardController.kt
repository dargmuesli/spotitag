package de.dargmuesli.spotitag.ui.controller

import com.mpatric.mp3agic.EncodedText
import com.mpatric.mp3agic.ID3v23Tag
import com.mpatric.mp3agic.Mp3File
import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.model.music.Track
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.state.SpotitagState
import de.dargmuesli.spotitag.persistence.state.settings.SpotitagSettings
import de.dargmuesli.spotitag.ui.SpotitagStage
import de.dargmuesli.spotitag.util.ID3v2TXXXFrameData
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
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
import java.io.File


class DashboardController : CoroutineScope {

    override val coroutineContext: JavaFxDispatcher
        get() = Dispatchers.JavaFx

    companion object {
        const val IS_SYNCHRONIZED = true
    }

    private val directoryChooser = DirectoryChooser()

    @FXML
    private lateinit var directoryTextField: TextField

    @FXML
    private lateinit var isSubdirectoryIncludedCheckBox: CheckBox

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
        launch(Dispatchers.IO) {
            SpotitagSettings.fileSystem.sourceDirectory?.let {
                val x = scanFiles(File(it))

                val filesFound = x[0]
                val filesFoundWithSpotifyId = x[1]
                val filesFoundWithSpotitagVersion = x[2]
                val filesFoundWithSpotitagVersionNewest = x[3]

                launch(Dispatchers.JavaFx) {
                    println("filesFound $filesFound")
                    filesFoundLabel.text = filesFound.toString()
                    println("filesFoundWithSpotifyId $filesFoundWithSpotifyId")
                    filesFoundWithSpotifyIdLabel.text = filesFoundWithSpotifyId.toString()
                    println("filesFoundWithSpotitagVersion $filesFoundWithSpotitagVersion")
                    filesFoundWithSpotitagVersionLabel.text = filesFoundWithSpotitagVersion.toString()
                    println("filesFoundWithNewestSpotitagVersion $filesFoundWithSpotitagVersionNewest")
                    filesFoundWithSpotitagVersionNewestLabel.text = filesFoundWithSpotitagVersionNewest.toString()
                }
            }
        }
    }

    private fun scanFiles(folder: File): IntArray {
        val logger = LogManager.getLogger()
        val listOfFiles = folder.listFiles()

        var filesFound = 0
        var filesFoundWithSpotifyId = 0
        var filesFoundWithSpotitagVersion = 0
        var filesFoundWithSpotitagVersionNewest = 0

        listOfFiles?.let {
            for (file in listOfFiles) {
                if (file.isFile) {
                    println("File " + file.name)

                    filesFound++

                    val mp3File = Mp3File(file)

                    if (!mp3File.hasId3v1Tag()) {
                        logger.warn("File \"${mp3File.filename}\" does not have Id3v1 Tag!")
                    }

                    if (!mp3File.hasId3v2Tag()) {
                        mp3File.id3v2Tag = ID3v23Tag()
                    }

                    val id3v2Tag = mp3File.id3v2Tag

                    if (id3v2Tag.version != "4.0") {
                        logger.warn("File \"${mp3File.filename}\" does not have the preferred Id3v2 version! (${id3v2Tag.version} instead of 4.0)")
                    }

                    val version: EncodedText? = ID3v2TXXXFrameData.extract(
                        id3v2Tag.frameSets,
                        IS_SYNCHRONIZED,
                        "Version"
                    )?.value

                    SpotitagState.data.fileSystemData.trackData[file.absolutePath] =
                        MusicFile(
                            Track(
                                album = Album(
                                    artists = listOf(Artist(name = id3v2Tag.albumArtist)),
                                    name = id3v2Tag.album
                                ),
                                artists = listOf(Artist(name = id3v2Tag.artist)),
                                durationMs = mp3File.lengthInMilliseconds,
                                id = id3v2Tag.audioSourceUrl,
                                name = id3v2Tag.title
                            ),
                            version?.toString()
                        )

                    if (version != null) {
                        filesFoundWithSpotitagVersion++

                        if (version.toString() == Persistence.getVersion()) {
                            filesFoundWithSpotitagVersionNewest++
                        }
                    }

                    if (id3v2Tag.audioSourceUrl !== null) {
                        filesFoundWithSpotifyId++
                    }
                } else if (file.isDirectory) {
                    println("Directory " + file.name)
                }
            }
        }

        return IntArray(4)
    }
}