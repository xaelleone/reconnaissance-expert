public class Physics {
	public static final double SCALE = 0.002;
	public static final double FRICTION = 0;
	public static final double SPRING_K = 1;
	public static final double MASS = 10;
	public static final double ORIGIN_X = TrackerConstants.SCREEN_DIVISION_X + (Tracker.APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2;
	public static final double ORIGIN_Y = Tracker.APPLICATION_HEIGHT / 2;
	
	private double lastTimeStep;
	/*private double lastX;
	private double lastY;
	private double lastVx;
	private double lastVy;*/
	private double delT;
	public double mouseX;
	public double mouseY;
	public double cursorX;
	public double cursorY;
	
	public Physics () {
		lastTimeStep = System.currentTimeMillis();
		/*lastX = x;
		lastY = y;
		lastVx = lastVy = 0;*/
	}
	
	public Tuple computeMove () { // computes how far to move, not where to move
		Tuple fMouse = new Tuple(mouseX - cursorX, mouseY - cursorY).scalarMultiple(SCALE);
		//Tuple fFric = computeFriction();
		//Tuple fSpring = computeSpringForce().scalarMultiple(SCALE);
		delT = System.currentTimeMillis() - lastTimeStep;
		lastTimeStep = System.currentTimeMillis();
		Tuple change = fMouse./*add(fSpring).*/scalarMultiple(Math.pow(delT, 2) / MASS);
		cursorX += change.x;
		cursorY += change.y;
		return change;
	}
	
	private Tuple computeSpringForce () {
		return new Tuple(cursorX - ORIGIN_X, cursorY - ORIGIN_Y).scalarMultiple(0.1);
	}
	
	private Tuple computeMouseForce (double nextX, double nextY) {
		/*delT = System.currentTimeMillis() - lastTimeStep;
		lastTimeStep = System.currentTimeMillis();
		double nextVx = (nextX - lastX) / delT;
		double nextVy = (nextX - lastY) / delT;
		double aX = (nextVx - lastVx) / delT;
		double aY = (nextVy - lastVy) / delT;
		lastX = nextX;
		lastY = nextY;
		lastVx = nextVx;
		lastVy = nextVy;
		System.out.println(aX + " " + aY);
		return new Tuple(MASS * aX, MASS * aY);*/
		return null;
	}
	
	/*private Tuple computeFriction () {
		Tuple vTuple = new Tuple(lastVx, lastVy);
		return vTuple.scalarMultiple(-1 * FRICTION / vTuple.innerProduct());
	}*/
}
