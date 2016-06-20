package jacz.face.state;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import jacz.database.*;
import jacz.database.util.GenreCode;
import jacz.face.controllers.ClientAccessor;
import jacz.face.util.MediaItemType;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.Callback;

import java.util.List;
import java.util.function.Predicate;

/**
 * Stores observable lists for the integrated database (movies and series)
 *
 * todo add sortedlist, and filteredlist. Use filteredlist to filter remote content
 *
 * todo add timer that allows marking items with new media content for some time (and for sorting)
 *
 * todo add lowercase title to improve title filtering efficiency
 */
public class MediaDatabaseProperties extends GenericStateProperties {

    public static class MediaItem {
        // todo companies, genres

        private final MediaItemType type;

        private final Integer id;

        private final StringProperty title;

        private final StringProperty originalTitle;

        private final StringProperty imagePath;

        private final ObjectProperty<Integer> year;

        private final ObjectProperty<List<CountryCode>> countries;

        private final ObjectProperty<List<String>> creators;

        private final ObjectProperty<List<String>> actors;

        private final ObjectProperty<List<String>> productionCompanies;

        private final ObjectProperty<List<GenreCode>> genres;

        private final ObjectProperty<LanguageCode> language;

        private final ObjectProperty<Integer> minutes;

        private MediaItem(CreationItem creationItem, MediaItemType type, String imagePath, List<String> productionCompanies, List<GenreCode> genres, Integer minutes) {
            this.type = type;
            this.id = creationItem.getId();
            this.title = new SimpleStringProperty(creationItem.getTitle());
            this.originalTitle = new SimpleStringProperty(creationItem.getOriginalTitle());
            this.imagePath = new SimpleStringProperty(imagePath);
            this.year = new SimpleObjectProperty<>(creationItem.getYear());
            this.countries = new SimpleObjectProperty<>(creationItem.getCountries());
            this.creators = new SimpleObjectProperty<>(creationItem.getCreators());
            this.actors = new SimpleObjectProperty<>(creationItem.getActors());
            this.productionCompanies = new SimpleObjectProperty<>(productionCompanies);
            this.genres = new SimpleObjectProperty<>(genres);
            this.language = new SimpleObjectProperty<>(creationItem.getLanguage());
            this.minutes = new SimpleObjectProperty<>(minutes);
        }

        public MediaItem(ProducedCreationItem producedCreationItem, MediaItemType type, String imagePath, Integer minutes) {
            this(producedCreationItem, type, imagePath, producedCreationItem.getProductionCompanies(), producedCreationItem.getGenres(), minutes);
        }

        public MediaItem(Movie movie) {
            this(movie, MediaItemType.MOVIE, buildImagePath(movie), movie.getMinutes());
        }

        public MediaItem(TVSeries tvSeries) {
            this(tvSeries, MediaItemType.TV_SERIES, buildImagePath(tvSeries), null);
        }

        public MediaItem(Chapter chapter) {
            // todo season and chapter number???
            this(chapter, MediaItemType.CHAPTER, buildImagePath(chapter), null, null, chapter.getMinutes());
        }

        public MediaItem(MediaItemType type, Integer id) {
            this.type = type;
            this.id = id;
            this.title = null;
            this.originalTitle = null;
            this.imagePath = null;
            this.year = null;
            this.countries = null;
            this.creators = null;
            this.actors = null;
            this.productionCompanies = null;
            this.genres = null;
            this.language = null;
            this.minutes = null;
        }

        protected void update(CreationItem creationItem, String imagePath, Integer minutes) {
            Util.setLater(this.title, creationItem.getTitle());
            Util.setLater(this.originalTitle, creationItem.getOriginalTitle());
            Util.setLater(this.imagePath, imagePath);
            Util.setLater(this.year, creationItem.getYear());
            Util.setLater(this.countries, creationItem.getCountries());
            Util.setLater(this.creators, creationItem.getCreators());
            Util.setLater(this.actors, creationItem.getActors());
            Util.setLater(this.language, creationItem.getLanguage());
            Util.setLater(this.minutes, minutes);
        }

        protected void update(Movie movie) {
            update(movie, buildImagePath(movie), movie.getMinutes());
        }

        protected void update(TVSeries tvSeries) {
            update(tvSeries, buildImagePath(tvSeries), null);
        }

        protected void update(Chapter chapter) {
            update(chapter, buildImagePath(chapter), chapter.getMinutes());
        }

        public MediaItemType getType() {
            return type;
        }

        public Integer getId() {
            return id;
        }

        public String getTitle() {
            return title.get();
        }

        public StringProperty titleProperty() {
            return title;
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

        public LanguageCode getLanguage() {
            return language.get();
        }

        public ObjectProperty<LanguageCode> languageProperty() {
            return language;
        }

        public Integer getMinutes() {
            return minutes.get();
        }

        public ObjectProperty<Integer> minutesProperty() {
            return minutes;
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

//    public static class Movie extends MediaItem {
//
//        public Movie(jacz.database.Movie movie) {
//            super(DatabaseMediator.ItemType.MOVIE, id, title);
//        }
//    }
//
//    public static class TVSeries extends MediaItem {
//
//        public TVSeries(Integer id, String title) {
//            super(DatabaseMediator.ItemType.TV_SERIES, id, title);
//        }
//    }

    private static final Predicate<MediaItem> moviesFilter = mediaItem -> mediaItem.getType() == MediaItemType.MOVIE;

    private static final Predicate<MediaItem> seriesFilter = mediaItem -> mediaItem.getType() == MediaItemType.TV_SERIES;

    private String integratedDB;

    private final ObservableList<MediaItem> itemList;

    private final ObservableList<MediaItem> movieList;

    private final ObservableList<MediaItem> seriesList;

//    private final ObservableList<Movie> movieList;
//
//    private final ObservableList<TVSeries> tvSeriesList;

    public MediaDatabaseProperties() {
        this.integratedDB = null;
        itemList = FXCollections.observableArrayList(new Callback<MediaItem, Observable[]>() {
            @Override
            public Observable[] call(MediaItem p) {
                // todo add rest of properties
                return new Observable[]{
                        p.titleProperty(),
                        p.originalTitleProperty(),
                        p.imagePathProperty(),
                        p.yearProperty(),
                        p.countriesProperty(),
                        p.creatorsProperty(),
                        p.actorsProperty(),
                        p.languageProperty(),
                        p.minutesProperty()};
            }
        });
        movieList = new FilteredList<>(itemList, moviesFilter);
        seriesList = new FilteredList<>(itemList, seriesFilter);
//        movieList = FXCollections.observableArrayList();
//        tvSeriesList = FXCollections.observableArrayList();
    }

    @Override
    public void setClient(PeerEngineClient client) {
        super.setClient(client);
        integratedDB = client.getDatabases().getIntegratedDB();
        Movie.getMovies(integratedDB).stream().forEach(this::newMovie);
        TVSeries.getTVSeries(integratedDB).stream().forEach(this::newTVSeries);
        Chapter.getChapters(integratedDB).stream().forEach(this::newChapter);
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

    //    public ObservableList<Movie> getMovieList() {
//        return movieList;
//    }
//
//    public ObservableList<TVSeries> getTVSeriesList() {
//        return tvSeriesList;
//    }
//
//    public void addNewMovie(Integer id) {
//        movieList.add(getMovie(id));
//    }
//
//    public void updateMovie(Integer id) {
//        Movie movie = getMovie(id);
//        movieList.set(movieList.indexOf(movie), movie);
//    }
//
//    public void updateMovieWithNewMediaContent(Integer id) {
//        updateMovie(id);
//    }
//
//    public void addNewTVSeries(Integer id) {
//        tvSeriesList.add(getTVSeries(id));
//    }
//
//    public void updateTVSeries(Integer id) {
//        TVSeries tvSeries = getTVSeries(id);
//        tvSeriesList.set(tvSeriesList.indexOf(tvSeries), tvSeries);
//    }
//
//    public void updateTVSeriesWithNewMediaContent(Integer id) {
//        updateTVSeries(id);
//    }
//
//    private Movie getMovie(Integer id) {
//        jacz.database.Movie databaseMovie = (jacz.database.Movie) DatabaseMediator.getItem(integratedDB, DatabaseMediator.ItemType.MOVIE, id);
//        return new Movie(id, databaseMovie.getTitle());
//    }
//
//    private TVSeries getTVSeries(Integer id) {
//        jacz.database.TVSeries databaseTVSeries = (jacz.database.TVSeries) DatabaseMediator.getItem(integratedDB, DatabaseMediator.ItemType.TV_SERIES, id);
//        return new TVSeries(id, databaseTVSeries.getTitle());
//    }

    public void newMediaItem(DatabaseMediator.ItemType type, Integer id) {
        switch (type) {
            case MOVIE:
                Movie movie = Movie.getMovieById(integratedDB, id);
                if (movie != null) {
                    newMovie(movie);
                }
                break;
            case TV_SERIES:
                TVSeries tvSeries = TVSeries.getTVSeriesById(integratedDB, id);
                if (tvSeries != null) {
                    newTVSeries(tvSeries);
                }
                break;
            case CHAPTER:
                Chapter chapter = Chapter.getChapterById(integratedDB, id);
                if (chapter != null) {
                    newChapter(chapter);
                }
                break;
        }
    }

    private void newMovie(Movie movie) {
        Platform.runLater(() -> itemList.add(new MediaItem(movie)));
    }

    private void newTVSeries(TVSeries tvSeries) {
        // todo
        itemList.add(new MediaItem(tvSeries));
    }

    private void newChapter(Chapter chapter) {
        itemList.add(new MediaItem(chapter));
    }

    public void updateMediaItem(DatabaseMediator.ItemType itemType, Integer id, boolean hasNewMediaContent) {
        MediaItemType type = MediaItemType.buildType(itemType);
        int index = findMediaItem(type, id);
        if (index >= 0) {
            switch (type) {
                case MOVIE:
                    Movie movie = Movie.getMovieById(integratedDB, id);
                    if (movie != null) {
                        itemList.get(index).update(movie);
                    }
                    break;
                case TV_SERIES:
                    TVSeries tvSeries = TVSeries.getTVSeriesById(integratedDB, id);
                    if (tvSeries != null) {
                        itemList.get(index).update(tvSeries);
                    }
                    break;
                case CHAPTER:
                    Chapter chapter = Chapter.getChapterById(integratedDB, id);
                    if (chapter != null) {
                        itemList.get(index).update(chapter);
                    }
                    break;
            }
        }
    }

    public void mediaItemRemoved(DatabaseMediator.ItemType itemType, Integer id) {
        MediaItemType type = MediaItemType.buildType(itemType);
        int index = findMediaItem(type, id);
        if (index >= 0) {
            itemList.remove(index);
        }
    }

    public MediaItem getMediaItem(MediaItemType type, Integer id) {
        return itemList.get(itemList.indexOf(new MediaItem(type, id)));
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


}
