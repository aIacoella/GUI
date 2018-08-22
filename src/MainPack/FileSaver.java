package MainPack;

import java.util.ArrayList;

public class FileSaver {

    private ArrayList<WayPoint> wayPoints;


    public FileSaver(ArrayList<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public String getWP() {
        String result = "WP";
        for (int i=0; i<wayPoints.size(); i++) {
            result += "{|" + wayPoints.get(i).getX() + "||" + wayPoints.get(i).getY() + "||" + wayPoints.get(i).getTheta() + "||" + wayPoints.get(i).getVelocity() + "|}";
        }
        result += "WP";
        return result;
    }

    public String getDB() {
        String result = "DB{";
        for (int i=0; i<Database.DATASIZE; i++)
            result += "|" + Database.getData(i) + "|";
        result += "}DB";
        return result;
    }

    @Override
    public String toString() {
        return getWP() + getDB();
    }
}
