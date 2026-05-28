package cz.cvut.fel.pjv.logicsim.logicalComponents.gates;

import javafx.scene.paint.Color;

/**
 * Inverting XOR gate (XNOR).
 */
public class XnorGate extends XorGate {


  /**
   * Creates an XNOR gate instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public XnorGate(double x, double y, Color color) {
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
    return "XNOR gate";
  }
}
