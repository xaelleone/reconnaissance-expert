import java.applet.AudioClip;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class Trial {
	public static final int NUM_PICTURES = 4;
	
	public ArrayList<String> imageSet;
	public Color color;
	public AudioClip clip;
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
}
