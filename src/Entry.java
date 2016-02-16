import java.util.ArrayList;

import com.theeyetribe.client.data.GazeData;

public class Entry {
	public Trial t;
	public boolean identifiedEnemy;
	public double timeSpent;
	public double meanDistance;
	public int trialNumber;
	public double absoluteStartTime;
	public ArrayList<GazeData> eyeData;
	public Tuple canvasPosOnScreen;
	
	public Entry (Trial tr, int a, double meanDist, double time, int counter, ArrayList<GazeData> gazeData, Tuple canvas) {
		t = tr;
		resolveAnswer (a);
		meanDistance = meanDist;
		timeSpent = time;
		trialNumber = counter;
		absoluteStartTime = System.currentTimeMillis();
		eyeData = new ArrayList<GazeData>(gazeData);
		canvasPosOnScreen = canvas;
	}
	
	private void resolveAnswer (int a) {
		if (a == 0) {
			identifiedEnemy = true;
		}
		else if (a == 1) {
			identifiedEnemy = false;
		}
		else { //user is wrong no matter what
			identifiedEnemy = !t.containsEnemy;
		}
	}
	
	public double getDetectionScore () {
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
	
	public double firstFixation () {
		for (GazeData g : eyeData) {
			if (g.isFixated) {
				return 10000 + g.timeStamp - this.absoluteStartTime;
			}
		}
		return 10000; //never fixated: what is wrong with your eyes?
	}
	
	private ArrayList<Double> eyePercentage (boolean dwellOnly) {
		ArrayList<Double> percentages = new ArrayList<Double>();
		for (int i = 0; i < 6; i++) {
			percentages.add(0.0);
		}
		int classification;
		for (GazeData g : eyeData) {
			classification = classifyEyeLocation(g);
			if (dwellOnly || g.isFixated) {
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
