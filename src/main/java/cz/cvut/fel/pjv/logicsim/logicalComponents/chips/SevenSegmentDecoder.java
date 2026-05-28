package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Decoder converting a 4-bit value to seven-segment outputs.
 */
public class SevenSegmentDecoder extends DigitalChipComponent {

  private static final boolean[][] SYMBOLS = {
      {true, true, true, true, true, true, false},    // 0
      {false, true, true, false, false, false, false},// 1
      {true, true, false, true, true, false, true},   // 2
      {true, true, true, true, false, false, true},   // 3
      {false, true, true, false, false, true, true},  // 4
      {true, false, true, true, false, true, true},   // 5
      {true, false, true, true, true, true, true},    // 6
      {true, true, true, false, false, false, false}, // 7
      {true, true, true, true, true, true, true},     // 8
      {true, true, true, true, false, true, true},    // 9
      {true, true, true, false, true, true, true},    // A
      {false, false, true, true, true, true, true},   // b
      {true, false, false, true, true, true, false},  // C
      {false, true, true, true, true, false, true},   // d
      {true, false, false, true, true, true, true},   // E
      {true, false, false, false, true, true, true}   // F
  };

  /**
   * Creates a seven-segment decoder chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public SevenSegmentDecoder(double x, double y, Color color) {
    super(x, y, color);
  }

  @Override
  void setupNodes() {
    nodes.clear();

    ComponentNode outA = new ComponentNode(TILE * 4, TILE, true, true, color);
    outA.createPin(TILE * 3, TILE, TILE * 4, TILE, componentModel, "a", TILE * 2 + 5, TILE + 5, 13);
    nodes.add(outA);

    ComponentNode outB = new ComponentNode(TILE * 4, TILE * 2, true, true, color);
    outB.createPin(TILE * 3, TILE * 2, TILE * 4, TILE * 2, componentModel, "b", TILE * 2 + 5, TILE * 2 + 5, 13);
    nodes.add(outB);

    ComponentNode outC = new ComponentNode(TILE * 4, TILE * 3, true, true, color);
    outC.createPin(TILE * 3, TILE * 3, TILE * 4, TILE * 3, componentModel, "c", TILE * 2+ 5, TILE * 3 + 5, 13);
    nodes.add(outC);

    ComponentNode outD = new ComponentNode(TILE * 4, TILE * 4, true, true, color);
    outD.createPin(TILE * 3, TILE * 4, TILE * 4, TILE * 4, componentModel, "d", TILE * 2+ 5, TILE * 4 + 5, 13);
    nodes.add(outD);

    ComponentNode outE = new ComponentNode(TILE * 4, TILE * 5, true, true, color);
    outE.createPin(TILE * 3, TILE * 5, TILE * 4, TILE * 5, componentModel, "e", TILE * 2+ 5, TILE * 5 + 5, 13);
    nodes.add(outE);

    ComponentNode outF = new ComponentNode(TILE * 4, TILE * 6, true, true, color);
    outF.createPin(TILE * 3, TILE * 6, TILE * 4, TILE * 6, componentModel, "f", TILE * 2+ 5, TILE * 6 + 5, 13);
    nodes.add(outF);

    ComponentNode outG = new ComponentNode(TILE * 4, TILE * 7, true, false, color);
    outG.createPin(TILE * 3, TILE * 7, TILE * 4, TILE * 7, componentModel, "g", TILE * 2+ 5, TILE * 7 + 5, 13);
    nodes.add(outG);

    ComponentNode inI0 = new ComponentNode(-TILE, TILE * 4, false, false, color);
    inI0.createPin(0, TILE * 4, -TILE, TILE * 4, componentModel, "I0", 5, TILE * 4 + 5, 13);
    nodes.add(inI0);

    ComponentNode inI1 = new ComponentNode(-TILE, TILE * 3, false, false, color);
    inI1.createPin(0, TILE * 3, -TILE, TILE * 3, componentModel, "I1", 5, TILE * 3 + 5, 13);
    nodes.add(inI1);

    ComponentNode inI2 = new ComponentNode(-TILE, TILE * 2, false, false, color);
    inI2.createPin(0, TILE * 2, -TILE, TILE * 2, componentModel, "I2", 5, TILE * 2 + 5, 13);
    nodes.add(inI2);

    ComponentNode inI3 = new ComponentNode(-TILE, TILE, false, false, color);
    inI3.createPin(0, TILE, -TILE, TILE, componentModel, "I3", 5, TILE + 5, 13);
    nodes.add(inI3);

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "Seven Segment Decoder";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, TILE * 3, TILE * 8);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    setupNodes();
    componentModel.getChildren().add(body);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.size() < 11) {
      return;
    }

    int input = 0;
    if (nodes.get(7).getValue()) {
      input += 1;
    }
    if (nodes.get(8).getValue()) {
      input += 2;
    }
    if (nodes.get(9).getValue()) {
      input += 4;
    }
    if (nodes.get(10).getValue()) {
      input += 8;
    }

    // Lookup output segment pattern for hexadecimal value 0..15.
    for (int i = 0; i < 7; i++) {
      nodes.get(i).setValue(SYMBOLS[input][i]);
    }
  }
}
