import java.util.ArrayList;

import com.theeyetribe.client.data.GazeData;

public class DataAggregator {
	public ArrayList<Entry> entryList;
	public double joystickControl;
	public ArrayList<PollResult> pollResults;
	public double totalStartTime;
	public ArrayList<TrackerEntry> trackerData;
	public ArrayList<GazeData> eyeData;
	public String fileNameBase;
	
	public DataAggregator (double startTime, String file) {
		fileNameBase = file;
		entryList = new ArrayList<Entry>();
		pollResults = new ArrayList<PollResult>();
		trackerData = new ArrayList<TrackerEntry>();
		totalStartTime = startTime;
	}
	
	public void add (Entry e) {
		entryList.add(e);
	}
	
	public void addPollResult (PollResult p) {
		pollResults.add(p);
	}
	
	public void addGazeData (GazeData g) {
		eyeData.add(g);
	}
	
	public void addTrackerEntry (TrackerEntry t) {
		trackerData.add(t);
	}
	
	public void printOutput () {
		
	}
	
	public int getScore () {
		int total = 0;
		for (Entry e : entryList) {
			total += e.getScore();
		}
		return total;
	}
}
