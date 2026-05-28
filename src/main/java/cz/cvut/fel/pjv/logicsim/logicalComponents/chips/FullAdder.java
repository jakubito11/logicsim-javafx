package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * One-bit full adder with carry-in and carry-out.
 */
public class FullAdder extends DigitalChipComponent {


  /**
   * Creates a full adder chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public FullAdder(double x, double y, Color color) {
    super(x, y, color);
  }

  @Override
  void setupNodes() {
    nodes.clear();

    ComponentNode inputA = new ComponentNode(-TILE, TILE, false, false, color);
    inputA.createPin(0, TILE, -TILE, TILE, componentModel, "A", 5, TILE + 5, 13);
    nodes.add(inputA);

    ComponentNode inputB = new ComponentNode(-TILE, TILE * 2, false, false, color);
    inputB.createPin(0, TILE * 2, -TILE, TILE * 2, componentModel, "B", 5, TILE * 2 + 5, 13);
    nodes.add(inputB);

    ComponentNode inputCin = new ComponentNode(-TILE, TILE * 3, false, false, color);
    inputCin.createPin(0, TILE * 3, -TILE, TILE * 3, componentModel, "Cin", 5, TILE * 3 + 5, 13);
    nodes.add(inputCin);

    ComponentNode sumOutput = new ComponentNode(TILE * 4, TILE, true, false, color);
    sumOutput.createPin(TILE * 3, TILE, TILE * 4, TILE, componentModel, "S", TILE * 2 - 5, TILE + 5, 13);
    nodes.add(sumOutput);

    ComponentNode carryOutput = new ComponentNode(TILE * 4, TILE * 3, true, false, color);
    carryOutput.createPin(TILE * 3, TILE * 3, TILE * 4, TILE * 3, componentModel, "C", TILE * 2 - 5, TILE * 3 + 5, 13);
    nodes.add(carryOutput);

    startNodeId = nodes.get(0).getId();

  }

  @Override
  String getChipName() {
    return "Full Adder";
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

    boolean a = nodes.get(0).getValue();
    boolean b = nodes.get(1).getValue();
    boolean cin = nodes.get(2).getValue();

    nodes.get(3).setValue(a ^ b ^ cin);
    nodes.get(4).setValue((a && b) || (b && cin) || (a && cin));
  }
}
