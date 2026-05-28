package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Four-bit latch that stores inputs on load signal edge.
 */
public class Latch extends DigitalChipComponent {

  private static final int BITS = 4;
  private static final int TILE = 25;
  private boolean lastLoad;

  /**
   * Creates a latch chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public Latch(double x, double y, Color color) {
    super(x, y, color);
    this.lastLoad = false;
  }

  @Override
  void setupNodes() {
    nodes.clear();

    int shift = TILE;
    for (int i = 0; i < BITS; i++) {
      ComponentNode inputNode = new ComponentNode(-TILE, shift, false, false, color);
      inputNode.createPin(0, shift, -TILE, shift, componentModel, "I" + (BITS - i - 1), 5, shift + 5, 13);
      nodes.add(inputNode);
      shift += TILE;
    }

    ComponentNode loadNode = new ComponentNode(-TILE, shift, false, false, color);
    loadNode.createPin(0, shift, -TILE, shift, componentModel, "LD", 5, shift + 5, 13);
    nodes.add(loadNode);

    shift = TILE;
    for (int i = 0; i < BITS; i++) {
      ComponentNode outputNode = new ComponentNode(TILE * 4, shift, true, false, color);
      outputNode.createPin(TILE * 3, shift, TILE * 4, shift, componentModel, "O", TILE * 2 + 5, shift + 5, 13);
      nodes.add(outputNode);
      shift += TILE;
    }

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "Latch";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, TILE * 3, BITS * TILE + TILE * 2);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    int loadIndex = BITS;
    int firstOutputIndex = BITS + 1;
    if (nodes.size() < firstOutputIndex + BITS) {
      return;
    }

    boolean load = nodes.get(loadIndex).getValue();
    // Store current inputs only when load transitions from 0 to 1.
    if (load && !lastLoad) {
      for (int i = 0; i < BITS; i++) {
        nodes.get(firstOutputIndex + i).setValue(nodes.get(i).getValue());
      }
    }

    lastLoad = load;
  }
}
