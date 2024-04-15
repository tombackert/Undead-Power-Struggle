/**
 * The main module of the ups application.
 */
module ups {
    requires javafx.controls;
    requires transitive javafx.graphics;
    
    exports ups.gui;
    exports ups.model;
    exports ups.view;
}
