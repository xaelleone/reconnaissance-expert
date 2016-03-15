import java.sql.Timestamp;
import java.util.ArrayList;

import com.theeyetribe.client.data.GazeData;

public class Entry {
	public Trial t;
	public boolean identifiedEnemy;
	public boolean outOfTime;
	public double timeSpent;
	public double meanDistance;
	public int trialNumber;
	public double absoluteStartTime;
	public ArrayList<EyeEntry> eyeData;
	public Tuple canvasPosOnScreen;
	public boolean earlyJoystick;
	
	public Entry (Trial tr, int a, double meanDist, double startTime, int counter, ArrayList<EyeEntry> gazeData, Tuple canvas, boolean noDetection, double spent) {
		t = tr;
		resolveAnswer (a);
		meanDistance = meanDist;
		timeSpent = spent;
		trialNumber = counter;
		absoluteStartTime = startTime;
		eyeData = new ArrayList<EyeEntry>(gazeData);
		canvasPosOnScreen = canvas;	
		earlyJoystick = noDetection;
	}
	
	private void resolveAnswer (int a) {
		if (a == 0) {
			identifiedEnemy = true;
			outOfTime = false;
		}
		else if (a == 1) {
			identifiedEnemy = false;
			outOfTime = false;
		}
		else { //user is wrong no matter what
			identifiedEnemy = !t.containsEnemy;
			outOfTime = true;
		}
	}
	
	public double getDetectionScore () {
		if (earlyJoystick) return 0;
		double score = 0;
		if (identifiedEnemy == t.containsEnemy) {
			score += 5;
		}
		else {
			score -= 5;
		}
		score -= timeSpent / 5000;
		
		return score;
	}
	
	public double getTrackerScore () {
		//return Math.min(Math.sqrt(1.0 * TrackerConstants.TARGET_SIZE / meanDistance), 1) * 10;
		return meanDistance * 10; //mean distance is actually proportion that it is in the circle
	}
	
	public double getScore () {
		return getDetectionScore() + getTrackerScore();
	}
	
	public ArrayList<Double> percentageDwell () {
		return eyePercentage(true);
	}
	
	public ArrayList<Double> fixationDuration () {
		return eyePercentage(false);
	}
	
	public Tuple firstFixation () {
		return new Tuple(firstLeftFixation(), firstRightFixation());
	}
	
	public double firstLeftFixation () {
		for (EyeEntry data : eyeData) {
			if (data.g.isFixated && data.g.smoothedCoordinates.x < TrackerConstants.SCREEN_DIVISION_X && data.startTime > this.absoluteStartTime) {
				return data.startTime - this.absoluteStartTime;
			}
		}
		return 10000; //never fixated: what is wrong with your eyes?
	}
	
	public double firstRightFixation () {
		for (EyeEntry data : eyeData) {
			if (data.g.isFixated && data.g.smoothedCoordinates.x > TrackerConstants.SCREEN_DIVISION_X && data.startTime > this.absoluteStartTime) {
				return data.startTime - this.absoluteStartTime;
			}
		}
		return 10000; //actually probably just looking away
	}
	
	private ArrayList<Double> eyePercentage (boolean dwellOnly) {
		ArrayList<Double> percentages = new ArrayList<Double>();
		for (int i = 0; i < 6; i++) {
			percentages.add(0.0);
		}
		int classification;
		for (EyeEntry data : eyeData) {
			classification = classifyEyeLocation(data.g);
			if (dwellOnly || data.g.isFixated) {
				percentages.set(classification, percentages.get(classification) +  1.0 / eyeData.size());
			}
		}
		return percentages;
	}
	
	private int classifyEyeLocation (GazeData g) {
		Tuple realPos = new Tuple(g.smoothedCoordinates.x - canvasPosOnScreen.x, g.smoothedCoordinates.y - canvasPosOnScreen.y);
		if (realPos.y > Tracker.APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM) return 5;
		if (realPos.x > TrackerConstants.SCREEN_DIVISION_X) return 4;
		int right = realPos.x > (Tracker.APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2 ? 1 : 0;
		int bottom = realPos.y > (Tracker.APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM) / 2 ? 1 : 0;
		return 2 * bottom + right;
	}
	
	public String getRecommendationString () {
		for (int i = 0; i < QuotaSet.RECOMMENDATION_STRINGS.length; i++) {
			if (t.color == QuotaSet.LIKELIHOOD_COLORS[i]) {
				return QuotaSet.RECOMMENDATION_STRINGS[i];
			}
		}
		return "???"; //unclear what occurred
	}
	
	/*public boolean enemyContained;
	public boolean automationRecommendedEnemy;
	public boolean subjectIdentifiedEnemy;
	
	public Entry (boolean enemy, boolean automationSaidSo, boolean subjectSaidSo) {
		enemyContained = enemy;
		automationRecommendedEnemy = automationSaidSo;
		subjectIdentifiedEnemy = subjectSaidSo;
	}
	
	public static boolean xnor (boolean a, boolean b) {
		return (a && b) || (!a && !b);
	}
	
	public boolean automationWasCorrect () {
		return xnor(enemyContained, automationRecommendedEnemy);
	}
	
	public boolean subjectFollowedRecommendation () {
		return xnor(automationRecommendedEnemy, subjectIdentifiedEnemy);
	}
	
	public boolean subjectWasCorrect () {
		return xnor (enemyContained, subjectIdentifiedEnemy);
	}*/
}
