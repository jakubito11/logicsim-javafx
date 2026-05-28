package cz.cvut.fel.pjv.logicsim.gui;

import javafx.application.Application;

/**
 * Plain Java launcher class delegating startup to JavaFX runtime.
 */
public final class LogicSimLauncher {

  private LogicSimLauncher() {
  }

  /**
   * Starts the JavaFX application.
   *
   * @param args CLI arguments passed to JavaFX
   */
  public static void main(String[] args) {
    Application.launch(LogicSimApplication.class, args);
  }
}
