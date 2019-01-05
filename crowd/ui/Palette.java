package crowd.ui;

import javafx.scene.paint.Color;

import java.util.Map;
import java.util.HashMap;

public class Palette {
	private final static Map<String, Color> fallbackColor = new HashMap<String, Color>(){
		{
			put("background", Color.WHITE);
			put("foreground", Color.BLACK);
		}
	};
	private final static Map<String, String> fallbackStype = new HashMap<String, String>(){
		{
			put("background", "-fx-background: #ffffff;-fx-background-color: #ffffff;");
		}
	};
	public static String getStyle(String name) {
		String ret = fallbackStype.get(name);
		if(ret == null) return "";
		return ret;
	}
	public static Color getColor(String name) {
		Color ret = fallbackColor.get(name);
		if(ret == null) return Color.WHITE;
		return ret;
	}
}