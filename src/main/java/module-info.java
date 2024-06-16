/**
 * The main module of the ups application.
 */
module ups {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.base;

    exports ups;
    exports ups.model to javafx.graphics;
    exports ups.view to javafx.graphics;
    exports ups.controller to javafx.graphics;
}
