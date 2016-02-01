
public class TrackerEntry {
	public int trialNumber;
	public Tuple position;
	public Tuple joystickPos;
	public double absoluteTime;
	
	public TrackerEntry (int counter, Tuple cursor, Tuple joy) {
		trialNumber = counter;
		position = cursor; //relative to a 0,0 origin
		joystickPos = joy;
		absoluteTime = System.currentTimeMillis();
	}
}
