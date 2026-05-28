package cz.cvut.fel.pjv.logicsim.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Right-side toolbar view with tool mode controls and simulation controls.
 */
public class RightToolbar extends VBox {

  private static final double TOOLBAR_WIDTH = 180;
  private static final double BUTTON_WIDTH = 148;
  private static final double BUTTON_HEIGHT = 42;

  private final ToggleButton selectDragToolButton;
  private final ToggleButton moveToolButton;
  private final Button simulationToggleButton;
  private final Button resetSimulationButton;

  /**
   * Builds toolbar layout and all UI controls.
   */
  public RightToolbar() {

    setAlignment(Pos.TOP_CENTER);
    setPrefWidth(TOOLBAR_WIDTH);
    setMinWidth(TOOLBAR_WIDTH);
    setMaxWidth(TOOLBAR_WIDTH);
    setSpacing(12);
    getStyleClass().add("right-toolbar");

    Label toolsSectionLabel = new Label("Tools");
    toolsSectionLabel.getStyleClass().add("toolbar-section-title");

    selectDragToolButton = createToolButton("Select/Drag");
    moveToolButton = createToolButton("Move");

    Label simulationSectionLabel = new Label("Simulation");
    simulationSectionLabel.getStyleClass().add("toolbar-section-title");

    simulationToggleButton = createToolbarButton("Run");
    resetSimulationButton = createToolbarButton("Reset");

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    getChildren().addAll(
        toolsSectionLabel,
        selectDragToolButton,
        moveToolButton,
        simulationSectionLabel,
        simulationToggleButton,
        resetSimulationButton,
        spacer
    );
  }

  private ToggleButton createToolButton(String text) {
    ToggleButton toggleButton = new ToggleButton(text);
    applyButtonSizing(toggleButton);
    toggleButton.getStyleClass().add("tool-button");
    toggleButton.setMnemonicParsing(false);
    return toggleButton;
  }

  private Button createToolbarButton(String text) {
    Button button = new Button(text);
    applyButtonSizing(button);
    button.getStyleClass().add("toolbar-button");
    button.setMnemonicParsing(false);
    return button;
  }

  private void applyButtonSizing(Labeled labeledButton) {
    labeledButton.setPrefWidth(BUTTON_WIDTH);
    labeledButton.setPrefHeight(BUTTON_HEIGHT);
    labeledButton.setMinWidth(BUTTON_WIDTH);
    labeledButton.setMinHeight(BUTTON_HEIGHT);
    labeledButton.setMaxWidth(BUTTON_WIDTH);
    labeledButton.setMaxHeight(BUTTON_HEIGHT);
  }

  public Button getResetSimulationButton() {
    return resetSimulationButton;
  }

  public ToggleButton getSelectDragToolButton() {
    return selectDragToolButton;
  }

  public ToggleButton getMoveToolButton() {
    return moveToolButton;
  }

  public Button getSimulationToggleButton() {
    return simulationToggleButton;
  }
}
