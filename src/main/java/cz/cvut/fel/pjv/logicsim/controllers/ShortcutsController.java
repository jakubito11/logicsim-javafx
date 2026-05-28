package cz.cvut.fel.pjv.logicsim.controllers;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Utility class that applies keyboard shortcuts to top-menu actions.
 */
public final class ShortcutsController {

  private ShortcutsController() {
  }

  /**
   * Assigns default keyboard accelerators to menu items.
   *
   * @param newBlankCircuitMenuItem action for creating a blank circuit
   * @param openFileMenuItem action for opening a file
   * @param saveAsMenuItem action for saving a file
   * @param toggleFullScreenMenuItem action for fullscreen mode
   * @param exitMenuItem action for closing the application
   * @param deleteMenuItem action for deleting selected components
   * @param editZoomInMenuItem action for zooming in
   * @param editZoomOutMenuItem action for zooming out
   */
  public static void applyShortcuts(
      MenuItem newBlankCircuitMenuItem,
      MenuItem openFileMenuItem,
      MenuItem saveAsMenuItem,
      MenuItem toggleFullScreenMenuItem,
      MenuItem exitMenuItem,
      MenuItem deleteMenuItem,
      MenuItem editZoomInMenuItem,
      MenuItem editZoomOutMenuItem
  ) {
    newBlankCircuitMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+N"));
    openFileMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
    saveAsMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
    toggleFullScreenMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F11));
    exitMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));

    deleteMenuItem.setAccelerator(KeyCombination.keyCombination("Delete"));
    editZoomInMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHORTCUT_DOWN));
    editZoomOutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.SHORTCUT_DOWN));
  }



}
