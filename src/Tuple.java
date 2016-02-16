import java.awt.Point;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Tuple {
	public double x;
	public double y;
	
	public Tuple (double a, double b) {
		x = a;
		y = b;
	}
	
	public Tuple(Point locationOnScreen) {
		x = locationOnScreen.getX();
		y = locationOnScreen.getY();
	}

	public double innerProduct () {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Tuple scalarMultiple (double s) {
		return new Tuple (x * s, y * s);
	}
	
	public Tuple add (Tuple other) {
		return new Tuple (x + other.x, y + other.y);
	}
	
	public double distance (Tuple other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
	}
	
	public String toString () {
		NumberFormat f = new DecimalFormat("#0.00");
		return f.format(x) + " " + f.format(y);
	}
}
