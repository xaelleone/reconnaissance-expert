import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Version;

//X AXIS IS 12, Y AXIS IS 13

public class JoystickTest {
	/*static {
	    try {
	    	System.load("/Users/kmli/Downloads/archive/dist/libjinput-osx.jnilib");
	    } 
	    catch (UnsatisfiedLinkError e) {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	}*/
	
	public static void main(String[] args) { 
		System.out.println("JInput version: " + Version.getVersion()); 
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment(); 
		Controller[] cs = ce.getControllers(); 
		for (int i = 0; i < cs.length; i++) 
			System.out.println(i + ". " + cs[i].getName() + ", " + cs[i].getType() ); 
		Component[] comps = cs[2].getComponents();
		System.out.println("Components: (" + comps.length + ")");
		for (int i = 0; i < comps.length; i++)
			System.out.println( i + ". " +
					comps[i].getName() + ", " +
					getIdentifierName(comps[i]) + ", " +
					(comps[i].isRelative() ? "relative" : "absolute") + ", " +
					(comps[i].isAnalog() ? "analog" : "digital") + ", " +
					comps[i].getDeadZone());
	 
	}	
	
	private static String getIdentifierName(Component comp)
	{
	 Component.Identifier id = comp.getIdentifier();
	 if (id == Component.Identifier.Button.UNKNOWN)
	 return "button"; // an unknown button
	 else if(id == Component.Identifier.Key.UNKNOWN)
	 return "key"; // an unknown key
	 else
	 return id.getName();
	} 
}