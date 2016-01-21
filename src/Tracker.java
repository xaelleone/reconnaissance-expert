import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JRadioButton;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class Tracker extends GraphicsProgram implements MouseMotionListener {
	public static final int APPLICATION_HEIGHT = 800;
	public static final int APPLICATION_WIDTH = 1600;
	private GOval cursor;
	private GLabel timer;
	private GRect tracker;
	private GOval targetInline;
	private int leftCount;
	private int totalTimeSteps;
	private double totalInnacuracy;
	private GRect automationRecommendation;
	private ArrayList<GImage> noEnemyImages;
	private ArrayList<GImage> yesEnemyImages;
	private ArrayList<GImage> imagesToUse;
	public ArrayList<Entry> entries;
	private int selectorPosition = 4;
	private boolean noEnemy;
	private boolean automationCorrect;
	private Physics p;
	
	public static void main (String[] args) {
		new Tracker().start();
	}
	
	public void init () {
		initScreen();
	}
	
	private void initMainScreen () {
		entries = new ArrayList<Entry>();
		
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
		
		cursor = new GOval (1250, 250, TrackerConstants.CURSOR_SIZE, TrackerConstants.CURSOR_SIZE);
		cursor.setFilled(true);
		cursor.setColor(Color.WHITE);
		add(cursor);
		
		timer = new GLabel("0");
		timer.setColor(Color.WHITE);
		timer.setFont(new Font("Arial", Font.BOLD, 14));
		timer.setLocation(TrackerConstants.TIMER_X, TrackerConstants.TIMER_Y);
		add(timer);
		
		addTooltip();
		
		p = new Physics();
		p.cursorX = cursor.getX();
		p.cursorY = cursor.getY();
	}
	
	private void initScreen () {
		JRadioButton accuracy60 = new JRadioButton("60");
		add(accuracy60, 60, 60);
	}
	
	private void addTooltip () {
		String[] strs = new String[] {"Report to commander:", "Y: Enemy spotted", "N: Area clear"};
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
	
	private void putRandomImages () {
		//TODO: precompute all image sets so that proportions are complied with
		for (GImage im : imagesToUse) {
			this.remove(im);
		}
		imagesToUse = new ArrayList<GImage>();
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
        for (int i = 0; i < imagesToUse.size(); i++) {
        	imagesToUse.get(i).setLocation(TrackerConstants.IMAGE_POSITIONS[i][0], TrackerConstants.IMAGE_POSITIONS[i][1]);
        	imagesToUse.get(i).setSize(TrackerConstants.IMAGE_WIDTH, TrackerConstants.IMAGE_HEIGHT);
        	this.add(imagesToUse.get(i));
        }
        
        automationCorrect = (Math.random() < TrackerConstants.AUTOMATION_CORRECT_PERCENTAGE);
        if (Entry.xnor(automationCorrect, noEnemy)) {
        	automationRecommendation.setColor(Color.GREEN);
        }
        else {
        	automationRecommendation.setColor(Color.RED);
        }
	}
	
	private void loadImages () {
		try {
			noEnemyImages = new ArrayList<GImage>();
			yesEnemyImages = new ArrayList<GImage>();
			Scanner fin = new Scanner(new BufferedReader(new FileReader("fileList.txt")));
			String fileName;
			GImage temp;
			while (fin.hasNext()) {
				fileName = fin.next();
				temp = new GImage(fileName);
				if (Character.isAlphabetic(fileName.charAt(fileName.length() - 5))) {
					yesEnemyImages.add(temp);
				}
				else {
					noEnemyImages.add(temp);
				}
			}
			fin.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void addEntry () {
		entries.add(new Entry(!noEnemy, Entry.xnor(automationCorrect, !noEnemy), selectorPosition != 4));
	}
	
	private void addTarget () {
		GOval targetOutline = new GOval(TrackerConstants.SCREEN_DIVISION_X + (APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2 - TrackerConstants.CURSOR_SIZE, (APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM) / 2 - TrackerConstants.CURSOR_SIZE, TrackerConstants.CURSOR_SIZE * 2, TrackerConstants.CURSOR_SIZE * 2);
		targetOutline.setFillColor(Color.GRAY);
		targetOutline.setFilled(true);
		targetInline = new GOval(TrackerConstants.SCREEN_DIVISION_X + (APPLICATION_WIDTH - TrackerConstants.SCREEN_DIVISION_X) / 2 - TrackerConstants.CURSOR_SIZE / 2, (APPLICATION_HEIGHT - TrackerConstants.TRACKER_AREA_BOTTOM) / 2 - TrackerConstants.CURSOR_SIZE / 2, TrackerConstants.CURSOR_SIZE, TrackerConstants.CURSOR_SIZE);
		targetInline.setFillColor(Color.BLACK);
		targetInline.setFilled(true);
		add(targetOutline);
		add(targetInline);
	}
	
	public void mouseMoved (MouseEvent e) {
		p.mouseX = e.getX();
		p.mouseY = e.getY();
	}
	
	public void keyPressed (KeyEvent e) {
		/*if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
			addEntry();
			putRandomImages();
		}
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) 
			moveSelectorDirection(0);
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) 
			moveSelectorDirection(1);
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) 
			moveSelectorDirection(2);
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) 
			moveSelectorDirection(3);*/
	}
	
	private void runMainScreen () {
		/*final GazeManager gm = GazeManager.getInstance();
        boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
        final IGazeListener listener = new IGazeListener () {
        	@Override
            public void onGazeUpdate(GazeData gazeData)
            {
        		// cursor.setLocation(gazeData.smoothedCoordinates.x - this.getGCanvas().getLocationOnScreen().x, gazeData.smoothedCoordinates.y - this.getGCanvas().getLocationOnScreen().y);
        		if (gazeData.smoothedCoordinates.x - this.getGCanvas().getLocationOnScreen().x < TrackerConstants.SCREEN_DIVISION_X) leftCount++;
        		totalTimeSteps++;
        		cursor.getLocation().toPoint().distance(targetInline.getLocation().toPoint());
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
        });*/
		double startTime = System.currentTimeMillis();	
		addMouseListeners();
		addKeyListeners();
		double lastTime = System.currentTimeMillis();
		double currentTime;
		double lastAngle = 0;
		double seed = Math.random();
		Tuple move;
		while (true) {
			timer.setLabel("Time left: " + Double.toString((TrackerConstants.TRIAL_LENGTH_MS - (System.currentTimeMillis() - startTime)) / 1000d));
			if (TrackerConstants.TRIAL_LENGTH_MS - (System.currentTimeMillis() - startTime) <= 0) {
				addEntry();
				putRandomImages();
				startTime = System.currentTimeMillis();
			}
			if (System.currentTimeMillis() % 1000 == 0)
				seed = Math.random();
			if (System.currentTimeMillis() % 10 == 0) {
				p.cursorX = cursor.getX();
				p.cursorY = cursor.getY();
				move = p.computeMove();
				cursor.move(move.x, move.y);
				currentTime = System.currentTimeMillis() / 200d / Math.PI;
				cursor.movePolar((Math.sin(currentTime) - Math.sin(lastTime)) * 40, lastAngle);
				lastTime = System.currentTimeMillis() / 200d / Math.PI;
				lastAngle += seed / 1500d;
				if (cursor.getX() + cursor.getWidth() < TrackerConstants.SCREEN_DIVISION_X && cursor.isVisible()) cursor.setVisible(false);
				if (cursor.getX() + cursor.getWidth() > TrackerConstants.SCREEN_DIVISION_X && !cursor.isVisible()) cursor.setVisible(true);
			}	
		}
	}
	
	public void run () {
		
	}
}

