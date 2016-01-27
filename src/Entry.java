
public class Entry {
	public Trial t;
	public boolean identifiedEnemy;
	public double trackerScore;
	
	public Entry (Trial tr, int a, double meanDist) {
		t = tr;
		resolveAnswer (a);
		trackerScore = meanDist;
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
	
	public int getScore () {
		//TODO: implement this method when the formula is known
		return 0;
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
