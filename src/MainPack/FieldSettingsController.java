package MainPack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FieldSettingsController implements Initializable{

    @FXML
    TextField widthField = new TextField();

    @FXML
    TextField heightField = new TextField();

    @FXML
    Label errorLabel = new Label();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        widthField.setText("" + Database.getData(Database.FIELDWIDTH));
        heightField.setText("" + Database.getData(Database.FIELDHEIGHT));
    }

    public void saveSettings(){
        String widthString = widthField.getText();
        String heightString = heightField.getText();

        Double width;
        Double height;

        try {
            width = Double.parseDouble(widthString);
            height = Double.parseDouble(heightString);

            Database.setData(Database.FIELDWIDTH, width);
            Database.setData(Database.FIELDHEIGHT, height);

            Database.save();

            System.exit(0);

        } catch (Exception e) {
            errorLabel.setText("Invalid Input");
        }



    }

}
