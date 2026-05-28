package cz.cvut.fel.pjv.logicsim.gui;

import cz.cvut.fel.pjv.logicsim.controllers.UserPreferencesController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * JavaFX application entry point for stage creation and window
 * configuration.
 */
public class LogicSimApplication extends Application {

  private final UserPreferencesController userPreferences = new UserPreferencesController();

  /**
   * Initializes and shows the primary stage.
   *
   * @param stage JavaFX primary stage
   */
  @Override
  public void start(Stage stage) {

    MainWindow mainWindow = new MainWindow(stage::close, userPreferences);

    Scene scene = new Scene(
        mainWindow.getRoot(),
        userPreferences.getWindowWidth(),
        userPreferences.getWindowHeight()
    );

    stage.setTitle("LogicSim");
    stage.setMinWidth(UserPreferencesController.DEFAULT_WINDOW_WIDTH * 0.75);
    stage.setMinHeight(UserPreferencesController.DEFAULT_WINDOW_HEIGHT * 0.75);
    stage.setScene(scene);
    stage.getIcons().add(
        new Image(Objects.requireNonNull(LogicSimApplication.class.getResourceAsStream(
            "/cz/cvut/fel/pjv/logicsim/icons/app-icon.png")))
    );

    // resizable window
    stage.widthProperty().addListener((observable, oldValue, newValue) ->
        userPreferences.setWindowWidth(newValue.doubleValue()));

    stage.heightProperty().addListener((observable, oldValue, newValue) ->
        userPreferences.setWindowHeight(newValue.doubleValue()));

    stage.setOnCloseRequest(event -> mainWindow.shutdown());

    stage.show();
  }
}
