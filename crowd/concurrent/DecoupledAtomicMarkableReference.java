package crowd.concurrent;

import java.util.concurrent.atomic.*;

public class DecoupledAtomicMarkableReference<V> {

	private static class Pair<T> {
		final T reference;
		final boolean mark;
		private Pair(T reference, boolean mark) {
			this.reference = reference;
			this.mark = mark;
		}
		static <T> Pair<T> of(T reference, boolean mark) {
			return new Pair<T>(reference, mark);
		}
	}

	private AtomicReference<Pair<V>> pair;

	/**
	 * Creates a new {@code AtomicMarkableReference} with the given
	 * initial values.
	 *
	 * @param initialRef the initial reference
	 * @param initialMark the initial mark
	 */
	public DecoupledAtomicMarkableReference(V initialRef, boolean initialMark) {
		pair = new AtomicReference<Pair<V>>(Pair.of(initialRef, initialMark));
	}

	/**
	 * Returns the current value of the reference.
	 *
	 * @return the current value of the reference
	 */
	public V getReference() {
		return pair.get().reference;
	}

	/**
	 * Returns the current value of the mark.
	 *
	 * @return the current value of the mark
	 */
	public boolean isMarked() {
		return pair.get().mark;
	}

	/**
	 * Returns the current values of both the reference and the mark.
	 * Typical usage is {@code boolean[1] holder; ref = v.get(holder); }.
	 *
	 * @param markHolder an array of size of at least one. On return,
	 * {@code markholder[0]} will hold the value of the mark.
	 * @return the current value of the reference
	 */
	public V get(boolean[] markHolder) {
		Pair<V> pair = this.pair.get();
		markHolder[0] = pair.mark;
		return pair.reference;
	}

	/**
	 * Atomically sets the value of both the reference and mark
	 * to the given update values if the
	 * current reference is {@code ==} to the expected reference
	 * and the current mark is equal to the expected mark.
	 *
	 * <p><a href="package-summary.html#weakCompareAndSet">May fail
	 * spuriously and does not provide ordering guarantees</a>, so is
	 * only rarely an appropriate alternative to {@code compareAndSet}.
	 *
	 * @param expectedReference the expected value of the reference
	 * @param newReference the new value for the reference
	 * @param expectedMark the expected value of the mark
	 * @param newMark the new value for the mark
	 * @return {@code true} if successful
	 */
	public boolean weakCompareAndSet(V       expectedReference,
									 V       newReference,
									 boolean expectedMark,
									 boolean newMark) {
		return compareAndSet(expectedReference, newReference,
							 expectedMark, newMark);
	}

	/**
	 * Atomically sets the value of both the reference and mark
	 * to the given update values if the
	 * current reference is {@code ==} to the expected reference
	 * and the current mark is equal to the expected mark.
	 *
	 * @param expectedReference the expected value of the reference
	 * @param newReference the new value for the reference
	 * @param expectedMark the expected value of the mark
	 * @param newMark the new value for the mark
	 * @return {@code true} if successful
	 */
	public boolean compareAndSet(V       expectedReference,
								 V       newReference,
								 boolean expectedMark,
								 boolean newMark) {
		Pair<V> current = pair.get();
		return
			expectedReference == current.reference &&
			expectedMark == current.mark &&
			((newReference == current.reference &&
			  newMark == current.mark) ||
			 casPair(current, Pair.of(newReference, newMark)));
	}

	/**
	 * Unconditionally sets the value of both the reference and mark.
	 *
	 * @param newReference the new value for the reference
	 * @param newMark the new value for the mark
	 */
	public void set(V newReference, boolean newMark) {
		Pair<V> current = pair.get();
		if (newReference != current.reference || newMark != current.mark)
			this.pair.set(Pair.of(newReference, newMark));
	}

	/**
	 * Atomically sets the value of the mark to the given update value
	 * if the current reference is {@code ==} to the expected
	 * reference.  Any given invocation of this operation may fail
	 * (return {@code false}) spuriously, but repeated invocation
	 * when the current value holds the expected value and no other
	 * thread is also attempting to set the value will eventually
	 * succeed.
	 *
	 * @param expectedReference the expected value of the reference
	 * @param newMark the new value for the mark
	 * @return {@code true} if successful
	 */
	public boolean attemptMark(V expectedReference, boolean newMark) {
		Pair<V> current = pair.get();
		return
			expectedReference == current.reference &&
			(newMark == current.mark ||
			 casPair(current, Pair.of(expectedReference, newMark)));
	}

	/**
	 * New method introduced to set reference alone, unconditionally
	 */
	public V getAndMark(boolean newMark, boolean[] ret) {
		Pair<V> current;
		do {
			current = pair.get();
		} while(current.mark != newMark && !casPair(current, Pair.of(current.reference, newMark)));
		ret[0] = current.mark;
		return current.reference;
	}
	public boolean compareAndSet(V expectedReference, V newReference) {
		Pair<V> current = pair.get();
		return
			expectedReference == current.reference &&
			(newReference == current.reference ||
			 casPair(current, Pair.of(newReference, current.mark)));
	}

	private boolean casPair(Pair<V> cmp, Pair<V> val) {
		return pair.compareAndSet(cmp, val);
	}

}