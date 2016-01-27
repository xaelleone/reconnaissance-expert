import java.util.ArrayList;

public class DataAggregator {
	public ArrayList<Entry> entryList;
	
	public DataAggregator () {
		entryList = new ArrayList<Entry>();
	}
	
	public void add (Entry e) {
		entryList.add(e);
	}
	
	public int getScore () {
		int total = 0;
		for (Entry e : entryList) {
			total += e.getScore();
		}
		return total;
	}
}
