package cz.cvut.fel.pjv.logicsim;

import cz.cvut.fel.pjv.logicsim.controllers.RightToolbarController;
import cz.cvut.fel.pjv.logicsim.controllers.TopMenuController;
import cz.cvut.fel.pjv.logicsim.controllers.UserPreferencesController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Main coordinator that wires editor, toolbars, menu actions,
 * keyboard shortcuts, and UI logging.
 */
public class Main {

  private static final double UI_MESSAGE_TIMEOUT_SECONDS = 4.0;
  private static final String UI_MESSAGE_BASE_STYLE = "editor-log-message";
  private static final String UI_MESSAGE_INFO_STYLE = "editor-log-info";
  private static final String UI_MESSAGE_ERROR_STYLE = "editor-log-error";

  private static ComponentsMap selectedComponent = ComponentsMap.NONE;

  private final StackPane root;
  private final BorderPane mainContent;
  private final StackPane editorContainer;
  private final TopMenuController menuBarController;
  private final RightToolbarController rightToolbarController;
  private final CircuitEditor circuitEditor;
  private final Label uiMessageLabel;
  private final PauseTransition hideUiMessageTransition;

  private final RadioMenuItem selectDragMenuItem;
  private final ToggleGroup componentToggleGroup;

  private final EventHandler<KeyEvent> escapeKeyHandler = this::handleEscapeShortcut;
  private final EventHandler<KeyEvent> toolShortcutHandler = this::handleToolShortcuts;

  private Runnable closeApplicationAction;
  private UserPreferencesController userPreferences;
  private ToolMode toolMode;
  private boolean simulationRunning;
  private boolean suppressNextSimulationStoppedMessage;
  private final UiMessageLogger uiMessageLogger;

  /**
   * Creates and configures the full main window.
   */
  public Main() {
    root = new StackPane();
    mainContent = new BorderPane();
    editorContainer = new StackPane();
    menuBarController = new TopMenuController();
    rightToolbarController = new RightToolbarController();
    circuitEditor = new CircuitEditor();
    uiMessageLabel = new Label();
    hideUiMessageTransition = new PauseTransition(Duration.seconds(UI_MESSAGE_TIMEOUT_SECONDS));
    uiMessageLogger = new UiMessageLogger() {
      @Override
      public void info(String message) {
        if (!AppLog.isEnabled()) {
          return;
        }
        showUiMessage(message, false);
      }

      @Override
      public void error(String message) {
        if (!AppLog.isEnabled()) {
          return;
        }
        showUiMessage(message, true);
      }
    };

    root.getStyleClass().add("main-window");

    mainContent.setTop(menuBarController.getMenuBar());
    mainContent.setRight(rightToolbarController.getRightToolbar());

    root.getChildren().add(mainContent);

    selectDragMenuItem = menuBarController.getSelectDragMenuItem();
    componentToggleGroup = menuBarController.getComponentToggleGroup();

    configureUiMessageOverlay();
    initialize();
  }

  /**
   * Registers the close callback used by menu actions.
   *
   * @param closeApplicationAction callback that closes the JavaFX stage
   */
  public void setCloseApplicationAction(Runnable closeApplicationAction) {
    this.closeApplicationAction = closeApplicationAction;
    menuBarController.setCloseApplicationAction(closeApplicationAction);
  }

  /**
   * Stores preferences handler and forwards it to menu controller.
   *
   * @param userPreferences preferences used by this window
   */
  public void setUserPreferences(UserPreferencesController userPreferences) {
    this.userPreferences = userPreferences;
    menuBarController.setUserPreferences(userPreferences);
  }

  /**
   * Applies persisted preferences to active UI controls and editor state.
   */
  public void applySavedPreferences() {
    menuBarController.applySavedPreferences();
  }

  /**
   * Hook for graceful shutdown (reserved for future background services).
   */
  public void shutdown() {
    // Reserved for background task/service shutdown.
  }

  /**
   * Returns root node of the main application layout.
   *
   * @return stack pane containing menu, toolbar, editor, and overlays
   */
  public StackPane getRoot() {
    return root;
  }

  /**
   * Returns currently selected component type for placement mode.
   *
   * @return selected component enum value
   */
  public static ComponentsMap getSelectedComponent() {
    return selectedComponent;
  }

  private void initialize() {
    mainContent.setCenter(editorContainer);
    circuitEditor.setMinSize(0, 0);
    selectedComponent = ComponentsMap.NONE;
    toolMode = ToolMode.SELECT_DRAG;
    simulationRunning = false;
    suppressNextSimulationStoppedMessage = false;

    menuBarController.setMainBoard(circuitEditor);
    menuBarController.setUiMessageLogger(uiMessageLogger);
    circuitEditor.setUiMessageLogger(uiMessageLogger);
    if (closeApplicationAction != null) {
      menuBarController.setCloseApplicationAction(closeApplicationAction);
    }
    if (userPreferences != null) {
      menuBarController.setUserPreferences(userPreferences);
    }

    rightToolbarController.setOnSelectDragAction(this::activateSelectDragMode);
    rightToolbarController.setOnMoveAction(this::activateMoveMode);
    rightToolbarController.setOnSimulationRunningChanged(this::onSimulationRunningChanged);
    rightToolbarController.setOnSimulationResetAction(this::onSimulationResetAction);

    configureComponentSelection();

    root.sceneProperty().addListener(ignored -> {

      root.addEventFilter(KeyEvent.KEY_PRESSED, escapeKeyHandler);
      root.addEventFilter(KeyEvent.KEY_PRESSED, toolShortcutHandler);
    });

    activateSelectDragMode();
  }

  private void configureUiMessageOverlay() {
    editorContainer.getChildren().addAll(circuitEditor, uiMessageLabel);
    StackPane.setAlignment(uiMessageLabel, Pos.BOTTOM_RIGHT);
    StackPane.setMargin(uiMessageLabel, new Insets(0, 14, 14, 0));

    uiMessageLabel.setManaged(true);
    uiMessageLabel.setVisible(false);
    uiMessageLabel.setMouseTransparent(true);
    uiMessageLabel.getStyleClass().add(UI_MESSAGE_BASE_STYLE);

    hideUiMessageTransition.setOnFinished(event -> {
      uiMessageLabel.setVisible(false);
      uiMessageLabel.setText("");
    });
  }

  private void showUiMessage(String message, boolean isError) {
    if (!AppLog.isEnabled() || message == null || message.trim().isEmpty()) {
      return;
    }

    Runnable displayMessageAction = () -> {
      uiMessageLabel.setText(message);
      uiMessageLabel.getStyleClass().removeAll(UI_MESSAGE_INFO_STYLE, UI_MESSAGE_ERROR_STYLE);
      uiMessageLabel.getStyleClass().add(isError ? UI_MESSAGE_ERROR_STYLE : UI_MESSAGE_INFO_STYLE);
      uiMessageLabel.setVisible(true);

      hideUiMessageTransition.stop();
      hideUiMessageTransition.playFromStart();
    };

    if (Platform.isFxApplicationThread()) {
      displayMessageAction.run();
    } else {
      Platform.runLater(displayMessageAction);
    }
  }

  private void configureComponentSelection() {
    componentToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
      if (!(newToggle instanceof RadioMenuItem)) {
        return;
      }

      RadioMenuItem selectedItem = (RadioMenuItem) newToggle;
      ComponentsMap component = ComponentsMap.valueOf(selectedItem.getUserData().toString());
      if (component == ComponentsMap.NONE) {
        activateSelectDragMode();
        return;
      }

      activatePlacingMode(component);
    });
  }

  private void activateSelectDragMode() {
    if (simulationRunning) {
      return;
    }

    toolMode = ToolMode.SELECT_DRAG;

    selectedComponent = ComponentsMap.NONE;

    circuitEditor.setPlacementMode(false);

    setActiveTool(ToolMode.SELECT_DRAG);

    rightToolbarController.activateSelectDragMode();

    selectDragMenuItem.setSelected(true);
  }

  private void activateMoveMode() {
    toolMode = ToolMode.MOVE;

    selectedComponent = ComponentsMap.NONE;

    circuitEditor.setPlacementMode(false);

    setActiveTool(ToolMode.MOVE);

    rightToolbarController.activateMoveMode();

    clearComponentMenuSelection();
  }

  private void activatePlacingMode(ComponentsMap component) {
    if (simulationRunning) {
      return;
    }

    toolMode = ToolMode.PLACING;

    selectedComponent = component;

    circuitEditor.setPlacementMode(true);

    setActiveTool(ToolMode.SELECT_DRAG);

    rightToolbarController.activatePlacingMode();

    if (selectDragMenuItem.isSelected()) {
      selectDragMenuItem.setSelected(false);
    }
  }

  private void clearComponentMenuSelection() {
    if (componentToggleGroup != null && componentToggleGroup.getSelectedToggle() != null) {
      componentToggleGroup.getSelectedToggle().setSelected(false);
    }
  }

  private void setActiveTool(ToolMode toolMode) {
    circuitEditor.setActiveTool(toolMode);
  }

  private void onSimulationRunningChanged(boolean simulationRunning) {
    boolean wasRunning = this.simulationRunning;
    this.simulationRunning = simulationRunning;
    circuitEditor.setSimulationRunning(simulationRunning);
    selectDragMenuItem.setDisable(simulationRunning);
    rightToolbarController.setSelectDragEnabled(!simulationRunning);

    if (simulationRunning && !wasRunning) {
      uiMessageLogger.info("Simulation running.");
      return;
    }

    if (!simulationRunning && wasRunning) {
      if (suppressNextSimulationStoppedMessage) {
        suppressNextSimulationStoppedMessage = false;
        return;
      }
      uiMessageLogger.info("Simulation stopped.");
    }
  }

  private void onSimulationResetAction() {
    suppressNextSimulationStoppedMessage = simulationRunning;
    circuitEditor.resetSimulation();
    uiMessageLogger.info("Simulation reset.");
  }

  private void handleEscapeShortcut(KeyEvent event) {
    if (event.getCode() != KeyCode.ESCAPE) {
      return;
    }

    if (circuitEditor.cancelOpenedWire()) {
      event.consume();
      return;
    }

    if (selectedComponent != ComponentsMap.NONE) {
      activateSelectDragMode();
      event.consume();
    }
  }

  private void handleToolShortcuts(KeyEvent event) {
    if (event.getCode() == KeyCode.SPACE && !event.isAltDown() && !event.isShortcutDown()) {
      if (simulationRunning) {
        return;
      }
      activateSelectDragMode();
      event.consume();
      return;
    }
    if (event.getCode() == KeyCode.M && event.isShortcutDown()) {
      activateMoveMode();
      event.consume();
    }
  }
}
