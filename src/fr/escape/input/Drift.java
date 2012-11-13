package fr.escape.input;

import java.util.List;

import fr.escape.app.Input;

//TODO Comment
public final class Drift implements Gesture {

	private static int DEFAULT_FAULT_TOLERANCE = 25;
	private final static int COEFFICIENT = 1;
	private static double COEFFICIENT_A = 0.3;
	private static double COEFFICIENT_B = 1.7;
	private static float VELOCITY = -4.0f;
	
	@Override
	public boolean accept(Input start, List<Input> events, Input end, float[] velocity) {

		if(start.getY() <= end.getY()) {
			return false;
		}

		double upCoeffDir;
		double downCoeffDir;
		int faultTolerance = DEFAULT_FAULT_TOLERANCE;
		boolean isRight = start.getX() < end.getX();

		upCoeffDir = (isRight)?(-COEFFICIENT_A):(COEFFICIENT_B);
		downCoeffDir = (isRight)?(-COEFFICIENT_B):(COEFFICIENT_A);

		double div = (end.getX() - start.getX());
		double cd = (double) (end.getY() - start.getY()) / ((div!=0)?div:1);

		if(downCoeffDir < cd && cd < upCoeffDir) {

			double pUp = (end.getY() + faultTolerance) - (cd * (end.getX() + faultTolerance));
			double pDown = (end.getY() - faultTolerance) - (cd * (end.getX() - faultTolerance));
			
			for(Input event : events) {
				int yUp = (int) ((cd * event.getX()) + Math.max(pUp, pDown)) + faultTolerance;
				int yDown = (int) ((cd * event.getX()) + Math.min(pUp, pDown)) - faultTolerance;

				if(event.getY() > yUp || event.getY() < yDown) {
					return false;
				}
			}

			velocity[0] = ((isRight)?((end.getX() - start.getX()) / COEFFICIENT):((start.getX() - end.getX()) / COEFFICIENT));
			velocity[1] = ((isRight)?(-VELOCITY):(VELOCITY));
			velocity[2] = VELOCITY;

			return true;
		}

		return false;
	}	
}
