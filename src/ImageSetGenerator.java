import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class ImageSetGenerator {
	public static final String imageListFile = "";
	public boolean isBinaryAlarm;
	public double reliability;
	private Queue<String> targetImages = new LinkedList<String>();
	private PriorityQueue<AbsentImageBin> absentImages = new PriorityQueue<AbsentImageBin>();
	private QuotaSet quotas;
	
	public ImageSetGenerator (boolean alarm, double r) {
		isBinaryAlarm = alarm;
		reliability = r;
		readQuotaTable();
		readImageNames();
	}
	
	public ArrayList<Trial> getImages () {
		ArrayList<Trial> allTrials = assignColors(pickImages());
		Collections.shuffle(allTrials);
		return allTrials;
	}
	
	private void readQuotaTable () {
		ArrayList<Integer> list = new ArrayList<Integer>();
		String fileName = isBinaryAlarm ? "binaryQuotas.txt" : "likelihoodQuotas.txt";
		try {
			Scanner fin = new Scanner(new BufferedReader(new FileReader(fileName)));
			for (int i = 0; fin.hasNextLine(); i++) {
				if (TrackerConstants.AUTOMATION_CORRECT_PERCENTAGES[i] * 100 == reliability) {
					String[] splitted = fin.nextLine().split(" ");
					for (String s : splitted) {
						list.add(Integer.parseInt(s));
					}
					break;
				}
				else {
					fin.nextLine();
				}
			}
			fin.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		quotas = new QuotaSet(list);
	}
	
	private ArrayList<Trial> assignColors (ArrayList<Trial> allTrials) {
		int overallIndex = 0;
		for (int i = 0; i < quotas.list.size(); i++) {
			for (int j = 0; j < quotas.list.get(i); j++) {
				allTrials.get(overallIndex).color = quotas.getColor(i, isBinaryAlarm);
				overallIndex++;
			}
		}
		return allTrials;
	}
	
	private void readImageNames () {	
		try {
			HashMap<Character, AbsentImageBin> absentImageMap = new HashMap<Character, AbsentImageBin>();
			Scanner fin = new Scanner(new BufferedReader(new FileReader("file_list.txt")));
			String fileName;
			char map;
			while (fin.hasNextLine()) {
				fileName = fin.nextLine();
				if (fileName.charAt(0) == '!') {
					fileName = fileName.substring(1);
					targetImages.add(fileName);
				}
				else {
					map = mapName(fileName);
					if (!absentImageMap.containsKey(map)) {
						absentImageMap.put(map, new AbsentImageBin(map));
					}
					absentImageMap.get(map).add(fileName);
				}
			}
			fin.close();
			reorganizeAbsentImages(absentImageMap);
			Collections.shuffle((LinkedList<String>)targetImages);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reorganizeAbsentImages (HashMap<Character, AbsentImageBin> absentImageMap) {
		Iterator<Character> it = absentImageMap.keySet().iterator();
		AbsentImageBin b;
		while (it.hasNext()) {
			b = absentImageMap.get(it.next());
			b.shuffle();
			absentImages.add(b);
		}
	}
	
	private ArrayList<Trial> pickImages () {
		ArrayList<Trial> presentOnly = pickImagesOfType(true);
		ArrayList<Trial> absentOnly = pickImagesOfType(false);
		for (Trial t : presentOnly) {
			absentOnly.add(t);
		}
		return absentOnly;
	}
	
	private ArrayList<Trial> pickImagesOfType (boolean present) {
		ArrayList<Trial> list = new ArrayList<Trial>();
		Trial t;	
		char illegal;
		String targetImageName;
		int iterations = present ? quotas.present() : quotas.absent();
		for (int i = 0; i < iterations; i++) {
			t = new Trial();
			illegal = '!';
			if (present) {
				if (targetImages.isEmpty()) {
					System.out.println("ran out of target images");
					return list;
				}
				targetImageName = targetImages.poll();
				illegal = mapName(targetImageName);
				t.add(targetImageName);
				t.containsEnemy = true;
			}
			list.add(fillTrial(t, illegal));
		}
		return list;
	}
	
	private Trial fillTrial (Trial t, char illegal) {
		ArrayList<AbsentImageBin> waitingArea = new ArrayList<AbsentImageBin>();
		AbsentImageBin temp;
		String name;
		while (t.imageSet.size() < Trial.NUM_PICTURES) {
			temp = absentImages.poll();
			if (temp.map != illegal) {
				name = temp.get();
				t.add(name);
			}
			waitingArea.add(temp);
		}
		for (AbsentImageBin b : waitingArea) {
			absentImages.add(b);
		}
		t.shuffle();
		return t;
	}
	
	private char mapName (String fileName) { //using only one character!
		for (int i = fileName.length() - 1; i >= 0; i--) {
			if (fileName.charAt(i) == '/') {
				return fileName.charAt(i + 1);
			}
		}
		return '?';
	}
}
