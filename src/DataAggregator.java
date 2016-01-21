import java.util.ArrayList;

public class DataAggregator {
	public ArrayList<Entry> entryList;
	
	public DataAggregator (ArrayList<Entry> list) {
		entryList = new ArrayList<Entry>(list);
	}
	
	
	
	private int count (BoolCombine b) {
		int count = 0;
		for (Entry e : entryList) {
			if (b.combine(e.enemyContained, e.automationRecommendedEnemy, e.subjectIdentifiedEnemy)) 
				count++;
		}
		return count;
	}
}
