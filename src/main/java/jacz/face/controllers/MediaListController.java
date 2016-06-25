package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.database.TVSeries;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.MediaItemType;
import jacz.face.util.Util;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Created by alberto on 6/7/16.
 */
public class MediaListController extends GenericController {

//    private static class MediaItemGridCellEventHandler implements EventHandler<MouseEvent> {
//
//        private final DatabaseMediator.ItemType type;
//
//        private final Integer id;
//
//        public MediaItemGridCellEventHandler(DatabaseMediator.ItemType type, Integer id) {
//            this.type = type;
//            this.id = id;
//        }
//
//        @Override
//        public void handle(MouseEvent event) {
//            System.out.println("mouse clicked: " + type + ", " + id.toString());
//        }
//    }

    private static class MediaItemGridCell extends GridCell<MediaDatabaseProperties.MediaItem> {
        private AnchorPane rootPane;
        private final Main main;

        public MediaItemGridCell(Main main) {
            this.main = main;
            Pane imagePane = new Pane();
            imagePane.setPrefWidth(140);
            imagePane.setPrefHeight(180);
            Label title = new Label();
            Label year = new Label();

            imagePane.setStyle("-fx-background-color: red");
            title.setStyle("-fx-background-color: aqua");
            year.setStyle("-fx-background-color: blue");

            VBox vBoxPane = new VBox(imagePane, title, year);
            vBoxPane.setPrefWidth(160);
            vBoxPane.setPrefHeight(210);

            rootPane = new AnchorPane(vBoxPane);
            AnchorPane.setLeftAnchor(vBoxPane, 10d);
            AnchorPane.setTopAnchor(vBoxPane, 10d);
            AnchorPane.setRightAnchor(vBoxPane, 10d);
            AnchorPane.setBottomAnchor(vBoxPane, 10d);
            rootPane.setPrefWidth(150);
            rootPane.setPrefHeight(200);

//            setOnMouseClicked(event -> {
//                System.out.println("click on cell " + this.getItem().getTitle());
//                main.getNavigationHistory().navigate(NavigationHistory.Element.itemDetail(NavigationHistory.MediaType.MOVIES, itemId));
//                try {
//                    main.displayCurrentNavigationWindow();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });

//            setOnMouseClicked(new MediaItemGridCellEventHandler(this.getItem().getType(), this.getItem().getId()));
        }


        protected void updateItem(MediaDatabaseProperties.MediaItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                this.setGraphic(null);
            } else {
                VBox vBoxPane = (VBox) rootPane.getChildren().get(0);
                Pane imagePane = (Pane) vBoxPane.getChildren().get(0);
                //ImageView imageView = (ImageView) vBoxPane.getChildren().get(0);
                Label title = (Label) vBoxPane.getChildren().get(1);
                Label year = (Label) vBoxPane.getChildren().get(2);
                Util.displayImage(imagePane, item.getImagePath());
//                if (item.getImagePath() != null) {
//                    File imageFile = new File(item.getImagePath());
//                    Image image = new Image(imageFile.toURI().toString(), 140, 180, true, true);
//                    imageView.setImage(image);
//                    imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
////                    imageView.setFitHeight(50);
////                    //imageView.setFitHeight(200);
////                    imageView.setPreserveRatio(true);
////                    imageView.setSmooth(true);
//
//                }
                title.setText(item.getTitle());
                if (item.getYear() != null) {
                    year.setText("(" + item.getYear().toString() + ")");
                } else {
                    year.setText("?");
                }
                this.setGraphic(rootPane);

                setOnMouseClicked(event -> {
                    System.out.println("click on cell " + this.getItem().getTitle());
                    main.getNavigationHistory().navigate(NavigationHistory.Element.itemDetail(item.getType(), item.getId(), item.getLocalId()));
                    try {
                        main.displayCurrentNavigationWindow();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                //this.setText(item.getTitle());
            }
        }
    }


    @FXML
    VBox vBoxPane;

    @FXML
    TextField filterTextField;

    private FilteredList<MediaDatabaseProperties.MediaItem> filteredMovies;

    private FilteredList<MediaDatabaseProperties.MediaItem> filteredSeries;

    @FXML
    GridView<MediaDatabaseProperties.MediaItem> itemsGridView;

    private class TitleFilter implements Predicate<MediaDatabaseProperties.MediaItem> {

        protected final String text;

        public TitleFilter(String text) {
            this.text = text.toLowerCase();
        }

        @Override
        public boolean test(MediaDatabaseProperties.MediaItem mediaItem) {
            return mediaItem.getTitle().toLowerCase().contains(text);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo rest of bindings

        //itemsGridView = new GridView<>();
        filteredMovies = new FilteredList<>(PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMovieList());
        filteredSeries = new FilteredList<>(PropertiesAccessor.getInstance().getMediaDatabaseProperties().getSeriesList());
        itemsGridView.setCellFactory(mediaItem -> new MediaItemGridCell(main));
        //final ObservableList<Color> list = FXCollections.observableArrayList();


        //GridView<Color> moviesGridView = new GridView<>(list);
//        moviesGridView.setCellFactory(new Callback<GridView<Color>, GridCell<Color>>() {
//            @Override public GridCell<Color> call(GridView<Color> arg0) {
//                return new ColorGridCell();
//            }
//        });
//
//        Random r = new Random(System.currentTimeMillis());
//        for(int i = 0; i < 15; i++) {
//            list.add(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 1.0));
//        }
//        System.out.println(list.size());

        //moviesGridView.setStyle("-fx-border-color: black");

        //vBoxPane.getChildren().add(itemsGridView);
        itemsGridView.setCellHeight(200);
        itemsGridView.setCellWidth(150);
        itemsGridView.setHorizontalCellSpacing(10);
        itemsGridView.setVerticalCellSpacing(10);

        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("update filter with: " + newValue);
            if (!newValue.isEmpty()) {
                filteredMovies.setPredicate(new TitleFilter(newValue));
                filteredSeries.setPredicate(new TitleFilter(newValue));
            } else {
                filteredMovies.setPredicate(null);
                filteredSeries.setPredicate(null);
            }
        });

//        moviesListView.setItems(PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMovies());
//        moviesListView.setCellFactory(mediaItem -> new MediaItemListCell());
    }

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        if (main.getNavigationHistory().getCurrentElement().mediaItemType == MediaItemType.MOVIE) {
            System.out.println("movies");
            itemsGridView.setItems(filteredMovies);
        } else {
            System.out.println("series");
            itemsGridView.setItems(filteredSeries);
        }
    }


    public void newMovie() {
        // todo DRY code
        System.out.println("new movie");


        switch (main.getNavigationHistory().getCurrentElement().mediaItemType) {

            case MOVIE:
                Optional<EditMovieController.MovieData> resultMovie = main.editMovie(NavigationHistory.DialogIntention.NEW);

                System.out.println("new movie completed");

                resultMovie.ifPresent(newMovie -> {
                    System.out.println("creating new movie: " + newMovie.toString());
                    // the new movie is stored at the local database, as it has been created by the user
                    Movie movie = new Movie(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), newMovie.title);
                    try {
                        DatabaseItem integratedItem = EditMovieController.changeMovie(movie, newMovie);
                        PropertiesAccessor.getInstance().getMediaDatabaseProperties().updateMediaItem(integratedItem, true);
                        main.getNavigationHistory().navigate(NavigationHistory.Element.itemDetail(MediaItemType.MOVIE, integratedItem.getId(), movie.getId()));
                        try {
                            main.displayCurrentNavigationWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case TV_SERIES:
                Optional<EditTVSeriesController.TVSeriesData> resultTVSeries = main.editTVSeries(NavigationHistory.DialogIntention.NEW);

                System.out.println("new movie completed");

                resultTVSeries.ifPresent(newTVSeries -> {
                    System.out.println("creating new movie: " + newTVSeries.toString());
                    TVSeries tvSeries = new TVSeries(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), newTVSeries.title);
                    try {
                        DatabaseItem integratedItem = EditTVSeriesController.changeTVSeries(tvSeries, newTVSeries);
                        PropertiesAccessor.getInstance().getMediaDatabaseProperties().updateMediaItem(integratedItem, true);
                        main.getNavigationHistory().navigate(NavigationHistory.Element.itemDetail(MediaItemType.TV_SERIES, integratedItem.getId(), tvSeries.getId()));
                        try {
                            main.displayCurrentNavigationWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
                break;
        }


    }

}
