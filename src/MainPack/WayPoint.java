package MainPack;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class WayPoint implements Serializable{
    private double x;
    private double y;
    private double theta;
    private double velocity;
    private boolean selected;
    private int id;
    private final int wpRadius = 8;


    public Group wPInstance;
    private Rectangle pointer;
    private Circle selectCircle;
    private Label labelID;

    public WayPoint(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = 0;
        this.velocity = 0;


        wPInstance = new Group();
        Circle base = new Circle(0,0,wpRadius);
        base.setStroke(Color.BLACK);
        base.setFill(null);

        Circle touch = new Circle(0,0,wpRadius);
        touch.setFill(Color.TRANSPARENT);

        wPInstance.getChildren().addAll(base,touch);

        pointer = new Rectangle(8, 2);
        wPInstance.getChildren().add(pointer);

        pointer.setX(wpRadius/2);
        pointer.setY(-1);

        labelID = new Label();
        wPInstance.getChildren().add(labelID);
        labelID.setTranslateY(wpRadius);
        labelID.setTranslateX(-3);

        selectCircle = new Circle(0,0,10);
        selectCircle.setFill(Color.CORAL);
        selectCircle.setOpacity(0.80);

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

    public void rotate(double theta){
        while(theta<-180)
            theta += 360;
        while(theta>180)
            theta -= 360;

        this.theta = theta;
        wPInstance.setRotate(-theta);
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
        labelID.setText(Integer.toString(id));
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

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
}
