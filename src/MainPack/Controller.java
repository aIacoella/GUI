package MainPack;

import TrajectoryGenerator.TG;
import TrajectoryGenerator.WaypointTG;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable{

    private enum selection{
        UNSELECTED, SELECTED, MOVING, POSITIONING;
    }

    @FXML
    public AnchorPane field = new AnchorPane();

    @FXML
    public SplitPane pane = new SplitPane();

    @FXML
    public VBox WPList = new VBox();

    @FXML
    public AnchorPane wayPointUI = new AnchorPane();

    @FXML
    public Label wpTitle = new Label();

    @FXML
    public TextField xLabel = new TextField();

    @FXML
    public TextField yLabel = new TextField();

    @FXML
    public TextField thetaLabel = new TextField();

    @FXML
    public TextField vLabel = new TextField();

    private static ArrayList<WayPoint> wayPoints;
    private static int selectIndex = -1;
    private static selection selected = selection.UNSELECTED;

    private Group trajectory;

    private double precision = 0.01;

    private double fieldWidth = 10;
    private double fieldHeight = 8;

    private final double fieldMultiplicator = 50;

    private double robotWidth;
    private double robotHeight;

    private Group initialRobot;
    private Group lastRobot;

    Rectangle initialRobotSprite;
    Rectangle lastRobotSprite;

    private ArrayList<Button> wPButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Database first
        Database.load();
        updateDBValues();
        //END OF DATABASE

        field.setPrefSize(fieldWidth * fieldMultiplicator, fieldHeight * fieldMultiplicator);

        wayPoints = new ArrayList<>();
        trajectory = new Group();
        wPButtons = new ArrayList<>();

        initialRobot = new Group();
        lastRobot = new Group();

        initialRobotSprite = new Rectangle(sizeScaledUPX(robotWidth), sizeScaledUPY(robotHeight));
        initialRobotSprite.setFill(Color.TRANSPARENT);
        initialRobotSprite.setStroke(Color.BLACK);
        initialRobotSprite.setX(sizeScaledUPX(-robotWidth)/2);
        initialRobotSprite.setY(sizeScaledUPY(-robotHeight)/2);

        lastRobotSprite = new Rectangle(sizeScaledUPX(robotWidth), sizeScaledUPY(robotHeight));
        lastRobotSprite.setFill(Color.TRANSPARENT);
        lastRobotSprite.setStroke(Color.BLACK);
        lastRobotSprite.setX(sizeScaledUPX(-robotWidth)/2);
        lastRobotSprite.setY(sizeScaledUPY(-robotHeight)/2);

        initialRobot.getChildren().add(initialRobotSprite);
        lastRobot.getChildren().add(lastRobotSprite);

        field.getChildren().add(trajectory);

        wayPointUI.setVisible(false);



        //  EVENT HANDLERS

        field.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveMouse(event.getX(), event.getY());
            }
        });

        field.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                releaseMouse(event.getX(), event.getY());
            }
        });

        field.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                moveMouse(event.getX(), event.getY());
            }
        });

    }

    private void moveWP(double x, double y, double theta){
        wayPoints.get(selectIndex).setPosition(x,y);
        wayPoints.get(selectIndex).move(scaledUPX(x),scaledUPY(y));
        wayPoints.get(selectIndex).rotate(theta);

        updateData();
    }

    private void moveWP(WayPoint wp){
        double x = wp.getX();
        double y = wp.getY();
        double theta = wp.getTheta();

        wayPoints.get(selectIndex).setPosition(x,y);
        wayPoints.get(selectIndex).move(scaledUPX(x),scaledUPY(y));
        wayPoints.get(selectIndex).rotate(theta);
    }

    private void moveMouse(double x, double y) {

        if (selected == selection.MOVING || selected==selection.POSITIONING){
            moveWP(scaledDOWNX(x), scaledDOWNY(y), wayPoints.get(selectIndex).getTheta());
        }
    }
    private void releaseMouse(double x,double y){
        if (selected == selection.MOVING || selected==selection.POSITIONING){
            selected = selection.SELECTED;
            updateTrajectory();
        }

    }

    public void addWaypoint(){
        Unselect();

        WayPoint newWP = new WayPoint(0, 0, 0);
        selectIndex++;
        wayPoints.add(selectIndex, newWP);

        wayPoints.get(selectIndex).wPInstance.setOnMousePressed(event -> handleSelection(event));


        //selected = selection.POSITIONING;

        field.getChildren().add(wayPoints.get(selectIndex).wPInstance);
        updateWPList();

        setSelection();
    }

    private void handleSelection(MouseEvent event){
        if(selected!=selection.POSITIONING){
            for(int i=0;i<wayPoints.size();i++) {
                if (event.getSource().equals(wayPoints.get(i).wPInstance)) {
                    if (i != selectIndex) {
                        Select(i);
                    } else {
                        startDragging(i);
                    }
                }
            }
        }
    }

    private void startDragging(int i){
        selected = selection.MOVING;
    }

    public Group drawSpline(Spline s){
        Group function = new Group();

        for (double dx = 0; dx<s.getDistance(); dx += precision){
            double x = scaledUPX(s.getxOffset() + dx * Math.cos(s.getThetaOffset()));
            double y = scaledUPY(s.getyOffset() + s.eval(dx) + dx * Math.sin(s.getThetaOffset()));
            Circle point = new Circle(x,y,1);
            function.getChildren().add(point);
        }
        return function;

    }

    private Circle newWayPoint(double x,double y,double theta){
        wayPoints.add(new WayPoint(x,y,theta));
        return new Circle(scaledCoordinates(x,y)[0],scaledCoordinates(x,y)[1],6);
    }

    private double[] scaledCoordinates(double x, double y){
        y = fieldHeight-y;

        double newX = x/fieldWidth*field.getPrefWidth();
        double newY = y/fieldHeight*field.getPrefHeight();

        return new double[] {newX, newY};
    }

    private double scaledUPX(double x){
        return x/fieldWidth*field.getPrefWidth();
    }

    private double scaledDOWNX(double x){
        return x/field.getPrefWidth()*fieldWidth;
    }

    private double scaledUPY(double y){
        y = fieldHeight-y;
        return y/fieldHeight*field.getPrefHeight();
    }

    private double sizeScaledUPX(double x){
        return x/fieldWidth*field.getPrefWidth();
    }
    private double sizeScaledUPY(double y){
        return y/fieldHeight*field.getPrefHeight();
    }

    private double scaledDOWNY(double y){
        y = y/field.getPrefHeight() * fieldHeight;
        return fieldHeight-y;
    }
    private void setUnselected(){
        Unselect();
        selected = selection.UNSELECTED;
        selectIndex = -1;
        wayPointUI.setVisible(false);

    }

    private void setSelection(int i){
        selectIndex = i;
        wpTitle.setText(wPButtons.get(selectIndex).getText());
        wPButtons.get(selectIndex).setStyle("-fx-background-color: lightcoral");
        selected = selection.SELECTED;

        updateData();
    }

    private void setSelection(){
        wpTitle.setText(wPButtons.get(selectIndex).getText());
        wPButtons.get(selectIndex).setStyle("-fx-background-color: lightcoral");
        selected = selection.POSITIONING;

        updateData();
    }

    private void updateWPList(){
        WPList.getChildren().clear();
        wPButtons.clear();

        for(int i = 0; i<wayPoints.size(); i++){
            wayPoints.get(i).setId(i+1);
            if(i!=0 && i!=wayPoints.size()-1)
                wayPoints.get(i).setVelocity(Database.getData(Database.SPEED));
            String lbl = "Waypoint " + (i+1);
            //WPList.getChildren().add(new Label(lbl));
            Button b = new Button(lbl);
            b.setBorder(Border.EMPTY);
            wPButtons.add(b);
            wPButtons.get(i).setOnAction(event -> {
                for(int f=0;f<wPButtons.size();f++){
                    if(event.getSource().equals(wPButtons.get(f)))
                        Select(f);
                }
            });

            WPList.getChildren().add(b);
        }

        if(wayPoints.size()>0) {

            wayPoints.get(wayPoints.size() - 1).wPInstance.getChildren().remove(lastRobot);

            if (!wayPoints.get(0).wPInstance.getChildren().contains(initialRobot))
                wayPoints.get(0).wPInstance.getChildren().add(initialRobot);

            if (wayPoints.size() > 1)
                wayPoints.get(wayPoints.size() - 1).wPInstance.getChildren().add(lastRobot);

        }
    }

    private void updateTrajectory(){
        trajectory.getChildren().clear();
        for(int i=1; i<wayPoints.size(); i++){
            Spline s = new Spline(wayPoints.get(i-1), wayPoints.get(i));
            trajectory.getChildren().add(drawSpline(s));
        }

    }

    public void Select(int i){
        Unselect();
        Select(wayPoints.get(i));
    }

    public void Unselect(){
        for (int i=0; i<wayPoints.size(); i++){
            wayPoints.get(i).setSelected(false);
            wPButtons.get(i).setStyle("-fx-background-color: none");
        }
    }

    public void Select(WayPoint wP){
        for (int i=0; i<wayPoints.size(); i++){
            if(wP.equals(wayPoints.get(i))){
                wP.setSelected(true);
                setSelection(i);
                break;
            }
        }
    }

    private void updateData(){
        wayPointUI.setVisible(true);

        String xString = Double.toString(wayPoints.get(selectIndex).getX());
        xLabel.setText(xString.substring(0,Math.min(xString.length(), 4)));

        String yString = Double.toString(wayPoints.get(selectIndex).getY());
        yLabel.setText(yString.substring(0,Math.min(yString.length(), 4)));

        String thetaString = Double.toString(wayPoints.get(selectIndex).getTheta());
        thetaLabel.setText(thetaString.substring(0,Math.min(thetaString.length(), 4)));

        String vString = Double.toString(wayPoints.get(selectIndex).getVelocity());
        vLabel.setText(vString.substring(0,Math.min(vString.length(), 4)));

        if (selectIndex != 0 && selectIndex != wayPoints.size()-1)
            vLabel.setDisable(true);
        else
            vLabel.setDisable(false);


    }

    public void changeData(){
        try {
            Double x = xLabel.getText().length() >= 1 ? Double.parseDouble(xLabel.getText()) : 0.0;
            Double y = yLabel.getText().length() >= 1 ? Double.parseDouble(yLabel.getText()) : 0.0;
            Double theta = thetaLabel.getText().length() >= 1 ? Double.parseDouble(thetaLabel.getText()) : 0.0;
            Double velocity = vLabel.getText().length() >= 1 ? Double.parseDouble(vLabel.getText()) : 0.0;

            wayPoints.get(selectIndex).setVelocity(velocity);
            moveWP(x, y, theta);

            updateTrajectory();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteWP(){
        field.getChildren().remove(wayPoints.get(selectIndex).wPInstance);
        wayPoints.remove(selectIndex);
        updateWPList();
        setUnselected();
        updateTrajectory();
    }

    public void deleteWP(int i){
        field.getChildren().remove(wayPoints.get(i).wPInstance);
        wayPoints.remove(i);
    }

    public void openRobotSettings() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../FXMLScenes/RobotSettings.fxml"));
        final Stage robotSettings = new Stage();
        robotSettings.setTitle("Robot Settings");
        robotSettings.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(root, 400, 300);
        robotSettings.setScene(dialogScene);
        robotSettings.show();
        robotSettings.setOnCloseRequest(event -> {
            updateDBObjects();
        });
    }

    public void openFieldSettings() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../FXMLScenes/FieldSettings.fxml"));
        final Stage fieldSettings = new Stage();
        fieldSettings.setTitle("Field Settings");
        fieldSettings.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(root, 400, 300);
        fieldSettings.setScene(dialogScene);
        fieldSettings.show();
    }

    public void openTrajectory() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../FXMLScenes/Trajectory.fxml"));
        final Stage trajectory = new Stage();
        trajectory.setTitle("Trajectory Settings");
        trajectory.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(root, 400, 300);
        trajectory.setScene(dialogScene);
        trajectory.show();
        trajectory.setOnCloseRequest(event -> {
            updateDBObjects();
            updateWPList();
            if (wayPoints.size()>0)
                vLabel.setText(wayPoints.get(selectIndex).getVelocity() + "");
        });
    }

    public void preview() throws Exception{

        generate();

        Parent root = FXMLLoader.load(getClass().getResource("../FXMLScenes/Preview.fxml"));
        final Stage preview = new Stage();
        preview.setTitle("Preview");
        preview.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(root, 400, 800);
        preview.setScene(dialogScene);
        preview.show();
    }

    public void openGeneralSettings() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../FXMLScenes/GeneralSettings.fxml"));
        final Stage generalSettings = new Stage();
        generalSettings.setTitle("General Settings");
        generalSettings.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(root, 400, 300);
        generalSettings.setScene(dialogScene);
        generalSettings.show();
    }

    public void updateDBObjects(){
        //Robot Settings
        robotWidth = Database.getData(Database.ROBOTWIDTH);
        robotHeight = Database.getData(Database.ROBOTHEIGHT);

        initialRobotSprite.setWidth(sizeScaledUPX(robotWidth));
        initialRobotSprite.setHeight(sizeScaledUPY(robotHeight));
        initialRobotSprite.setX(sizeScaledUPX(-robotWidth)/2);
        initialRobotSprite.setY(sizeScaledUPY(-robotHeight)/2);

        lastRobotSprite.setWidth(sizeScaledUPX(robotWidth));
        lastRobotSprite.setHeight(sizeScaledUPY(robotHeight));
        lastRobotSprite.setX(sizeScaledUPX(-robotWidth)/2);
        lastRobotSprite.setY(sizeScaledUPY(-robotHeight)/2);

        fieldWidth = Database.getData(Database.FIELDWIDTH);
        fieldHeight = Database.getData(Database.FIELDHEIGHT);

        field.setPrefSize(fieldWidth * fieldMultiplicator, fieldHeight * fieldMultiplicator);
        if (selectIndex != 0 && selectIndex != wayPoints.size()-1)
            vLabel.setDisable(true);
        else
            vLabel.setDisable(false);
    }

    public void updateDBValues(){
        robotWidth = Database.getData(Database.ROBOTWIDTH);
        robotHeight = Database.getData(Database.ROBOTHEIGHT);

        fieldWidth = Database.getData(Database.FIELDWIDTH);
        fieldHeight = Database.getData(Database.FIELDHEIGHT);

        //if (selectIndex != 0 && selectIndex != wayPoints.size()-1)
        //    vLabel.setDisable(true);
        //else
        //    vLabel.setDisable(false);

        //vLabel.setText("" + Database.getData(Database.SPEED));
    }

    public void save(){

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Enviroment");
        try {
            File selectedFile = chooser.showSaveDialog(null);

            PrintWriter outFile = null;
            try {
                outFile = new PrintWriter(selectedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FileSaver saver = new FileSaver(wayPoints);
            outFile.write(saver.toString());

            outFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void open(){

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Enviroment");

        File selectedFile = chooser.showOpenDialog(null);

        Scanner inFile = null;
        String data = null;

        try {
            inFile = new Scanner(selectedFile);
            data = inFile.nextLine();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }


        if (data != null) {
            FileLoader opener = new FileLoader(data);
            for (int i = 0; i<Database.DATASIZE; i++){
                Database.setData(i, opener.getDatabaseDto(i));
            }
            updateDBValues();

            int iters = wayPoints.size();
            for(int i=0; i<iters; i++){
                deleteWP(0);
            }

            wayPoints = opener.getWayPoints();
            updateWPList();
            updateTrajectory();
            for(int i=0; i<wayPoints.size(); i++) {
                Select(i);
                moveWP(wayPoints.get(i));
                wayPoints.get(selectIndex).wPInstance.setOnMousePressed(event -> handleSelection(event));
                field.getChildren().add(wayPoints.get(i).wPInstance);
            }


        }


    }

    public void export(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Trajectory");
        try {
            File selectedFile = chooser.showSaveDialog(null);

            PrintWriter outFile = null;
            try {
                outFile = new PrintWriter(selectedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            generate();
            String result = TG.getResult();
            Scanner s = new Scanner(result);

            outFile.write("");

            while(s.hasNextLine()){
                outFile.println(s.nextLine());
            }

            outFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public WaypointTG[] wpToWPTG(ArrayList<WayPoint> wayPoints){
        WaypointTG[] waypointTGS = new WaypointTG[wayPoints.size()];
        for(int i=0; i<wayPoints.size(); i++){
            waypointTGS[i] = new WaypointTG(wayPoints.get(i).getX(), wayPoints.get(i).getY(), Math.toRadians(wayPoints.get(i).getTheta()));
        }
        return waypointTGS;
    }

    public void generate() {
        if (wayPoints.size() > 0) {
            TG.configure(wpToWPTG(wayPoints), Database.getData(Database.SPEED), Database.getData(Database.ACCELERATION), Database.getData(Database.DECELERATION), wayPoints.get(0).getVelocity(), wayPoints.get(wayPoints.size() - 1).getVelocity(), Database.getData(Database.CALCULUSDT), Database.getData(Database.DT));
            TG.generate();
        }
    }



}
