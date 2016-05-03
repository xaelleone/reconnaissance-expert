import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

public class PracticeDataOutput {
	public DataAggregator entries;
	
	public PracticeDataOutput (DataAggregator d) {
		entries = d;
	}
	
	public void output () {
		String fileName = entries.fileNameBase + "practice.txt";
		try {
			PrintWriter fout = new PrintWriter(new FileWriter(fileName));
			fout.println("SUMMARY");
			double[] rms = computeRms();
			Entry en;
			for (int i = 0; i < entries.entryList.size(); i++) {
				en = entries.entryList.get(i);
				fout.println(en.trialNumber + " " + new Timestamp((long)en.absoluteStartTime) + " " + (en.absoluteStartTime - entries.totalStartTime) + " " + en.meanDistance + " "  + rms[i]);
			}
			fout.println("=============");
			for (TrackerEntry t : entries.trackerData) {
				fout.println(t.trialNumber + " " + 
						new Timestamp((long)t.absoluteTime) + " " +
						(t.absoluteTime - entries.totalStartTime) + " " + 
						t.position + " " +
						t.joystickPos);
			}
			fout.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double[] computeRms () {
		double[] quadraticSums = new double[entries.trackerData.get(entries.trackerData.size() - 1).trialNumber];
		int[] counts = new int[quadraticSums.length];
		for (TrackerEntry t : entries.trackerData) {
			quadraticSums[t.trialNumber - 1] += Math.pow(t.position.x, 2) + Math.pow(t.position.y, 2);
			counts[t.trialNumber - 1]++;
		}
		for (int i = 0; i < quadraticSums.length; i++) {
			quadraticSums[i] = Math.sqrt(quadraticSums[i] / counts[i]);
		}
		return quadraticSums;
	}
}
