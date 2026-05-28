package cz.cvut.fel.pjv.logicsim.controllers;

import cz.cvut.fel.pjv.logicsim.AppLog;
import cz.cvut.fel.pjv.logicsim.CircuitEditor;
import cz.cvut.fel.pjv.logicsim.FileManager;
import cz.cvut.fel.pjv.logicsim.FileManager.FileOperationResult;
import cz.cvut.fel.pjv.logicsim.UiMessageLogger;
import cz.cvut.fel.pjv.logicsim.gui.TopMenu;
import java.io.File;
import javafx.concurrent.Task;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller responsible for top menu behavior and wiring menu actions to the
 * editor.
 */
public class TopMenuController {

  private final MenuBar menuBar;
  private final CheckMenuItem showGridMenuItem;
  private final CheckMenuItem whiteBackgroundMenuItem;
  private final CheckMenuItem enableLoggingMenuItem;
  private final MenuItem exitMenuItem;
  private final MenuItem toggleFullScreenMenuItem;
  private final RadioMenuItem selectDragMenuItem;
  private final ToggleGroup componentToggleGroup;
  private final MenuItem newBlankCircuitMenuItem;
  private final MenuItem openFileMenuItem;
  private final MenuItem saveAsMenuItem;
  private final MenuItem deleteMenuItem;
  private final MenuItem editZoomInMenuItem;
  private final MenuItem editZoomOutMenuItem;
  private final FileManager fileManager;

  private CircuitEditor circuitEditor;
  private UserPreferencesController userPreferences;
  private Runnable closeApplicationAction;
  private UiMessageLogger uiMessageLogger;

  /**
   * Creates menu controller, captures menu item references, and binds actions.
   */
  public TopMenuController() {
    TopMenu topMenu = new TopMenu();

    menuBar = topMenu.getMenuBar();
    showGridMenuItem = topMenu.getShowGridMenuItem();
    whiteBackgroundMenuItem = topMenu.getWhiteBackgroundMenuItem();
    enableLoggingMenuItem = topMenu.getEnableLoggingMenuItem();
    exitMenuItem = topMenu.getExitMenuItem();
    toggleFullScreenMenuItem = topMenu.getToggleFullScreenMenuItem();
    selectDragMenuItem = topMenu.getSelectDragMenuItem();
    componentToggleGroup = topMenu.getComponentToggleGroup();
    newBlankCircuitMenuItem = topMenu.getNewBlankCircuitMenuItem();
    openFileMenuItem = topMenu.getOpenFileMenuItem();
    saveAsMenuItem = topMenu.getSaveAsMenuItem();
    deleteMenuItem = topMenu.getDeleteMenuItem();
    editZoomInMenuItem = topMenu.getEditZoomInMenuItem();
    editZoomOutMenuItem = topMenu.getEditZoomOutMenuItem();
    fileManager = new FileManager();
    enableLoggingMenuItem.setSelected(AppLog.isEnabled());

    bindActionsToTopMenuItems();
    applyShortcuts();
  }

  /**
   * Returns menu bar view managed by this controller.
   *
   * @return top-level menu bar
   */
  public MenuBar getMenuBar() {
    return menuBar;
  }

  /**
   * Returns Select/Drag menu item used for tool-state synchronization.
   *
   * @return select-drag radio menu item
   */
  public RadioMenuItem getSelectDragMenuItem() {
    return selectDragMenuItem;
  }

  /**
   * Returns toggle group containing all component placement menu items.
   *
   * @return component toggle group
   */
  public ToggleGroup getComponentToggleGroup() {
    return componentToggleGroup;
  }

  /**
   * Connects the main circuit editor instance to menu actions.
   *
   * @param circuitEditor editor instance controlled by the menu
   */
  public void setMainBoard(CircuitEditor circuitEditor) {
    this.circuitEditor = circuitEditor;
    if (this.circuitEditor != null) {
      this.circuitEditor.setGridVisible(showGridMenuItem.isSelected());
      this.circuitEditor.setWhiteBackground(whiteBackgroundMenuItem.isSelected());
    }
  }

  /**
   * Injects preferences controller used for persisting menu options.
   *
   * @param userPreferences preferences storage/controller
   */
  public void setUserPreferences(UserPreferencesController userPreferences) {
    this.userPreferences = userPreferences;
  }

  /**
   * Injects callback used by File/Exit action.
   *
   * @param closeApplicationAction stage-close callback
   */
  public void setCloseApplicationAction(Runnable closeApplicationAction) {
    this.closeApplicationAction = closeApplicationAction;
  }

  /**
   * Injects transient UI message logger used for operation feedback.
   *
   * @param uiMessageLogger logger implementation bound to current window
   */
  public void setUiMessageLogger(UiMessageLogger uiMessageLogger) {
    this.uiMessageLogger = uiMessageLogger;
  }

  /**
   * Applies persisted user preferences to menu controls and editor rendering.
   */
  public void applySavedPreferences() {
    if (userPreferences == null) {
      return;
    }

    showGridMenuItem.setSelected(userPreferences.isShowGrid());
    whiteBackgroundMenuItem.setSelected(userPreferences.isWhiteBackground());

    if (circuitEditor != null) {
      circuitEditor.setGridVisible(showGridMenuItem.isSelected());
      circuitEditor.setWhiteBackground(whiteBackgroundMenuItem.isSelected());
    }
  }

  private void bindActionsToTopMenuItems() {
    newBlankCircuitMenuItem.setOnAction(event -> onNewBlankCircuitAction());
    openFileMenuItem.setOnAction(event -> onOpenFileAction());
    saveAsMenuItem.setOnAction(event -> onSaveAsAction());
    toggleFullScreenMenuItem.setOnAction(event -> onToggleFullScreenAction());
    exitMenuItem.setOnAction(event -> onExitAction());
    deleteMenuItem.setOnAction(event -> onDeleteAction());
    editZoomInMenuItem.setOnAction(event -> onZoomInAction());
    editZoomOutMenuItem.setOnAction(event -> onZoomOutAction());

    showGridMenuItem.setOnAction(event -> onShowGridAction());
    whiteBackgroundMenuItem.setOnAction(event -> onWhiteBackgroundAction());
    enableLoggingMenuItem.setOnAction(event -> onEnableLoggingAction());
  }

  private void applyShortcuts() {
    ShortcutsController.applyShortcuts(
        newBlankCircuitMenuItem,
        openFileMenuItem,
        saveAsMenuItem,
        toggleFullScreenMenuItem,
        exitMenuItem,
        deleteMenuItem,
        editZoomInMenuItem,
        editZoomOutMenuItem
    );
  }

  private void onNewBlankCircuitAction() {
    if (circuitEditor != null) {
      circuitEditor.newBlankCircuit();
    }
  }

  private void onOpenFileAction() {
    if (circuitEditor != null) {
      FileOperationResult result = fileManager.loadFile(getStage(), circuitEditor);
      if (result == FileOperationResult.SUCCESS) {
        notifyInfo("File opened.");
      } else if (result == FileOperationResult.FAILED) {
        notifyError("Failed to open file.");
      }
    }
  }

  private void onSaveAsAction() {
    if (circuitEditor == null) {
      return;
    }

    File targetFile = fileManager.chooseSaveTarget(getStage());
    if (targetFile == null) {
      return;
    }

    FileManager.WorkspaceData snapshot = fileManager.buildWorkspaceSnapshot(circuitEditor);
    if (snapshot == null) {
      notifyError("Failed to save file.");
      return;
    }

    Task<String> saveTask = new Task<>() {
      @Override
      protected String call() throws Exception {
        String workerThreadName = Thread.currentThread().getName();
        fileManager.writeWorkspaceToFile(targetFile, snapshot);
        return workerThreadName;
      }
    };

    saveTask.setOnSucceeded(event -> {
      fileManager.setLoadedFile(targetFile);
      notifyInfo("File saved.");
    });

    saveTask.setOnFailed(event -> notifyError("Failed to save file."));

    Thread saveThread = new Thread(saveTask, "save-task");
    saveThread.setDaemon(true);
    saveThread.start();
  }

  private void onToggleFullScreenAction() {
    Stage stage = getStage();
    if (stage == null) {
      return;
    }
    stage.setFullScreen(!stage.isFullScreen());
  }

  private void onExitAction() {
    if (closeApplicationAction != null) {
      closeApplicationAction.run();
      return;
    }
    Stage stage = getStage();
    if (stage != null) {
      stage.close();
    }
  }

  private void onDeleteAction() {
    if (circuitEditor != null) {
      circuitEditor.deleteSelectedComponents();
    }
  }

  private void onZoomInAction() {
    if (circuitEditor != null) {
      circuitEditor.zoomIn();
    }
  }

  private void onZoomOutAction() {
    if (circuitEditor != null) {
      circuitEditor.zoomOut();
    }
  }

  private void onShowGridAction() {
    if (circuitEditor != null) {
      circuitEditor.setGridVisible(showGridMenuItem.isSelected());
    }
    saveShowGridPreference();
  }

  private void onWhiteBackgroundAction() {
    if (circuitEditor != null) {
      circuitEditor.setWhiteBackground(whiteBackgroundMenuItem.isSelected());
    }
    saveWhiteBackgroundPreference();
  }

  private void onEnableLoggingAction() {
    AppLog.setEnabled(enableLoggingMenuItem.isSelected());
  }

  private void notifyInfo(String message) {
    if (uiMessageLogger != null) {
      uiMessageLogger.info(message);
    }
  }

  private void notifyError(String message) {
    if (uiMessageLogger != null) {
      uiMessageLogger.error(message);
    }
  }

  private Stage getStage() {
    if (menuBar.getScene() == null) {
      return null;
    }
    Window window = menuBar.getScene().getWindow();
    if (window instanceof Stage) {
      return (Stage) window;
    }
    return null;
  }

  private void saveShowGridPreference() {
    if (userPreferences != null) {
      userPreferences.setShowGrid(showGridMenuItem.isSelected());
    }
  }

  private void saveWhiteBackgroundPreference() {
    if (userPreferences != null) {
      userPreferences.setWhiteBackground(whiteBackgroundMenuItem.isSelected());
    }
  }

}
