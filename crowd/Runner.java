package crowd;

public interface Runner {
	String respond(Simulator simulator, String message);
	String report(Simulator simulator);
}