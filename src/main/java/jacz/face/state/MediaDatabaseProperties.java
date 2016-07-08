package jacz.face.state;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import jacz.database.*;
import jacz.database.util.GenreCode;
import jacz.database.util.LocalizedLanguage;
import jacz.database.util.QualityCode;
import jacz.face.controllers.ClientAccessor;
import jacz.face.util.MediaItemType;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import jacz.peerengineservice.PeerId;
import org.aanguita.jacuzzi.lists.tuple.Duple;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Stores observable lists for the integrated database (movies and series)
 * <p>
 * todo add sortedlist, and filteredlist. Use filteredlist to filter remote content
 * <p>
 * todo add timer that allows marking items with new media content for some time (and for sorting)
 * <p>
 * todo add lowercase title to improve title filtering efficiency
 */
public class MediaDatabaseProperties extends GenericStateProperties {

    public static class MediaItem {

        private final MediaItemType type;

        private final Integer id;

        // todo we also need the deletedId, it counts as local content
        // todo if editing, what to do with deleted content???
        private final ObjectProperty<Integer> localId;

        private final ObjectProperty<List<Duple<PeerId, Integer>>> remoteIds;

        private final ObjectProperty<Integer> deletedId;

        private final StringProperty title;

        private final ObjectProperty<LocalizedLanguage> titleLocalizedLanguage;

        private final StringProperty originalTitle;

        private final StringProperty imagePath;

        private final ObjectProperty<Integer> year;

        private final StringProperty synopsis;

        private final ObjectProperty<LocalizedLanguage> synopsisLocalizedLanguage;

        private final ObjectProperty<List<CountryCode>> countries;

        private final ObjectProperty<List<String>> creators;

        private final ObjectProperty<List<String>> actors;

        private final ObjectProperty<List<String>> productionCompanies;

        private final ObjectProperty<List<GenreCode>> genres;

        //private final ObjectProperty<LanguageCode> language;

        private final ObjectProperty<Integer> minutes;

        private final ObservableList<VideoFileModel> videoFiles;

        private MediaItem(CreationItem creationItem, MediaItemType type, String imagePath, List<String> productionCompanies, List<GenreCode> genres, Integer minutes, List<VideoFileModel> videoFiles) {
            this.type = type;
            this.id = creationItem.getId();
            this.localId = new SimpleObjectProperty<>(ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToLocal().get(type.parse(), creationItem.getId()));
            this.remoteIds = new SimpleObjectProperty<>(ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToRemote().get(type.parse(), creationItem.getId()));
            this.deletedId = new SimpleObjectProperty<>(ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToDeleted().get(type.parse(), creationItem.getId()));
            this.title = new SimpleStringProperty(creationItem.getTitle());
            this.titleLocalizedLanguage = new SimpleObjectProperty<>(creationItem.getTitleLocalizedLanguage());
            this.originalTitle = new SimpleStringProperty(creationItem.getOriginalTitle());
            this.imagePath = new SimpleStringProperty(imagePath);
            this.year = new SimpleObjectProperty<>(creationItem.getYear());
            this.synopsis = new SimpleStringProperty(creationItem.getSynopsis());
            this.synopsisLocalizedLanguage = new SimpleObjectProperty<>(creationItem.getSynopsisLocalizedLanguage());
            this.countries = new SimpleObjectProperty<>(creationItem.getCountries());
            this.creators = new SimpleObjectProperty<>(creationItem.getCreators());
            this.actors = new SimpleObjectProperty<>(creationItem.getActors());
            this.productionCompanies = new SimpleObjectProperty<>(productionCompanies);
            this.genres = new SimpleObjectProperty<>(genres);
            //this.language = new SimpleObjectProperty<>(creationItem.getLanguage());
            this.minutes = new SimpleObjectProperty<>(minutes);
            this.videoFiles = FXCollections.observableArrayList(new Callback<VideoFileModel, Observable[]>() {
                @Override
                public Observable[] call(VideoFileModel v) {
                    return new Observable[]{
                            v.getAdditionalSources(),
                            v.minutesProperty(),
                            v.resolutionProperty(),
                            v.qualityProperty(),
                            v.getSubtitleFiles(),
                            v.getLanguages()};
                }
            });
            this.videoFiles.addAll(videoFiles);
        }

        public MediaItem(ProducedCreationItem producedCreationItem, MediaItemType type, String imagePath, Integer minutes, List<VideoFileModel> videoFiles) {
            this(producedCreationItem, type, imagePath, producedCreationItem.getProductionCompanies(), producedCreationItem.getGenres(), minutes, videoFiles);
        }

        public MediaItem(Movie movie) {
            this(movie, MediaItemType.MOVIE, buildImagePath(movie), movie.getMinutes(), VideoFileModel.buildVideoFileModelList(movie.getVideoFiles()));
            ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToLocal().get(DatabaseMediator.ItemType.MOVIE, movie.getId());
        }

        public MediaItem(TVSeries tvSeries) {
            this(tvSeries, MediaItemType.TV_SERIES, buildImagePath(tvSeries), null, new ArrayList<>());
        }

        public MediaItem(Chapter chapter) {
            // todo season and chapter number???
            this(chapter, MediaItemType.CHAPTER, buildImagePath(chapter), null, null, chapter.getMinutes(), VideoFileModel.buildVideoFileModelList(chapter.getVideoFiles()));
        }

        public MediaItem(MediaItemType type, Integer id) {
            this.type = type;
            this.id = id;
            this.localId = null;
            this.remoteIds = null;
            this.deletedId = null;
            this.title = null;
            this.titleLocalizedLanguage = null;
            this.originalTitle = null;
            this.imagePath = null;
            this.year = null;
            this.synopsis = null;
            this.synopsisLocalizedLanguage = null;
            this.countries = null;
            this.creators = null;
            this.actors = null;
            this.productionCompanies = null;
            this.genres = null;
            //this.language = null;
            this.minutes = null;
            this.videoFiles = null;
        }

        public DatabaseItem getItem() {
            switch (getType()) {
                case MOVIE:
                    return Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), getId());
                case TV_SERIES:
                    return TVSeries.getTVSeriesById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), getId());
                case CHAPTER:
                    return Chapter.getChapterById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), getId());
                default:
                    return null;
            }
        }

        private void update(CreationItem creationItem, boolean setLater) {
            Util.setLaterIf(this.localId, ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToLocal().get(type.parse(), creationItem.getId()), setLater);
            Util.setLaterIf(this.remoteIds, ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToRemote().get(type.parse(), creationItem.getId()), setLater);
            Util.setLaterIf(this.deletedId, ClientAccessor.getInstance().getClient().getDatabases().getItemRelations().getIntegratedToDeleted().get(type.parse(), creationItem.getId()), setLater);
            Util.setLaterIf(this.title, creationItem.getTitle(), setLater);
            Util.setLaterIf(this.originalTitle, creationItem.getOriginalTitle(), setLater);
            Util.setLaterIf(this.year, creationItem.getYear(), setLater);
            Util.setLaterIf(this.synopsis, creationItem.getSynopsis(), setLater);
            Util.setLaterIf(this.countries, creationItem.getCountries(), setLater);
            Util.setLaterIf(this.creators, creationItem.getCreators(), setLater);
            Util.setLaterIf(this.actors, creationItem.getActors(), setLater);
            Util.setLaterIf(this.productionCompanies, creationItem.getActors(), setLater);
            //Util.setLaterIf(this.language, creationItem.getLanguage(), setLater);
        }

        public void update(ProducedCreationItem producedCreationItem, boolean setLater) {
            update((CreationItem) producedCreationItem, setLater);
            Util.setLaterIf(this.imagePath, buildImagePath(producedCreationItem), setLater);
            Util.setLaterIf(this.productionCompanies, producedCreationItem.getProductionCompanies(), setLater);
        }

        public void update(Movie movie, boolean setLater) {
            update((ProducedCreationItem) movie, setLater);
            Util.setLaterIf(this.minutes, movie.getMinutes(), setLater);
            Util.runLaterIf(() -> {
                this.videoFiles.clear();
                this.videoFiles.addAll(VideoFileModel.buildVideoFileModelList(movie.getVideoFiles()));
            }, setLater);
        }

        protected void update(TVSeries tvSeries, boolean setLater) {
            update((ProducedCreationItem) tvSeries, setLater);
        }

        protected void update(Chapter chapter, boolean setLater) {
            update((CreationItem) chapter, setLater);
            Util.setLaterIf(this.imagePath, buildImagePath(chapter), setLater);
            Util.setLaterIf(this.minutes, chapter.getMinutes(), setLater);
        }

        public MediaItemType getType() {
            return type;
        }

        public Integer getId() {
            return id;
        }

        public Integer getLocalId() {
            return localId.get();
        }

        public ObjectProperty<Integer> localIdProperty() {
            return localId;
        }

        public List<Duple<PeerId, Integer>> getRemoteIds() {
            return remoteIds.get();
        }

        public ObjectProperty<List<Duple<PeerId, Integer>>> remoteIdsProperty() {
            return remoteIds;
        }

        public Integer getDeletedId() {
            return deletedId.get();
        }

        public ObjectProperty<Integer> deletedIdProperty() {
            return deletedId;
        }

        public String getTitle() {
            return title.get();
        }

        public StringProperty titleProperty() {
            return title;
        }

        public LocalizedLanguage getTitleLocalizedLanguage() {
            return titleLocalizedLanguage.get();
        }

        public ObjectProperty<LocalizedLanguage> titleLocalizedLanguageProperty() {
            return titleLocalizedLanguage;
        }

        public String getOriginalTitle() {
            return originalTitle.get();
        }

        public StringProperty originalTitleProperty() {
            return originalTitle;
        }

        public String getImagePath() {
            return imagePath.get();
        }

        public StringProperty imagePathProperty() {
            return imagePath;
        }

        public Integer getYear() {
            return year.get();
        }

        public ObjectProperty<Integer> yearProperty() {
            return year;
        }

        public String getSynopsis() {
            return synopsis.get();
        }

        public StringProperty synopsisProperty() {
            return synopsis;
        }

        public LocalizedLanguage getSynopsisLocalizedLanguage() {
            return synopsisLocalizedLanguage.get();
        }

        public ObjectProperty<LocalizedLanguage> synopsisLocalizedLanguageProperty() {
            return synopsisLocalizedLanguage;
        }

        public List<CountryCode> getCountries() {
            return countries.get();
        }

        public ObjectProperty<List<CountryCode>> countriesProperty() {
            return countries;
        }

        public List<String> getCreators() {
            return creators.get();
        }

        public ObjectProperty<List<String>> creatorsProperty() {
            return creators;
        }

        public List<String> getActors() {
            return actors.get();
        }

        public ObjectProperty<List<String>> actorsProperty() {
            return actors;
        }

        public List<String> getProductionCompanies() {
            return productionCompanies.get();
        }

        public ObjectProperty<List<String>> productionCompaniesProperty() {
            return productionCompanies;
        }

        public List<GenreCode> getGenres() {
            return genres.get();
        }

        public ObjectProperty<List<GenreCode>> genresProperty() {
            return genres;
        }

//        public LanguageCode getLanguage() {
//            return language.get();
//        }
//
//        public ObjectProperty<LanguageCode> languageProperty() {
//            return language;
//        }

        public Integer getMinutes() {
            return minutes.get();
        }

        public ObjectProperty<Integer> minutesProperty() {
            return minutes;
        }

//        public List<VideoFileModel> getVideoFiles() {
//            return videoFiles.get();
//        }

        public ObservableList<VideoFileModel> videoFilesProperty() {
            return videoFiles;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MediaItem)) return false;

            MediaItem mediaItem = (MediaItem) o;
            return type == mediaItem.type && id.equals(mediaItem.id);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }

    public static class FileModel {

        public final int itemId;

        public final String hash;

        private final ObjectProperty<Long> length;

        private final StringProperty name;

        private final ObservableList<String> additionalSources;

        public FileModel(File file) {
            this.itemId = file.getId();
            hash = file.getHash();
            length = new SimpleObjectProperty<>(file.getLength());
            name = new SimpleStringProperty(file.getName());
            additionalSources = FXCollections.observableList(file.getAdditionalSources());
        }

        public FileModel(String hash) {
            this.itemId = -1;
            this.hash = hash;
            length = new SimpleObjectProperty<>(null);
            name = new SimpleStringProperty(null);
            additionalSources = FXCollections.emptyObservableList();
        }

        public int getItemId() {
            return itemId;
        }

        public String getHash() {
            return hash;
        }

        public Long getLength() {
            return length.get();
        }

        public void setLength(Long length) {
            this.length.set(length);
        }

        public ObjectProperty<Long> lengthProperty() {
            return length;
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public StringProperty nameProperty() {
            return name;
        }

        public ObservableList<String> getAdditionalSources() {
            return additionalSources;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FileModel)) return false;

            FileModel fileModel = (FileModel) o;

            return hash.equals(fileModel.hash);
        }

        @Override
        public int hashCode() {
            return hash.hashCode();
        }
    }

    public static class VideoFileModel extends FileModel {


        private final ObjectProperty<Integer> minutes;
        private final ObjectProperty<Integer> resolution;
        private final ObjectProperty<Integer> bitrate;
        private final ObjectProperty<QualityCode> quality;
        private final ObservableList<SubtitleFileModel> subtitleFiles;
        private final ObservableList<LocalizedLanguage> languages;

        public VideoFileModel(VideoFile videoFile) {
            super(videoFile);
            minutes = new SimpleObjectProperty<>(videoFile.getMinutes());
            resolution = new SimpleObjectProperty<>(videoFile.getResolution());
            bitrate = new SimpleObjectProperty<>(videoFile.getBitrate());
            quality = new SimpleObjectProperty<>(videoFile.getQuality());
            subtitleFiles = FXCollections.observableList(SubtitleFileModel.buildSubtitleFileModelList(videoFile.getSubtitleFiles()));
            languages = FXCollections.observableList(videoFile.getLocalizedLanguages());
        }

        public VideoFileModel(String hash) {
            super(hash);
            minutes = new SimpleObjectProperty<>(null);
            resolution = new SimpleObjectProperty<>(null);
            bitrate = new SimpleObjectProperty<>(null);
            quality = new SimpleObjectProperty<>(null);
            subtitleFiles = FXCollections.emptyObservableList();
            languages = FXCollections.emptyObservableList();
        }

        public static List<VideoFileModel> buildVideoFileModelList(List<VideoFile> videoFiles) {
            return videoFiles.stream().map(VideoFileModel::new).collect(Collectors.toList());
        }

        public Integer getMinutes() {
            return minutes.get();
        }

        public void setMinutes(Integer minutes) {
            this.minutes.set(minutes);
        }

        public ObjectProperty<Integer> minutesProperty() {
            return minutes;
        }

        public Integer getResolution() {
            return resolution.get();
        }

        public void setResolution(Integer resolution) {
            this.resolution.set(resolution);
        }

        public ObjectProperty<Integer> resolutionProperty() {
            return resolution;
        }

        public Integer getBitrate() {
            return bitrate.get();
        }

        public ObjectProperty<Integer> bitrateProperty() {
            return bitrate;
        }

        public void setBitrate(Integer bitrate) {
            this.bitrate.set(bitrate);
        }

        public QualityCode getQuality() {
            return quality.get();
        }

        public void setQuality(QualityCode quality) {
            this.quality.set(quality);
        }

        public ObjectProperty<QualityCode> qualityProperty() {
            return quality;
        }

        public ObservableList<SubtitleFileModel> getSubtitleFiles() {
            return subtitleFiles;
        }

        public ObservableList<LocalizedLanguage> getLanguages() {
            return languages;
        }
    }

    public static class SubtitleFileModel extends FileModel {

        private final ObjectProperty<LocalizedLanguage> language;

        public SubtitleFileModel(SubtitleFile subtitleFile) {
            super(subtitleFile);
            language = new SimpleObjectProperty<>(subtitleFile.getLocalizedLanguage());
        }

        public SubtitleFileModel(String hash) {
            super(hash);
            language = new SimpleObjectProperty<>(null);
        }

        public static List<SubtitleFileModel> buildSubtitleFileModelList(List<SubtitleFile> subtitleFiles) {
            return subtitleFiles.stream().map(SubtitleFileModel::new).collect(Collectors.toList());
        }

        public LocalizedLanguage getLanguage() {
            return language.get();
        }

        public ObjectProperty<LocalizedLanguage> languageProperty() {
            return language;
        }
    }


    private static final Predicate<MediaItem> moviesFilter = mediaItem -> mediaItem.getType() == MediaItemType.MOVIE;

    private static final Predicate<MediaItem> seriesFilter = mediaItem -> mediaItem.getType() == MediaItemType.TV_SERIES;

    private String integratedDB;

    private final ObservableList<MediaItem> itemList;

    private final ObservableList<MediaItem> movieList;

    private final ObservableList<MediaItem> seriesList;

    public MediaDatabaseProperties() {
        this.integratedDB = null;
        itemList = FXCollections.observableArrayList(new Callback<MediaItem, Observable[]>() {
            @Override
            public Observable[] call(MediaItem p) {
                // todo add rest of properties
                return new Observable[]{
                        p.titleProperty(),
                        p.titleLocalizedLanguageProperty(),
                        p.originalTitleProperty(),
                        p.imagePathProperty(),
                        p.yearProperty(),
                        p.synopsisProperty(),
                        p.synopsisLocalizedLanguageProperty(),
                        p.countriesProperty(),
                        p.creatorsProperty(),
                        p.actorsProperty(),
                        p.productionCompaniesProperty(),
                        //p.languageProperty(),
                        p.minutesProperty()};
            }
        });
        movieList = new FilteredList<>(itemList, moviesFilter);
        seriesList = new FilteredList<>(itemList, seriesFilter);
    }

    @Override
    public void setClient(PeerEngineClient client) {
        super.setClient(client);
        integratedDB = client.getDatabases().getIntegratedDB();
        Movie.getMovies(integratedDB).stream().forEach(movie -> newMovie(movie, true));
        TVSeries.getTVSeries(integratedDB).stream().forEach(tvSeries -> newTVSeries(tvSeries, true));
        Chapter.getChapters(integratedDB).stream().forEach(chapter -> newChapter(chapter, true));
    }

    public ObservableList<MediaItem> getItemList() {
        return itemList;
    }

    public ObservableList<MediaItem> getMovieList() {
        return movieList;
    }

    public ObservableList<MediaItem> getSeriesList() {
        return seriesList;
    }

    public synchronized void updateMediaItem(DatabaseMediator.ItemType type, Integer id, boolean inJavaFXThread) {
        switch (type) {
            case MOVIE:
                Movie movie = Movie.getMovieById(integratedDB, id);
                updateMediaItem(movie, inJavaFXThread);
                break;
            case TV_SERIES:
                TVSeries tvSeries = TVSeries.getTVSeriesById(integratedDB, id);
                updateMediaItem(tvSeries, inJavaFXThread);
                break;
            case CHAPTER:
                Chapter chapter = Chapter.getChapterById(integratedDB, id);
                updateMediaItem(chapter, inJavaFXThread);
                break;
        }
    }

    public synchronized void updateMediaItem(DatabaseItem integratedItem, boolean inJavaFXThread) {
        int index = findMediaItem(MediaItemType.buildType(integratedItem.getItemType()), integratedItem.getId());
        if (index >= 0) {
            // the item already exists -> update
            switch (MediaItemType.buildType(integratedItem.getItemType())) {
                case MOVIE:
                    itemList.get(index).update((Movie) integratedItem, !inJavaFXThread);
                    break;
                case TV_SERIES:
                    itemList.get(index).update((TVSeries) integratedItem, !inJavaFXThread);
                    break;
                case CHAPTER:
                    itemList.get(index).update((Chapter) integratedItem, !inJavaFXThread);
                    break;
            }
        } else {
            // the item is new -> create it
            switch (MediaItemType.buildType(integratedItem.getItemType())) {
                case MOVIE:
                    newMovie((Movie) integratedItem, inJavaFXThread);
                    break;
                case TV_SERIES:
                    newTVSeries((TVSeries) integratedItem, inJavaFXThread);
                    break;
                case CHAPTER:
                    newChapter((Chapter) integratedItem, inJavaFXThread);
                    break;
            }
        }
    }

    private void newMovie(Movie movie, boolean inJavaFXThread) {
        Util.runLaterIf(() -> itemList.add(new MediaItem(movie)), !inJavaFXThread);
    }

    private void newTVSeries(TVSeries tvSeries, boolean inJavaFXThread) {
        Util.runLaterIf(() -> itemList.add(new MediaItem(tvSeries)), !inJavaFXThread);
    }

    private void newChapter(Chapter chapter, boolean inJavaFXThread) {
        Util.runLaterIf(() -> itemList.add(new MediaItem(chapter)), !inJavaFXThread);
    }

    public void mediaItemRemoved(DatabaseMediator.ItemType itemType, Integer id, boolean inJavaFXThread) {
        MediaItemType type = MediaItemType.buildType(itemType);
        int index = findMediaItem(type, id);
        if (index >= 0) {
            Util.runLaterIf(() -> itemList.remove(index), !inJavaFXThread);
        }
    }

    public MediaItem getMediaItem(MediaItemType type, Integer id) {
        try {
            return itemList.get(itemList.indexOf(new MediaItem(type, id)));
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private int findMediaItem(MediaItemType type, Integer id) {
        return itemList.indexOf(new MediaItem(type, id));
    }

    public static String buildImagePath(ProducedCreationItem producedCreationItem) {
        String hash = producedCreationItem.getImageHash() != null ? producedCreationItem.getImageHash().getHash() : null;
        if (hash != null) {
            return ClientAccessor.getInstance().getClient().getFile(hash);
        } else {
            return null;
        }
    }

    public static String buildImagePath(Chapter chapter) {
        List<TVSeries> tvSeriesList = chapter.getTVSeries();
        if (!tvSeriesList.isEmpty()) {
            return buildImagePath(tvSeriesList.get(0));
        } else {
            return null;
        }
    }

    public static DatabaseItem getItem(MediaItem mediaItem) {
        switch (mediaItem.getType()) {
            case MOVIE:
                return Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), mediaItem.getId());
            case TV_SERIES:
                return TVSeries.getTVSeriesById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), mediaItem.getId());
            case CHAPTER:
                return Chapter.getChapterById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), mediaItem.getId());
            default:
                return null;
        }
    }
}
