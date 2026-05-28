package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import javafx.scene.paint.Color;

/**
 * class for digital chips
 */
abstract class DigitalChipComponent extends CircuitComponent {

  protected static final int TILE = 25;

  /**
   * Creates a chip component at the given editor position.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color stroke color
   */
  public DigitalChipComponent(double x, double y, Color color) {
    super(x, y, color);
  }

  abstract void setupNodes();

  abstract String getChipName();

  /**
   * Creates the JavaFX visual model chip and its pins.
   */
  public abstract void render();

  /**
   * Executes one logical update of this chip.
   */
  public abstract void execute();

}
