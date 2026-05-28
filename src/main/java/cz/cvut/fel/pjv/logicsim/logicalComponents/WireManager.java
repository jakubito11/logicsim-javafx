package cz.cvut.fel.pjv.logicsim.logicalComponents;

import javafx.scene.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages wire creation, rendering updates, and wire removal.
 */
public class WireManager {

  /**
   * Result of attempting to add/click a node during wire drawing.
   */
  public enum AddNodeResult {
    STARTED,
    COMPLETED,
    IGNORED_NULL,
    INPUT_ALREADY_CONNECTED,
    INVALID_SAME_NODE,
    INVALID_OUTPUT_TO_OUTPUT,
    INVALID_INPUT_TO_INPUT,
    INTERNAL_STATE_ERROR
  }

  private final List<Wire> wires = new ArrayList<>();
  private final Group wireLayer;

  private boolean opened = false;
  private boolean finishedDrawing = true;
  private double pointerX = 0;
  private double pointerY = 0;

  /**
   * Creates wire manager bound to a JavaFX layer.
   *
   * @param wireLayer layer where wire graphics should be added
   */
  public WireManager(Group wireLayer) {
    this.wireLayer = wireLayer;
  }

  /**
   * Redraws all wires and removes invalid ones.
   */
  public void draw() {
    for (int i = wires.size() - 1; i >= 0; i--) {
      Wire currentWire = wires.get(i);

      if (currentWire.getEndNode() == null) {
        currentWire.updateEnd(pointerX, pointerY);
      }

      boolean result = currentWire.draw();
      if (!result) {
        opened = false;
        currentWire.destroy();
        wires.remove(i);
      }
    }
  }

  /**
   * Propagates logical values through all wires.
   */
  public void update() {
    for (int i = 0; i < wires.size(); i++) {
      wires.get(i).updateWiresBetweenNodes();
    }
  }

  /**
   * Resets visual style of all wires.
   */
  public void resetVisualState() {
    for (int i = 0; i < wires.size(); i++) {
      wires.get(i).resetVisualState();
    }
  }

  /**
   * Handles node click for opening/closing a wire connection.
   *
   * @param node clicked node
   * @return outcome of the operation
   */
  public AddNodeResult addNode(ComponentNode node) {
    if (node == null) {
      return AddNodeResult.IGNORED_NULL;
    }
    if (!(node.getInputState() == InputState.FREE || node.isOutput())) {
      return AddNodeResult.INPUT_ALREADY_CONNECTED;
    }

    // First click opens a temporary wire that follows mouse pointer.
    if (!opened) {
      wires.add(new Wire(node, wireLayer));
      opened = true;
      finishedDrawing = false;
      return AddNodeResult.STARTED;
    }

    // Second click validates compatibility and either finalizes or cancels.
    int index = wires.size() - 1;
    if (index < 0) {
      opened = false;
      finishedDrawing = true;
      return AddNodeResult.INTERNAL_STATE_ERROR;
    }

    Wire drawingWire = wires.get(index);
    ComponentNode startNode = drawingWire.getStartNode();

    if (node == startNode) {
      drawingWire.destroy();
      wires.remove(index);
      opened = false;
      finishedDrawing = true;
      return AddNodeResult.INVALID_SAME_NODE;
    }

    if (startNode.isOutput() && node.isOutput()) {
      drawingWire.destroy();
      wires.remove(index);
      opened = false;
      finishedDrawing = true;
      return AddNodeResult.INVALID_OUTPUT_TO_OUTPUT;
    }

    if (!startNode.isOutput() && !node.isOutput()) {
      drawingWire.destroy();
      wires.remove(index);
      opened = false;
      finishedDrawing = true;
      return AddNodeResult.INVALID_INPUT_TO_INPUT;
    }

    drawingWire.setEndNode(node);

    opened = false;
    finishedDrawing = true;
    return AddNodeResult.COMPLETED;
  }


  public List<Wire> getWires() {
    return wires;
  }

  public boolean isOpened() {
    return opened;
  }

  public boolean isFinishedDrawing() {
    return finishedDrawing;
  }

  /**
   * Updates current pointer coordinates used for temporary wire preview.
   *
   * @param pointerX pointer x in wire layer coordinates
   * @param pointerY pointer y in wire layer coordinates
   */
  public void updatePointer(double pointerX, double pointerY) {
    this.pointerX = pointerX;
    this.pointerY = pointerY;
  }

  /**
   * Removes all wires and resets drawing state.
   */
  public void clear() {
    for (int i = 0; i < wires.size(); i++) {
      wires.get(i).destroy();
    }
    wires.clear();
    opened = false;
    finishedDrawing = true;
  }

  /**
   * Cancels a currently opened unfinished wire.
   *
   * @return {@code true} when a pending wire was canceled
   */
  public boolean cancelOpenedWire() {
    if (!opened || wires.isEmpty()) {
      return false;
    }

    int index = wires.size() - 1;
    Wire pendingWire = wires.get(index);
    if (pendingWire.getEndNode() != null) {
      return false;
    }

    pendingWire.destroy();
    wires.remove(index);
    opened = false;
    finishedDrawing = true;
    return true;
  }

  /**
   * Removes all wires connected to any of the given nodes.
   *
   * @param nodes nodes belonging to removed components
   */
  public void removeWiresConnectedTo(List<ComponentNode> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return;
    }

    Wire openedWire = null;
    if (opened && !wires.isEmpty()) {
      openedWire = wires.get(wires.size() - 1);
    }


    boolean openedWireRemoved = false;

    for (int i = wires.size() - 1; i >= 0; i--) {
      Wire wire = wires.get(i);
      if (!isConnectedToNodes(wire, nodes)) {
        continue;
      }

      if (wire == openedWire && wire.getEndNode() == null) {
        openedWireRemoved = true;
      }

      wire.destroy();
      wires.remove(i);

    }

    if (openedWireRemoved || wires.isEmpty()) {
      opened = false;
      finishedDrawing = true;
    }

  }

  private boolean isConnectedToNodes(Wire wire, List<ComponentNode> nodes) {
    if (wire == null) {
      return false;
    }

    ComponentNode startNode = wire.getStartNode();
    ComponentNode endNode = wire.getEndNode();

    for (int i = 0; i < nodes.size(); i++) {
      ComponentNode node = nodes.get(i);
      if (node == null) {
        continue;
      }

      if (node == startNode || node == endNode) {
        return true;
      }
    }

    return false;
  }
}
