module cz.cvut.fel.pjv.logicsim {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.prefs;
  requires java.logging;
  requires com.google.gson;

  opens cz.cvut.fel.pjv.logicsim to javafx.fxml, com.google.gson;
  exports cz.cvut.fel.pjv.logicsim;
  exports cz.cvut.fel.pjv.logicsim.gui to javafx.graphics;
}
