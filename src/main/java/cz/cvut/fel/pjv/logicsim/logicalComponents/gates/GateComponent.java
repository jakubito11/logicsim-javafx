package cz.cvut.fel.pjv.logicsim.logicalComponents.gates;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;

/**
 * Gate components rendered with input/output
 * pins.
 */
abstract class GateComponent extends CircuitComponent {

  protected static final double BODY_WIDTH = 50;

  protected static final double FIRST_PIN_Y = 25;
  protected static final double PIN_SPACING = 50;

  protected static final double INPUT_PIN_X = -75;
  protected static final double OUTPUT_PIN_X = 125;

  protected static final double LABEL_OUTPUT = 135;
  protected static final double LABEL_INPUT = -100;
  protected static final double LABEL_Y = 55;


  protected int numberOfInputs = 2;

  /**
   * Creates a gate component at the given canvas position.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public GateComponent(double x, double y, Color color) {
    super(x, y, color);
  }

  void setupNodes() {
    nodes.clear();
    double shift = FIRST_PIN_Y;

    for (int i = 0; i < numberOfInputs; i++) {
      ComponentNode inputNode = new ComponentNode(INPUT_PIN_X,  shift, false, false, color);
      inputNode.createPin(0, shift, INPUT_PIN_X, shift, componentModel, "I" + i, LABEL_INPUT, shift + 5, 13);
      nodes.add(inputNode);
      shift += PIN_SPACING;
    }

    double outputY = (numberOfInputs * PIN_SPACING) / 2.0;
    ComponentNode outputNode = new ComponentNode(OUTPUT_PIN_X, outputY, true, false, color);
    outputNode.createPin(BODY_WIDTH, outputY, OUTPUT_PIN_X, outputY, componentModel, "Y0", LABEL_OUTPUT, LABEL_Y, 13);
    nodes.add(outputNode);

    if (!nodes.isEmpty()) {
      startNodeId = nodes.get(0).getId();
    }
  }

  /**
   * Returns gate name
   *
   * @return gate name
   */
  public abstract String gateGetName();

  abstract boolean calculateFunction();

  /**
   * Evaluates gate output for the current simulation step.
   */
  public abstract void execute();

  /**
   * Builds or rebuilds all JavaFX nodes representing this gate.
   */
  public abstract void render();

  /**
   * Indicates whether output should be inverted (for NAND/NOR/XNOR).
   *
   * @return {@code true} when output inversion is enabled
   */
  public boolean isInverting() { return false; }

}
