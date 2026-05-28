package cz.cvut.fel.pjv.logicsim.controllers;

import java.util.prefs.Preferences;

/**
 * Preferences {@link Preferences} that stores user-level UI settings.
 */
public class UserPreferencesController {

  public static final double DEFAULT_WINDOW_WIDTH = 1200;
  public static final double DEFAULT_WINDOW_HEIGHT = 800;

  private static final String WINDOW_WIDTH_KEY = "window.width";
  private static final String WINDOW_HEIGHT_KEY = "window.height";
  private static final String SHOW_GRID_KEY = "options.showGrid";
  private static final String WHITE_BACKGROUND_KEY = "options.whiteBackground";
  private static final String IEC_GATES_KEY = "options.iecGates";

  private final Preferences preferences;

  /**
   * Creates preferences controller
   */
  public UserPreferencesController() {
    this.preferences = Preferences.userNodeForPackage(UserPreferencesController.class);
  }

  public double getWindowWidth() {
    return preferences.getDouble(WINDOW_WIDTH_KEY, DEFAULT_WINDOW_WIDTH);
  }

  public void setWindowWidth(double width) {
    preferences.putDouble(WINDOW_WIDTH_KEY, width);
  }

  public double getWindowHeight() {
    return preferences.getDouble(WINDOW_HEIGHT_KEY, DEFAULT_WINDOW_HEIGHT);
  }

  public void setWindowHeight(double height) {
    preferences.putDouble(WINDOW_HEIGHT_KEY, height);
  }

  public boolean isShowGrid() {
    return preferences.getBoolean(SHOW_GRID_KEY, true);
  }

  public void setShowGrid(boolean showGrid) {
    preferences.putBoolean(SHOW_GRID_KEY, showGrid);
  }

  public boolean isWhiteBackground() {
    return preferences.getBoolean(WHITE_BACKGROUND_KEY, false);
  }

  public void setWhiteBackground(boolean whiteBackground) {
    preferences.putBoolean(WHITE_BACKGROUND_KEY, whiteBackground);
  }



  public void setIecGates(boolean iecGates) {
    preferences.putBoolean(IEC_GATES_KEY, iecGates);
  }

}
