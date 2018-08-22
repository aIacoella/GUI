package MainPack;

import TrajectoryGenerator.TG;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class PreviewController implements Initializable{

    @FXML
    TextArea textArea = new TextArea();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.setText(TG.getResult());
    }



}
