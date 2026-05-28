package cz.cvut.fel.pjv.logicsim.logicalComponents.outputs;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Single-bit output indicator.
 */
public class LogicalOutput extends CircuitComponent {

  private Text valueText;
  private Text labelText;

  /**
   * Creates a logical output component.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public LogicalOutput(double x, double y, Color color) {
    super(x, y, color);
  }


  private void setupNodes() {
    nodes.clear();
    ComponentNode inputNode = new ComponentNode(-60, 20, false, false, color);
    inputNode.createPin(0, 20, -60, 20, componentModel, "I0", -86, 25, 13);
    nodes.add(inputNode);
    startNodeId = inputNode.getId();
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

    labelText = new Text(20, -8, "");
    labelText.setFill(color);
    labelText.setFont(Font.font("Arial", 14));
    labelText.setTextOrigin(javafx.geometry.VPos.CENTER);

    valueText = new Text(15, 30, "?");
    valueText.setFill(color);
    valueText.setFont(Font.font("Arial", 25));

    setupNodes();
    componentModel.getChildren().addAll(body, valueText, labelText);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.isEmpty() || nodes.get(0) == null) {
      return;
    }
    valueText.setText(nodes.get(0).getValue() ? "1" : "0");
  }
}
