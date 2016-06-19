package jacz.face.controllers;

import jacz.database.DatabaseMediator;
import jacz.database.Movie;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Created by alberto on 6/7/16.
 */
public class MediaListController extends GenericController {

    private static class MediaItemGridCellEventHandler implements EventHandler<MouseEvent> {

        private final DatabaseMediator.ItemType type;

        private final Integer id;

        public MediaItemGridCellEventHandler(DatabaseMediator.ItemType type, Integer id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public void handle(MouseEvent event) {
            System.out.println("mouse clicked: " + type + ", " + id.toString());
        }
    }

    private static class MediaItemGridCell extends GridCell<MediaDatabaseProperties.MediaItem> {
        private AnchorPane rootPane;
        private final Main main;

        public MediaItemGridCell(Main main) {
            this.main = main;
            Pane imagePane = new Pane();
            imagePane.setPrefWidth(140);
            imagePane.setPrefHeight(180);
            ImageView image = new ImageView();
            //imagePane.getChildren().add(image);
            Label title = new Label();
            Label year = new Label();

            imagePane.setStyle("-fx-background-color: red");
            title.setStyle("-fx-background-color: aqua");
            year.setStyle("-fx-background-color: blue");

            VBox vBoxPane = new VBox(image, title, year);
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
            if(empty) {
                this.setGraphic(null);
            } else {
                VBox vBoxPane = (VBox) rootPane.getChildren().get(0);
                //Pane imagePane = (Pane) vBoxPane.getChildren().get(0);
                ImageView imageView = (ImageView) vBoxPane.getChildren().get(0);
                Label title = (Label) vBoxPane.getChildren().get(1);
                Label year = (Label) vBoxPane.getChildren().get(2);
                if (item.getImagePath() != null) {
                    File imageFile = new File(item.getImagePath());
                    Image image = new Image(imageFile.toURI().toString(), 140, 180, true, true);
                    imageView.setImage(image);
                    imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
//                    imageView.setFitHeight(50);
//                    //imageView.setFitHeight(200);
//                    imageView.setPreserveRatio(true);
//                    imageView.setSmooth(true);

                }
                title.setText(item.getTitle());
                if (item.getYear() != null) {
                    year.setText("(" + item.getYear().toString() + ")");
                } else {
                    year.setText("?");
                }
                this.setGraphic(rootPane);

                setOnMouseClicked(event -> {
                    System.out.println("click on cell " + this.getItem().getTitle());
                    main.getNavigationHistory().navigate(NavigationHistory.Element.itemDetail(NavigationHistory.MediaType.MOVIES, item.getId()));
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

    GridView<MediaDatabaseProperties.MediaItem> moviesGridView;

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

        moviesGridView = new GridView<>();
        filteredMovies = new FilteredList<>(PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMovieList());
        filteredSeries = new FilteredList<>(PropertiesAccessor.getInstance().getMediaDatabaseProperties().getSeriesList());
        moviesGridView.setCellFactory(mediaItem -> new MediaItemGridCell(main));
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

        vBoxPane.getChildren().add(moviesGridView);
        moviesGridView.setCellHeight(200);
        moviesGridView.setCellWidth(150);
        moviesGridView.setHorizontalCellSpacing(10);
        moviesGridView.setVerticalCellSpacing(10);

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
        if (main.getNavigationHistory().getCurrentElement().mediaType == NavigationHistory.MediaType.MOVIES) {
            System.out.println("movies");
            moviesGridView.setItems(filteredMovies);
        } else {
            System.out.println("series");
            moviesGridView.setItems(filteredSeries);
        }
    }


    public void newMovie() {
        System.out.println("new movie");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane newMoviePane = fxmlLoader.load(getClass().getResource("/view/edit_movie.fxml").openStream());
            //AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("view/settings.fxml").openStream());
            final EditMovieController editMovieController = fxmlLoader.getController();
            editMovieController.setMain(main);

            Dialog<EditMovieController.MovieData> newMovieDialog = new Dialog<>();
            newMovieDialog.setTitle("new movie");
            newMovieDialog.getDialogPane().setContent(newMoviePane);

            // Set the button types.
            newMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            // Convert the result to a settings value when the ok button is clicked.
            newMovieDialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return editMovieController.buildMovieData();
                } else {
                    return null;
                }
            });

            Optional<EditMovieController.MovieData> result = newMovieDialog.showAndWait();

            result.ifPresent(newMovie -> {
                System.out.println(newMovie.toString());
                Movie movie = new Movie(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), newMovie.title);
                movie.setOriginalTitle(newMovie.originalTitle);
                movie.setYear(newMovie.year);
                ClientAccessor.getInstance().getClient().localItemModified(movie);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
