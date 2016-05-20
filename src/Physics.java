
//used to compute the next tracker location using joystick input
public class Physics {
	public static final double C_X = (1 + Math.sqrt(5)) / 2;
	public static final double C_Y = Math.PI / 2;
	public static final double DR = TrackerConstants.CURSOR_SIZE / 2;
	public static final int N_MAX = 5;
	public static final double MOUSE_PROPORTION = 0.11;
	public static final double A1 = 0.957;
	public static final double A2 = 0.0000003;
	public static final double A3 = 0.000010382;
	public static final double A4 = 0.0016288; // these were arbitrarily changed from the paper
	public static final double TIME_STEP = 2000;
	public static final double ORIGIN_X = TrackerConstants.SCREEN_DIVISION_X + (Tracker.APPLICATION_WIDTH - TrackerConstants.RIGHT_BUFFER - TrackerConstants.SCREEN_DIVISION_X) / 2;
	public static final double ORIGIN_Y = (Tracker.APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM) / 2;
	public static final Tuple ORIGIN = new Tuple(ORIGIN_X, ORIGIN_Y);
	
	private double[] phaseAngles = new double[N_MAX + 1];
	public Tuple cursorPrev = new Tuple(0, 0);
	public Tuple cursor = new Tuple(0, 0);
	public Tuple mouseDiff = new Tuple(0, 0);
	
	public Physics () {
		calculatePhaseAngles();
	}
	
	public Tuple computeMove () {
		Tuple nextPosition = cursor.add(cursorPrev.scalarMultiple(-1)).scalarMultiple(A1)
				.add(cursorPrev.scalarMultiple(A2))
				//.add(gravityForce().scalarMultiple(A3))
				.add(buffetForce().add(mouseForce()).scalarMultiple(A4));
		
		moveCursor(nextPosition.x, nextPosition.y);
		/*System.out.println(cursor);
		System.out.println(cursorPrev);
		System.out.println("next: " + nextPosition.x + ", " + nextPosition.y);*/
		return nextPosition;
	}
	
	public void setCursor (double x, double y) {
		cursorPrev = new Tuple(cursor.x / DR, cursor.y / DR);
		cursor = new Tuple(x, y);
	}
	
	private void moveCursor (double x, double y) {
		cursorPrev = new Tuple(cursor.x, cursor.y);
		cursor = new Tuple(cursor.x + x, cursor.y + y);
	}
	
	private Tuple buffetForce () {
		Tuple force = new Tuple(0, 0);
		double fbx, fby;
		for (int i = 0; i <= N_MAX; i++) {
			fbx = Math.pow(C_X, -1 * i) * Math.cos(phaseAngles[i] + Math.pow(C_X, i) * System.currentTimeMillis() / TIME_STEP);
			fby = Math.pow(C_Y, -1 * i) * Math.cos(phaseAngles[i] + Math.pow(C_Y, i) * System.currentTimeMillis() / TIME_STEP);
			force = force.add(new Tuple(fbx, fby));
		}
		//System.out.println("Buffet force: " + force.x + ", " + force.y);
		return force;
	}
	
	private Tuple gravityForce () {
		double magnitude;
		double distanceToOrigin = cursor.distance(new Tuple(0, 0));
		if (distanceToOrigin <= 6) {
			magnitude = Math.pow(distanceToOrigin, 2) / 2;
		}
		else {
			magnitude = -3 * (distanceToOrigin - 20) / 2;
		}
		Tuple force;
		if (distanceToOrigin == 0) {
			force = new Tuple(0, 0);
		}
		else {
			force = cursor.scalarMultiple(magnitude / distanceToOrigin);
		}
		//System.out.println("Gravity force: " + force.x + ", " + force.y);
		return force;
	}
	
	private Tuple mouseForce () {
		return mouseDiff.scalarMultiple(MOUSE_PROPORTION);
	}
	
	private void calculatePhaseAngles () {
		for (int i = 0; i <= N_MAX; i++) {
			phaseAngles[i] = Math.random() * Math.PI * 2;
		}
	}
}
	/*
	 * old code:
	 * Tuple fMouse = new Tuple(mouseX - cursorX, mouseY - cursorY).scalarMultiple(SCALE);
		delT = System.currentTimeMillis() - lastTimeStep;
		lastTimeStep = System.currentTimeMillis();
		Tuple change = fMouse.scalarMultiple(Math.pow(delT, 2) / MASS);
		cursorX += change.x;
		cursorY += change.y;
		return change;
	 */
	