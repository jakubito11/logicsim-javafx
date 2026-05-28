package cz.cvut.fel.pjv.logicsim.logicalComponents.inputs;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Constant logical-zero source.
 */
public class LowInput extends CircuitComponent {

  private static final boolean CONSTANT_VALUE = false;

  /**
   * Creates a constant-low input component.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public LowInput(double x, double y, Color color) {
    super(x, y, color);
  }

  private void setupNodes() {
    nodes.clear();

    ComponentNode outputNode = new ComponentNode(100, 25, true, CONSTANT_VALUE, color);
    outputNode.createPin(50, 25, 100, 25, componentModel, "Y0", 108, 25, 13);
    nodes.add(outputNode);

    startNodeId = outputNode.getId();
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, 50, 50);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    Text lowLabel = new Text(17.5, 30, "0");
    lowLabel.setFill(color);
    lowLabel.setFont(Font.font("Arial", 25));

    setupNodes();
    componentModel.getChildren().addAll(body, lowLabel);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (!nodes.isEmpty() && nodes.get(0) != null) {
      nodes.get(0).setValue(CONSTANT_VALUE);
    }
  }
}
