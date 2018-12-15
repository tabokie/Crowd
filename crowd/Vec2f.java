package crowd;

public class Vec2f {
	public float[] data = new float[2];
	public Vec2f(float a, float b) {
		data[0] = a;
		data[1] = b;
	}
	public Vec2f(Vec2f rhs){
		if(rhs != null) {
			data[0] = rhs.data[0];
			data[1] = rhs.data[1];
		}
	}
	public Vec2f() { }
	public void copy(Vec2f rhs) {
		if(rhs != null) {
			data[0] = rhs.data[0];
			data[1] = rhs.data[1];
		}
	}
}