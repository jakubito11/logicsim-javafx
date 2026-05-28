package cz.cvut.fel.pjv.logicsim;

public interface UiMessageLogger {

  /**
   * Displays an information message
   *
   * @param message text to show to the user
   */
  void info(String message);

  /**
   * Displays an error message.
   *
   * @param message text to show to the user
   */
  void error(String message);
}
