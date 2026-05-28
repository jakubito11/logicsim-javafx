package cz.cvut.fel.pjv.logicsim.logicalComponents;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.List;

/**
 * Visual and logical representation of a wire between two component nodes.
 */
public class Wire {

  private ComponentNode startNode;
  private ComponentNode endNode;
  private int startId;
  private int endId;
  private final Group wireLayer;
  private final Group wireGroup = new Group();
  private final Line wireLine;
  private double endX = 0;
  private double endY = 0;
  private boolean drawing = false;

  /**
   * Creates a wire starting from one node.
   *
   * @param startNode origin node
   * @param wireLayer JavaFX layer where wire graphics are rendered
   */
  public Wire(ComponentNode startNode, Group wireLayer) {
    this.startNode = startNode;
    this.wireLayer = wireLayer;
    this.startId = startNode.getId();
    this.wireLine = new Line();
    wireLine.setStroke(Color.GREY);
    wireLine.setStrokeWidth(3);
    wireLine.setPickOnBounds(false);
    wireLine.setMouseTransparent(false);

    wireLine.setOnMouseEntered(event -> mouseOver());
    wireLine.setOnMouseExited(event -> mouseOut());

    wireGroup.getChildren().add(wireLine);
    wireLayer.getChildren().add(wireGroup);

  }

  /**
   * Applies hover transparency effect.
   */
  public void mouseOver() {
    wireLine.setOpacity(0.2);
  }

  /**
   * Restores default opacity after hover.
   */
  public void mouseOut() {
    wireLine.setOpacity(1);
  }

  /**
   * Recalculates and draws line endpoints according to current node positions.
   *
   * @return {@code true} when wire remains valid, {@code false} when it should
   *         be removed
   */
  public boolean draw() {
    if (endNode == null) {
      if (!isNodeAlive(startNode)) {
        return false;
      }

      drawing = true;
      Point2D startPoint = resolveNodePoint(startNode);
      wireLine.setStartX(startPoint.getX());
      wireLine.setStartY(startPoint.getY());
      wireLine.setEndX(endX);
      wireLine.setEndY(endY);
      return true;
    }

    if (!isNodeAlive(startNode) || !isNodeAlive(endNode)) {
      if (endNode != null) {
        endNode.setValue(false);
      }
      return false;
    }

    drawing = false;
    Point2D startPoint = resolveNodePoint(startNode);
    Point2D endPoint = resolveNodePoint(endNode);
    wireLine.setStartX(startPoint.getX());
    wireLine.setStartY(startPoint.getY());
    wireLine.setEndX(endPoint.getX());
    wireLine.setEndY(endPoint.getY());
    return true;
  }


  /**
   * Sets wire stroke color.
   *
   * @param color color representing current signal state
   */
  public void fillWires(Color color) {
    wireLine.setStroke(color);
  }

  /**
   * Resets wire style to default non-active appearance.
   */
  public void resetVisualState() {
    wireLine.setOpacity(1);
    wireLine.setStroke(Color.GREY);
  }

  /**
   * Propagates logical value across connected nodes and updates wire color.
   */
  public void updateWiresBetweenNodes() {
    if (startNode == null || endNode == null) {
      return;
    }

    generateNodeValue();
    if (startNode.getValue() && endNode.getValue()) {
      fillWires(Color.GREEN);
    } else {
      fillWires(Color.GREY);
    }
  }

  /**
   * Removes wire graphics and frees connected input states.
   */
  public void destroy() {
    if (startNode != null) {
      startNode.setInputState(InputState.FREE);
    }

    if (endNode != null) {
      endNode.setValue(false);
      endNode.setInputState(InputState.FREE);
    }

    wireLayer.getChildren().remove(wireGroup);
  }

  /**
   * Computes signal value transfer between connected nodes.
   */
  public void generateNodeValue() {
    if (startNode == null || endNode == null) {
      return;
    }

    if ((startNode.isOutput() && endNode.isOutput()) || (!startNode.isOutput() && !endNode.isOutput())) {
      startNode.setValue(startNode.getValue() || endNode.getValue());
      endNode.setValue(startNode.getValue());
      return;
    }

    endNode.setValue(startNode.getValue());
  }

  public ComponentNode getStartNode() {
    return startNode;
  }

  /**
   * Sets temporary wire end for preview while drawing.
   *
   * @param endX current pointer x in wire layer coordinates
   * @param endY current pointer y in wire layer coordinates
   */
  public void updateEnd(double endX, double endY) {
    this.endX = endX;
    this.endY = endY;
  }

  /**
   * Finalizes end node of this wire and assigns input occupancy.
   *
   * @param endNode target node of this wire
   */
  public void setEndNode(ComponentNode endNode) {
    if (endNode == null) {
      return;
    }

    if (endNode.isOutput()) {
      ComponentNode tempNode = startNode;
      startNode = endNode;
      this.endNode = tempNode;
      this.endNode.setInputState(InputState.TAKEN);
    } else {
      this.endNode = endNode;
      startNode.setInputState(InputState.TAKEN);
      this.endNode.setInputState(InputState.TAKEN);
    }

    startId = startNode.getId();
    endId = this.endNode.getId();
  }

  public int getStartId() {
    return startId;
  }

  public int getEndId() {
    return endId;
  }

  public boolean isDrawing() {
    return drawing;
  }

  public ComponentNode getEndNode() {
    return endNode;
  }

  private Point2D resolveNodePoint(ComponentNode node) {
    Circle nodeCircle = node.draw();
    Bounds sceneBounds = nodeCircle.localToScene(nodeCircle.getBoundsInLocal());
    if (sceneBounds == null) {
      return new Point2D(node.getPosX(), node.getPosY());
    }

    double centerSceneX = (sceneBounds.getMinX() + sceneBounds.getMaxX()) / 2.0;
    double centerSceneY = (sceneBounds.getMinY() + sceneBounds.getMaxY()) / 2.0;
    return wireLayer.sceneToLocal(centerSceneX, centerSceneY);
  }

  private boolean isNodeAlive(ComponentNode node) {
    if (node == null) {
      return false;
    }

    int id = node.getId();
    List<ComponentNode> allNodes = ComponentNode.getNodeList();
    return id >= 0 && id < allNodes.size() && allNodes.get(id) == node;
  }

}
