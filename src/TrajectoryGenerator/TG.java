package TrajectoryGenerator;

public class TG {

    private static Trajectory trajectory;
    /*
    public static void main(String[] args) {
        double x0 = 0.0;
        double y0 = 0.0;
        double x1 = 1.53;
        double y1 = 2.65;
        double theta0 = 0;
        double theta1 = Math.PI/3;
        double maxv = 2.5;
        double maxa = 3;
        double maxd = 3;
        double initialVel = 0;
        double finalVel = 0;
        double calcdt = 0.0001;
        double dt = 0.02;
        
        WaypointTG[] points = new WaypointTG[]{
        	new WaypointTG(x0, y0, theta0),
        	new WaypointTG(x1, y1, theta1),
        	new WaypointTG(5, 4, Math.PI/2)
        };

        Trajectory t = new Trajectory(points);
        t.configureTrajectory(maxv, maxa, maxd, initialVel, finalVel, calcdt, dt);

        t.generate();
    }
    */

    public static void configure(WaypointTG[] points, double maxv, double maxa, double maxd, double initialVel, double finalVel, double calcdt, double dt){
        trajectory = new Trajectory(points);
        trajectory.configureTrajectory(maxv, maxa, maxd, initialVel, finalVel, calcdt, dt);
    }

    public static void generate(){
        trajectory.generate();
    }

    public static String getResult(){
        return trajectory.getResult();
    }
}