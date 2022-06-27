package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.MainApp
import de.dargmuesli.spotitag.model.enums.Id3Properties
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import de.dargmuesli.spotitag.persistence.SpotitagConfig
import de.dargmuesli.spotitag.persistence.cache.FileSystemCache
import de.dargmuesli.spotitag.persistence.cache.SpotifyCache
import de.dargmuesli.spotitag.persistence.config.FileSystemConfig
import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.persistence.state.SpotifyState
import de.dargmuesli.spotitag.provider.FileSystemProvider
import de.dargmuesli.spotitag.provider.FileSystemProvider.writeMusicFile
import de.dargmuesli.spotitag.provider.SpotifyProvider
import de.dargmuesli.spotitag.provider.SpotifyProvider.getTrackFromSpotifyTrack
import de.dargmuesli.spotitag.ui.SpotitagNotification
import de.dargmuesli.spotitag.ui.SpotitagStage
import de.dargmuesli.spotitag.util.Util
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ListChangeListener
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.paint.Paint
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
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.abs


class DashboardController : CoroutineScope {
    override val coroutineContext: JavaFxDispatcher
        get() = Dispatchers.JavaFx

    companion object {
        const val IS_SYNCHRONIZED = true

        val RED: Paint = Paint.valueOf("#aa0000")
        val GREEN: Paint = Paint.valueOf("#00aa00")
        val YELLOW: Paint = Paint.valueOf("#aaaa00")

        val LOGGER: Logger = LogManager.getLogger()
    }

    private val fileList = observableArrayList<File>()
    private val fileListIndex: SimpleIntegerProperty = SimpleIntegerProperty(-1)

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
    private lateinit var titleFromLabel: Label

    @FXML
    private lateinit var artistsFromLabel: Label

    @FXML
    private lateinit var albumFromLabel: Label

    @FXML
    private lateinit var idFromLabel: Label

    @FXML
    private lateinit var coverFromImageView: ImageView

    @FXML
    private lateinit var coverFromSizeLabel: Label

    @FXML
    private lateinit var durationFromLabel: Label

    @FXML
    private lateinit var titleToLabel: Label

    @FXML
    private lateinit var artistsToLabel: Label

    @FXML
    private lateinit var albumToLabel: Label

    @FXML
    private lateinit var idToLabel: Label

    @FXML
    private lateinit var coverToImageView: ImageView

    @FXML
    private lateinit var coverToSizeLabel: Label

    @FXML
    private lateinit var durationToLabel: Label

    @FXML
    private lateinit var writeTitleButton: Button

    @FXML
    private lateinit var writeArtistsButton: Button

    @FXML
    private lateinit var writeAlbumButton: Button

    @FXML
    private lateinit var writeIdButton: Button

    @FXML
    private lateinit var writeCoverButton: Button

    @FXML
    private lateinit var previousButton: Button

    @FXML
    private lateinit var writeAllButton: Button

    @FXML
    private lateinit var nextButton: Button

    @FXML
    private lateinit var indexLabel: Label

    @FXML
    fun initialize() {
        FileSystemConfig.sourceDirectory.addListener { _ ->
            launch(Dispatchers.JavaFx) {
                if (directoryTextField.text != FileSystemConfig.sourceDirectory.value) {
                    directoryTextField.text = FileSystemConfig.sourceDirectory.value
                }
            }
        }

        FileSystemConfig.isSubDirectoryIncluded.addListener { _ ->
            launch(Dispatchers.JavaFx) {
                if (isSubdirectoryIncludedCheckBox.isSelected != FileSystemConfig.isSubDirectoryIncluded.value) {
                    isSubdirectoryIncludedCheckBox.isSelected = FileSystemConfig.isSubDirectoryIncluded.value
                }
            }
        }

        Persistence.isInitialized.addListener { _ ->
            launch(Dispatchers.JavaFx) {
                container.isDisable = !Persistence.isInitialized.value
            }
        }

        fileList.addListener(
            ListChangeListener {
                updateNavigation()
            }
        )

        fileListIndex.addListener { _ ->
            updateNavigation()
        }
    }

    private fun updateNavigation() {
        launch(Dispatchers.JavaFx) {
            previousButton.isDisable = fileListIndex.value <= 0
            nextButton.isDisable = fileListIndex.value >= fileList.size - 1
            indexLabel.text = "${fileListIndex.value + 1} / ${fileList.size}"
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
            Persistence.save(PersistenceTypes.CACHE)
        }
    }

    @FXML
    fun chooseDirectory(actionEvent: ActionEvent) {
        FileSystemConfig.sourceDirectory.value.let {
            val sourceDirectory = File(it)
            directoryChooser.initialDirectory = if (sourceDirectory.exists()) sourceDirectory else null
        }

        val file: File = directoryChooser.showDialog((actionEvent.source as Node).scene.window as Stage)

        directoryTextField.text = file.absolutePath
        FileSystemConfig.sourceDirectory.set(file.absolutePath)
    }

    @FXML
    fun onDirectoryInput() {
        FileSystemConfig.sourceDirectory.set(directoryTextField.text)
    }

    @FXML
    fun onSubdirectoryInclusionToggle() {
        FileSystemConfig.isSubDirectoryIncluded.set(isSubdirectoryIncludedCheckBox.isSelected)
    }

    @FXML
    fun onGo() {
        launch(Dispatchers.IO) {
            fileList.clear()
            fileListIndex.set(-1)

            FileSystemConfig.sourceDirectory.value.let {
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

                scanFiles(directory)
                onNext()
            }
        }
    }

    @FXML
    private fun onWriteTitle() {
        launch(Dispatchers.IO) {
            writeMusicFile(Id3Properties.TITLE)
            launch(Dispatchers.JavaFx) {
                updateView()
            }
        }
    }

    @FXML
    private fun onWriteArtists() {
        launch(Dispatchers.IO) {
            writeMusicFile(Id3Properties.ARTISTS)
            launch(Dispatchers.JavaFx) {
                updateView()
            }
        }
    }

    @FXML
    private fun onWriteAlbum() {
        launch(Dispatchers.IO) {
            writeMusicFile(Id3Properties.ALBUM)
            launch(Dispatchers.JavaFx) {
                updateView()
            }
        }
    }

    @FXML
    private fun onWriteId() {
        launch(Dispatchers.IO) {
            writeMusicFile(Id3Properties.ID)
            launch(Dispatchers.JavaFx) {
                updateView()
            }
        }
    }

    @FXML
    private fun onWriteCover() {
        launch(Dispatchers.IO) {
            writeMusicFile(Id3Properties.COVER)
            launch(Dispatchers.JavaFx) {
                updateView()
            }
        }
    }

    @FXML
    private fun onWriteAll() {
        launch(Dispatchers.IO) {
            writeMusicFile()
            launch(Dispatchers.JavaFx) {
                updateView()
            }
        }
    }

    @FXML
    private fun onPrevious() {
        fileListIndex.set(fileListIndex.value - 1)
        updateView()
    }

    @FXML
    private fun onNext() {
        if (fileListIndex.value == -1 && fileList.size == 0) {
            return
        }

        fileListIndex.set(fileListIndex.value + 1)
        updateView()
    }

    private fun scanFiles(file: File) {
        val listOfFiles = file.listFiles()

        listOfFiles?.let {
            for (currentFile in listOfFiles) {
                if (currentFile.isFile && currentFile.extension == "mp3") {
                    fileList.add(currentFile)
                } else if (currentFile.isDirectory && FileSystemConfig.isSubDirectoryIncluded.value == true) {
                    scanFiles(currentFile)
                }
            }
        }
    }

    private fun updateView() {
        launch(Dispatchers.IO) {
            if (fileListIndex.value == -1) {
                LOGGER.error("Update view called without any files!")
                return@launch
            }

            val currentFile = fileList[fileListIndex.value]

            val musicFile = if (FileSystemCache.trackData.containsKey(currentFile.absolutePath)) {
                LOGGER.debug("Using cache for \"${currentFile.name}\".")
                FileSystemCache.trackData[currentFile.absolutePath]
            } else {
                LOGGER.info("Processing \"${currentFile.name}\"...")
                FileSystemProvider.getMusicFile(currentFile).also {
                    FileSystemCache.trackData[it.path] = it
                }
            }

            if (musicFile == null) {
                LOGGER.error("File not found!")
                return@launch
            }

            val fileSystemTrack = musicFile.track
            val spotifyLibTrack = if (SpotifyCache.trackData.containsKey(fileSystemTrack.id)) {
                SpotifyCache.trackData[fileSystemTrack.id]
            } else {
                SpotifyProvider.getSpotifyTrack(musicFile)?.also {
                    SpotifyCache.trackData[it.id] = it
                }
            }

            if (spotifyLibTrack == null) {
                LOGGER.warn("Spotify track not found!")
                return@launch
            }

            val spotifyTrack = getTrackFromSpotifyTrack(spotifyLibTrack)

            FileSystemState.currentFile = currentFile
            FileSystemState.currentTrack = fileSystemTrack
            SpotifyState.currentTrack = spotifyTrack

            Persistence.save(PersistenceTypes.CACHE)

            launch(Dispatchers.JavaFx) {
                titleFromLabel.text = fileSystemTrack.name
                artistsFromLabel.text = fileSystemTrack.artists?.joinToString()
                albumFromLabel.text = fileSystemTrack.album?.name
                idFromLabel.text = fileSystemTrack.id
                coverFromImageView.image = fileSystemTrack.album?.coverBase64?.let {
                    val byteArray = Base64.getDecoder().decode(it)
                    val byteArrayInputStream = ByteArrayInputStream(byteArray)
                    val image = ImageIO.read(byteArrayInputStream)
                    coverFromSizeLabel.text = Util.humanReadableByteCountBin(byteArray.size.toLong())
                    byteArrayInputStream.close()
                    SwingFXUtils.toFXImage(image, null)
                }
                durationFromLabel.text = fileSystemTrack.durationMs?.toString()

                titleToLabel.text = spotifyTrack.name
                artistsToLabel.text = spotifyTrack.artists?.joinToString()
                albumToLabel.text = spotifyTrack.album?.name
                idToLabel.text = spotifyTrack.id
                coverToImageView.image = spotifyTrack.album?.coverBase64?.let {
                    val byteArray = Base64.getDecoder().decode(it)
                    val byteArrayInputStream = ByteArrayInputStream(byteArray)
                    val image = ImageIO.read(byteArrayInputStream)
                    coverToSizeLabel.text = Util.humanReadableByteCountBin(byteArray.size.toLong())
                    byteArrayInputStream.close()
                    SwingFXUtils.toFXImage(image, null)
                }
                durationToLabel.text = spotifyTrack.durationMs?.toString()

                if (titleFromLabel.text != titleToLabel.text) {
                    titleFromLabel.textFill = RED
                    titleToLabel.textFill = RED
                    writeTitleButton.isDisable = false
                } else {
                    titleFromLabel.textFill = GREEN
                    titleToLabel.textFill = GREEN
                    writeTitleButton.isDisable = true
                }

                if (artistsFromLabel.text != artistsToLabel.text) {
                    artistsFromLabel.textFill = RED
                    artistsToLabel.textFill = RED
                    writeArtistsButton.isDisable = false
                } else {
                    artistsFromLabel.textFill = GREEN
                    artistsToLabel.textFill = GREEN
                    writeArtistsButton.isDisable = true
                }

                if (albumFromLabel.text != albumToLabel.text) {
                    albumFromLabel.textFill = RED
                    albumToLabel.textFill = RED
                    writeAlbumButton.isDisable = false
                } else {
                    albumFromLabel.textFill = GREEN
                    albumToLabel.textFill = GREEN
                    writeAlbumButton.isDisable = true
                }

                if (idFromLabel.text != idToLabel.text) {
                    if (SpotitagConfig.isIdChecked.value) {
                        idFromLabel.textFill = RED
                        idToLabel.textFill = RED
                        writeIdButton.isDisable = false
                    } else {
                        idFromLabel.textFill = YELLOW
                        idToLabel.textFill = YELLOW
                    }
                } else {
                    idFromLabel.textFill = GREEN
                    idToLabel.textFill = GREEN
                    writeIdButton.isDisable = true
                }

                if (spotifyTrack.album?.coverBase64 != null && fileSystemTrack.album?.coverBase64 != spotifyTrack.album.coverBase64) {
                    if (SpotitagConfig.isIdChecked.value) {
                        coverFromSizeLabel.textFill = RED
                        coverToSizeLabel.textFill = RED
                        writeCoverButton.isDisable = false
                    } else {
                        coverFromSizeLabel.textFill = YELLOW
                        coverToSizeLabel.textFill = YELLOW
                    }
                } else {
                    coverFromSizeLabel.textFill = GREEN
                    coverToSizeLabel.textFill = GREEN
                    writeCoverButton.isDisable = true
                }

                if (durationFromLabel.text != durationToLabel.text) {
                    if (fileSystemTrack.durationMs != null && spotifyTrack.durationMs != null
                        && abs(fileSystemTrack.durationMs - spotifyTrack.durationMs) <= SpotitagConfig.durationTolerance.value
                    ) {
                        durationFromLabel.textFill = YELLOW
                        durationToLabel.textFill = YELLOW
                    } else {
                        durationFromLabel.textFill = RED
                        durationToLabel.textFill = RED
                    }
                } else {
                    durationFromLabel.textFill = GREEN
                    durationToLabel.textFill = GREEN
                }

                writeAllButton.isDisable =
                    writeTitleButton.isDisable && writeArtistsButton.isDisable && writeAlbumButton.isDisable && writeIdButton.isDisable && writeCoverButton.isDisable

                progressBar.progress = (fileListIndex.value).toDouble() / (fileList.size - 1)
            }
        }
    }
}