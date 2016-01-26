import java.awt.Color;
import java.util.ArrayList;

public class QuotaSet {
	public static final Color[] BINARY_COLORS = new Color [] { Color.RED, Color.GREEN };
	public static final Color[] LIKELIHOOD_COLORS = new Color [] { Color.RED, 
			new Color(210, 210, 0), new Color(80, 210, 0), Color.GREEN };
	
	public ArrayList<Integer> list;
	//starts with n present counts, and then n absent counts;
	
	public QuotaSet (ArrayList<Integer> quotas) {
		list = new ArrayList<Integer>(quotas);
	}
	
	public int present () {
		int count = 0;
		for (int i = 0; i < list.size() / 2; i++) {
			count += list.get(i);
		}
		return count;
	}
	
	public int absent () {
		int count = 0;
		for (int i = list.size() / 2; i < list.size(); i++) {
			count += list.get(i);
		}
		return count;
	}
	
	public Color getColor (int index, boolean isBinaryAlarm) {
		Color[] list = isBinaryAlarm ? BINARY_COLORS : LIKELIHOOD_COLORS;
		return list[index % list.length];
	}
}
