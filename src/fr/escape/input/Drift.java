package fr.escape.input;

import java.util.List;

import fr.escape.app.Input;

//TODO Comment
public final class Drift implements Gesture {

	private static int DEFAULT_FAULT_TOLERANCE = 30;
	private final static int COEFFICIENT = 3;
	private static double COEFFICIENT_A = 0.3;
	private static double COEFFICIENT_B = 1.7;
	private static float VELOCITY = -2.0f;
	
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

		double cd = (double) (end.getY() - start.getY()) / (end.getX() - start.getX());

		if(downCoeffDir < cd && cd < upCoeffDir) {

			double pUp = (end.getY() + faultTolerance) - (cd * (end.getX() + faultTolerance));
			double pDown = (end.getY() - faultTolerance) - (cd * (end.getX() - faultTolerance));
			
			for(Input event : events) {
				int tmpUp = (int) ((cd * event.getX()) + pUp);
				int tmpDown = (int) ((cd * event.getX()) + pDown);
				
				int yUp = Math.max(tmpUp, tmpDown);
				int yDown = Math.min(tmpUp, tmpDown);

				// TODO Remove ?
				if(yUp < event.getY() || yDown > event.getY()) {
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
