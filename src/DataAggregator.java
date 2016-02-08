import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.theeyetribe.client.data.GazeData;

public class DataAggregator {
	public ArrayList<Entry> entryList;
	public double joystickControl;
	public ArrayList<PollResult> pollResults;
	public double totalStartTime;
	public ArrayList<TrackerEntry> trackerData;
	public String fileNameBase;
	public double reliability;
	public boolean isBinaryAlarm;
	public boolean isControl;
	
	public DataAggregator (double startTime, String file, double r, boolean binary, boolean control) {
		fileNameBase = file;
		entryList = new ArrayList<Entry>();
		pollResults = new ArrayList<PollResult>();
		trackerData = new ArrayList<TrackerEntry>();
		totalStartTime = startTime;
		reliability = r;
		isBinaryAlarm = binary;
		isControl = control;
		modifyFileNameBase();
	}
	
	public void modifyFileNameBase () {
		fileNameBase += "_" + (isControl ? "c" : (isBinaryAlarm ? "b" : "l") + "_" + Integer.toString((int)(reliability * 100))) + "_";
	}
	
	public void add (Entry e) {
		if (entryList.size() == 0 || entryList.get(entryList.size() - 1).trialNumber != e.trialNumber) //take only first answer
			entryList.add(e);
	}
	
	public void addPollResult (PollResult p) {
		pollResults.add(p);
	}
	
	public void addTrackerEntry (TrackerEntry t) {
		trackerData.add(t);
	}
	
	public void printOutput () {
		printDetectionOutput();
		printPollOutput();
		printTrackerOutput();
		printEyeOutput();
	}
	
	public void printEyeOutput () {
		try {
			PrintWriter fout = new PrintWriter(new FileWriter(fileNameBase + "eye_output.txt"));
			for (Entry e : entryList) {
				for (GazeData g : e.eyeData) {
					fout.println(e.trialNumber + " " + (e.absoluteStartTime - this.totalStartTime) + " " + g.isFixated + " " + g.smoothedCoordinates);
				}
			}
			fout.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printTrackerOutput () {
		try {
			PrintWriter fout = new PrintWriter(new FileWriter(fileNameBase + "tracker_output.txt"));
			for (TrackerEntry t : trackerData) {
				fout.println(t.trialNumber + " " + 
						(t.absoluteTime - this.totalStartTime) + " " + 
						t.position + " " +
						t.joystickPos);
			}
			fout.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printPollOutput () {
		try {
			PrintWriter fout = new PrintWriter(new FileWriter(fileNameBase + "poll_output.txt"));
			for (PollResult p : pollResults) {
				fout.print(p.trialNumber + " ");
				for (int r : p.results) {
					fout.print(r + " ");
				}
				fout.println();
			}
			fout.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printDetectionOutput () {
		try {
			PrintWriter fout = new PrintWriter(new FileWriter(fileNameBase + "detection_output.txt"));
			for (Entry e : entryList) {
				fout.print(e.trialNumber + " " + 
						(e.absoluteStartTime - this.totalStartTime) + " " + 
						e.t.containsEnemy + " " + 
						e.t.targetLocation() + " " + 
						e.getRecommendationString() + " " + 
						e.identifiedEnemy + " " + 
						e.timeSpent + " " + 
						e.getTrackerScore() + " " + 
						e.getDetectionScore() + " ");
				for (double d : e.percentageDwell()) {
					fout.print(d + " ");
				}
				fout.print(e.firstFixation() + " ");
				for (double d : e.fixationDuration()) {
					fout.print(d + " ");
				}
				fout.println();
			}
			fout.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public double getScore () {
		double total = 0;
		for (Entry e : entryList) {
			total += e.getScore();
		}
		return total;
	}
	
	public double getTrackerScore () {
		double total = 0;
		for (Entry e : entryList) {
			total += e.getTrackerScore();
		}
		return total;
	}
	
	public double getDetectionScore () {
		double total = 0;
		for (Entry e : entryList) {
			total += e.getDetectionScore();
		}
		return total;
	}
	
	public Entry getMostRecentEntry () {
		return entryList.get(entryList.size() - 1);
	}
}
