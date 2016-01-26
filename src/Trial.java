import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import acm.graphics.GImage;

public class Trial {
	public static final int NUM_PICTURES = 4;
	
	public ArrayList<GImage> imageSet;
	public Color color;
	public boolean containsEnemy;
	
	public Trial () {
		imageSet = new ArrayList<GImage>();
		containsEnemy = false;
	}
	
	public void shuffle () {
		Collections.shuffle(imageSet);
	}
	
	public void add (String imageName) {
		imageSet.add(new GImage(imageName));
	}
}
