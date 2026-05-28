package cz.cvut.fel.pjv.logicsim.logicalComponents.gates;

import javafx.scene.paint.Color;

/**
 * Inverting OR gate (NOR).
 */
public class NorGate extends OrGate {

  /**
   * Creates a NOR gate instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public NorGate(double x, double y, Color color) {
    super(x, y, color);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInverting() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public String gateGetName() {
    return "NOR gate";
  }

}
