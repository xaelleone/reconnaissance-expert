import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AbsentImageBin implements Comparable<AbsentImageBin> {
	public static final int HUGE = 800;
	public Queue<String> bin;
	public char map;
	
	public AbsentImageBin (char mapName) {
		map = mapName;
		bin = new LinkedList<String>();
	}
	
	public void add (String s) {
		bin.add(s);
	}
	
	public void shuffle () {
		Collections.shuffle((LinkedList<String>)bin);
	}
	
	public String get () {
		return bin.poll();
	}
	
	public int compareTo (AbsentImageBin other) {
		if (bin.size() == other.bin.size()) {
			return (Math.random() > 0.5)? 1:-1;
		}
		else {
			return other.bin.size() - bin.size();
		}
	}
}
