import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Tracker extends GraphicsProgram implements MouseMotionListener {
	private static final long serialVersionUID = 1L;
	public static final int APPLICATION_HEIGHT = 800;
	public static final int APPLICATION_WIDTH = 1600;
	private GLabel timer;
	private GRect tracker;
	private int counter = 0;
	private GRect automationRecommendation;
	private ArrayList<GImage> imagesToUse;
	public DataAggregator entries;
	private GLabel trialNumber;
	private Physics p;
	private double reliability; //important!!!
	private boolean isBinaryAlarm; //important!!!
	private boolean running = false;
	private JButton toPractice;
	private boolean inPracticeMode = true;
	private ArrayList<String> practiceText = new ArrayList<String>();
	private ArrayList<ArrayList<GImage>> practiceImages = new ArrayList<ArrayList<GImage>>();
	private GLabel otherPracticeTip;
	private Tuple mousePos;
	private Controller joystick;
	private ArrayList<Trial> allTrials;
	private double startTime;
	private ArrayList<GLine> cursorSwarm;
	private double totalDistance = 0;
	private int totalTimeSteps = 0;
	private AudioPlayer audio = new AudioPlayer();
	
	public static void main (String[] args) {
		new Tracker().start();
	}
	
	public void init () {
		try {
			Scanner fin = new Scanner(new BufferedReader(new FileReader(isBinaryAlarm ? "practiceText" : "likelihoodPracticeText")));
			while (fin.hasNextLine()) {
				practiceText.add(fin.nextLine());
			}
			fin.close();
			loadPracticeImages();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		initializeControllers();
		String[] stringForm = new String[TrackerConstants.AUTOMATION_CORRECT_PERCENTAGES.length];
		for (int i = 0; i < stringForm.length; i++) {
			stringForm[i] = Double.toString((int)(TrackerConstants.AUTOMATION_CORRECT_PERCENTAGES[i] * 100));
		}
		reliability = Double.parseDouble((String)JOptionPane.showInputDialog(this, "Reliability:", "Setup", JOptionPane.PLAIN_MESSAGE, null, stringForm, null));
		String[] alarmTypeOptions = new String[] {"Binary alarm", "Likelihood alarm"};
		isBinaryAlarm = ((String)JOptionPane.showInputDialog(this, "Alarm type:", "Setup", JOptionPane.PLAIN_MESSAGE, null, alarmTypeOptions, null)).equals(alarmTypeOptions[0]);
		initScreen();
	}
	
	private void initMainScreen () {
		this.removeAll();
		entries = new DataAggregator(startTime, "test");
		
		automationRecommendation = new GRect(TrackerConstants.SCREEN_DIVISION_X + (APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2 + TrackerConstants.RECOMMENDER_BUFFER, APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM + TrackerConstants.RECOMMENDER_BUFFER, (APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2 - TrackerConstants.RECOMMENDER_BUFFER * 2, TrackerConstants.TRACKER_AREA_BOTTOM - TrackerConstants.RECOMMENDER_BUFFER * 2);
		automationRecommendation.setFilled(true);
		add(automationRecommendation);
		
		imagesToUse = new ArrayList<GImage>();
		
		loadImages();
		putRandomImages();
		
		tracker = new GRect(TrackerConstants.SCREEN_DIVISION_X, 0, APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X, APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM);
		tracker.setFillColor(Color.BLACK);
		tracker.setFilled(true);
		add(tracker);
		
		addTarget();
		initCursorSwarm();
		for (GLine g : cursorSwarm) {
			g.setColor(Color.YELLOW);
			this.add(g);
		}
		
		timer = new GLabel("0");
		timer.setColor(Color.WHITE);
		timer.setFont(new Font("Arial", Font.BOLD, 14));
		timer.setLocation(TrackerConstants.TIMER_X, TrackerConstants.TIMER_Y);
		add(timer);
		
		addTooltip();
		
		p = new Physics();
		
		addTrialLabels();
	}
	
	private void initializeControllers() {
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment(); 
		Controller[] cs = ce.getControllers();
		joystick = cs[3]; //SUBJECT TO CHANGE.
	}
	
	private void addTrialLabels () {
		trialNumber = new GLabel("Trial: " + counter + "/" + TrackerConstants.TRIAL_COUNT);
		if (inPracticeMode) {
			trialNumber.setLabel(practiceText.get(0));
		}
		trialNumber.setFont(new Font("Arial", Font.PLAIN, 24));
		trialNumber.setLocation(TrackerConstants.RECOMMENDER_BUFFER, APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM + TrackerConstants.RECOMMENDER_BUFFER);
		otherPracticeTip = new GLabel("");
		otherPracticeTip.setFont(new Font("Arial", Font.PLAIN, 24));
		otherPracticeTip.setLocation(TrackerConstants.RECOMMENDER_BUFFER, TrackerConstants.LINE_HEIGHT + APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM + TrackerConstants.RECOMMENDER_BUFFER);
		add(trialNumber);
		add(otherPracticeTip);
		running = true;
	}
	
	private void initCursorSwarm () {
		Tuple o = new Tuple(Physics.ORIGIN_X, Physics.ORIGIN_Y);
		double unit = TrackerConstants.CURSOR_SIZE / 2;
		double sep = TrackerConstants.CURSOR_SIZE * 3;
		cursorSwarm = new ArrayList<GLine>();
		cursorSwarm = addCross(cursorSwarm, o, unit);
		for (int i = 1; i <= 3; i++) {
			cursorSwarm = addCross(cursorSwarm, new Tuple(o.x - sep * i, o.y), unit / 2);
			cursorSwarm = addCross(cursorSwarm, new Tuple(o.x + sep * i, o.y), unit / 2);
			cursorSwarm = addCross(cursorSwarm, new Tuple(o.x, o.y - sep * i), unit / 2);
			cursorSwarm = addCross(cursorSwarm, new Tuple(o.x, o.y + sep * i), unit / 2);
		}
		cursorSwarm = inwardDashes(cursorSwarm, new Tuple(o.x - sep, o.y), sep, unit / 2, false);
		cursorSwarm = inwardDashes(cursorSwarm, new Tuple(o.x + sep, o.y), sep, unit / 2, false);
		cursorSwarm = inwardDashes(cursorSwarm, new Tuple(o.x, o.y - sep), sep, unit / 2, true);
		cursorSwarm = inwardDashes(cursorSwarm, new Tuple(o.x, o.y + sep), sep, unit / 2, true);
	}
	
	private ArrayList<GLine> inwardDashes (ArrayList<GLine> cursorSwarm, Tuple center, double sep, double unit, boolean horiz) {
		if (horiz) {
			cursorSwarm.add(new GLine(center.x - sep, center.y, center.x - sep + unit, center.y));
			cursorSwarm.add(new GLine(center.x + sep, center.y, center.x + sep - unit, center.y));
		}
		else {
			cursorSwarm.add(new GLine(center.x, center.y - sep, center.x, center.y - sep + unit));
			cursorSwarm.add(new GLine(center.x, center.y + sep, center.x, center.y + sep - unit));
		}
		return cursorSwarm;
	}
	
	private ArrayList<GLine> addCross (ArrayList<GLine> cursorSwarm, Tuple loc, double arm) {
		cursorSwarm.add(new GLine(loc.x - arm, loc.y, loc.x + arm, loc.y));
		cursorSwarm.add(new GLine(loc.x, loc.y - arm, loc.x, loc.y + arm));
		return cursorSwarm;
	}
	
	private void initScreen () {
		try {
			Scanner fin = new Scanner(new BufferedReader(new FileReader("initScreenText")));
			GLabel temp;
			for (int i = 0; fin.hasNextLine(); i++) {
				temp = new GLabel(fin.nextLine());
				temp.setFont(new Font("Arial", Font.PLAIN, 24));
				temp.setLocation(TrackerConstants.INIT_SCREEN_BUFFER, i * TrackerConstants.LINE_HEIGHT + TrackerConstants.INIT_SCREEN_BUFFER);
				add(temp);
			}
			fin.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		toPractice = new JButton("Practice");
		this.add(toPractice, TrackerConstants.INIT_SCREEN_BUFFER, 8 * TrackerConstants.LINE_HEIGHT + TrackerConstants.INIT_SCREEN_BUFFER);
	}
	
	private void practice () {
		this.removeAll();
		initMainScreen();
		inPracticeMode = true;
		//runMainScreen();
	}
	
	private void addTooltip () {
		String[] strs = new String[] {"Report to commander:", "Z: Enemy spotted", "X: Area clear"};
		GLabel temp;
		for (int i = 0; i < strs.length; i++) {
			temp = new GLabel(strs[i]);
			temp.setFont(new Font("Arial", Font.PLAIN, 24));
			temp.setLocation(TrackerConstants.SCREEN_DIVISION_X + TrackerConstants.RECOMMENDER_BUFFER, i * TrackerConstants.LINE_HEIGHT + APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM + TrackerConstants.RECOMMENDER_BUFFER);
			add(temp);
		}
	}
	
	/*private void moveSelectorDirection (int direction) {
		selectorPosition = TrackerConstants.SELECTOR_TRANSPOSITIONS[direction][selectorPosition];
		moveSelector();
	}
	
	private void moveSelector () {
		int position = selectorPosition;
		if (position < 4) {
			selector.setLocation(TrackerConstants.IMAGE_POSITIONS[position][0] - TrackerConstants.SELECTOR_BUFFER, TrackerConstants.IMAGE_POSITIONS[position][1] - TrackerConstants.SELECTOR_BUFFER);
			selector.setSize(TrackerConstants.IMAGE_WIDTH + TrackerConstants.SELECTOR_BUFFER * 2, TrackerConstants.IMAGE_HEIGHT + TrackerConstants.SELECTOR_BUFFER * 2);		
		}
		else {
			selector.setLocation((TrackerConstants.SCREEN_DIVISION_X - TrackerConstants.BOTTOM_SELECTOR_WIDTH) / 2, TrackerConstants.NO_ENEMY_OPTION_Y_POS - TrackerConstants.BOTTOM_SELECTOR_HEIGHT / 2);
			selector.setSize(TrackerConstants.BOTTOM_SELECTOR_WIDTH, TrackerConstants.BOTTOM_SELECTOR_HEIGHT);
		}
		selector.sendToBack();
	}*/
	
	private void loadPracticeImages () throws IOException {
		Scanner fin = new Scanner(new BufferedReader(new FileReader(isBinaryAlarm ? "practiceImageSets.txt": "likelihoodPracticeImageSets.txt")));
		ArrayList<GImage> temp;
		String[] splitted;
		while (fin.hasNextLine()) {
			temp = new ArrayList<GImage>();
			splitted = fin.nextLine().split(" ");
			for (String s : splitted) {
				temp.add(new GImage("Picture for practice/" + s + ".png"));
			}
			practiceImages.add(temp);
		}
		fin.close();
	}
	
	private void getPracticeImages () {
		imagesToUse = new ArrayList<GImage>(practiceImages.get(counter));
		Collections.shuffle(imagesToUse);
		addImagesToCanvas();
		if (counter == 5 || counter == 7) {
			automationRecommendation.setColor(Color.RED);
			audio.play("Danger.wav");
			audio = new AudioPlayer();
		}
		else if ((counter == 9 || counter == 11) && !isBinaryAlarm) {
			automationRecommendation.setColor(QuotaSet.LIKELIHOOD_COLORS[1]);
			audio.play("warning.wav");
			audio = new AudioPlayer();
		}
		else if (counter == 10 || counter == 12) {
			automationRecommendation.setColor(QuotaSet.LIKELIHOOD_COLORS[2]);
			audio.play("Caution.wav");
			audio = new AudioPlayer();
		}
		else {
			if (counter == 6 || counter == 8) {
				audio.play("clear.wav");
				audio = new AudioPlayer();
			}
			automationRecommendation.setColor(Color.GREEN);
		}
	}
	
	private void putRandomImages () {
		//TODO: precompute all image sets so that proportions are complied with
		for (GImage im : imagesToUse) {
			this.remove(im);
		}
		if (inPracticeMode) {
			getPracticeImages();
			return;
		}
		Trial t = allTrials.get(counter - 1);
		imagesToUse = new ArrayList<GImage>();
		for (String s : t.imageSet) {
			imagesToUse.add(new GImage(s));
		}
		addImagesToCanvas();
		automationRecommendation.setColor(t.color);
		audio.play(t.clip);
		audio = new AudioPlayer();
		/*imagesToUse = new ArrayList<GImage>();
		noEnemy = (Math.random() < TrackerConstants.NO_ENEMY_PROPORTION);
		int noEnemyImageCount = noEnemy ? 4 : 3;
		ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < noEnemyImages.size(); i++) {
            indices.add(new Integer(i));
        }
        Collections.shuffle(indices);
        for (int i = 0; i < noEnemyImageCount; i++) {
            imagesToUse.add(noEnemyImages.get(indices.get(i)));
        }
        if (!noEnemy) {
        	imagesToUse.add((int)(Math.random() * 4), yesEnemyImages.get((int)(Math.random() * yesEnemyImages.size())));
        }
        addImagesToCanvas();
        automationCorrect = (Math.random() < TrackerConstants.AUTOMATION_CORRECT_PERCENTAGE);
        if (Entry.xnor(automationCorrect, noEnemy)) {
        	automationRecommendation.setColor(Color.GREEN);
        }
        else {
        	automationRecommendation.setColor(Color.RED);
        }*/
	}
	
	private void addImagesToCanvas () {
		for (int i = 0; i < imagesToUse.size(); i++) {
        	imagesToUse.get(i).setLocation(TrackerConstants.IMAGE_POSITIONS[i][0], TrackerConstants.IMAGE_POSITIONS[i][1]);
        	imagesToUse.get(i).setSize(TrackerConstants.IMAGE_WIDTH, TrackerConstants.IMAGE_HEIGHT);
        	this.add(imagesToUse.get(i));
        }
	}
	
	private void loadImages () {
		allTrials = new ImageSetGenerator(isBinaryAlarm, reliability).getImages();
	}
	
	private void addEntry (int answer) {
		entries.add(new Entry(allTrials.get(counter - 1), answer, totalDistance / totalTimeSteps, System.currentTimeMillis() - startTime, counter));
	}
	
	private void addTarget () {
		ArrayList<GObject> shapes = new ArrayList<GObject>();
		double unit = TrackerConstants.TARGET_SIZE;
		double sep = unit * 3d / 4;
		Tuple o = Physics.ORIGIN;
		shapes.add(new GOval(TrackerConstants.SCREEN_DIVISION_X + (APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2 - unit, (APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM) / 2 - unit, unit * 2, unit * 2));
		shapes.add(new GLine(o.x - sep, o.y, o.x - sep - unit / 2, o.y));
		shapes.add(new GLine(o.x + sep, o.y, o.x + sep + unit / 2, o.y));
		shapes.add(new GLine(o.x, o.y - sep, o.x, o.y - sep - unit / 2));
		shapes.add(new GLine(o.x, o.y + sep, o.x, o.y + sep + unit / 2));
		for (GObject s : shapes) {
			s.setColor(Color.GREEN);
			this.add(s);
		}
	}
	
	public void mouseMoved (MouseEvent e) {
		if (mousePos != null) {
			p.mouseDiff = new Tuple(e.getX() - mousePos.x, e.getY() - mousePos.y);
		}
		mousePos = new Tuple(e.getX(), e.getY());
	}
	
	private void moveJoystick (double x, double y) {
		p.mouseDiff = (new Tuple(x, y)).scalarMultiple(TrackerConstants.JOYSTICK_SENSITIVITY);
	}
	
	public void keyPressed (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_Y) {
			nextRound(0);
		}
		if (e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_N) {
			nextRound(1);
		}
	}
	
	private void incrementTrialNumber () {
		if (!inPracticeMode && counter == TrackerConstants.TRIAL_COUNT) {
			entries.printOutput();
			this.exit();
		}
		if (!inPracticeMode && counter % 5 == 0) {
			running = false; //pause the tracker
			displayAndLogPolls();
			running = true;
		}
		counter++;
		if (counter >= (isBinaryAlarm ? 10:14) && inPracticeMode) {
			inPracticeMode = false;
			counter = 1;
		}
		if (inPracticeMode) {
			trialNumber.setLabel(practiceText.get(counter * 2));
			otherPracticeTip.setLabel(practiceText.get(counter * 2 + 1));
		}
		else {
			trialNumber.setLabel("Trial " + counter + "/" + TrackerConstants.TRIAL_COUNT);
			otherPracticeTip.setLabel("Score: " + entries.getScore() + "/" + 15 * counter);
		}
	}
	
	private void nextRound (int answer) { //0: spotted enemy, 1: all clear, -1: no answer
		if (!inPracticeMode) addEntry(answer);
		else entries.joystickControl = totalDistance / totalTimeSteps;
		incrementTrialNumber();
		putRandomImages();
		startTime = System.currentTimeMillis();
		totalTimeSteps = 0;
		totalDistance = 0;
	}
	
	private void moveCursorSwarm (double x, double y) {
		for (GLine g : cursorSwarm) {
			g.move(x, y);
		}
	}
	
	private void displayAndLogPolls () {
		Dictionary<Integer, JLabel> labels;
		String message;
		PollResult p = new PollResult();
		for (int i = 0; i < 3; i++) {
			labels = new Hashtable<Integer, JLabel>();
			switch (i) {
			case 0:
				message = "How confident are you in completing the task without the detector?";
				labels.put(0, new JLabel("Not confident at all"));
				labels.put(100, new JLabel("Absolutely confident"));
				break;
			case 1:
				message = "How reliable are the automated detector's recommendations?";
				labels.put(0,  new JLabel("Not reliable at all"));
				labels.put(100,  new JLabel("Absolutely reliable"));
				break;
			default:
				message = "How much do you trust the automated detector's recommendations?";
				labels.put(0, new JLabel("I don't trust it at all"));
				labels.put(100, new JLabel("I absolutely trust it"));
			}
			p.results.add(displayPoll(message, labels));
		}
		entries.addPollResult(p);
	}
	
	private int displayPoll (String message, Dictionary<Integer, JLabel> labels) {
		JFrame parent = new JFrame();
		JOptionPane optionPane = new JOptionPane();
		JSlider slider = getSlider(optionPane, labels);
		optionPane.setMessage(new Object[] {message, slider });
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = optionPane.createDialog(parent, "Question");
		dialog.setVisible(true);
		Object output = optionPane.getInputValue();
		if (output.equals(JOptionPane.UNINITIALIZED_VALUE)) {
			return 50; //default average value
		}
		return (int) optionPane.getInputValue();
	}

	private JSlider getSlider(final JOptionPane optionPane, Dictionary<Integer, JLabel> labels) {
	    JSlider slider = new JSlider();
	    slider.setMajorTickSpacing(25);
	    slider.setPaintTicks(true);
	    slider.setPaintLabels(true);
	    slider.setLabelTable(labels);
	    slider.setValue(50);
	    ChangeListener changeListener = new ChangeListener() {
	    	public void stateChanged(ChangeEvent changeEvent) {
	    		JSlider theSlider = (JSlider) changeEvent.getSource();
	    		if (!theSlider.getValueIsAdjusting()) {
	    			optionPane.setInputValue(new Integer(theSlider.getValue()));
	    		}
	    	}
	    };
	    slider.addChangeListener(changeListener);
	    return slider;
	}
	
	public void run () {
		toPractice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				practice();
			}
		});
		final GazeManager gm = GazeManager.getInstance();
        boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
        final IGazeListener listener = new IGazeListener () {
        	@Override
            public void onGazeUpdate(GazeData gazeData)
            {
        		entries.addGazeData(gazeData);
            }
        };
        gm.addGazeListener(listener);
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                gm.removeGazeListener(listener);
                gm.deactivate();
            }
        });
		startTime = System.currentTimeMillis();	
		Tuple move;
		boolean initialized = false;
		while (true) {
			System.out.print(running?"":""); //it is unclear why this is required, but something needs to check the running variable
			if (running) {
				if (!initialized) {
					initialized = true;
					addMouseListeners();
					addKeyListeners();
				}				
				/*if (System.currentTimeMillis() % 1000 == 0)
					seed = Math.random();*/
				if (System.currentTimeMillis() % 40 == 0) {
					joystick.poll();
					moveJoystick(joystick.getComponents()[12].getPollData(), joystick.getComponents()[13].getPollData());
					move = p.computeMove();
					moveCursorSwarm(move.x, move.y);
					entries.addTrackerEntry(new TrackerEntry(counter, p.cursor, p.mouseDiff));
					totalDistance += p.cursor.distance(new Tuple(0, 0));
					totalTimeSteps++;
					/*currentTime = System.currentTimeMillis() / 200d / Math.PI;
					cursor.movePolar((Math.sin(currentTime) - Math.sin(lastTime)) * 40, lastAngle);
					lastTime = System.currentTimeMillis() / 200d / Math.PI;
					lastAngle += seed / 1500d;*/
					//if (cursor.getX() + cursor.getWidth() < TrackerConstants.SCREEN_DIVISION_X && cursor.isVisible()) cursor.setVisible(false);
				}	
				if (System.currentTimeMillis() % 10 == 0) {
					if (audio.playCompleted) audio.close();
					timer.setLabel("Time left: " + Double.toString((int)(100 * (TrackerConstants.TRIAL_LENGTH_MS - (System.currentTimeMillis() - startTime)) / 1000d) / 100d));
					if (TrackerConstants.TRIAL_LENGTH_MS - (System.currentTimeMillis() - startTime) <= 0) {
						nextRound(-1);
					}
				}
			}
		}
	}
}

