package TrajectoryGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
public class TrajectoryGeneration {
  
	private double maxAcceleration;
	private double maxDeceleration;
	private double initialVelocity;
	private double finalVelocity;
	private double dt;
	private double x = 0;
	private Spline s;
	private Spline[] splines;
	private int index = 0;
	private int splineIndex = 0;
	private double currentUpperPos = 0, currentLowerPos = 0, currentUpperVel, currentLowerVel;
	private double currentInnerArcLength = 0;
	private double cruiseVel;
	private double decelerateDistance;
	private double calculationCurrentTime;
	private double currentTime;
	private double totalUpperArcLength;
	private double totalLowerArcLength;
	private double previousUpperArcLengths;
	private double previousLowerArcLengths;
	private double totalXDistance;
	private double expected_dt;
	private boolean isUpperDecelWheel = false;
	private ArrayList<TrajectoryPoint> upperWheel = new ArrayList<TrajectoryPoint>();
	private ArrayList<TrajectoryPoint> lowerWheel = new ArrayList<TrajectoryPoint>();
	DecimalFormat df = new DecimalFormat("#.####");

	private String result;
	
	public TrajectoryGeneration(double maxVelocity, double maxAcceleration, double maxDeceleration,
			double initialVel, double finalVel, double calculation_dt, double dt, Spline[] splines) {
		// TODO Auto-generated constructor stub
		this.maxAcceleration = maxAcceleration;
		this.maxDeceleration = maxDeceleration;
		this.initialVelocity = initialVel;
		this.finalVelocity = finalVel;
		this.currentLowerVel = initialVel;
		this.currentUpperVel = initialVel;
		this.dt = calculation_dt;
		this.expected_dt = dt;
		this.calculationCurrentTime = 0;
		this.currentTime = 0;
		this.previousUpperArcLengths = 0;
		this.previousLowerArcLengths = 0;
		this.x = 0;
		setState(MotionState.ACCELERATING);
		for(Spline s : splines){
			this.totalUpperArcLength += s.getUpperArcLength();
			this.totalLowerArcLength += s.getLowerArcLength();
			this.totalXDistance += s.getDistance();
		}
		this.cruiseVel = Math.min(getCruiseVel(), maxVelocity);
		this.decelerateDistance = getDecelerateDistance(cruiseVel);
		this.splines = splines;
		this.splineIndex = 0;
		this.s = splines[0];
		//System.out.println("cruise vel: " + cruiseVel + " " + getCruiseVel());
		//System.out.println(decelerateDistance + " decelerate distance");

		result = "";
	}
	
	public void configureNewTrajectory(double maxVelocity, double maxAcceleration, double maxDeceleration,
			double initialVel, double finalVel, double calculation_dt, double dt, Spline[] splines) {
		// TODO Auto-generated constructor stub
		this.maxAcceleration = maxAcceleration;
		this.maxDeceleration = maxDeceleration;
		this.initialVelocity = initialVel;
		this.finalVelocity = finalVel;
		this.currentLowerVel = initialVel;
		this.currentUpperVel = initialVel;
		this.currentInnerArcLength = 0;
		this.expected_dt = dt;
		this.dt = calculation_dt;
		this.x = 0;
		setState(MotionState.ACCELERATING);
		this.totalXDistance = 0;
		for(Spline s : splines){
			this.totalUpperArcLength += s.getUpperArcLength();
			this.totalLowerArcLength += s.getLowerArcLength();
			this.totalXDistance += s.getDistance();
		}
		this.cruiseVel = Math.min(getCruiseVel(), maxVelocity);
		this.decelerateDistance = getDecelerateDistance(cruiseVel);
		this.splines = splines;
		this.splineIndex = 0;
		this.index = 0;
		this.s = splines[0];


	}
	
	private enum MotionState{
		ACCELERATING, CRUISING, DECELERATING, END
	}
	
	private MotionState state = MotionState.END;
	
	private void setState(MotionState newState){
		state = newState;
	}
	
	private MotionState getState(){
		return state;
	}
	
	public class TrajectoryPoint{
		public double pos = 0, vel = 0, time;
		
		public TrajectoryPoint(double pos, double vel, double time){
			this.pos = pos;
			this.vel = vel;
			this.time = time;
		}
	}
	
	public double getCruiseVel(){
		double first = Math.pow(finalVelocity, 2) - Math.pow(initialVelocity, 2) + 2*maxDeceleration*(totalUpperArcLength < totalLowerArcLength ? totalUpperArcLength : totalLowerArcLength);
		double denom = maxAcceleration / (maxAcceleration + maxDeceleration);
		return Math.sqrt(first * denom + Math.pow(initialVelocity, 2));
	}
	
	public double getDecelerateDistance(double currentVel){
		//System.out.println("cruise vel " + cruiseVel);
		return (Math.pow(finalVelocity, 2) - Math.pow(currentVel, 2)) / (2 * -maxDeceleration);
	}

	public void generate(){
		//System.out.println(s);
		//System.out.println(s.getArcLengths().length);
		while(x < this.totalXDistance && getState() != MotionState.END){
			if(getState() == MotionState.ACCELERATING){
				if(s.isConcaveUp(x)){
					currentLowerPos += currentLowerVel * dt + maxAcceleration * dt * dt * 0.5;
					currentLowerVel = currentLowerVel + maxAcceleration * dt;
					//System.out.println("Accelerating");
					updateMotionState();
					
					if(getState() == MotionState.ACCELERATING){
						double velRatio = s.getWheelVelRatio(x);
						currentUpperVel = currentLowerVel * velRatio;
						currentUpperPos += currentUpperVel * dt;
						
						double inVelRatio = s.getInnVelRatio(x, false);
						double innerVel = currentLowerVel / inVelRatio;
						currentInnerArcLength += innerVel * dt + maxAcceleration / inVelRatio * dt * dt * 0.5;

						x = inverseArcLength(currentInnerArcLength);
					}
				}
				else{
					currentUpperPos += currentUpperVel * dt + maxAcceleration * dt * dt * 0.5;
					currentUpperVel = currentUpperVel + maxAcceleration * dt;
					updateMotionState();
					
					if(getState() == MotionState.ACCELERATING){
						double velRatio = s.getWheelVelRatio(x);
						currentLowerVel = currentUpperVel / velRatio;
						currentLowerPos += currentLowerVel * dt;

						double inVelRatio = s.getInnVelRatio(x, true);
						double innerVel = currentUpperVel / inVelRatio;
						currentInnerArcLength += innerVel * dt + maxAcceleration / inVelRatio * dt * dt * 0.5;;

						x = inverseArcLength(currentInnerArcLength);
					}
				}
			}
			else if(getState() == MotionState.CRUISING){
				if(s.isConcaveUp(x)){
					currentLowerPos += cruiseVel * dt;
					currentLowerVel = cruiseVel;
					updateMotionState();
					
					if(getState() == MotionState.CRUISING){
						double velRatio = s.getWheelVelRatio(x);
						currentUpperVel = currentLowerVel * velRatio;
						currentUpperPos += currentUpperVel * dt;

						double inVelRatio = s.getInnVelRatio(x, false);
						double innerVel = currentLowerVel / inVelRatio;
						currentInnerArcLength += innerVel * dt;

						x = inverseArcLength(currentInnerArcLength);
					}
				}
				else{
					currentUpperPos += cruiseVel * dt;
					currentUpperVel = cruiseVel;
					updateMotionState();
					
					if(getState() == MotionState.CRUISING){
						double velRatio = s.getWheelVelRatio(x);
						currentLowerVel = currentUpperVel / velRatio;
						currentLowerPos += currentLowerVel * dt;

						double inVelRatio = s.getInnVelRatio(x, true);
						double innerVel = currentUpperVel / inVelRatio;
						currentInnerArcLength += innerVel * dt;

						x = inverseArcLength(currentInnerArcLength);
					}
				}
			}
			else{
				updateMotionState();
				if(!isUpperDecelWheel){
					currentLowerPos += currentLowerVel * dt - maxDeceleration * dt * dt * 0.5;
					currentLowerVel = currentLowerVel - maxDeceleration * dt;

					double velRatio = s.getWheelVelRatio(x);
					currentUpperVel = currentLowerVel * velRatio;
					currentUpperPos += currentUpperVel * dt;

					double inVelRatio = s.getInnVelRatio(x, false);
					double innerVel = currentLowerVel / inVelRatio;
					currentInnerArcLength += innerVel * dt - maxDeceleration / inVelRatio * dt * dt * 0.5;

					x = inverseArcLength(currentInnerArcLength);
				}
				else{
					currentUpperPos += currentUpperVel * dt - maxDeceleration * dt * dt * 0.5;
					currentUpperVel = currentUpperVel - maxDeceleration * dt;
					
					double velRatio = s.getWheelVelRatio(x);
					currentLowerVel = currentUpperVel / velRatio;
					currentLowerPos += currentLowerVel * dt;

					double inVelRatio = s.getInnVelRatio(x, true);
					double innerVel = currentUpperVel / inVelRatio;
					currentInnerArcLength += innerVel * dt - maxDeceleration / inVelRatio * dt * dt * 0.5;

					x = inverseArcLength(currentInnerArcLength);
				}
			}
			calculationCurrentTime += dt;
			if(calculationCurrentTime >= currentTime + expected_dt){
				currentTime += expected_dt;

				//System.out.println(df.format(currentLowerVel) + "," + df.format(currentLowerPos) + "," + df.format(currentUpperVel) + "," + df.format(currentUpperPos) + "," + df.format(currentTime));
				result +=(df.format(currentLowerVel) + "|" + df.format(currentLowerPos) + "|" + df.format(currentUpperVel) + "|" + df.format(currentUpperPos) + "|" + df.format(currentTime)) + "\n";

				TrajectoryPoint upper = new TrajectoryPoint(currentUpperPos, currentUpperVel, currentTime);
				TrajectoryPoint lower = new TrajectoryPoint(currentLowerPos, currentLowerVel, currentTime);
				
				upperWheel.add(upper);
				lowerWheel.add(lower);
			}
		}
	}
	
	public double inverseArcLength(double arcLength){
		while(index < s.getArcLengths().length && arcLength > s.getArcLengths()[index]){
			index++;
		}
		if(getState() == MotionState.END){
			index = s.getArcLengths().length;
		}
		return index * s.getDX();
	}
	
	private void updateMotionState(){
		//System.out.println(currentUpperPos - previousUpperArcLengths);
		if(currentUpperPos - previousUpperArcLengths >= s.getUpperArcLength() || 
				currentLowerPos - previousLowerArcLengths >= s.getLowerArcLength()){
			if(splineIndex < splines.length - 1){
				splineIndex++;
				index = 0;
				currentInnerArcLength = 0;
				previousUpperArcLengths = currentUpperPos;
				previousLowerArcLengths = currentLowerPos;
				this.s = splines[splineIndex];
				//System.out.println(s.getArcLength());
			}
		}

		if(getState() == MotionState.DECELERATING){
			if(isUpperDecelWheel == s.isConcaveUp(x)){
				setState(MotionState.ACCELERATING);
				//System.out.println("Accelerating");
			}
		}
		if((this.totalUpperArcLength - currentUpperPos <= getDecelerateDistance(currentUpperVel)) &&
				(currentUpperVel >= currentLowerVel)
				&& (getState() == MotionState.CRUISING || getState() == MotionState.ACCELERATING)){
			isUpperDecelWheel = true;
			setState(MotionState.DECELERATING);
			//System.out.println("DECELERATING upper");
			//System.out.println(s.isConcaveUp(x));
		}
		else if((this.totalLowerArcLength - currentLowerPos <= getDecelerateDistance(currentLowerVel)) &&
				(currentUpperVel <= currentLowerVel)
				&& (getState() == MotionState.CRUISING || getState() == MotionState.ACCELERATING)){
			isUpperDecelWheel = false;
			setState(MotionState.DECELERATING);
			//System.out.println("DECELERATING lower");
			//System.out.println(s.isConcaveUp(x));
		}
		if(getState() == MotionState.ACCELERATING){
			if(currentUpperVel >= cruiseVel || currentLowerVel >= cruiseVel){
				setState(MotionState.CRUISING);
				//System.out.println("CRUISING");
			}
		}
		if((currentUpperVel < finalVelocity && currentLowerVel < finalVelocity) && getState() == MotionState.DECELERATING) {
			setState(MotionState.END);
			currentTime += expected_dt;
			//System.out.println(df.format(finalVelocity) + "," + df.format(currentLowerPos) + "," + df.format(finalVelocity) + "," + df.format(currentUpperPos) + "," + df.format(currentTime));
			result += df.format(finalVelocity) + "|" + df.format(currentLowerPos) + "|" + df.format(finalVelocity) + "|" + df.format(currentUpperPos) + "|" + df.format(currentTime) + "\n";
			TrajectoryPoint upper = new TrajectoryPoint(currentUpperPos, finalVelocity, currentTime);
			TrajectoryPoint lower = new TrajectoryPoint(currentLowerPos, finalVelocity, currentTime);
			
			upperWheel.add(upper);
			lowerWheel.add(lower);
		}
	}
	
	public String toString(){
		return "Upper Position: " + currentUpperPos + ", Lower Position: " + currentLowerPos;
	}
	
	public ArrayList<TrajectoryPoint> getLeftWheel(){
		return upperWheel;
	}
	
	public ArrayList<TrajectoryPoint> getRightWheel(){
		return lowerWheel;
	}

	public String getResult() {return result;}
}
