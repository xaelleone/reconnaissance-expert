import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

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
	//public NumberFormat f = new DecimalFormat("#0.00");
	public boolean isPracticeRun;
	public PrintWriter detectionOut;
	//public PrintWriter eyeOut;
	public PrintWriter pollOut;
	public PrintWriter trackerOut;
	public PrintWriter toggleOut;
	public long lastToggleTime = -1;
	
	public DataAggregator (double startTime, String file, double r, boolean binary, boolean control, boolean practice) {
		fileNameBase = "results" + File.separator + file;
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
			if (!isPracticeRun) {
				File toggleFile = new File(fileNameBase + "toggle_output.txt");
				toggleFile.getParentFile().mkdirs();
				toggleOut = new PrintWriter(new FileWriter(toggleFile));
				detectionOut = new PrintWriter(new FileWriter(fileNameBase + "detection_output.txt"));
				//eyeOut = new PrintWriter(new FileWriter(fileNameBase + "eye_output.txt"));
				trackerOut = new PrintWriter(new FileWriter(fileNameBase + "tracker_output.txt"));
				pollOut = new PrintWriter(new FileWriter(fileNameBase + "poll_output.txt"));
				Timestamp t = new Timestamp(System.currentTimeMillis());
				detectionOut.println(t);
				//eyeOut.println(t);
				trackerOut.println(t);
				pollOut.println(t);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void modifyFileNameBase () {
		fileNameBase += "_" + (isControl ? "c" : (isBinaryAlarm ? "b" : "l") + "_" + Integer.toString((int)(reliability * 100))) + File.separator;
	}
	
	public void add (Entry e) {
		if (entryList.size() == 0 || entryList.get(entryList.size() - 1).trialNumber != e.trialNumber) {//take only first answer
			entryList.add(e);
			this.computeLastEntryRms(e.trialNumber);
			if (!isPracticeRun) {
				//printEyeOutput(e);
				printDetectionOutput(e);
			}
		}
	}
	
	public void addToggle (double trialStartTime, int trial, boolean toTracker) {
		long time = System.currentTimeMillis();
		toggleOut.println(trial + " " + new Timestamp(time) + " " + (time - trialStartTime) + " " + toTracker);
		if (this.lastToggleTime == -1) {
			lastToggleTime = time;
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
		if (!isPracticeRun) {
			//eyeOut.close();
			pollOut.close();
			detectionOut.close();
			trackerOut.close();
			toggleOut.close();
		}
	}
	
	/*public void printEyeOutput (Entry e) {
		if (entryList.size() == 1) {
			eyeOut.println(entryList.get(0).canvasPosOnScreen);
		}
		PrintWriter fout = eyeOut;
		for (EyeEntry data : e.eyeData) {
			fout.println(e.trialNumber + " " + data.g.timeStampString + " " + (data.startTime - totalStartTime) + " " + data.g.isFixated + " " + 
					new Tuple(data.g.rawCoordinates).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					new Tuple(data.g.smoothedCoordinates).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					new Tuple(data.g.leftEye.rawCoordinates).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					new Tuple(data.g.leftEye.smoothedCoordinates).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					data.g.leftEye.pupilSize + " " +
					new Tuple(data.g.leftEye.pupilCenterCoordinates) + " " + 
					new Tuple(data.g.rightEye.rawCoordinates).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					new Tuple(data.g.rightEye.smoothedCoordinates).add(e.canvasPosOnScreen.scalarMultiple(-1)) + " " + 
					data.g.rightEye.pupilSize + " " +
					new Tuple(data.g.rightEye.pupilCenterCoordinates) + " " + 
					data.onTrackerScreen
					);
		}
	}*/
	
	public void printTrackerOutput (TrackerEntry t) {
		PrintWriter fout = trackerOut;
		fout.println(t.trialNumber + " " + 
				new Timestamp((long)t.absoluteTime) + " " +
				(t.absoluteTime - this.totalStartTime) + " " + 
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
	
	public void computeLastEntryRms(int trial) {
		double quadraticSum = 0;
		int count = 0;
		for (int i = trackerData.size() - 1; i >= 0; i--) {
			if (trackerData.get(i).trialNumber != trial) {
				break;
			}
			quadraticSum += Math.pow(trackerData.get(i).position.x, 2) + Math.pow(trackerData.get(i).position.y, 2);
			count++;
		}
		this.getMostRecentEntry().rms = Math.sqrt(quadraticSum / count);
	}
	
	public void printDetectionOutput (Entry e) {
		if (lastToggleTime == -1) {
			lastToggleTime = (long) (e.absoluteStartTime - 1);
		}
		PrintWriter fout = detectionOut;
		fout.print(e.trialNumber + " " + 
				new Timestamp((long)e.absoluteStartTime) + " " + 
				(e.absoluteStartTime - this.totalStartTime) + " " + 
				e.t.containsEnemy + " " + 
				e.t.targetLocation() + " " + 
				e.getRecommendationString() + " " + 
				e.identifiedEnemy + " " + 
				e.outOfTime + " " + 
				e.timeSpent + " " + 
				(lastToggleTime - e.absoluteStartTime) + " " + 
				(e.getDetectionScore()) + " " +
				(e.getTrackerScore()) + " " + 
				(e.rms) + " "
				);
		/*for (double d : e.percentageDwell()) {
			fout.print(f.format(d) + " ");
		}
		for (double d : e.fixationDuration()) {
			fout.print(f.format(d) + " ");
		}
		fout.print(e.firstFixation() + " ");*/
		fout.print(e.onTrackerPercentage + " ");
		fout.print(e.toggleCount + " ");
		for (String s : e.t.imageSet) {
			fout.print(stripFileName(s) + " ");
		}
		fout.println();
		lastToggleTime = -1;
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
	
	private String stripFileName (String fileName) {
		String[] split = fileName.split("/");
		return split[split.length - 1];
	}
}
