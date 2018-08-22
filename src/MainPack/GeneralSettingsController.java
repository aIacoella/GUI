package MainPack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class GeneralSettingsController implements Initializable{

    @FXML
    TextField dtField = new TextField();

    @FXML
    TextField calcdtField = new TextField();

    @FXML
    Label errorLabel = new Label();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dtField.setText("" + Database.getData(Database.DT));
        calcdtField.setText("" + Database.getData(Database.CALCULUSDT));
    }

    public void saveSettings(){
        String dtString = dtField.getText();
        String calcdtString = calcdtField.getText();

        Double dt;
        Double calcdt;
        try {
            dt = Double.parseDouble(dtString);
            calcdt = Double.parseDouble(calcdtString);

            Database.setData(Database.DT, dt);
            Database.setData(Database.CALCULUSDT, calcdt);
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
