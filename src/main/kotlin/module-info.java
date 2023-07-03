module ru.etu.visualkruskal {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens ru.etu.visualkruskal to javafx.fxml;
    exports ru.etu.visualkruskal;
}