package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Two-bit selector multiplexor
 */
public class Multiplexor extends DigitalChipComponent {

  private static final int BITS = 2;

  /**
   * Creates a multiplexer chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public Multiplexor(double x, double y, Color color) {
    super(x, y, color);
  }

  @Override
  void setupNodes() {
    nodes.clear();

    int shift = TILE;
    int inputCount = 1 << BITS;
    int bodyWidth = BITS * TILE * 2;

    for (int i = 0; i < inputCount; i++) {
      ComponentNode inputNode = new ComponentNode(-TILE, shift, false, false, color);
      inputNode.createPin(0, shift, -TILE, shift, componentModel, "I" + i, 10, shift + 5, 13);
      nodes.add(inputNode);
      shift += TILE;
    }

    shift = TILE;
    ComponentNode outputNode = new ComponentNode(bodyWidth + TILE, TILE, true, false, color);
    outputNode.createPin(bodyWidth, shift, bodyWidth + TILE, shift, componentModel, "Q", bodyWidth - 20, TILE + 5, 13);
    nodes.add(outputNode);

    int selectorNodeY = inputCount * TILE + TILE * 3;
    int selectorPinY = inputCount * TILE + TILE * 2;
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
    return "MUX chip";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    int inputCount = 1 << BITS;
    Rectangle body = new Rectangle(0, 0, BITS * TILE * 2, inputCount * TILE + TILE * 2);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    int outputIndex = 1 << BITS;
    int firstSelectorIndex = outputIndex + 1;
    if (nodes.size() <= firstSelectorIndex + BITS - 1) {
      return;
    }

    int selectedValue = 0;
    for (int i = 0; i < BITS; i++) {
      if (nodes.get(firstSelectorIndex + i).getValue()) {
        selectedValue |= 1 << i;
      }
    }

    // Forward only the selected input to output.
    nodes.get(outputIndex).setValue(nodes.get(selectedValue).getValue());
  }
}
