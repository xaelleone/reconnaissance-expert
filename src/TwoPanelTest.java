import acm.graphics.GCanvas;
import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class TwoPanelTest extends GraphicsProgram {
	public static final int APPLICATION_HEIGHT = 400;
	public static final int APPLICATION_WIDTH = 400;
	
	public static void main (String[] args) {
		new TwoPanelTest().start();
	}

	public void init () {
		this.getGCanvas().setSize(0, 0);
		GCanvas one = new GCanvas();
		GCanvas two = new GCanvas();
		
		one.add(new GRect(5,10,15,20));
		two.add(new GLabel("blah blah blah", 10, 50));
		one.setVisible(true);
		two.setVisible(true);
		this.add(two);
	}
	
	public void run () {
		
	}
}
