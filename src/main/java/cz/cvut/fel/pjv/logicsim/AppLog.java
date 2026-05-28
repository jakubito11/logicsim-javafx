package cz.cvut.fel.pjv.logicsim;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central logging utility for the application.
 * It keeps references to created loggers so logging can be enabled or disabled
 * globally from the UI.
 */
public final class AppLog {

  private static final Set<Logger> REGISTERED_LOGGERS = ConcurrentHashMap.newKeySet();
  private static volatile boolean enabled = true;

  private AppLog() {
    // Utility class.
  }

  /**
   * Returns a logger associated with the given class and registers it for
   * global on/off control.
   *
   * @param owner class that owns the logger
   * @return configured logger instance
   */
  public static Logger getLogger(Class<?> owner) {
    Logger logger = Logger.getLogger(owner.getName());
    REGISTERED_LOGGERS.add(logger);
    applyLevel(logger);
    return logger;
  }

  /**
   * Indicates whether application logging is currently enabled.
   *
   * @return {@code true} when logging is enabled
   */
  public static boolean isEnabled() {
    return enabled;
  }

  /**
   * Enables or disables all registered loggers at once.
   *
   * @param enabled requested logging state
   */
  public static void setEnabled(boolean enabled) {
    AppLog.enabled = enabled;
    for (Logger logger : REGISTERED_LOGGERS) {
      if (logger != null) {
        applyLevel(logger);
      }
    }
  }

  private static void applyLevel(Logger logger) {
    logger.setLevel(enabled ? Level.ALL : Level.OFF);
  }
}
