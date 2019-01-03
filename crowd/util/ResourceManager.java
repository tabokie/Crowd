package crowd.util;

import java.io.*;

public class ResourceManager {
	public static String getProjectPath() {
		return System.getProperty("user.dir");
	}
	public static String getFullPath(String filename) {
		return getProjectPath() + "\\resource\\" + filename; 
	}
	public static String getUnixProjectPath() {
		return "file:/" + getProjectPath().replace("\\", "/");
	}
	public static String getUnixFullPath(String filename) {
		return getUnixProjectPath() + "/resource/" + filename;
	}
}