package jacz.face.state;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import jacz.database.*;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;
import java.util.function.Predicate;

/**
 * Stores observable lists for the integrated database (movies and series)
 *
 * todo add sortedlist, and filteredlist. Use filteredlist to filter remote content
 *
 * todo add timer that allows marking items with new media content for some time (and for sorting)
 */
public class MediaDatabaseProperties extends GenericStateProperties {

    public static class MediaItem {

        private final DatabaseMediator.ItemType type;

        private final Integer id;

        private final StringProperty title;

        private final StringProperty originalTitle;

        private final StringProperty imagePath;

        private final ObjectProperty<Integer> year;

        private final ObjectProperty<List<CountryCode>> countries;

        private final ObjectProperty<List<String>> creators;

        private final ObjectProperty<List<String>> actors;

        private final ObjectProperty<LanguageCode> language;

        private final ObjectProperty<Integer> minutes;

        private MediaItem(CreationItem creationItem, DatabaseMediator.ItemType type, String imagePath, Integer minutes) {
            this.type = type;
            this.id = creationItem.getId();
            this.title = new SimpleStringProperty(creationItem.getTitle());
            this.originalTitle = new SimpleStringProperty(creationItem.getOriginalTitle());
            this.imagePath = new SimpleStringProperty(imagePath);
            this.year = new SimpleObjectProperty<>(creationItem.getYear());
            this.countries = new SimpleObjectProperty<>(creationItem.getCountries());
            this.creators = new SimpleObjectProperty<>(creationItem.getCreators());
            this.actors = new SimpleObjectProperty<>(creationItem.getActors());
            this.language = new SimpleObjectProperty<>(creationItem.getLanguage());
            this.minutes = new SimpleObjectProperty<>(minutes);
        }

        public MediaItem(Movie movie) {
            this(movie, DatabaseMediator.ItemType.MOVIE, getImagePath(movie), movie.getMinutes());
        }

        public MediaItem(TVSeries tvSeries) {
            this(tvSeries, DatabaseMediator.ItemType.TV_SERIES, getImagePath(tvSeries), null);
        }

        public MediaItem(Chapter chapter) {
            this(chapter, DatabaseMediator.ItemType.CHAPTER, getImagePath(chapter), chapter.getMinutes());
        }

        public MediaItem(DatabaseMediator.ItemType type, Integer id) {
            this.type = type;
            this.id = id;
            this.title = null;
            this.originalTitle = null;
            this.imagePath = null;
            this.year = null;
            this.countries = null;
            this.creators = null;
            this.actors = null;
            this.language = null;
            this.minutes = null;
        }


        private static String getImagePath(ProducedCreationItem producedCreationItem) {
            String hash = producedCreationItem.getImageHash() != null ? producedCreationItem.getImageHash().getHash() : null;
            if (hash != null) {
                return "";
            } else {
                return null;
            }
        }

        private static String getImagePath(Chapter chapter) {
            List<TVSeries> tvSeriesList = chapter.getTVSeries();
            if (!tvSeriesList.isEmpty()) {
                return getImagePath(tvSeriesList.get(0));
            } else {
                return null;
            }
        }

        protected void update(String title) {
            // todo
            Util.setLater(this.title, title);
        }

        public DatabaseMediator.ItemType getType() {
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

    private static final Predicate<MediaItem> moviesFilter = mediaItem -> mediaItem.getType() == DatabaseMediator.ItemType.MOVIE;

    private String integratedDB;

    private final ObservableList<MediaItem> itemList;

    private final ObservableList<MediaItem> moviesList;

//    private final ObservableList<Movie> movieList;
//
//    private final ObservableList<TVSeries> tvSeriesList;

    public MediaDatabaseProperties() {
        this.integratedDB = null;
        itemList = FXCollections.observableArrayList();
        moviesList = new FilteredList<>(itemList, moviesFilter);
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

    public ObservableList<MediaItem> getMovies() {
        return moviesList;
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
        itemList.add(new MediaItem(movie));
    }

    private void newTVSeries(TVSeries tvSeries) {
        itemList.add(new MediaItem(tvSeries));
    }

    private void newChapter(Chapter chapter) {
        itemList.add(new MediaItem(chapter));
    }

    public void updateMediaItem(DatabaseMediator.ItemType type, Integer id, boolean hasNewMediaContent) {
        int index = findMediaItem(type, id);
        if (index >= 0) {
            itemList.get(index).update("");
        }
    }

    public void mediaItemRemoved(DatabaseMediator.ItemType type, Integer id) {
        int index = findMediaItem(type, id);
        if (index >= 0) {
            itemList.remove(index);
        }
    }

    private int findMediaItem(DatabaseMediator.ItemType type, Integer id) {
        return itemList.indexOf(new MediaItem(type, id));
    }
}
