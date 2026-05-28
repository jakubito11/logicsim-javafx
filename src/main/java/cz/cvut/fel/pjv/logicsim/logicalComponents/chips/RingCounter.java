package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Ten-bit ring counter with clock-enable and reset.
 */
public class RingCounter extends DigitalChipComponent {

  private static final int BITS = 10;
  private boolean lastClock;

  /**
   * Creates a ring counter chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public RingCounter(double x, double y, Color color) {
    super(x, y, color);
    this.lastClock = false;
  }

  @Override
  void setupNodes() {
    nodes.clear();

    int shift = TILE;
    for (int i = 0; i < BITS; i++) {
      ComponentNode outputNode = new ComponentNode(shift, -TILE, true, false, color);
      outputNode.createPin(shift, 0, shift, -TILE, componentModel, "Q" + i, shift - 10, 18, 9);
      nodes.add(outputNode);
      shift += TILE;
    }

    ComponentNode ceNode = new ComponentNode(TILE * 3, TILE * 4, false, false, color);
    ceNode.createPin(TILE * 3, TILE * 3, TILE * 3, TILE * 4, componentModel, "CE'", TILE * 2, TILE * 2 + 5, 13);
    nodes.add(ceNode);

    ComponentNode resetNode = new ComponentNode(TILE * 5, TILE * 4, false, false, color);
    resetNode.createPin(TILE * 5, TILE * 3, TILE * 5, TILE * 4, componentModel, "R", TILE * 5 - 5, TILE * 2 + 5, 13);
    nodes.add(resetNode);

    ComponentNode clockNode = new ComponentNode(-TILE, TILE * 2, false, false, color);
    clockNode.createPin(0, TILE * 2, -TILE, TILE * 2, componentModel, "CLK", 5, TILE * 2 + 5, 13);
    nodes.add(clockNode);

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "Ring Counter";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, BITS * TILE + TILE, TILE * 3);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.size() < BITS + 3) {
      return;
    }

    int ceIndex = BITS;
    int resetIndex = BITS + 1;
    int clkIndex = BITS + 2;

    boolean running = !nodes.get(ceIndex).getValue();

    int activeIndex = BITS;
    for (int i = 0; i < BITS; i++) {
      if (nodes.get(i).getValue()) {
        activeIndex = i;
        break;
      }
    }

    boolean clock = nodes.get(clkIndex).getValue();
    // Shift the active bit by one position on each rising edge.
    if (clock && !lastClock && running) {
      if (activeIndex < BITS) {
        nodes.get(activeIndex).setValue(false);
        activeIndex++;
      }
      activeIndex %= BITS;
      nodes.get(activeIndex).setValue(true);
    }

    // Reset forces the first output active and all others low.
    if (nodes.get(resetIndex).getValue()) {
      for (int i = 1; i < BITS; i++) {
        nodes.get(i).setValue(false);
      }
      nodes.get(0).setValue(true);
    }

    lastClock = clock;
  }
}
