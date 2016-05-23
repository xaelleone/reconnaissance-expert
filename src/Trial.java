import java.applet.AudioClip;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

//contains all information about one trial
public class Trial {
	public static final int NUM_PICTURES = 4;
	
	public ArrayList<String> imageSet;
	public Color color;
	public String clip;
	public boolean containsEnemy;
	
	public Trial () {
		imageSet = new ArrayList<String>();
		containsEnemy = false;
	}
	
	public void shuffle () {
		Collections.shuffle(imageSet);
	}
	
	public void add (String imageName) {
		imageSet.add(imageName);
	}
	
	public int targetLocation () {
		for (int i = 0; i < imageSet.size(); i++) {
			if (imageSet.get(i).contains("Target")) return i;
		}
		return -1;
	}
	
	public String toString() {
		String totalString = "";
		for (String s : imageSet) {
			totalString += s + "\n";
		}
		totalString += Tracker.getRecommendationString(color);
		return totalString;
	}
	
	public void reassignPresentImage (int index) {
		imageSet.add(index, imageSet.remove(targetLocation()));
	}
}
