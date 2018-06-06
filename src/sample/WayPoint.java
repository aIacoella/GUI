package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class WayPoint implements Serializable{
    private double x;
    private double y;
    private double theta;
    private boolean selected;
    private int id;


    public Group wPInstance;
    private Circle selectCircle;

    public WayPoint(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;

        wPInstance = new Group();
        wPInstance.getChildren().add(new Circle(0,0,6));

        selectCircle = new Circle(0,0,8);
        selectCircle.setFill(Color.RED);

        setSelected(true);
    }

    public WayPoint() {
    }

    public void move(double x, double y){
        wPInstance.setTranslateX(x);
        wPInstance.setTranslateY(y);
    }

    public void setPosition(double x, double y){
        setX(x);
        setY(y);
    }

    @Override
    public String toString() {
        return String.format("X: %f Y: %f Theta: %f\n", x , y, theta);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getTheta() {
        return theta;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSelected(boolean selected) {
        if (selected){
            wPInstance.getChildren().add(selectCircle);
        } else
        {
            wPInstance.getChildren().remove(selectCircle);
        }
        this.selected = selected;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }
}
