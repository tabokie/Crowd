package crowd;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.io.*;

import crowd.concurrent.*;

public class Test {
	private JavaCompiler javaCompiler;
	private void compile(String name) {
		if(javaCompiler == null) return ; 
		String classpath = System.getProperty("user.dir") + "\\build";
		// System.out.println("Target classpath: " + classpath);
		int compileStatus = javaCompiler.run(null, null, null, "-d", classpath , name );
		if(compileStatus != 0) {
			System.out.println("Compilation broke");
		}
	 	try {
	 		Class injectedClass = Class.forName("crowd.MyTest");
		 	Object injectedObject = injectedClass.newInstance();
		 	Method respond = injectedClass.getDeclaredMethod("respond", Simulator.class, String.class);
		 	Method report = injectedClass.getDeclaredMethod("report", Simulator.class);
		 	Protocol protocol = new DefaultProtocol();
		 	Simulator simulator = new DefaultSimulator(protocol);
	 		String result = (String)respond.invoke(injectedObject, simulator, "000:hello I'm 000");
	 		System.out.println("repond returns: " + result);
	 		result = (String)report.invoke(injectedObject, simulator);
	 		System.out.println("report returns: " + result);
	 	}
	 	catch(Exception e) {
	 		e.printStackTrace();
	 	}
	}
	Test(boolean a) {
		javaCompiler = ToolProvider.getSystemJavaCompiler();
		if(javaCompiler == null) {
			System.out.println("Can't find system javac");
			System.out.println("Probable solution: copy `tools.lib` from jdk directory to `" + System.getProperty("java.home") + "\\lib`");
		}
	}
	Test(int a) {
		DiscreteEventScheduler scheduler = new DiscreteEventScheduler();
		scheduler.enqueue(new Actor(0, (Actor actor) -> {
			System.out.println("A running");
			actor.act(1, () -> {
				System.out.println("A.1 done");
			});
			actor.act(2, () -> {
				System.out.println("A.2 done");
			});
		}));
		scheduler.enqueue(new Actor(0, (Actor actor) -> {
			System.out.println("B running");
			actor.act(1, () -> {
				System.out.println("B.1 done");
			});
			actor.act(10, () -> {
				System.out.println("B.10 done");
			});
		}));

		scheduler.start();
		scheduler.close();
	}
	public static void main(String[] args) {
		Test testInstance = new Test(1);


		// BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		// while(true) {
		// 	try {
		// 		String line = reader.readLine();
		// 		if(line.startsWith("exit")){
		// 			break;
		// 		}
		// 		else {
		// 			testInstance.compile(line);
		// 		}	
		// 	}
		// 	catch(Exception e) {
		// 		e.printStackTrace();
		// 		break;
		// 	}
		// }
	}
}