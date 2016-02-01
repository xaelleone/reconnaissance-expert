import java.util.ArrayList;

public class PollResult {
	public ArrayList<Integer> results;
	public int trialNumber;
	
	public PollResult (int counter) {
		results = new ArrayList<Integer>();
		trialNumber = counter;
	}
}
