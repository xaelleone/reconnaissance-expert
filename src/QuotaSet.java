import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class QuotaSet {
	public static final Color[] BINARY_COLORS = new Color [] { Color.RED, Color.GREEN };
	public static final Color[] LIKELIHOOD_COLORS = new Color [] { Color.RED, 
			new Color(255, 201, 14), new Color(181, 230, 29), Color.GREEN };
	
	private ArrayList<String> clips = new ArrayList<String>();
	public ArrayList<Integer> list;
	//starts with n present counts, and then n absent counts;
	
	public QuotaSet (ArrayList<Integer> quotas) {
		list = new ArrayList<Integer>(quotas);
		loadAudioClips("audioclip_list.txt");
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
	
	private void loadAudioClips (String fileName) {
		try {
			Scanner fin = new Scanner(new BufferedReader(new FileReader(fileName)));
			while (fin.hasNextLine()) {
				clips.add(fin.nextLine());
			}
			fin.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getAudioClip (boolean isBinaryAlarm, int index) {
		if (isBinaryAlarm) {
			if (index % 2 == 0) return clips.get(0);
			else return clips.get(3);
		}
		else {
			return clips.get(index % 4);
		}
 	}
}
