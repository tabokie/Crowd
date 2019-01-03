package crowd.util;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.io.*;

public class JavaRuntime {
	private static JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
	// compile file in `resource` folder in this project
	private static boolean compile(String filename) {
		if(javaCompiler == null) {
			System.out.println("Can't find system javac");
			System.out.println("Probable solution: copy `tools.lib` from jdk directory to `" + System.getProperty("java.home") + "\\lib`");
			return false;
		}
		String outputpath = ResourceManager.getProjectPath() + "\\build";
		String inputpath = ResourceManager.getFullPath(filename);
		int compileStatus = javaCompiler.run(null, null, null, "-d", outputpath, inputpath );
		if(compileStatus != 0) {
			System.out.println("Compilation broke");
			return false;
		}
		return true;
	 	// try {
	 	// 	Class injectedClass = Class.forName("crowd.MyTest");
		 // 	Object injectedObject = injectedClass.newInstance();
		 // 	Method respond = injectedClass.getDeclaredMethod("respond", Simulator.class, String.class);
		 // 	Method report = injectedClass.getDeclaredMethod("report", Simulator.class);
		 // 	Protocol protocol = new DefaultProtocol();
		 // 	Simulator simulator = new DefaultSimulator(protocol);
	 	// 	String result = (String)respond.invoke(injectedObject, simulator, "000:hello I'm 000");
	 	// 	System.out.println("repond returns: " + result);
	 	// 	result = (String)report.invoke(injectedObject, simulator);
	 	// 	System.out.println("report returns: " + result);
	 	// }
	 	// catch(Exception e) {
	 	// 	e.printStackTrace();
	 	// }
	}
	public static Class LoadClassFromResource(String classname) throws Exception {
		String filename = classname + ".java";
		if(compile(filename)) {
			return Class.forName("crowd.concurrent." + classname);
		}
		return null;
	}
	public static Object LoadObjectFromResource(String classname) throws Exception {
		String filename = classname + ".java";
		if(compile(filename)) {
			return Class.forName("crowd.concurrent." + classname).newInstance();
		}
		return null;
	}
}