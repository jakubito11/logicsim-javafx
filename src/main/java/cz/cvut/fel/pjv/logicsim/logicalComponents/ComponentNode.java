package cz.cvut.fel.pjv.logicsim.logicalComponents;

import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one connectable pin of a logical component.
 * <p>
 * Each node keeps logical value, input occupancy state, and visual
 * representation used by the editor.
 */
public class ComponentNode {

  public static List<ComponentNode> NODE_LIST = new ArrayList<>();
  private static final Color ACTIVE_FILL_COLOR = Color.web("#57c84d");
  private static int currentId = 0;
  private final Circle nodeCircle;
  boolean value;
  private boolean isAlive;
  private double posX;
  private double posY;
  private final boolean isOutput;
  private final double radius;
  private InputState inputState;
  private int id;
  private final Color color;
  private final Color fillColor;

  /**
   * Creates a node with default stroke color.
   *
   * @param posX x-coordinate
   * @param posY y-coordinate
   * @param isOutput {@code true} for output pin, {@code false} for input pin
   * @param value initial logical value
   */
  public ComponentNode(double posX, double posY, boolean isOutput, boolean value) {
    this(posX, posY, isOutput, value, Color.web("#b8b8b8"));
  }

  /**
   * Creates a node with defined stroke color.
   *
   * @param posX x-coordinate
   * @param posY y-coordinate
   * @param isOutput {@code true} for output pin, {@code false} for input pin
   * @param value initial logical value
   * @param strokeColor pin outline color
   */
  public ComponentNode(double posX, double posY, boolean isOutput, boolean value, Color strokeColor) {

    this.isAlive = true;
    this.radius = 7;
    this.color = strokeColor != null ? strokeColor : Color.web("#b8b8b8");
    this.fillColor = Color.web("#5a5a5a");
    this.posX = posX;
    this.posY = posY;
    this.nodeCircle = new Circle(posX, posY, radius, fillColor);
    this.nodeCircle.setStroke(color);
    this.nodeCircle.setStrokeWidth(2);
    this.isOutput = isOutput;
    this.value = value;
    this.inputState = InputState.FREE;

    this.id = currentId++;

    if (id < NODE_LIST.size()) {
      NODE_LIST.set(id, this);
    } else {
      NODE_LIST.add(this);
    }

    addMouseHandlers();
  }

  private void addMouseHandlers() {
    nodeCircle.setOnMouseEntered(event -> mouseOver());
    nodeCircle.setOnMouseExited(event -> mouseOut());
  }

  /**
   * Marks node as deleted and removes it from global nodelist.
   */
  public void destroy() {

    isAlive = false;

    if (id >= 0 && id < NODE_LIST.size()) {
      NODE_LIST.set(id, null);
    }
  }

  /**
   * Sets global node ID counter used during component reconstruction from files.
   *
   * @param id next ID that should be assigned to a created node
   */
  public static void setCurrentId(int id) {
    if (id < 0) {
      currentId = 0;
      return;
    }

    while (NODE_LIST.size() < id) {
      NODE_LIST.add(null);
    }
    currentId = id;
  }

  /**
   * Replaces the global node registry with a new list.
   *
   * @param newNodeList new global node list
   */
  public static void setNewNodeList(List<ComponentNode> newNodeList) {
    NODE_LIST = newNodeList;
  }

  /**
   * Returns global node registry used by wires and serialization.
   *
   * @return shared node list
   */
  public static List<ComponentNode> getNodeList() {
    return NODE_LIST;
  }


  /**
   * Creates visual pin elements (line + label + circle) and adds them to
   * component graphics.
   *
   * @param x pin line start x
   * @param y pin line start y
   * @param lineEndX pin line end x
   * @param lineEndY pin line end y
   * @param component owning JavaFX group
   * @param label text label of this pin
   * @param textX x position of label
   * @param textY y position of label
   * @param fontSize requested font size (or default when non-positive)
   */
  public void createPin(double x, double y, double lineEndX, double lineEndY, Group component, String label, double textX, double textY, double fontSize) {

    // optional either 13 or different number
    double fs = fontSize > 0 ? fontSize : 13;

    posX = lineEndX;
    posY = lineEndY;
    nodeCircle.setCenterX(lineEndX);
    nodeCircle.setCenterY(lineEndY);

    Line pinLine = new Line(x, y, lineEndX, lineEndY);
    pinLine.setStroke(color);
    pinLine.setStrokeWidth(2);

    Text pinText = new Text(textX, textY, label);
    pinText.setFill(color);
    pinText.setFont(Font.font("Arial", FontWeight.BOLD, fs));

    component.getChildren().addAll(pinLine, pinText, nodeCircle);
  }

  /**
   * Returns JavaFX circle representing this node.
   *
   * @return node circle
   */
  public Circle draw() {
    return nodeCircle;
  }

  /**
   * Applies hover styling to this node.
   *
   * @return always {@code true}
   */
  public boolean mouseOver() {
    nodeCircle.setStroke(color);
    nodeCircle.setStrokeWidth(4);
    nodeCircle.setRadius(8);

    return true;
  }

  /**
   * Restores default node styling after hover.
   */
  public void mouseOut() {
    nodeCircle.setStroke(color);
    nodeCircle.setStrokeWidth(2);
    nodeCircle.setRadius(radius);
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  /**
   * Updates visual fill based on current logical value.
   */
  public void fillValue() {
    nodeCircle.setFill(resolveFillColor());
  }

  public void setInputState(InputState state) {
    this.inputState = state;
  }

  public boolean getValue() {
    return value;
  }

  public boolean isOutput() {
    return isOutput;
  }

  public int getId() {
    return id;
  }


  public double getPosX() {
    return posX;
  }

  public double getPosY() {
    return posY;
  }

  public InputState getInputState() {
    return inputState;
  }

  private Color resolveFillColor() {
    if (value) {
      return ACTIVE_FILL_COLOR;
    }
    return fillColor;
  }

}
