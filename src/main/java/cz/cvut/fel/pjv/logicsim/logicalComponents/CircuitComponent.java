package cz.cvut.fel.pjv.logicsim.logicalComponents;

import cz.cvut.fel.pjv.logicsim.ComponentsMap;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all logical components rendered on the editor canvas.
 */
public abstract class CircuitComponent {

  /**
   * Root JavaFX group representing this component in the editor.
   */
  public final Group componentModel;
  /**
   * Base stroke color of the component and its pins.
   */
  public Color color;
  /**
   * Selection flag used by editor tools.
   */
  public boolean fullySelected = false;
  /**
   * ID of the first created node, used for serialization ordering.
   */
  public int startNodeId;
  private ComponentsMap componentType;
  /**
   * Pin list in creation order.
   */
  public final List<ComponentNode> nodes = new ArrayList<>();
  private double posX;
  private double posY;

  /**
   * Creates a circuit component with initial position and stroke color.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public CircuitComponent(double x, double y, Color color) {
    this.componentModel = new Group();
    this.color = color;
    this.componentModel.setTranslateX(x);
    this.componentModel.setTranslateY(y);
    this.posX = x;
    this.posY = y;

  }

  /**
   * Creates all JavaFX nodes for this component and its pins.
   */
  public abstract void render();

  /**
   * Executes one simulation step and updates output nodes.
   */
  public abstract void execute();

  public Group getComponentModel() {
    return componentModel;
  }

  public boolean isFullySelected() {
    return fullySelected;
  }

  public ComponentsMap getComponentType() {
    return componentType;
  }

  public void setComponentType(ComponentsMap componentType) {
    this.componentType = componentType;
  }

  public void setFullySelected(boolean fullySelected) {
    this.fullySelected = fullySelected;
  }


  /**
   * Removes this component and all its pins from the editor.
   */
  public void destroy() {
    for (int i = 0; i < nodes.size(); i++) {
      ComponentNode node = nodes.get(i);
      if (node == null) {
        continue;
      }

      node.destroy();
      nodes.set(i, null);
    }
    nodes.clear();

    componentModel.getChildren().clear();

    Parent parent = componentModel.getParent();

    if (parent instanceof Pane) {

      ((Pane) parent).getChildren().remove(componentModel);
    }
    else if (parent instanceof Group) {
      ((Group) parent).getChildren().remove(componentModel);
    }
  }

}
