package jacz.face.state;

import jacz.database.DatabaseMediator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Stores observable lists for the integrated database (movies and series)
 *
 * todo add sortedlist, and filteredlist. Use filteredlist to filter remote content
 *
 * todo add timer that allows marking items with new media content for some time (and for sorting)
 */
public class MediaDatabaseProperties {

    public static class MediaItem {

        public final Integer id;

        public final String title;

        public MediaItem(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MediaItem)) return false;

            MediaItem mediaItem = (MediaItem) o;
            return id.equals(mediaItem.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public static class Movie extends MediaItem {

        public Movie(Integer id, String title) {
            super(id, title);
        }
    }

    public static class TVSeries extends MediaItem {

        public TVSeries(Integer id, String title) {
            super(id, title);
        }
    }

    private final String integratedDB;

    private final ObservableList<Movie> movieList;

    private final ObservableList<TVSeries> tvSeriesList;

    public MediaDatabaseProperties(String integratedDB) {
        this.integratedDB = integratedDB;
        // todo load initial content with a stream op
        movieList = FXCollections.observableArrayList();
        tvSeriesList = FXCollections.observableArrayList();
    }

    public ObservableList<Movie> getMovieList() {
        return movieList;
    }

    public ObservableList<TVSeries> getTVSeriesList() {
        return tvSeriesList;
    }

    public void addNewMovie(Integer id) {
        movieList.add(getMovie(id));
    }

    public void updateMovie(Integer id) {
        Movie movie = getMovie(id);
        movieList.set(movieList.indexOf(movie), movie);
    }

    public void updateMovieWithNewMediaContent(Integer id) {
        updateMovie(id);
    }

    public void addNewTVSeries(Integer id) {
        tvSeriesList.add(getTVSeries(id));
    }

    public void updateTVSeries(Integer id) {
        TVSeries tvSeries = getTVSeries(id);
        tvSeriesList.set(tvSeriesList.indexOf(tvSeries), tvSeries);
    }

    public void updateTVSeriesWithNewMediaContent(Integer id) {
        updateTVSeries(id);
    }

    private Movie getMovie(Integer id) {
        jacz.database.Movie databaseMovie = (jacz.database.Movie) DatabaseMediator.getItem(integratedDB, DatabaseMediator.ItemType.MOVIE, id);
        return new Movie(id, databaseMovie.getTitle());
    }

    private TVSeries getTVSeries(Integer id) {
        jacz.database.TVSeries databaseTVSeries = (jacz.database.TVSeries) DatabaseMediator.getItem(integratedDB, DatabaseMediator.ItemType.TV_SERIES, id);
        return new TVSeries(id, databaseTVSeries.getTitle());
    }
}
