package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Edge-triggered JK flip-flop with Q and inverted Q outputs.
 */
public class JKflipFlop extends DigitalChipComponent {

  private boolean lastClock;

  /**
   * Creates a JK flip-flop chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public JKflipFlop(double x, double y, Color color) {
    super(x, y, color);
    this.lastClock = false;
  }

  @Override
  void setupNodes() {
    nodes.clear();

    ComponentNode jInput = new ComponentNode(-TILE, TILE, false, false, color);
    jInput.createPin(0, TILE, -TILE, TILE, componentModel, "J", 5, TILE + 5, 13);
    nodes.add(jInput);

    ComponentNode clkInput = new ComponentNode(-TILE, TILE * 2, false, false, color);
    clkInput.createPin(0, TILE * 2, -TILE, TILE * 2, componentModel, "CLK", 5, TILE * 2 + 5, 13);
    nodes.add(clkInput);

    ComponentNode kInput = new ComponentNode(-TILE, TILE * 3, false, false, color);
    kInput.createPin(0, TILE * 3, -TILE, TILE * 3, componentModel, "K", 5, TILE * 3 + 5, 13);
    nodes.add(kInput);

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
    return "JKFF chip";
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
    if (nodes.size() < 5) {
      return;
    }

    boolean clock = nodes.get(1).getValue();
    // Update output on falling edge to match original project behavior.
    boolean transition = !clock && lastClock;

    if (transition) {
      boolean q = nodes.get(3).getValue();
      boolean j = nodes.get(0).getValue();
      boolean k = nodes.get(2).getValue();

      if (j) {
        if (k) {
          q = !q;
        } else {
          q = true;
        }
      } else if (k) {
        q = false;
      }

      nodes.get(3).setValue(q);
    }

    lastClock = clock;
    nodes.get(4).setValue(!nodes.get(3).getValue());
  }
}
