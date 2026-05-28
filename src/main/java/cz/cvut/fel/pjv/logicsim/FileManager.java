package cz.cvut.fel.pjv.logicsim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cz.cvut.fel.pjv.logicsim.logicalComponents.CircuitComponent;
import cz.cvut.fel.pjv.logicsim.logicalComponents.ComponentNode;
import cz.cvut.fel.pjv.logicsim.logicalComponents.Wire;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Handles loading and saving circuits as JSON files.
 */
public class FileManager {

  /**
   * Result of a file operation triggered from the UI.
   */
  public enum FileOperationResult {
    SUCCESS,
    CANCELED,
    FAILED
  }

  private static final String JSON_EXTENSION = ".json";
  private static final String DEFAULT_FILENAME = "circuit.json";
  private static final String DEFAULT_COMPONENT_COLOR = "#808080";
  private static final double GRID_TILE_SIZE = 25.0;
  private static final FileChooser.ExtensionFilter JSON_FILE_FILTER =
      new FileChooser.ExtensionFilter("LogicSim Files (*.json)", "*.json");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Logger LOGGER = AppLog.getLogger(FileManager.class);

  private File loadedFile;

  /**
   * Opens a save dialog and returns target file path
   *
   * @param ownerWindow dialog owner window
   * @return selected file with .json extension, or {@code null} when canceled
   */
  public File chooseSaveTarget(Window ownerWindow) {
    FileChooser chooser = createJsonChooser("Save Circuit As");
    chooser.setInitialFileName(loadedFile != null ? loadedFile.getName() : DEFAULT_FILENAME);

    File selectedFile = chooser.showSaveDialog(ownerWindow);
    if (selectedFile == null) {
      return null;
    }

    return ensureJsonExtension(selectedFile);
  }

  /**
   * Stores the latest successfully used file path for default save naming.
   *
   * @param loadedFile file that is currently loaded/saved
   */
  public void setLoadedFile(File loadedFile) {
    if (loadedFile == null) {
      return;
    }
    this.loadedFile = loadedFile;
  }

  /**
   * Captures current editor state
   *
   * @param editor source editor
   * @return data used for JSON output
   */
  public WorkspaceData buildWorkspaceSnapshot(CircuitEditor editor) {
    if (editor == null) {
      return null;
    }
    return buildWorkspace(editor);
  }

  /**
   * Writes a prepared workspace
   *
   * @param targetFile destination file
   * @param workspace workspace snapshot to serialize
   * @throws IOException when file creation or writing fails
   */
  public void writeWorkspaceToFile(File targetFile, WorkspaceData workspace) throws IOException {
    if (targetFile == null || workspace == null) {
      return;
    }

    File parentDirectory = targetFile.getParentFile();
    if (parentDirectory != null && !parentDirectory.exists()) {
      Files.createDirectories(parentDirectory.toPath());
    }

    String json = GSON.toJson(workspace);
    Files.writeString(
        targetFile.toPath(),
        json,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE
    );
  }

  /**
   * Loads file from a user-selected JSON file and applies it to editor.
   *
   * @param ownerWindow owner window for file chooser
   * @param editor destination editor
   * @return operation result including cancel/failure status
   */
  public FileOperationResult loadFile(Window ownerWindow, CircuitEditor editor) {
    if (editor == null) {
      return FileOperationResult.FAILED;
    }

    FileChooser chooser = createJsonChooser("Open Circuit");
    File selectedFile = chooser.showOpenDialog(ownerWindow);
    if (selectedFile == null) {
      return FileOperationResult.CANCELED;
    }

    try {
      String json = Files.readString(selectedFile.toPath(), StandardCharsets.UTF_8);
      WorkspaceData workspace = GSON.fromJson(json, WorkspaceData.class);
      if (workspace == null) {
        LOGGER.warning("Failed to load circuit file (parsed workspace is null): " + selectedFile.getAbsolutePath());
        return FileOperationResult.FAILED;
      }

      applyWorkspace(editor, workspace);
      loadedFile = selectedFile;
      LOGGER.info("Circuit loaded from: " + selectedFile.getAbsolutePath());
      return FileOperationResult.SUCCESS;
    } catch (IOException | JsonSyntaxException exception) {
      LOGGER.log(Level.WARNING, "Failed to load circuit file: " + selectedFile.getAbsolutePath(), exception);
      return FileOperationResult.FAILED;
    }
  }


  private WorkspaceData buildWorkspace(CircuitEditor editor) {
    WorkspaceData workspace = new WorkspaceData();
    workspace.editor.gridVisible = editor.isGridVisible();
    workspace.editor.whiteBackground = editor.isWhiteBackground();

    // Copy component placements and identities.
    for (CircuitComponent component : editor.components) {

      if (component == null) {
        continue;
      }

      ComponentsMap componentType = component.getComponentType();

      if (componentType == null) {
        continue;
      }

      Node model = component.getComponentModel();
      ComponentData componentData = new ComponentData();
      componentData.type = componentType.name();
      componentData.posX = snapToGrid(resolveEffectivePositionX(model));
      componentData.posY = snapToGrid(resolveEffectivePositionY(model));
      componentData.color = DEFAULT_COMPONENT_COLOR;
      componentData.startNodeId = component.startNodeId;
      workspace.components.add(componentData);
    }

    // Copy valid (fully connected) wires.
    for (Wire wire : editor.getWireManager().getWires()) {
      if (wire == null || wire.getEndNode() == null) {
        continue;
      }

      WireData wireData = new WireData();
      wireData.startId = wire.getStartId();
      wireData.endId = wire.getEndId();
      workspace.wires.add(wireData);
    }

    return workspace;
  }

  private void applyWorkspace(CircuitEditor editor, WorkspaceData workspace) {
    editor.newBlankCircuit();

    if (workspace.editor != null) {
      if (workspace.editor.gridVisible != null) {
        editor.setGridVisible(workspace.editor.gridVisible);
      }
      if (workspace.editor.whiteBackground != null) {
        editor.setWhiteBackground(workspace.editor.whiteBackground);
      }
    }

    // Recreate component instances first, so nodes exist before restoring wires.
    if (workspace.components != null) {
      for (ComponentData componentData : workspace.components) {
        if (componentData == null || componentData.type == null) {
          continue;
        }

        ComponentsMap componentType;
        try {
          componentType = ComponentsMap.valueOf(componentData.type);
        } catch (IllegalArgumentException exception) {
          continue;
        }

        CircuitComponent component = editor.createComponentForType(componentType);
        if (component == null) {
          continue;
        }

        int startNodeId = componentData.startNodeId != null ? componentData.startNodeId : 0;
        ComponentNode.setCurrentId(startNodeId);
        component.render();
        Node componentModel = component.getComponentModel();
        componentModel.setTranslateX(0);
        componentModel.setTranslateY(0);
        componentModel.setLayoutX(snapToGrid(componentData.posX != null ? componentData.posX : 0));
        componentModel.setLayoutY(snapToGrid(componentData.posY != null ? componentData.posY : 0));
        editor.addCircuitComponent(component);
      }
    }

    List<ComponentNode> nodeList = ComponentNode.getNodeList();
    // Reconnect wires by restored node IDs.
    if (workspace.wires != null) {
      for (WireData wireData : workspace.wires) {
        if (wireData == null || wireData.startId == null || wireData.endId == null) {
          continue;
        }
        if (!isNodeIndexValid(nodeList, wireData.startId) || !isNodeIndexValid(nodeList, wireData.endId)) {
          continue;
        }
        editor.getWireManager().addNode(nodeList.get(wireData.startId));
        editor.getWireManager().addNode(nodeList.get(wireData.endId));
      }
    }

    editor.getWireManager().draw();
    editor.resetSimulation();
  }

  private FileChooser createJsonChooser(String title) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    fileChooser.getExtensionFilters().add(JSON_FILE_FILTER);
    fileChooser.setSelectedExtensionFilter(JSON_FILE_FILTER);
    return fileChooser;
  }

  private File ensureJsonExtension(File file) {
    String filename = file.getName();
    if (filename.toLowerCase().endsWith(JSON_EXTENSION)) {
      return file;
    }
    return new File(file.getParentFile(), filename + JSON_EXTENSION);
  }

  private boolean isNodeIndexValid(List<ComponentNode> nodeList, int index) {
    return index >= 0 && index < nodeList.size() && nodeList.get(index) != null;
  }

  private double resolveEffectivePositionX(Node model) {
    if (model == null) {
      return 0;
    }
    return model.getLayoutX() + model.getTranslateX();
  }

  private double resolveEffectivePositionY(Node model) {
    if (model == null) {
      return 0;
    }
    return model.getLayoutY() + model.getTranslateY();
  }

  private double snapToGrid(double coordinate) {
    return Math.round(coordinate / GRID_TILE_SIZE) * GRID_TILE_SIZE;
  }

  /**
   * Top-level serialized workspace object.
   */
  public static final class WorkspaceData {
    public List<ComponentData> components = new ArrayList<>();
    public List<WireData> wires = new ArrayList<>();
    public EditorData editor = new EditorData();
  }

  /**
   * Serialized component placement record.
   */
  public static final class ComponentData {
    public String type;
    public Double posX;
    public Double posY;
    public String color;
    public Integer startNodeId;
  }

  /**
   * Serialized wire endpoint record.
   */
  public static final class WireData {
    public Integer startId;
    public Integer endId;
  }

  /**
   * Serialized editor-level visual settings.
   */
  public static final class EditorData {
    public Boolean gridVisible;
    public Boolean whiteBackground;
  }
}
