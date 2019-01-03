package crowd;

public abstract class Buildable {
	protected App parent;
	public Buildable(App app) {
		parent = app;
	}
	public Buildable() {
		parent = null;
	}
	public void bind(App app) {
		if(parent == null)
			parent = app;
	}
	public abstract App build();
}