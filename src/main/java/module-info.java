/**
 * The main module of the ups application.
 */
module ups {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires java.desktop;
    requires javafx.fxml;

    exports ups.gui;
    exports ups.model;
    opens ups.view to javafx.fxml;
    exports ups.view;
    opens ups.controller to javafx.fxml;
    exports ups.controller;
    opens ups.gui to javafx.fxml;
}