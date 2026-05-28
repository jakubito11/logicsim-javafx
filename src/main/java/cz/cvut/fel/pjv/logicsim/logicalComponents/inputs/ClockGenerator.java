package cz.cvut.fel.pjv.logicsim.logicalComponents.inputs;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

/**
 * Periodic logical source that toggles output between low and high
 * for now only set to change each 500ms
 */
public class ClockGenerator extends CircuitComponent {

  private static final long HALF_PERIOD_MILLIS = 500L;
  private static boolean simulationRunning = false;
  private static long accumulatedElapsedMillis = 0L;
  private static long lastTickMillis = System.currentTimeMillis();
  private boolean value;

  /**
   * Creates a clock generator component.
   *
   * @param x initial x position
   * @param y initial y position
   * @param color primary stroke color
   */
  public ClockGenerator(double x, double y, Color color) {
    super(x, y, color);
    this.value = false;
  }

  /**
   * synchronize with the simulation clock
   *
   * @param isRunning {@code true} when simulation loop is running
   */
  public static void setSimulationRunning(boolean isRunning) {
    long nowMillis = System.currentTimeMillis();
    if (simulationRunning == isRunning) {
      return;
    }

    if (isRunning) {
      lastTickMillis = nowMillis;
      simulationRunning = true;
      return;
    }

    accumulatedElapsedMillis += Math.max(0L, nowMillis - lastTickMillis);
    simulationRunning = false;
  }

  /**
   * Resets clock back to initial state
   */
  public static void resetClockPhase() {
    accumulatedElapsedMillis = 0L;
    lastTickMillis = System.currentTimeMillis();
  }


  private void setupNodes() {
    nodes.clear();

    ComponentNode outputNode = new ComponentNode(100, 25, true, value, color);
    outputNode.createPin(50, 25, 100, 25, componentModel, "Y0", 108, 25, 13);
    nodes.add(outputNode);

    startNodeId = outputNode.getId();
  }

  /** {@inheritDoc} */
  @Override
  public void render() {
    componentModel.getChildren().clear();
    nodes.clear();

    Rectangle body = new Rectangle(0, 0, 50, 50);
    body.setFill(Color.TRANSPARENT);
    body.setStroke(color);
    body.setStrokeWidth(2);

    Polyline symbol = new Polyline(
        10, 30,
        20, 30,
        20, 15,
        30, 15,
        30, 30,
        40, 30
    );
    symbol.setStroke(color);
    symbol.setStrokeWidth(2);
    symbol.setFill(Color.TRANSPARENT);

    setupNodes();
    componentModel.getChildren().addAll(body, symbol);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    long elapsedMillis = getElapsedMillis();
    // One full period is 1000 ms; HALF_PERIOD_MILLIS flips 0/1 state.
    long halfPeriods = elapsedMillis / HALF_PERIOD_MILLIS;
    value = (halfPeriods & 1L) == 1L;

    if (!nodes.isEmpty() && nodes.get(0) != null) {
      nodes.get(0).setValue(value);
    }
  }

  private static long getElapsedMillis() {
    if (!simulationRunning) {
      return accumulatedElapsedMillis;
    }

    long nowMillis = System.currentTimeMillis();
    long deltaMillis = Math.max(0L, nowMillis - lastTickMillis);
    accumulatedElapsedMillis += deltaMillis;
    lastTickMillis = nowMillis;
    return accumulatedElapsedMillis;
  }
}
