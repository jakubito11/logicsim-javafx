package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Rising-edge triggered D flip-flop with Q and inverted Q outputs.
 */
public class DflipFlop extends DigitalChipComponent {

  private boolean lastClock;

  /**
   * Creates a D flip-flop chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public DflipFlop(double x, double y, Color color) {
    super(x, y, color);
    this.lastClock = false;
  }

  @Override
  void setupNodes() {
    nodes.clear();

    ComponentNode dInput = new ComponentNode(-TILE, TILE, false, false, color);
    dInput.createPin(0, TILE, -TILE, TILE, componentModel, "D", 5, TILE + 5, 13);
    nodes.add(dInput);

    ComponentNode clkInput = new ComponentNode(-TILE, TILE * 3, false, false, color);
    clkInput.createPin(0, TILE * 3, -TILE, TILE * 3, componentModel, "CLK", 5, TILE * 3 + 5, 13);
    nodes.add(clkInput);

    ComponentNode qOutput = new ComponentNode(TILE * 4, TILE, true, false, color);
    qOutput.createPin(TILE * 3, TILE, TILE * 4, TILE, componentModel, "Q", TILE * 2 - 5, TILE + 5, 13);
    nodes.add(qOutput);

    ComponentNode qNotOutput = new ComponentNode(TILE * 4, TILE * 3, true, true, color);
    qNotOutput.createPin(TILE * 3, TILE * 3, TILE * 4, TILE * 3, componentModel, "Q'", TILE * 2 - 5, TILE * 3 + 5, 13);
    nodes.add(qNotOutput);

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "DFF chip";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, TILE * 3, TILE * 4);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.size() < 4) {
      return;
    }

    boolean clock = nodes.get(1).getValue();
    // Latch D into Q only on rising edge.
    if (clock && !lastClock) {
      nodes.get(2).setValue(nodes.get(0).getValue());
    }

    nodes.get(3).setValue(!nodes.get(2).getValue());
    lastClock = clock;
  }

}
