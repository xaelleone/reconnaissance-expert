import java.awt.Font;

import javax.swing.UIManager;

import acm.program.GraphicsProgram;

public class GraphicsProgramTest extends GraphicsProgram {
	public static final int APPLICATION_HEIGHT = 800;
	public static final int APPLICATION_WIDTH = 800;
	
	public static void main (String[] args) {
		new GraphicsProgramTest().start();
	}
	
	public void init() {
		OptionsScreen options = new OptionsScreen(this);
		options.display();
	}
	
	public void run () {
		
	}
}
