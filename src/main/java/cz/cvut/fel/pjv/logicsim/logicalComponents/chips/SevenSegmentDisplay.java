package cz.cvut.fel.pjv.logicsim.logicalComponents.chips;

import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Visual seven-segment display driven by seven input lines.
 */
public class SevenSegmentDisplay extends DigitalChipComponent {

  private final Line[] segments = new Line[7];
  private final Color ledColor = Color.RED;

  /**
   * Creates a seven-segment display chip instance.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public SevenSegmentDisplay(double x, double y, Color color) {
    super(x, y, color);
  }

  @Override
  void setupNodes() {
    nodes.clear();

    ComponentNode inA = new ComponentNode(-TILE, TILE, false, false, color);
    inA.createPin(0, TILE, -TILE, TILE, componentModel, "a", 5, TILE + 5, 13);
    nodes.add(inA);

    ComponentNode inB = new ComponentNode(-TILE, TILE * 2, false, false, color);
    inB.createPin(0, TILE * 2, -TILE, TILE * 2, componentModel, "b", 5, TILE * 2 + 5, 13);
    nodes.add(inB);

    ComponentNode inC = new ComponentNode(-TILE, TILE * 3, false, false, color);
    inC.createPin(0, TILE * 3, -TILE, TILE * 3, componentModel, "c", 5, TILE * 3 + 5, 13);
    nodes.add(inC);

    ComponentNode inD = new ComponentNode(-TILE, TILE * 4, false, false, color);
    inD.createPin(0, TILE * 4, -TILE, TILE * 4, componentModel, "d", 5, TILE * 4 + 5, 13);
    nodes.add(inD);

    ComponentNode inE = new ComponentNode(TILE * 2, TILE * 6, false, false, color);
    inE.createPin(TILE * 2, TILE * 5, TILE * 2, TILE * 6, componentModel, "e", TILE * 2 - 5, TILE * 5 - 5, 13);
    nodes.add(inE);

    ComponentNode inF = new ComponentNode(TILE * 3, TILE * 6, false, false, color);
    inF.createPin(TILE * 3, TILE * 5, TILE * 3, TILE * 6, componentModel, "f", TILE * 3 - 5, TILE * 5 - 5, 13);
    nodes.add(inF);

    ComponentNode inG = new ComponentNode(TILE * 4, TILE * 6, false, false, color);
    inG.createPin(TILE * 4, TILE * 5, TILE * 4, TILE * 6, componentModel, "g", TILE * 4 - 5, TILE * 5 - 5, 13);
    nodes.add(inG);

    startNodeId = nodes.get(0).getId();
  }

  @Override
  String getChipName() {
    return "Seven Segment Display";
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, TILE * 5, TILE * 5);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);


    setupNodes();
    createSegments();

    componentModel.getChildren().addAll(body);

    for (int i = 0; i < segments.length; i++) {
      componentModel.getChildren().add(segments[i]);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    if (nodes.size() < segments.length) {
      return;
    }

    for (int i = 0; i < segments.length; i++) {
      if (nodes.get(i).getValue()) {
        segments[i].setStroke(ledColor);
        segments[i].setOpacity(1);
      } else {
        segments[i].setOpacity(0);
      }
    }
  }

  private void createSegments() {
    // Segment index order a, b, c, d, e, f, g.
    segments[0] = createSegment(TILE * 2, TILE, TILE * 3, TILE); // a
    segments[1] = createSegment(TILE * 3, TILE, TILE * 3, TILE * 2); // b
    segments[2] = createSegment(TILE * 3, TILE * 2, TILE * 3, TILE * 3); // c
    segments[3] = createSegment(TILE * 2, TILE * 3, TILE * 3, TILE * 3); // d
    segments[4] = createSegment(TILE * 2, TILE * 2, TILE * 2, TILE * 3); // e
    segments[5] = createSegment(TILE * 2, TILE, TILE * 2, TILE * 2); // f
    segments[6] = createSegment(TILE * 2, TILE * 2, TILE * 3, TILE * 2); // g
  }

  private Line createSegment(double x1, double y1, double x2, double y2) {

    Line segment = new Line(x1, y1, x2, y2);
    segment.setStroke(ledColor);
    segment.setStrokeWidth(4);
    segment.setOpacity(0);
    return segment;
  }
}
