package MainPack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class RobotSettingsController implements Initializable{

    @FXML
    TextField widthField = new TextField();

    @FXML
    TextField heightField = new TextField();

    @FXML
    Label errorLabel = new Label();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        widthField.setText("" + Database.getData(Database.ROBOTWIDTH));
        heightField.setText("" + Database.getData(Database.ROBOTHEIGHT));
    }

    public void saveSettings(){
        String widthString = widthField.getText();
        String heightString = heightField.getText();

        Double width;
        Double height;

        try {
            width = Double.parseDouble(widthString);
            height = Double.parseDouble(heightString);

            Database.setData(Database.ROBOTWIDTH, width);
            Database.setData(Database.ROBOTHEIGHT, height);

            Database.save();

            Stage stage = (Stage) errorLabel.getScene().getWindow();

            stage.fireEvent(
                    new WindowEvent(
                            stage,
                            WindowEvent.WINDOW_CLOSE_REQUEST
                    )
            );

        } catch (Exception e) {
            errorLabel.setText("Invalid Input");
        }



    }

}
