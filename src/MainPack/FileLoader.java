package MainPack;

import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;

public class FileLoader {

    private ArrayList<WayPoint> wayPoints;

    private double[] databaseDto;

    public FileLoader(String data) {
        wayPoints = new ArrayList<>();
        databaseDto = new double[Database.DATASIZE];

        String wpListString = getObject(data, "WP");
        for (int i=0; i<((int)(wpListString.chars().filter(ch -> ch =='.').count())); i++) {
            String wpString = getInBrackets(wpListString);
            wpListString = wpListString.substring(wpString.length() + 2);
            double[] wpInfo = new double[4];

            for(int f=0; f<4; f++){
                String valueString = getValue(wpString);
                wpString = wpString.substring(valueString.length() + 2);
                wpInfo[f] = Double.parseDouble(valueString);
            }

            WayPoint wayPoint = new WayPoint(wpInfo[0], wpInfo[1], wpInfo[2]);
            wayPoint.setVelocity(wpInfo[3]);
            wayPoints.add(wayPoint);
        }

        String dbString = getInBrackets(getObject(data, "DB"));
        for(int i=0; i<Database.DATASIZE; i++){
            String valueString = getValue(dbString);
            dbString = dbString.substring(valueString.length() + 2);
            databaseDto[i] = Double.parseDouble(valueString);
        }

    }

    private String getObject(String data, String obj){
        int startIndex = data.indexOf(obj);
        int finalIndex = (startIndex + obj.length()) + data.substring(startIndex + obj.length()).indexOf(obj);
        return data.substring(startIndex + obj.length(), finalIndex);
    }

    private String getInBrackets(String data){
        int startIndex = data.indexOf("{") + 1;
        int finalIndex = data.indexOf("}");
        return data.substring(startIndex, finalIndex);
    }

    private String getValue(String data){
        int startIndex = data.indexOf("|") + 1;
        int finalIndex = startIndex + data.substring(startIndex).indexOf("|");
        return data.substring(startIndex, finalIndex);
    }

    public ArrayList<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public WayPoint getWayPoints(int i) {
        return wayPoints.get(i);
    }

    public double getDatabaseDto(int i) {
        return databaseDto[i];
    }
}
