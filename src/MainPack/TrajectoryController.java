package MainPack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.xml.crypto.Data;
import java.net.URL;
import java.util.ResourceBundle;

public class TrajectoryController implements Initializable{

    @FXML
    TextField speedField = new TextField();

    @FXML
    TextField accelerationField = new TextField();

    @FXML
    TextField decelerationField = new TextField();

    @FXML
    Label errorLabel = new Label();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        speedField.setText("" + Database.getData(Database.SPEED));
        accelerationField.setText("" + Database.getData(Database.ACCELERATION));
        decelerationField.setText("" + Database.getData(Database.DECELERATION));
    }

    public void saveSettings(){
        String speedString = speedField.getText();
        String accelerationString = accelerationField.getText();
        String decelerationString = decelerationField.getText();

        Double speed;
        Double acceleration;
        Double deceleration;
        try {
            speed = Double.parseDouble(speedString);
            acceleration = Double.parseDouble(accelerationString);
            deceleration = Double.parseDouble(decelerationString);

            Database.setData(Database.SPEED, speed);
            Database.setData(Database.ACCELERATION, acceleration);
            Database.setData(Database.DECELERATION, deceleration);
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
