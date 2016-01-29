
public class Entry {
	public Trial t;
	public boolean identifiedEnemy;
	public double timeSpent;
	public double meanDistance;
	public int trialNumber;
	public double absoluteStartTime;
	
	public Entry (Trial tr, int a, double meanDist, double time, int counter) {
		t = tr;
		resolveAnswer (a);
		meanDistance = meanDist;
		timeSpent = time;
		trialNumber = counter;
		absoluteStartTime = System.currentTimeMillis();
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
		return Math.min(Math.sqrt(1.0 * TrackerConstants.TARGET_SIZE / meanDistance), 1) * 10;
	}
	
	public double getScore () {
		return getDetectionScore() + getTrackerScore();
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
