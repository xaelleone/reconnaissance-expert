import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
	public NumberFormat f = new DecimalFormat("#0.00");
	public boolean isPracticeRun;
	public PrintWriter detectionOut;
	public PrintWriter eyeOut;
	public PrintWriter pollOut;
	public PrintWriter trackerOut;
	
	public DataAggregator (double startTime, String file, double r, boolean binary, boolean control, boolean practice) {
		fileNameBase = file;
		entryList = new ArrayList<Entry>();
		pollResults = new ArrayList<PollResult>();
		trackerData = new ArrayList<TrackerEntry>();
		totalStartTime = startTime;
		reliability = r;
		isBinaryAlarm = binary;
		isControl = control;
		modifyFileNameBase();
		isPracticeRun = practice;
		openAllFiles ();
	}
	
	private void openAllFiles () {
		try {
			detectionOut = new PrintWriter(new FileWriter(fileNameBase + "detection_output.txt"));
			eyeOut = new PrintWriter(new FileWriter(fileNameBase + "eye_output.txt"));
			trackerOut = new PrintWriter(new FileWriter(fileNameBase + "tracker_output.txt"));
			pollOut = new PrintWriter(new FileWriter(fileNameBase + "poll_output.txt"));
			detectionOut.println(totalStartTime);
			eyeOut.println(totalStartTime);
			trackerOut.println(totalStartTime);
			pollOut.println(totalStartTime);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void modifyFileNameBase () {
		fileNameBase += "_" + (isControl ? "c" : (isBinaryAlarm ? "b" : "l") + "_" + Integer.toString((int)(reliability * 100))) + "_";
	}
	
	public void add (Entry e) {
		if (entryList.size() == 0 || entryList.get(entryList.size() - 1).trialNumber != e.trialNumber) {//take only first answer
			entryList.add(e);
			if (!isPracticeRun) {
				printEyeOutput(e);
				printDetectionOutput(e);
			}
		}
	}
	
	public void addPollResult (PollResult p) {
		pollResults.add(p);
		if (!isPracticeRun) printPollOutput(p);
	}
	
	public void addTrackerEntry (TrackerEntry t) {
		trackerData.add(t);
		if (!isPracticeRun) printTrackerOutput(t);
	}
	
	public void closeAll () {
		eyeOut.close();
		pollOut.close();
		detectionOut.close();
		trackerOut.close();
	}
	
	public void printEyeOutput (Entry e) {
		PrintWriter fout = eyeOut;
		for (GazeData g : e.eyeData) {
			fout.println(e.trialNumber + " " + g.timeStampString + " " + g.isFixated + " " + 
					new Tuple(g.smoothedCoordinates.x, g.smoothedCoordinates.y).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					new Tuple(g.leftEye.pupilSize, g.rightEye.pupilSize));
		}
	}
	
	public void printTrackerOutput (TrackerEntry t) {
		PrintWriter fout = trackerOut;
		fout.println(t.trialNumber + " " + 
				f.format(t.absoluteTime - this.totalStartTime) + " " + 
				t.position + " " +
				t.joystickPos);
	}
	
	public void printPollOutput (PollResult p) {
		PrintWriter fout = pollOut;
		fout.print(p.trialNumber + " ");
		for (int r : p.results) {
			fout.print(r + " ");
		}
		fout.println();
	}
	
	public void printDetectionOutput (Entry e) {
		PrintWriter fout = detectionOut;
		fout.print(e.trialNumber + " " + 
				(e.absoluteStartTime - this.totalStartTime) + " " + 
				e.t.containsEnemy + " " + 
				e.t.targetLocation() + " " + 
				e.getRecommendationString() + " " + 
				e.identifiedEnemy + " " + 
				e.timeSpent + " " + 
				f.format(e.getDetectionScore()) + " " +
				f.format(e.getTrackerScore()) + " "
				);
		for (double d : e.percentageDwell()) {
			fout.print(f.format(d) + " ");
		}
		fout.print(e.firstFixation() + " ");
		for (double d : e.fixationDuration()) {
			fout.print(f.format(d) + " ");
		}
		fout.println();
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
