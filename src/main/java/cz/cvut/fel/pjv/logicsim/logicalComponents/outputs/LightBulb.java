package cz.cvut.fel.pjv.logicsim.logicalComponents.outputs;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * light bulb indicator is on when input is 1 otherwise off
 */
public class LightBulb extends CircuitComponent {

  private static final Color BULB_ON_COLOR = Color.ORANGE;
  private final Circle bulb;

  /**
   * Creates a light bulb output component.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public LightBulb(double x, double y, Color color) {
    super(x, y, color);
    this.bulb = new Circle(20, 20, 20);
    this.bulb.setStroke(color);
    this.bulb.setStrokeWidth(2);
    this.bulb.setFill(Color.TRANSPARENT);
  }

  private void setupNodes() {
    nodes.clear();
    ComponentNode inputNode = new ComponentNode(-80, 20, false, false, color);
    inputNode.createPin(0, 20, -80, 20, componentModel, "I0", -106, 25, 13);
    nodes.add(inputNode);
    startNodeId = inputNode.getId();
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Line slash1 = new Line(7, 7, 33, 33);
    slash1.setStroke(color);
    slash1.setStrokeWidth(2);

    Line slash2 = new Line(33, 7, 7, 33);
    slash2.setStroke(color);
    slash2.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().addAll(slash1, slash2, bulb);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.isEmpty() || nodes.get(0) == null) {
      return;
    }
    bulb.setFill(nodes.get(0).getValue() ? BULB_ON_COLOR : Color.TRANSPARENT);
  }
}
