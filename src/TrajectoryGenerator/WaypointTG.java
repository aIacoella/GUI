package TrajectoryGenerator;

public class WaypointTG {
	
	private double x, y, theta, vel, maxv, maxa, maxd, calcdt, dt;
	private int trajectoryID;
	private boolean switchAxis = true;

	public WaypointTG(double x, double y, double angle) {
		// TODO Auto-generated constructor stub
		if(!switchAxis){
			this.x = x;
			this.y = y;
			theta = angle;
		}
		else{
			this.x = y;
			this.y = -x;
			theta = -angle;
		}
		vel = 0;
		this.trajectoryID = 2;
	}
	
	public WaypointTG(double x, double y, double angle, double instantaneousVelocity) {
		// TODO Auto-generated constructor stub
		if(!switchAxis){
			this.x = x;
			this.y = y;
			theta = angle;
		}
		else{
			this.x = y;
			this.y = -x;
			theta = -angle;
		}
		vel = instantaneousVelocity;
		this.trajectoryID = 1;
	}
	
	public WaypointTG(double x, double y, double angle, double instantaneousVelocity,
					  double maxVel, double maxAcc, double maxDec, double calc_dt, double dt){
		if(!switchAxis){
			this.x = x;
			this.y = y;
			theta = angle;
		}
		else{
			this.x = y;
			this.y = -x;
			theta = -angle;
		}
		vel = instantaneousVelocity;
		setMaxv(maxVel);
		setMaxa(maxAcc);
		setMaxd(maxDec);
		setCalcdt(calc_dt);
		this.setDt(dt);
		this.trajectoryID = 0;
	}
	
	public void setAxis(boolean lineUpWithY){
		switchAxis = lineUpWithY;
	}

	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getAngle(){
		return theta;
	}
	
	public double getVelocity(){
		return vel;
	}
	
	public int getID(){
		return trajectoryID;
	}
	
	public void setID(int num){
		trajectoryID = num;
	}

	public double getMaxv() {
		return maxv;
	}

	public void setMaxv(double maxv) {
		this.maxv = maxv;
	}

	public double getMaxa() {
		return maxa;
	}

	public void setMaxa(double maxa) {
		this.maxa = maxa;
	}

	public double getMaxd() {
		return maxd;
	}

	public void setMaxd(double maxd) {
		this.maxd = maxd;
	}

	public double getCalcdt() {
		return calcdt;
	}

	public void setCalcdt(double calcdt) {
		this.calcdt = calcdt;
	}

	public double getDt() {
		return dt;
	}

	public void setDt(double dt) {
		this.dt = dt;
	}
}
