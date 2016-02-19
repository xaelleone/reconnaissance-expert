import com.theeyetribe.client.data.GazeData;

public class EyeEntry {
	public GazeData g;
	public double startTime;
	
	public EyeEntry (GazeData data) {
		g = data;
		startTime = System.currentTimeMillis();
	}
}
