package cz.cvut.fel.pjv.logicsim.gui;

import cz.cvut.fel.pjv.logicsim.controllers.UserPreferencesController;
import cz.cvut.fel.pjv.logicsim.Main;
import java.util.Objects;
import javafx.scene.Parent;

/**
 * Assembles the root JavaFX node graph for the main application window.
 */
public class MainWindow {

  private final Parent root;
  private final Main controller;

  /**
   * Creates main window content and applies the global stylesheet.
   *
   * @param closeApplicationAction action invoked when user requests app close
   * @param userPreferences preferences provider for persisted UI options
   */
  public MainWindow(Runnable closeApplicationAction, UserPreferencesController userPreferences) {
    this.controller = new Main();
    controller.setCloseApplicationAction(closeApplicationAction);
    controller.setUserPreferences(userPreferences);
    controller.applySavedPreferences();
    this.root = controller.getRoot();

    root.getStylesheets().add(
        Objects.requireNonNull(
            MainWindow.class.getResource("/cz/cvut/fel/pjv/logicsim/css/app.css")).toExternalForm()
    );
  }

  /**
   * Returns root node of the fully assembled main window.
   *
   * @return JavaFX root node for scene creation
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Executes controller shutdown logic before application exit.
   */
  public void shutdown() {
    controller.shutdown();
  }

}
