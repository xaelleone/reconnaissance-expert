import com.theeyetribe.client.data.GazeData;

public class EyeEntry {
	public GazeData g;
	public double startTime;
	public boolean onTrackerScreen;
	
	public EyeEntry (GazeData data, boolean onTracker) {
		g = data;
		startTime = System.currentTimeMillis();
		this.onTrackerScreen = onTracker;
	}
}
