package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private enum selection{
        UNSELECTED, SELECTED, MOVING;
    }

    @FXML
    public AnchorPane field = new AnchorPane();

    @FXML
    public SplitPane pane = new SplitPane();

    @FXML
    public VBox WPList = new VBox();

    @FXML
    public TextField xLabel = new TextField();

    private static ArrayList<WayPoint> wayPoints;
    private static int selectIndex = -1;
    private static selection selected = selection.UNSELECTED;

    private Group trajectory;

    private final double fieldWidth = 10;
    private final double fieldHeight = 8;

    private ArrayList<Button> wPButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wayPoints = new ArrayList<>();
        trajectory = new Group();
        wPButtons = new ArrayList<>();
        //field.getChildren().add(newWayPoint(2.0,2.0,0));
        //field.getChildren().add(newWayPoint(6,6.0,0));

        //Spline s = new Spline(2,2,0,6,6,0);

        //field.getChildren().add(drawSpline(s,0.01));

        //System.out.println(s);

        field.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                    moveMouse(event.getX(), event.getY());
            }
        });

        field.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clickMouse(event.getX(), event.getY());
            }
        });


    }

    private void moveMouse(double x, double y) {
        //System.out.println(field.getWidth());
        if (selected == selection.MOVING){
            wayPoints.get(selectIndex).setPosition(scaledDOWNX(x), scaledDOWNY(y));
            wayPoints.get(selectIndex).move(x,y);

            //System.out.println(wayPoints.get(selectIndex).getX());
            //System.out.println(wayPoints.get(selectIndex).getY());
            //System.out.println(field.getWidth());

        }
    }

    private void clickMouse(double x,double y){
        if (selected == selection.MOVING){
            selected = selection.SELECTED;
        }
    }

    public void addWaypoint(){
        Unselect();

        WayPoint newWP = new WayPoint(0, 0, 0);
        selectIndex++;
        wayPoints.add(selectIndex, newWP);
        selected = selection.MOVING;
        System.out.println(selectIndex);

        field.getChildren().add(wayPoints.get(selectIndex).wPInstance);
        updateWPList();
    }

    public Group drawSpline(Spline s, double precision){
        Group function = new Group();

        for (double dx = 0; dx<s.getDistance(); dx += precision){
            System.out.println(dx);
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

    private double scaledDOWNY(double y){
        y = y/field.getPrefHeight() * fieldHeight;
        return fieldHeight-y;
    }

    private static void setSelection(int i){
        selectIndex = i;
        selected = selection.SELECTED;
    }

    private void updateWPList(){
        WPList.getChildren().clear();
        wPButtons.clear();
        for(int i = 0; i<wayPoints.size(); i++){
            wayPoints.get(i).setId(i);
            String lbl = "Waypoint " + (i+1);
            //WPList.getChildren().add(new Label(lbl));
            Button b = new Button(lbl);
            wPButtons.add(b);
            wPButtons.get(i).setOnAction(event -> {
                for(int f=0;f<wPButtons.size();f++){
                    if(event.getSource().equals(wPButtons.get(f)))
                        Select(f);
                }
            });

            WPList.getChildren().add(b);
        }
    }

    private void updateTrajectory(int node){

    }

    public static void Select(int i){
        System.out.println(i);
        Unselect();
        Select(wayPoints.get(i));
    }

    public static void Unselect(){
        for (WayPoint w : wayPoints){
            w.setSelected(false);
        }
    }

    public static void Select(WayPoint wP){
        for (int i=0; i<wayPoints.size(); i++){
            if(wP.equals(wayPoints.get(i))){
                wP.setSelected(true);
                setSelection(i);
                break;
            }
        }
    }




}
