package cz.cvut.fel.pjv.logicsim.controllers;

import cz.cvut.fel.pjv.logicsim.gui.RightToolbar;
import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

/**
 * Controller for right-side toolbar actions (tool selection and simulation
 * controls).
 */
public class RightToolbarController {

  private static final String RUN_BUTTON_STYLE_CLASS = "simulation-run";
  private static final String STOP_BUTTON_STYLE_CLASS = "simulation-stop";
  private static final String RESET_BUTTON_STYLE_CLASS = "simulation-reset";
  private static final String TOOL_BUTTON_SELECTED_STYLE_CLASS = "tool-button-selected";

  private final RightToolbar rightToolbar;
  private final ToggleButton selectDragToolButton;
  private final ToggleButton moveToolButton;
  private final Button simulationToggleButton;
  private final Button resetSimulationButton;

  private Runnable onSelectDragAction;
  private Runnable onMoveAction;
  private Runnable onSimulationResetAction;
  private Consumer<Boolean> onSimulationRunningChanged;
  private boolean simulationRunning;

  /**
   * Creates toolbar controller, binds UI handlers, and initializes default
   * styles.
   */
  public RightToolbarController() {
    rightToolbar = new RightToolbar();
    selectDragToolButton = rightToolbar.getSelectDragToolButton();
    moveToolButton = rightToolbar.getMoveToolButton();
    simulationToggleButton = rightToolbar.getSimulationToggleButton();
    resetSimulationButton = rightToolbar.getResetSimulationButton();
    simulationRunning = false;

    bindActions();
    updateSimulationToggleButton();
    updateToolButtonStyles();
  }

  /**
   * Returns toolbar view managed by this controller.
   *
   * @return right toolbar UI component
   */
  public RightToolbar getRightToolbar() {
    return rightToolbar;
  }

  /**
   * Registers callback for Select/Drag tool activation.
   *
   * @param onSelectDragAction callback executed when button is pressed
   */
  public void setOnSelectDragAction(Runnable onSelectDragAction) {
    this.onSelectDragAction = onSelectDragAction;
  }

  /**
   * Registers callback for Move tool activation.
   *
   * @param onMoveAction callback executed when move button is pressed
   */
  public void setOnMoveAction(Runnable onMoveAction) {
    this.onMoveAction = onMoveAction;
  }

  /**
   * Registers callback for simulation reset button.
   *
   * @param onSimulationResetAction callback executed on reset
   */
  public void setOnSimulationResetAction(Runnable onSimulationResetAction) {
    this.onSimulationResetAction = onSimulationResetAction;
  }

  /**
   * Registers callback for simulation running-state changes.
   *
   * @param onSimulationRunningChanged callback receiving new running state
   */
  public void setOnSimulationRunningChanged(Consumer<Boolean> onSimulationRunningChanged) {
    this.onSimulationRunningChanged = onSimulationRunningChanged;
  }

  /**
   * Activates Select/Drag tool button visual state.
   */
  public void activateSelectDragMode() {
    selectDragToolButton.setSelected(true);
    moveToolButton.setSelected(false);
    updateToolButtonStyles();
  }

  /**
   * Activates Move tool button visual state.
   */
  public void activateMoveMode() {
    selectDragToolButton.setSelected(false);
    moveToolButton.setSelected(true);
    updateToolButtonStyles();
  }

  /**
   * Clears explicit tool button selection while component placing mode is
   * active.
   */
  public void activatePlacingMode() {
    selectDragToolButton.setSelected(false);
    moveToolButton.setSelected(false);
    updateToolButtonStyles();
  }

  /**
   * Enables or disables Select/Drag tool interaction.
   *
   * @param enabled {@code true} keeps Select/Drag button enabled
   */
  public void setSelectDragEnabled(boolean enabled) {
    selectDragToolButton.setDisable(!enabled);
  }

  private void bindActions() {
    selectDragToolButton.setOnAction(event -> {
      if (onSelectDragAction != null) {
        onSelectDragAction.run();
      }
    });
    moveToolButton.setOnAction(event -> {
      if (onMoveAction != null) {
        onMoveAction.run();
      }
    });
    simulationToggleButton.setOnAction(event -> toggleSimulation());
    resetSimulationButton.setOnAction(event -> resetSimulation());
  }

  private void toggleSimulation() {
    simulationRunning = !simulationRunning;
    if (onSimulationRunningChanged != null) {
      onSimulationRunningChanged.accept(simulationRunning);
    }
    updateSimulationToggleButton();
  }

  private void resetSimulation() {
    simulationRunning = false;
    if (onSimulationResetAction != null) {
      onSimulationResetAction.run();
    }
    if (onSimulationRunningChanged != null) {
      onSimulationRunningChanged.accept(simulationRunning);
    }
    updateSimulationToggleButton();
  }

  private void updateSimulationToggleButton() {
    simulationToggleButton.getStyleClass().removeAll(RUN_BUTTON_STYLE_CLASS, STOP_BUTTON_STYLE_CLASS);
    if (simulationRunning) {
      simulationToggleButton.setText("Stop");
      simulationToggleButton.getStyleClass().add(STOP_BUTTON_STYLE_CLASS);
    } else {
      simulationToggleButton.setText("Run");
      simulationToggleButton.getStyleClass().add(RUN_BUTTON_STYLE_CLASS);
    }
    if (!resetSimulationButton.getStyleClass().contains(RESET_BUTTON_STYLE_CLASS)) {
      resetSimulationButton.getStyleClass().add(RESET_BUTTON_STYLE_CLASS);
    }
  }

  private void updateToolButtonStyles() {
    selectDragToolButton.getStyleClass().remove(TOOL_BUTTON_SELECTED_STYLE_CLASS);
    moveToolButton.getStyleClass().remove(TOOL_BUTTON_SELECTED_STYLE_CLASS);
    if (selectDragToolButton.isSelected()) {
      selectDragToolButton.getStyleClass().add(TOOL_BUTTON_SELECTED_STYLE_CLASS);
    }
    if (moveToolButton.isSelected()) {
      moveToolButton.getStyleClass().add(TOOL_BUTTON_SELECTED_STYLE_CLASS);
    }
  }
}
