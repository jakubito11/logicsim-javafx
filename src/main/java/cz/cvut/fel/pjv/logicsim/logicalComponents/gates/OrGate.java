package cz.cvut.fel.pjv.logicsim.logicalComponents.gates;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * IEC-style OR gate
 */
public class OrGate extends GateComponent {

  /**
   * Creates an OR gate instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public OrGate(double x, double y, Color color) {
    super(x, y, color);
  }

  /** {@inheritDoc} */
  @Override
  public String gateGetName() {
    return "OR gate";
  }

  @Override
  boolean calculateFunction() {
    boolean q = false;

    for (int i = 0; i < numberOfInputs; i++) {
      q |= nodes.get(i).getValue();
    }

    return isInverting() ? !q : q;
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

    Text orSymbol = new Text(15, 40, "1");
    orSymbol.setFill(color);
    orSymbol.setFont(Font.font("Arial", FontWeight.BOLD, 25));

    if (isInverting()) {
      Circle dot = new Circle(55, PIN_SPACING, 7);
      dot.setFill(color);
      componentModel.getChildren().addAll(iecGateBody, orSymbol, dot);
    } else {
      componentModel.getChildren().addAll(iecGateBody, orSymbol);
    }

    setupNodes();
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.size() <= numberOfInputs) {
      return;
    }
    ComponentNode outputNode = nodes.get(numberOfInputs);
    if (outputNode != null) {
      outputNode.setValue(calculateFunction());
    }
  }
}
