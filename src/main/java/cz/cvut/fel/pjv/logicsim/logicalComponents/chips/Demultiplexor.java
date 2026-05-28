package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Two-bit demultiplexer routing one input to one of four outputs.
 */
public class Demultiplexor extends DigitalChipComponent {

  private static final int BITS = 2;

  /**
   * Creates a demultiplexer chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public Demultiplexor(double x, double y, Color color) {
    super(x, y, color);
  }

  @Override
  void setupNodes() {
    nodes.clear();

    int shift = TILE;
    int outputCount = 1 << BITS;
    int bodyWidth = BITS * TILE * 2;
    int outputNodeX = bodyWidth + TILE;

    for (int i = 0; i < outputCount; i++) {
      ComponentNode outputNode = new ComponentNode(outputNodeX, shift, true, false, color);
      outputNode.createPin(bodyWidth, shift, outputNodeX, shift, componentModel, "Q" + i, bodyWidth - 35, shift + 5, 13);
      nodes.add(outputNode);
      shift += TILE;
    }

    ComponentNode inputNode = new ComponentNode(-TILE, TILE, false, false, color);
    inputNode.createPin(0, TILE, -TILE, TILE, componentModel, "Q", 5, TILE + 5, 13);
    nodes.add(inputNode);

    shift = TILE;
    int selectorNodeY = outputCount * TILE + TILE * 3;
    int selectorPinY = outputCount * TILE + TILE * 2;
    for (int i = 0; i < BITS; i++) {
      ComponentNode selectNode = new ComponentNode(shift, selectorNodeY, false, false, color);
      selectNode.createPin(shift, selectorPinY, shift, selectorNodeY, componentModel, "S" + i, shift - 10, selectorPinY - 10, 13);
      nodes.add(selectNode);
      shift += TILE;
    }

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "DMUX chip";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    int outputCount = 1 << BITS;
    Rectangle body = new Rectangle(0, 0, BITS * TILE * 2, outputCount * TILE + TILE * 2);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {

    int outputCount = 1 << BITS;
    int inputIndex = outputCount;
    int firstSelectorIndex = inputIndex + 1;
    if (nodes.size() <= firstSelectorIndex + BITS - 1) {
      return;
    }

    int selectedValue = 0;
    for (int i = 0; i < BITS; i++) {
      if (nodes.get(firstSelectorIndex + i).getValue()) {
        selectedValue |= 1 << i;
      }
    }

    // Clear all outputs first, then copy input only to the selected one.
    for (int i = 0; i < outputCount; i++) {
      nodes.get(i).setValue(false);
    }

    nodes.get(selectedValue).setValue(nodes.get(inputIndex).getValue());
  }
}
