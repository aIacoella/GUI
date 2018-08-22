package TrajectoryGenerator;

import java.util.ArrayList;

public class Trajectory {

	private double dt, calculation_dt, maxa, maxv, maxd, vel, finalVel;
	private WaypointTG[] waypoints;
	private TrajectoryGeneration t;
	private ArrayList<Config> config = new ArrayList<Config>();
	private String result = "";
	
	public Trajectory(WaypointTG[] waypoints) {
		// TODO Auto-generated constructor stub
		this.waypoints = waypoints;
	}
	
	private ArrayList<Spline[]> fillSplines(){
		ArrayList<Spline[]> trajectories = new ArrayList<Spline[]>();
		int i = 0;
		while(waypoints[i].getID() != 3){
			//System.out.println(waypoints[i].getID());
			//System.out.println(i);
			if(waypoints[i].getID() == 1){
				config.add(new Config(this.maxa, this.maxv, this.maxd, waypoints[i].getVelocity(), this.calculation_dt, this.dt));
			}
			else if(waypoints[i].getID() == 0){
				WaypointTG w = waypoints[i];
				config.add(new Config(w.getMaxa(), w.getMaxv(), w.getMaxd(), w.getVelocity(), w.getCalcdt(), w.getDt()));
			}
			else{
				config.add(new Config(this.maxa, this.maxv, this.maxd, this.vel, this.calculation_dt, this.dt));
			}
			i++;
			Spline[] splines = new Spline[getSplineLength(i)];
			int num = 0;
			while(waypoints[i].getID() == 2){
				splines[num] = new Spline(waypoints[i-1], waypoints[i]);
				i++;
				num++;
			}
			splines[num] = new Spline(waypoints[i-1], waypoints[i]);
			trajectories.add(splines);
		}
		config.add(new Config(this.maxa, this.maxv, this.maxd, finalVel, this.calculation_dt, this.dt));
		return trajectories;
	}

	private int getSplineLength(int i){
		int num = 1;
		while(waypoints[i + num - 1].getID() == 2){
			num++;
		}
		return num;
	}
	
	private class Config{
		public double maxa, maxv, maxd, vel, calcdt, dt;
		
		public Config(double maxa, double maxv, double maxd, double vel, double calcdt, double dt){
			this.maxa = maxa;
			this.maxv = maxv;
			this.maxd = maxd;
			this.vel = vel;
			this.calcdt = calcdt;
			this.dt = dt;
		}
	}
	
	public void configureTrajectory(double maxv, double maxa, double maxd, double initialVelocity, double finalVelocity, double calculation_dt, double dt){
		this.dt = dt;
		this.calculation_dt = calculation_dt;
		this.maxa = maxa;
		this.maxd = maxd;
		this.maxv = maxv;
		this.vel = initialVelocity;
		this.finalVel = finalVelocity;
	}
	
	public void generate(){
		if(waypoints[waypoints.length - 1].getID() == 1 || waypoints[waypoints.length - 1].getID() == 0){
			finalVel = waypoints[waypoints.length - 1].getVelocity();
		}
		waypoints[waypoints.length - 1].setID(3);
		if(waypoints[0].getID() == 0){
			WaypointTG w = waypoints[0];
			this.dt = w.getDt();
			this.calculation_dt = w.getCalcdt();
			this.maxa = w.getMaxa();
			this.maxd = w.getMaxd();
			this.maxv = w.getMaxv();
			this.vel = w.getVelocity();
		}
		ArrayList<Spline[]> trajectories = fillSplines();
		Spline[] s = trajectories.get(0);
		//System.out.println(s.length);
		t = new TrajectoryGeneration(maxv, maxa, maxd, config.get(0).vel, config.get(1).vel, calculation_dt, dt, s);
		t.generate();
		for(int i = 1; i < trajectories.size(); i++){
			t.configureNewTrajectory(config.get(i).maxv, config.get(i).maxa, config.get(i).maxd, config.get(i).vel, 
					config.get(i+1).vel, config.get(i).calcdt, config.get(i).dt, trajectories.get(i));
			t.generate();
		}
		//System.out.println(t);
		result = t.getResult();
	}
	
	public ArrayList<TrajectoryGeneration.TrajectoryPoint> getLeftWheelTrajectory(){
		return t.getLeftWheel();
	}
	
	public ArrayList<TrajectoryGeneration.TrajectoryPoint> getRightWheelTrajectory(){
		return t.getRightWheel();
	}

	public String getResult(){
		return result;
	}
}
