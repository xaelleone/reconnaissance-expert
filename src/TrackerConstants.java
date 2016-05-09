
public class TrackerConstants {
	public static final double NO_ENEMY_PROPORTION = 0.4;
	public static final double AUTOMATION_CORRECT_PERCENTAGE = 0.7;
	
	public static final double[] AUTOMATION_CORRECT_PERCENTAGES = new double[] {0.7, 0.8, 0.9};
	
	public static final double TRIAL_LENGTH_MS = 10000;
	public static final int TRIAL_COUNT = 100; //edit this to change the number of trials
	
	public static final int EFFECTIVE_PRACTICE_TRIAL_COUNT = 8;
	public static final int TRACKER_ONLY_PRACTICE_COUNT = 30;
	
	public static final int JOYSTICK_SENSITIVITY = 50;
	
	public static final int TARGET_SIZE = 30;
	
	public static final int INACTIVE_ALARM_ALPHA = 30;
	public static final int ACTIVE_ALARM_ALPHA = 255;
	
	public static final int RIGHT_BUFFER = 25;
	
	public static final double[] RMS_THRESHOLDS = new double[] {16.5, 22.3, 28.1, 33.9, 39.7, 45.5, 51.3, 57.1, 62.9, 68.7};
	
	public static final int JOYSTICK_INPUT_NUMBER = 2;
	public static final int NUM_POLLS = 3;
	public static final int SCREEN_DIVISION_X = 1200;
	public static final int HORIZON_Y = 400;
	public static final int TRACKER_AREA_BOTTOM = 200;
	public static final int CURSOR_SIZE = 20;
	public static final int TIMER_X = SCREEN_DIVISION_X + 30;
	public static final int TIMER_Y = 30;
	public static final int[][] IMAGE_POSITIONS = new int[][] {{25, 50}, {625, 50}, {25, 450}, {625, 450}};
	public static final int IMAGE_WIDTH = 550;
	public static final int IMAGE_HEIGHT = 350;
	public static final int RECOMMENDER_BUFFER = 50;
	public static final int LINE_HEIGHT = 30;
	public static final int INIT_SCREEN_BUFFER = 100;
	/*public static final int BOTTOM_SELECTOR_WIDTH = 160;
	public static final int BOTTOM_SELECTOR_HEIGHT = 80;
	public static final int NO_ENEMY_OPTION_Y_POS = 720;
	public static final int[][] SELECTOR_TRANSPOSITIONS = new int[][] {{0, 1, 0, 1, 2}, {0, 0, 2, 2, 2}, {2, 3, 4, 4, 4}, {1, 1, 3, 3, 3}};*/ //UP, LEFT, DOWN, RIGHT
}
