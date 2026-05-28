package cz.cvut.fel.pjv.logicsim.gui;

import cz.cvut.fel.pjv.logicsim.controllers.TopMenuController;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Top menu view containing file/edit/options actions and component selection.
 */
public class TopMenu {

  private static final double MENU_ICON_SIZE = 16;
  private static final String ICON_PATH_PREFIX = "/cz/cvut/fel/pjv/logicsim/icons/";
  private final MenuBar menuBar;
  private final ToggleGroup componentToggleGroup;


  private final MenuItem newBlankCircuitMenuItem;
  private final MenuItem openFileMenuItem;
  private final MenuItem saveAsMenuItem;
  private final MenuItem toggleFullScreenMenuItem;
  private final MenuItem exitMenuItem;

  private final MenuItem deleteMenuItem;
  private final MenuItem editZoomInMenuItem;
  private final MenuItem editZoomOutMenuItem;

  private final RadioMenuItem selectDragMenuItem;

  private final CheckMenuItem showGridMenuItem;
  private final CheckMenuItem whiteBackgroundMenuItem;
  private final CheckMenuItem enableLoggingMenuItem;

  /**
   * Creates menu structure and loads menu icons.
   */
  public TopMenu() {
    componentToggleGroup = new ToggleGroup();

    newBlankCircuitMenuItem = new MenuItem("New Blank Circuit");
    openFileMenuItem = new MenuItem("Open File...");
    saveAsMenuItem = new MenuItem("Save As...");
    toggleFullScreenMenuItem = new MenuItem("Toggle Full Screen");
    exitMenuItem = new MenuItem("Exit");

    deleteMenuItem = new MenuItem("Delete");
    editZoomInMenuItem = new MenuItem("Zoom In");
    editZoomOutMenuItem = new MenuItem("Zoom Out");

    selectDragMenuItem = createComponentItem("Select/Drag", "NONE");
    selectDragMenuItem.setSelected(true);

    showGridMenuItem = new CheckMenuItem("Show Grid");
    showGridMenuItem.setSelected(true);
    whiteBackgroundMenuItem = new CheckMenuItem("White Background");
    enableLoggingMenuItem = new CheckMenuItem("Enable Logging");

    Menu fileMenu = buildFileMenu();
    Menu editMenu = buildEditMenu();
    Menu componentsMenu = buildComponentsMenu();
    Menu optionsMenu = buildOptionsMenu();

    menuBar = new MenuBar(fileMenu, editMenu, componentsMenu, optionsMenu);
    menuBar.getStyleClass().add("top-menu");
    addIcons();
  }

  public MenuBar getMenuBar() {
    return menuBar;
  }

  public ToggleGroup getComponentToggleGroup() {
    return componentToggleGroup;
  }

  public MenuItem getNewBlankCircuitMenuItem() {
    return newBlankCircuitMenuItem;
  }

  public MenuItem getOpenFileMenuItem() {
    return openFileMenuItem;
  }

  public MenuItem getSaveAsMenuItem() {
    return saveAsMenuItem;
  }

  public MenuItem getToggleFullScreenMenuItem() {
    return toggleFullScreenMenuItem;
  }

  public MenuItem getExitMenuItem() {
    return exitMenuItem;
  }

  public MenuItem getDeleteMenuItem() {
    return deleteMenuItem;
  }

  public MenuItem getEditZoomInMenuItem() {
    return editZoomInMenuItem;
  }

  public MenuItem getEditZoomOutMenuItem() {
    return editZoomOutMenuItem;
  }

  public RadioMenuItem getSelectDragMenuItem() {
    return selectDragMenuItem;
  }

  public CheckMenuItem getShowGridMenuItem() {
    return showGridMenuItem;
  }

  public CheckMenuItem getWhiteBackgroundMenuItem() {
    return whiteBackgroundMenuItem;
  }

  public CheckMenuItem getEnableLoggingMenuItem() {
    return enableLoggingMenuItem;
  }

  private Menu buildFileMenu() {
    Menu fileMenu = new Menu("File");
    fileMenu.getItems().addAll(
        newBlankCircuitMenuItem,
        openFileMenuItem,
        saveAsMenuItem,
        new SeparatorMenuItem(),
        toggleFullScreenMenuItem,
        new SeparatorMenuItem(),
        exitMenuItem
    );
    return fileMenu;
  }

  private Menu buildEditMenu() {
    Menu editMenu = new Menu("Edit");
    editMenu.getItems().addAll(
        deleteMenuItem,
        new SeparatorMenuItem(),
        editZoomInMenuItem,
        editZoomOutMenuItem
    );
    return editMenu;
  }

  private Menu buildComponentsMenu() {
    Menu componentsMenu = new Menu("Components");
    componentsMenu.getItems().addAll(
        selectDragMenuItem,
        new SeparatorMenuItem(),
        buildLogicGatesMenu(),
        buildInputsMenu(),
        buildOutputsMenu(),
        buildDigitalChipsMenu()
    );
    return componentsMenu;
  }

  private Menu buildLogicGatesMenu() {
    Menu menu = new Menu("Logic Gates");
    menu.getItems().addAll(
        createComponentItem("AND", "AND_GATE"),
        createComponentItem("OR", "OR_GATE"),
        createComponentItem("NOT", "NOT_GATE"),
        createComponentItem("NAND", "NAND_GATE"),
        createComponentItem("NOR", "NOR_GATE"),
        createComponentItem("XOR", "XOR_GATE"),
        createComponentItem("XNOR", "XNOR_GATE")
    );
    return menu;
  }

  private Menu buildInputsMenu() {
    Menu menu = new Menu("Inputs");
    menu.getItems().addAll(
        createComponentItem("Logical 1", "LOGICAL_ONE"),
        createComponentItem("Logical 0", "LOGICAL_ZERO"),
        createComponentItem("Clock Generator", "CLOCK_GENERATOR")
    );
    return menu;
  }

  private Menu buildOutputsMenu() {
    Menu menu = new Menu("Outputs");
    menu.getItems().addAll(
        createComponentItem("Logical Output", "LOGICAL_OUTPUT"),
        createComponentItem("Light Bulb", "LIGHT_BULB")
    );
    return menu;
  }

  private Menu buildDigitalChipsMenu() {
    Menu menu = new Menu("Digital Chips");
    menu.getItems().addAll(
        createComponentItem("JK Flip-Flop", "JK_FLIP_FLOP"),
        createComponentItem("D Flip-Flop", "D_FLIP_FLOP"),
        createComponentItem("T Flip-Flop", "T_FLIP_FLOP"),
        createComponentItem("Counter", "COUNTER"),
        createComponentItem("Multiplexor", "MULTIPLEXOR"),
        createComponentItem("Demultiplexor", "DEMULTIPLEXOR"),
        createComponentItem("7 Segment Decoder", "SEVEN_SEGMENT_DECODER"),
        createComponentItem("7 Segment Display", "SEVEN_SEGMENT_DISPLAY"),
        createComponentItem("Ring Counter", "RING_COUNTER"),
        createComponentItem("Latch", "LATCH")
    );
    return menu;
  }

  private Menu buildOptionsMenu() {
    Menu optionsMenu = new Menu("Options");
    optionsMenu.getItems().addAll(
        showGridMenuItem,
        whiteBackgroundMenuItem,
        enableLoggingMenuItem
    );
    return optionsMenu;
  }

  private RadioMenuItem createComponentItem(String text, String userData) {
    RadioMenuItem menuItem = new RadioMenuItem(text);
    menuItem.setToggleGroup(componentToggleGroup);
    menuItem.setUserData(userData);
    return menuItem;
  }

  private void addIcons() {
    newBlankCircuitMenuItem.setGraphic(loadMenuIcon("new-blank-circuit.png"));
    openFileMenuItem.setGraphic(loadMenuIcon("open-file.png"));
    saveAsMenuItem.setGraphic(loadMenuIcon("save-as.png"));
    toggleFullScreenMenuItem.setGraphic(loadMenuIcon("toggle-fullscreen.png"));

    deleteMenuItem.setGraphic(loadMenuIcon("cut.png"));
    editZoomInMenuItem.setGraphic(loadMenuIcon("zoom-in.png"));
    editZoomOutMenuItem.setGraphic(loadMenuIcon("zoom-out.png"));
  }

  private Node loadMenuIcon(String iconName) {
    Image image = new Image(
        Objects.requireNonNull(
            TopMenuController.class.getResourceAsStream(ICON_PATH_PREFIX + iconName)
        )
    );
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(MENU_ICON_SIZE);
    imageView.setFitHeight(MENU_ICON_SIZE);
    imageView.setPreserveRatio(true);
    return imageView;
  }
}
