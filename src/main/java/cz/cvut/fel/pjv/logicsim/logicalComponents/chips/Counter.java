package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Four-bit synchronous binary counter with reset input.
 */
public class Counter extends DigitalChipComponent {

  private static final int BITS = 4;
  private boolean lastClock;

  /**
   * Creates a counter chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public Counter(double x, double y, Color color) {
    super(x, y, color);
    this.lastClock = false;
  }

  @Override
  void setupNodes() {

    nodes.clear();

    int shift = 25;
    ComponentNode clockNode = new ComponentNode(-25, shift, false, false, color);
    clockNode.createPin(0, shift, -25, shift, componentModel, "CLK", 5, 27, 13);
    nodes.add(clockNode);

    for (int i = 0; i < BITS; i++) {
      ComponentNode outputNode = new ComponentNode(100, shift, true, false, color);
      outputNode.createPin(75, shift, 100, shift, componentModel, "Q" + (BITS - i - 1), 40, shift + 2, 13);
      nodes.add(outputNode);
      shift += 25;
    }


    ComponentNode resetNode = new ComponentNode(-20, shift - 20, false, false, color);
    resetNode.createPin(0, shift - 25, -20, shift - 25, componentModel, "R", 5, shift - 20, 13);
    nodes.add(1, resetNode);

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "Counter";
  }

  @Override
  /** {@inheritDoc} */
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, 75, BITS * 25 + 25);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  @Override
  /** {@inheritDoc} */
  public void execute() {

    if (nodes.size() < BITS + 2) {
      return;
    }

    // reset all values to low
    if (nodes.get(1).getValue()) {
      for (int i = 0; i < BITS; i++) {
        nodes.get(i + 2).setValue(false);
      }
    }

    // Increment only on a rising clock edge.
    if (nodes.get(0).getValue() && !lastClock) {
      int value = 0;

      int lastBit = 2 + BITS - 1;

      for (int i = 0; i < BITS; i++) {
        if (nodes.get(lastBit - i).getValue()) {
          value |= 1 << i;
        }
      }

      value += 1;

      for (int i = 0; i < BITS; i++) {
        nodes.get(lastBit - i).setValue((value & (1 << i)) != 0);
      }
    }

    lastClock = nodes.get(0).getValue();
  }
}
