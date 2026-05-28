package cz.cvut.fel.pjv.logicsim.logicalComponents.gates;

import javafx.scene.paint.Color;

/**
 * Inverting AND gate (NAND).
 */
public class NandGate extends AndGate {

  /**
   * Creates a NAND gate instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public NandGate(double x, double y, Color color) {
    super(x, y, color);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInverting() { return true; }


  /** {@inheritDoc} */
  @Override
  public String gateGetName() {
    return "NAND gate";
  }

}
