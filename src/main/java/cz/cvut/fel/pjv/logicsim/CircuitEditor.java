package cz.cvut.fel.pjv.logicsim;

import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import cz.cvut.fel.pjv.logicsim.logicalComponents.WireManager;
import cz.cvut.fel.pjv.logicsim.logicalComponents.WireManager.AddNodeResult;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.Demultiplexor;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.Counter;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.DflipFlop;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.FullAdder;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.HalfAdder;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.JKflipFlop;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.Latch;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.Multiplexor;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.RingCounter;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.SevenSegmentDecoder;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.SevenSegmentDisplay;
import cz.cvut.fel.pjv.logicsim.logicalComponents.chips.TflipFlop;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.AndGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.NandGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.NorGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.NotGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.OrGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.XnorGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.gates.XorGate;
import cz.cvut.fel.pjv.logicsim.logicalComponents.inputs.ClockGenerator;
import cz.cvut.fel.pjv.logicsim.logicalComponents.inputs.HighInput;
import cz.cvut.fel.pjv.logicsim.logicalComponents.inputs.LowInput;
import cz.cvut.fel.pjv.logicsim.logicalComponents.outputs.LightBulb;
import cz.cvut.fel.pjv.logicsim.logicalComponents.outputs.LogicalOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CircuitEditor extends Pane {


  private static final double TILE_SIZE = 25;
  private static final double MIN_SCALE = 0.25;
  private static final double MAX_SCALE = 10;
  private static final double ZOOM_FACTOR = 1.15;
  private static final int SIMULATION_ITERATIONS = 15;
  private static final Color GRID_COLOR = Color.web("#2e2e38");
  private static final Color LIGHT_GRID_COLOR = Color.web("#d7dce3");
  private static final Color COMPONENT_COLOR = Color.GRAY;
  private static final Color HIGHLIGHT_COLOR = Color.web("#2f7dff");
  private static final Logger LOGGER = AppLog.getLogger(CircuitEditor.class);


  private final Pane gridLayer;
  private final Pane contentLayer;
  private final Group wireLayer;
  private final WireManager wireManager;
  private final AnimationTimer simulationLoop;
  private final Text simulationClockText;
  private boolean gridVisible;
  private boolean whiteBackground;
  private ToolMode activeTool;
  private boolean placementMode;
  private boolean panning;
  private double scale;
  private double offsetX;
  private double offsetY;
  private double lastMouseX;
  private double lastMouseY;
  private Node draggingShape;
  private double dragStartSceneX;
  private double dragStartSceneY;
  private double dragStartLayoutX;
  private double dragStartLayoutY;
  private boolean simulationRunning;
  private long simulationStartNanos;
  private long elapsedSimulationNanos;
  public List<CircuitComponent> components = new ArrayList<>();
  private final List<CircuitComponent> selectedComponents = new ArrayList<>();
  private UiMessageLogger uiMessageLogger;

  public CircuitEditor() {

    Rectangle sceneClip = new Rectangle();
    sceneClip.widthProperty().bind(widthProperty());
    sceneClip.heightProperty().bind(heightProperty());
    this.gridLayer = new Pane();
    this.contentLayer = new Pane();
    this.gridVisible = true;
    this.whiteBackground = false;
    this.activeTool = ToolMode.SELECT_DRAG;
    this.placementMode = false;
    this.panning = false;
    this.scale = 1.0;
    this.offsetX = 0;
    this.offsetY = 0;
    this.draggingShape = null;
    this.simulationRunning = false;
    this.simulationStartNanos = 0;
    this.elapsedSimulationNanos = 0;

    this.gridLayer.setManaged(false);
    this.gridLayer.setMouseTransparent(true);
    this.contentLayer.setManaged(false);
    this.wireLayer = new Group();
    this.wireLayer.setManaged(false);
    this.wireLayer.setMouseTransparent(true);
    this.wireManager = new WireManager(wireLayer);
    this.simulationLoop = new AnimationTimer() {
      @Override
      public void handle(long now) {
        simulate();
      }
    };
    this.simulationClockText = new Text("00:00:00");
    simulationClockText.setManaged(false);
    simulationClockText.setMouseTransparent(true);
    simulationClockText.setX(12);
    simulationClockText.setY(24);
    simulationClockText.setFill(Color.web("#57c84d"));
    simulationClockText.setFont(Font.font("Arial", 18));
    this.contentLayer.getChildren().add(wireLayer);

    getChildren().add(gridLayer);
    getChildren().add(contentLayer);
    getChildren().add(simulationClockText);

    updateBackgroundStyle();
    updateCursor();
    mainEditorBoard();

    widthProperty().addListener((observable) -> redrawGrid());
    heightProperty().addListener((observable) -> redrawGrid());
    setClip(sceneClip);

    applyContentTransform();
    redrawGrid();
  }

  /**
   * Adds a rendered component to the editor and wires up interaction handlers.
   *
   * @param component component instance to place on the canvas
   */
  public void addCircuitComponent(CircuitComponent component) {
    if (component == null) {
      return;
    }
    Node node = component.getComponentModel();
    configureShapeDrag(node, component);
    configureNodeWireHandlers(component);
    contentLayer.getChildren().add(node);
    components.add(component);
  }
  /**
   * Enables or disables the background grid.
   *
   * @param gridVisible {@code true} to show grid lines
   */
  public void setGridVisible(boolean gridVisible) {
    this.gridVisible = gridVisible;
    redrawGrid();
  }

  /**
   * Returns whether the background grid is currently visible.
   *
   * @return {@code true} when grid is shown
   */
  public boolean isGridVisible() {
    return gridVisible;
  }

  /**
   * Switches the active editor tool mode.
   *
   * @param activeTool selected interaction tool
   */
  public void setActiveTool(ToolMode activeTool) {
    this.activeTool = activeTool;

    if (activeTool != ToolMode.MOVE) {
      panning = false;
    }

    if (activeTool != ToolMode.SELECT_DRAG) {
      draggingShape = null;
    }
    updateCursor();
  }

  /**
   * Enables or disables placement mode for adding new components.
   *
   * @param placementMode requested placement mode state
   */
  public void setPlacementMode(boolean placementMode) {
    this.placementMode = placementMode;
    if (placementMode) {
      panning = false;
      draggingShape = null;
    }
    updateCursor();
  }

  /**
   * Toggles white background for the editor.
   *
   * @param whiteBackground {@code true} for white background
   */
  public void setWhiteBackground(boolean whiteBackground) {
    this.whiteBackground = whiteBackground;
    updateBackgroundStyle();
    redrawGrid();
  }

  /**
   * Returns whether white background mode is enabled.
   *
   * @return {@code true} when white background is active
   */
  public boolean isWhiteBackground() {
    return whiteBackground;
  }

  /**
   * Clears workspace and resets simulation, component list, and wire list.
   */
  public void newBlankCircuit() {
    resetSimulation();
    cancelOpenedWire();
    wireManager.clear();

    for (int i = components.size() - 1; i >= 0; i--) {
      CircuitComponent component = components.get(i);
      if (component != null) {
        component.destroy();
      }
    }

    components.clear();
    selectedComponents.clear();
    ComponentNode.setNewNodeList(new ArrayList<>());
    ComponentNode.setCurrentId(0);
    refreshWires();
    updateCursor();
  }

  /**
   * Returns wire manager handling wire creation and updates.
   *
   * @return wire manager instance used by this editor
   */
  public WireManager getWireManager() {
    return wireManager;
  }

  /**
   * Injects UI message logger used for connection validation feedback.
   *
   * @param uiMessageLogger callback for editor-level informational/error text
   */
  public void setUiMessageLogger(UiMessageLogger uiMessageLogger) {
    this.uiMessageLogger = uiMessageLogger;
  }

  /**
   * Zooms the editor in around the viewport center.
   */
  public void zoomIn() {
    setScale(scale * ZOOM_FACTOR, getWidth() / 2, getHeight() / 2);
  }

  /**
   * Zooms the editor out around the viewport center.
   */
  public void zoomOut() {
    setScale(scale / ZOOM_FACTOR, getWidth() / 2, getHeight() / 2);
  }


  private void mainEditorBoard() {

    setOnMousePressed(event -> {
      updateWirePointer(event.getX(), event.getY());

      if (event.getButton() != MouseButton.PRIMARY) {
        return;
      }

      if (simulationRunning) {
        panning = true;
        lastMouseX = event.getX();
        lastMouseY = event.getY();
        updateCursor();
        event.consume();
        return;
      }

      if (placeSelectedComponent(event)) {
        event.consume();
        return;
      }

      if ((event.getTarget() == this || event.getTarget() == contentLayer)
          && canEditComponentsAndWires()) {
        unhighlightAllComponents();
      }

      if (activeTool != ToolMode.MOVE) {
        return;
      }

      panning = true;
      lastMouseX = event.getX();
      lastMouseY = event.getY();
      updateCursor();
      event.consume();
    });

    setOnMouseDragged(event -> {
      updateWirePointer(event.getX(), event.getY());

      if (!event.isPrimaryButtonDown()) {
        return;
      }

      if (!panning || (!simulationRunning && activeTool != ToolMode.MOVE)) {
        return;
      }

      double deltaX = event.getX() - lastMouseX;
      double deltaY = event.getY() - lastMouseY;
      offsetX += deltaX;
      offsetY += deltaY;
      lastMouseX = event.getX();
      lastMouseY = event.getY();
      applyContentTransform();
      redrawGrid();
      event.consume();
    });

    setOnMouseMoved(event -> {
      updateWirePointer(event.getX(), event.getY());
      if (wireManager.isOpened()) {
        refreshWires();
      }
    });

    setOnMouseReleased(event -> {
      if (event.getButton() != MouseButton.PRIMARY) {
        return;
      }

      if (panning) {
        panning = false;
        updateCursor();
        event.consume();
      }
    });

    addEventFilter(ScrollEvent.SCROLL, event -> {
      if (event.getDeltaY() > 0) {
        setScale(scale * ZOOM_FACTOR, event.getX(), event.getY());
      } else if (event.getDeltaY() < 0) {
        setScale(scale / ZOOM_FACTOR, event.getX(), event.getY());
      }
      event.consume();
    });
  }

  private void setScale(double newScale, double anchorScreenX, double anchorScreenY) {
    double clampedScale = clamp(newScale);
    double worldAnchorX = screenToWorldX(anchorScreenX);
    double worldAnchorY = screenToWorldY(anchorScreenY);

    scale = clampedScale;
    offsetX = anchorScreenX - worldAnchorX * scale;
    offsetY = anchorScreenY - worldAnchorY * scale;
    applyContentTransform();
    redrawGrid();
  }

  private double clamp(double value) {
    return Math.max(MIN_SCALE, Math.min(MAX_SCALE, value));
  }

  private void applyContentTransform() {
    contentLayer.setTranslateX(offsetX);
    contentLayer.setTranslateY(offsetY);
    contentLayer.setScaleX(scale);
    contentLayer.setScaleY(scale);
  }

  private void updateBackgroundStyle() {
    setStyle(whiteBackground ? "-fx-background-color: #ffffff;" : "");
  }

  private void updateCursor() {
    if (simulationRunning) {
      setCursor(Cursor.MOVE);
    } else if (placementMode || wireManager.isOpened()) {
      setCursor(Cursor.CROSSHAIR);
    } else if (activeTool == ToolMode.MOVE) {
      setCursor(Cursor.MOVE);
    } else {
      setCursor(Cursor.DEFAULT);
    }
  }

  private void redrawGrid() {
    double width = getWidth();
    double height = getHeight();
    gridLayer.getChildren().clear();

    if (!gridVisible || width <= 0 || height <= 0) {
      return;
    }

    Color gridColor = whiteBackground ? LIGHT_GRID_COLOR : GRID_COLOR;
    double scaledGridSize = TILE_SIZE * scale;
    double startX = offsetX % scaledGridSize;
    double startY = offsetY % scaledGridSize;

    for (double x = startX; x <= width; x += scaledGridSize) {
      Line line = new Line(x, 0, x, height);
      line.setStroke(gridColor);
      line.setStrokeWidth(1);
      line.setManaged(false);
      line.setMouseTransparent(true);
      gridLayer.getChildren().add(line);
    }

    for (double y = startY; y <= height; y += scaledGridSize) {
      Line line = new Line(0, y, width, y);
      line.setStroke(gridColor);
      line.setStrokeWidth(1);
      line.setManaged(false);
      line.setMouseTransparent(true);
      gridLayer.getChildren().add(line);
    }
  }

  private double screenToWorldX(double screenX) {
    return (screenX - offsetX) / scale;
  }

  private double screenToWorldY(double screenY) {
    return (screenY - offsetY) / scale;
  }

  private void configureShapeDrag(Node shape, CircuitComponent component) {
    shape.setOnMousePressed(event -> {
      if (event.getButton() != MouseButton.PRIMARY) {
        return;
      }

      if (simulationRunning) {
        return;
      }

      if (component != null
          && canEditComponentsAndWires()
          && !isNodeShape(component, event.getTarget())) {
        selectSingleComponent(component);
      }

      if (isShapeDragEnabled()) {
        return;
      }

      draggingShape = shape;
      dragStartSceneX = event.getSceneX();
      dragStartSceneY = event.getSceneY();
      dragStartLayoutX = draggingShape.getLayoutX();
      dragStartLayoutY = draggingShape.getLayoutY();
      event.consume();
    });

    shape.setOnMouseDragged(event -> {
      if (simulationRunning || draggingShape != shape || !event.isPrimaryButtonDown() || isShapeDragEnabled()) {
        return;
      }

      double deltaSceneX = event.getSceneX() - dragStartSceneX;
      double deltaSceneY = event.getSceneY() - dragStartSceneY;
      shape.setLayoutX(dragStartLayoutX + deltaSceneX / scale);
      shape.setLayoutY(dragStartLayoutY + deltaSceneY / scale);
      refreshWires();
      event.consume();
    });

    shape.setOnMouseReleased(event -> {
      if (simulationRunning || event.getButton() != MouseButton.PRIMARY || draggingShape != shape) {
        return;
      }

      snapToGrid(draggingShape);
      refreshWires();

      draggingShape = null;
      event.consume();
    });
  }

  void snapToGrid(Node shape) {
    if (shape == null) {
      return;
    }

    shape.setLayoutX(Math.round(shape.getLayoutX() / TILE_SIZE) * TILE_SIZE);
    shape.setLayoutY(Math.round(shape.getLayoutY() / TILE_SIZE) * TILE_SIZE);
  }

  private boolean isShapeDragEnabled() {
    return simulationRunning || activeTool != ToolMode.SELECT_DRAG || placementMode;
  }

  private boolean placeSelectedComponent(MouseEvent event) {
    if (!canPlaceComponents()) {
      return false;
    }

    ComponentsMap selectedComponent = Main.getSelectedComponent();
    if (selectedComponent == ComponentsMap.NONE) {
      return false;
    }

    CircuitComponent newComponent = createComponentForType(selectedComponent);

    if (newComponent == null) {
      return false;
    }

    newComponent.render();

    Node componentModel = newComponent.getComponentModel();
    Bounds bounds = componentModel.getBoundsInLocal();
    double componentCenterX = (bounds.getMinX() + bounds.getMaxX()) / 2.0;
    double componentCenterY = (bounds.getMinY() + bounds.getMaxY()) / 2.0;

    componentModel.setLayoutX(screenToWorldX(event.getX()) - componentCenterX);
    componentModel.setLayoutY(screenToWorldY(event.getY()) - componentCenterY);
    snapToGrid(componentModel);

    addCircuitComponent(newComponent);


    return true;
  }

  private void updateWirePointer(double editorX, double editorY) {
    wireManager.updatePointer(screenToWorldX(editorX), screenToWorldY(editorY));
  }

  private void configureNodeWireHandlers(CircuitComponent component) {
    for (int i = 0; i < component.nodes.size(); i++) {
      ComponentNode node = component.nodes.get(i);
      if (node == null) {
        continue;
      }

      node.draw().setOnMousePressed(event -> {
        if (event.getButton() != MouseButton.PRIMARY) {
          return;
        }
        if (!canEditComponentsAndWires()) {
          return;
        }

        Point2D editorPoint = sceneToLocal(event.getSceneX(), event.getSceneY());
        updateWirePointer(editorPoint.getX(), editorPoint.getY());
        AddNodeResult result = wireManager.addNode(node);
        handleAddNodeResult(result);
        refreshWires();
        updateCursor();
        event.consume();
      });
    }
  }

  private void handleAddNodeResult(AddNodeResult result) {
    if (result == null) {
      return;
    }

    switch (result) {
      case INVALID_OUTPUT_TO_OUTPUT:
        notifyError("Cannot connect output to output.");
        break;
      case INVALID_INPUT_TO_INPUT:
        notifyError("Cannot connect input to input.");
        break;
      case INPUT_ALREADY_CONNECTED:
        notifyError("Input pin is already connected.");
        break;
      case INVALID_SAME_NODE:
        notifyError("Cannot connect a pin to itself.");
        break;
      default:
        break;
    }
  }

  private void notifyError(String message) {
    if (uiMessageLogger != null) {
      uiMessageLogger.error(message);
    }
  }

  /**
   * Cancels currently opened unfinished wire (if any).
   *
   * @return {@code true} when an open wire was canceled
   */
  public boolean cancelOpenedWire() {
    boolean canceled = wireManager.cancelOpenedWire();
    if (canceled) {
      updateCursor();
    }
    return canceled;
  }

  /**
   * Deletes all currently selected components and connected wires.
   */
  public void deleteSelectedComponents() {
    if (!canEditComponentsAndWires() || selectedComponents.isEmpty()) {
      return;
    }

    cancelOpenedWire();

    for (int i = components.size() - 1; i >= 0; i--) {
      CircuitComponent component = components.get(i);
      if (component == null || !component.isFullySelected()) {
        continue;
      }

      wireManager.removeWiresConnectedTo(component.nodes);
      component.destroy();
      components.remove(i);
    }

    selectedComponents.clear();
    refreshWires();
  }

  /**
   * Starts or stops simulation loop and updates related editor state.
   *
   * @param simulationRunning requested simulation state
   */
  public void setSimulationRunning(boolean simulationRunning) {
    if (this.simulationRunning == simulationRunning) {
      return;
    }

    this.simulationRunning = simulationRunning;
    if (simulationRunning) {
      cancelOpenedWire();
      panning = false;
      draggingShape = null;
      unhighlightAllComponents();
      ClockGenerator.setSimulationRunning(true);
      simulationStartNanos = System.nanoTime() - elapsedSimulationNanos;
      simulationLoop.start();
      updateCursor();
      LOGGER.info("Simulation started.");
      return;
    }

    ClockGenerator.setSimulationRunning(false);
    if (simulationStartNanos > 0) {
      elapsedSimulationNanos = System.nanoTime() - simulationStartNanos;
      simulationStartNanos = 0;
    }
    simulationLoop.stop();
    updateCursor();
    LOGGER.info("Simulation stopped.");
  }

  /**
   * Resets simulation clock, node values, and wires graphics.
   */
  public void resetSimulation() {
    setSimulationRunning(false);
    elapsedSimulationNanos = 0;
    simulationStartNanos = 0;
    ClockGenerator.resetClockPhase();
    updateSimulationClockText(0);

    List<ComponentNode> nodeList = ComponentNode.getNodeList();
    for (ComponentNode node : nodeList) {
      if (node == null) {
        continue;
      }
      node.setValue(false);
      node.fillValue();
    }

    wireManager.resetVisualState();
    refreshWires();
  }

  private void refreshWires() {
    wireManager.draw();
  }

  private void simulate() {
    if (!simulationRunning) {
      return;
    }

    updateSimulationClockText(System.nanoTime() - simulationStartNanos);

    for (int i = 0; i < SIMULATION_ITERATIONS; i++) {
      for (CircuitComponent component : components) {
        component.execute();
      }
      wireManager.update();
    }

    List<ComponentNode> nodeList = ComponentNode.getNodeList();
    for (ComponentNode node : nodeList) {
      if (node == null) {
        continue;
      }
      node.fillValue();
    }
  }

  private void updateSimulationClockText(long elapsedNanos) {
    long totalSeconds = Math.max(0, elapsedNanos / 1_000_000_000L);
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;
    simulationClockText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
  }

  private void selectSingleComponent(CircuitComponent component) {
    if (component == null) {
      return;
    }

    if (selectedComponents.size() == 1 && selectedComponents.get(0) == component) {
      return;
    }

    unhighlightAllComponents();
    highlightComponent(component);
  }

  private void highlightComponent(CircuitComponent component) {
    if (component == null || simulationRunning) {
      return;
    }

    applyComponentStroke(component, HIGHLIGHT_COLOR);
    component.setFullySelected(true);
    selectedComponents.add(component);
  }

  private void unhighlightAllComponents() {
    for (CircuitComponent selectedComponent : selectedComponents) {
      if (selectedComponent == null) {
        continue;
      }
      applyComponentStroke(selectedComponent, selectedComponent.color);
      selectedComponent.setFullySelected(false);
    }
    selectedComponents.clear();
  }

  private void applyComponentStroke(CircuitComponent component, Color strokeColor) {
    List<Node> children = component.getComponentModel().getChildren();
    for (Node child : children) {
      if (!(child instanceof Shape)) {
        continue;
      }

      Shape shape = (Shape) child;
      if (isNodeShape(component, child)) {
        shape.setStroke(component.color);
      } else {
        shape.setStroke(strokeColor);
      }
    }
  }

  private boolean isNodeShape(CircuitComponent component, Object target) {
    if (!(target instanceof Node)) {
      return false;
    }

    Node targetNode = (Node) target;
    for (int i = 0; i < component.nodes.size(); i++) {
      ComponentNode node = component.nodes.get(i);
      if (node == null) {
        continue;
      }
      if (node.draw() == targetNode) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a component instance based on selected type.
   *
   * @param selectedComponent requested component type
   * @return new component instance or {@code null} when type is unsupported
   */
  public CircuitComponent createComponentForType(ComponentsMap selectedComponent) {
    if (selectedComponent == null) {
      return null;
    }
    CircuitComponent component;

    switch (selectedComponent) {
      // digital chips
      case DEMULTIPLEXOR: component = new Demultiplexor(0, 0, COMPONENT_COLOR); break;
      case D_FLIP_FLOP: component = new DflipFlop(0, 0, COMPONENT_COLOR); break;
      case FULL_ADDER: component = new FullAdder(0, 0, COMPONENT_COLOR); break;
      case HALF_ADDER: component = new HalfAdder(0, 0, COMPONENT_COLOR); break;
      case JK_FLIP_FLOP: component = new JKflipFlop(0, 0, COMPONENT_COLOR); break;
      case LATCH: component = new Latch(0, 0, COMPONENT_COLOR); break;
      case MULTIPLEXOR: component = new Multiplexor(0, 0, COMPONENT_COLOR); break;
      case COUNTER: component = new Counter(0, 0, COMPONENT_COLOR); break;
      case RING_COUNTER: component = new RingCounter(0, 0, COMPONENT_COLOR); break;
      case SEVEN_SEGMENT_DECODER: component = new SevenSegmentDecoder(0, 0, COMPONENT_COLOR); break;
      case SEVEN_SEGMENT_DISPLAY: component = new SevenSegmentDisplay(0, 0, COMPONENT_COLOR); break;
      case T_FLIP_FLOP: component = new TflipFlop(0, 0, COMPONENT_COLOR); break;

      // logic gates
      case AND_GATE: component = new AndGate(0, 0, COMPONENT_COLOR); break;
      case NAND_GATE: component = new NandGate(0, 0, COMPONENT_COLOR); break;
      case NOR_GATE: component = new NorGate(0, 0, COMPONENT_COLOR); break;
      case NOT_GATE: component = new NotGate(0, 0, COMPONENT_COLOR); break;
      case OR_GATE: component = new OrGate(0, 0, COMPONENT_COLOR); break;
      case XNOR_GATE: component = new XnorGate(0, 0, COMPONENT_COLOR); break;
      case XOR_GATE: component = new XorGate(0, 0, COMPONENT_COLOR); break;

      //inputs/outputs
      case CLOCK_GENERATOR: component = new ClockGenerator(0, 0, COMPONENT_COLOR); break;
      case LOGICAL_ONE:
      case HIGH_INPUT: component = new HighInput(0, 0, COMPONENT_COLOR); break;
      case LOGICAL_ZERO:
      case LOW_INPUT: component = new LowInput(0, 0, COMPONENT_COLOR); break;
      case LOGICAL_OUTPUT: component = new LogicalOutput(0, 0, COMPONENT_COLOR); break;
      case LIGHT_BULB: component = new LightBulb(0, 0, COMPONENT_COLOR); break;

      default:
        return null;
    }

    component.setComponentType(selectedComponent);
    return component;
  }

  private boolean canEditComponentsAndWires() {
    return !simulationRunning && activeTool == ToolMode.SELECT_DRAG && !placementMode;
  }

  private boolean canPlaceComponents() {
    return !simulationRunning && activeTool == ToolMode.SELECT_DRAG && placementMode;
  }

}
