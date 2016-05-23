import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import acm.program.GraphicsProgram;

public class OptionsScreen implements ActionListener {
	public GraphicsProgram t;
	public boolean isBinaryAlarm = true;
	public int reliability;
	private JTextField practiceTrialInput;
	private JTextField effectivePracticeTrialInput;
	private JTextField experimentTrialInput;
	private JTextArea[] imageListTexts;
	private Font displayFont = new Font("Helvetica Neue", Font.PLAIN, 16);
	private ImageSetGenerator imageGenerator;
	
	public OptionsScreen (GraphicsProgram tracker) {
		t = tracker;
	}
	
	public void display () {
		t.removeAll();
		practiceTrialCount();
		effectivePracticeTrialCount();
		experimentTrialCount();
		alarmType();
		imageLists();
	}
	
	private void practiceTrialCount () {
		JLabel practiceTrialCountLabel = new JLabel("Number of tracker-only practice trials:");
		practiceTrialCountLabel.setFont(displayFont);
		t.add(practiceTrialCountLabel, 50, 50);
		practiceTrialInput = new JTextField("30");
		normalizeTextField(practiceTrialInput);
		t.add(practiceTrialInput, 400, 50);
	}
	
	private void effectivePracticeTrialCount () {
		JLabel effectivePracticeTrialCountLabel = new JLabel("Number of tracker-and-detection practice trials:");
		effectivePracticeTrialCountLabel.setFont(displayFont);
		t.add(effectivePracticeTrialCountLabel, 50, 100);
		effectivePracticeTrialInput = new JTextField("8");
		normalizeTextField(effectivePracticeTrialInput);
		t.add(effectivePracticeTrialInput, 400, 100);
	}
	
	private void experimentTrialCount () {
		JLabel experimentTrialLabel = new JLabel("Number of experiment trials:");
		experimentTrialLabel.setFont(displayFont);
		t.add(experimentTrialLabel, 50, 150);
		experimentTrialInput = new JTextField("100");
		normalizeTextField(experimentTrialInput);
		t.add(experimentTrialInput, 400, 150);
	}
	
	private void normalizeTextField (JTextField f) {
		f.setFont(displayFont);
		f.setSize(60, f.getHeight());
		f.setHorizontalAlignment(JTextField.RIGHT);
	}
	
	private void alarmType () {
		JLabel alarmLabel = new JLabel("Alarm type:");
		alarmLabel.setFont(displayFont);
		JRadioButton binary = new JRadioButton("Binary");
		binary.setActionCommand("binary");
		binary.addActionListener(this);
		binary.setFont(displayFont);
		JRadioButton likelihood = new JRadioButton("Likelihood");
		likelihood.setActionCommand("likelihood");
		likelihood.addActionListener(this);
		likelihood.setFont(displayFont);
		ButtonGroup group = new ButtonGroup();
		group.add(binary);
		group.add(likelihood);
		t.add(alarmLabel, 50, 200);
		t.add(likelihood, 200, 200);
		t.add(binary, 350, 200);
	}
	
	private void reliability () {
		JLabel reliabilityLabel = new JLabel("Reliability:");
		reliabilityLabel.setFont(displayFont);
		JRadioButton seventy = new JRadioButton("70");
		seventy.setActionCommand("70");
		seventy.addActionListener(this);
		seventy.setFont(displayFont);
		JRadioButton eighty = new JRadioButton("80");
		eighty.setActionCommand("80");
		eighty.addActionListener(this);
		eighty.setFont(displayFont);
		JRadioButton ninety = new JRadioButton("90");
		ninety.setActionCommand("90");
		ninety.addActionListener(this);
		ninety.setFont(displayFont);
		ButtonGroup group = new ButtonGroup();
		group.add(seventy);
		group.add(eighty);
		group.add(ninety);
		t.add(reliabilityLabel, 50, 250);
		t.add(seventy, 150, 250);
		t.add(eighty, 250, 250);
		t.add(ninety, 350, 250);
	}
	
	private void pictureGeneration () {
		JLabel generatorLabel = new JLabel("Picture Generation:");
		generatorLabel.setFont(displayFont);
		JRadioButton random = new JRadioButton("Random");
		random.setActionCommand("random");
		random.addActionListener(this);
		random.setFont(displayFont);
		JRadioButton input = new JRadioButton("Input");
		input.setActionCommand("input");
		input.addActionListener(this);
		input.setFont(displayFont);
		ButtonGroup group = new ButtonGroup();
		group.add(random);
		group.add(input);
		t.add(generatorLabel, 50, 300);
		t.add(input, 200, 300);
		t.add(random, 350, 300);
	}
	
	private void imageLists () {
		JPanel[] lists = new JPanel[4];
		imageListTexts = new JTextArea[4];
		for (int i = 0; i < lists.length; i++) {
			imageListTexts[i] = new JTextArea("nejworld", 0, 0);
			imageListTexts[i].setPreferredSize(new Dimension(95, 290));
			lists[i] = new JPanel();
			lists[i].add(imageListTexts[i]);
			lists[i].setSize(100, 300);
			t.add(lists[i], 50 + i * 125, 350);
		}
	}
	
	public void actionPerformed (ActionEvent e) {
		switch (e.getActionCommand()) {
		case "binary":
			isBinaryAlarm = true;
			break;
		case "likelihood":
			isBinaryAlarm = false;
			break;
		case "70":
		case "80":
		case "90":
			reliability = Integer.parseInt(e.getActionCommand());
			break;
		case "random":
			
		}
	}
}
