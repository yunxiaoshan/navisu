/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bzh.terrevirtuelle.navisu.widgets.textlist;

import bzh.terrevirtuelle.navisu.widgets.Widget2DController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * @date 6 mars 2015
 * @author Serge Morvan
 */
public class TextListController
        extends Widget2DController {

    @FXML
    public Group textListPanel;
    @FXML
    GridPane gridPane;
    @FXML
    ScrollPane scrollPane;

    public TextListController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TextListPanel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        gridPane.setStyle("-fx-background-color: #00524e99");
        scrollPane.setStyle("-fx-background-color: #00524e99");
    }

    public TextListController(KeyCode keyCode, KeyCombination.Modifier keyCombination) {
        super(keyCode, keyCombination);
    }

    public Group getTextListPanel() {
        return textListPanel;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

}
