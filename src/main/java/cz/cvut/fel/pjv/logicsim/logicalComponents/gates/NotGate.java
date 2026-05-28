package cz.cvut.fel.pjv.logicsim.logicalComponents.gates;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * IEC-style NOT gate
 */
public class NotGate extends GateComponent {

  /**
   * Creates a NOT gate instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public NotGate(double x, double y, Color color) {
    super(x, y, color);
  }

  /** {@inheritDoc} */
  @Override
  public String gateGetName() {
    return "NOT gate";
  }

  @Override
  boolean calculateFunction() {
    if (nodes.isEmpty()) {
      return false;
    }
    ComponentNode inputNode = nodes.get(0);
    return inputNode == null || !inputNode.getValue();
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle iecGateBody = new Rectangle(0, 0, BODY_WIDTH, numberOfInputs * PIN_SPACING);
    iecGateBody.setFill(Color.TRANSPARENT);
    iecGateBody.setStroke(color);
    iecGateBody.setStrokeWidth(2);

    Text notSymbol = new Text(15, 40, "1");
    notSymbol.setFill(color);
    notSymbol.setFont(Font.font("Arial", FontWeight.BOLD, 25));

    Circle dot = new Circle(55, PIN_SPACING, 7);
    dot.setFill(color);

    componentModel.getChildren().addAll(iecGateBody, notSymbol, dot);
    setupNodes();
  }

  @Override
  void setupNodes() {
    nodes.clear();
    ComponentNode inputNode = new ComponentNode(INPUT_PIN_X, PIN_SPACING, false, false, color);
    inputNode.createPin(0, PIN_SPACING, INPUT_PIN_X, PIN_SPACING, componentModel, "I0", LABEL_INPUT, LABEL_Y, 13);
    nodes.add(inputNode);

    ComponentNode outputNode = new ComponentNode(OUTPUT_PIN_X, PIN_SPACING, true, false, color);
    outputNode.createPin(BODY_WIDTH, PIN_SPACING, OUTPUT_PIN_X, PIN_SPACING, componentModel, "Y0", LABEL_OUTPUT, LABEL_Y, 13);
    nodes.add(outputNode);

    startNodeId = nodes.get(0).getId();
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.size() < 2) {
      return;
    }

    ComponentNode outputNode = nodes.get(1);
    if (outputNode != null) {
      outputNode.setValue(calculateFunction());
    }
  }
}
