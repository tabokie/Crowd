package crowd.concurrent;

import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.Math;

public class ConcurrentPriorityQueue<E> {
	// workaround to avoid use of java.Unsafe 
	// because AtomicMarkableReference do not offer getAndSet method
	/* // deprecated for cas failure
	private class WrappedAtomicReference<Ref> {
		public AtomicReference<Ref> reference; // single instance through copy
		public final boolean flag; // no need for atomic protect
		WrappedAtomicReference(Ref ref, boolean f) {
			reference = new AtomicReference<Ref>(ref);
			flag = f;
		}
		WrappedAtomicReference(boolean f) {
			reference = new AtomicReference<Ref>();
			flag = f;
		}
		WrappedAtomicReference(AtomicReference<Ref> shadow, boolean f) {
			reference = shadow;
			flag = f;
		}
	}
	*/
	private class Node<E> {
		public E value;
		// a wrapped node pointer made atomic
		// private AtomicReference<WrappedAtomicReference<Node<E>>> firstNext
			// = new AtomicReference<WrappedAtomicReference<Node<E>>>(new WrappedAtomicReference<Node<E>>());
		private DecoupledAtomicMarkableReference<Node<E>> firstNext 
			= new DecoupledAtomicMarkableReference<Node<E>>(null, false);
		private AtomicReference<?>[] otherNext; // java dis-allow generic array
		private AtomicBoolean insertingFlag = new AtomicBoolean(true);
		private final int nlevels;
		Node(E value, int size) {
			this.value = value;
			nlevels = size;
			otherNext = new AtomicReference<?>[size - 1];
			for(int i = 0; i < size-1; i ++) {
				otherNext[i] = new AtomicReference<Node<E>>();
			}
		}
		Node(int size) {
			nlevels = size;
			otherNext = new AtomicReference<?>[size - 1];
			for(int i = 0; i < size-1; i ++) {
				otherNext[i] = new AtomicReference<Node<E>>();
			}
		}
		public Node<E> next(int level) {
			if(level == 0) {
				return firstNext.getReference();	
			}
			else if(level < nlevels) {
				AtomicReference<Node<E>> ref = (AtomicReference<Node<E>>)(otherNext[level - 1]);
				return ref.get();
			}
			else {
				return null;
			}
		}
		public void next(int level, Node<E> next) {
			if(level == 0) {
				firstNext.set(next, false);
			}
			else if(level < nlevels){
				((AtomicReference<Node<E>>)otherNext[level - 1]).set(next);
			}
		}
		public boolean getAndDeleteNext() {
			return firstNext.getAndMark(true);
		}
		public boolean compareAndSet(Node<E> expNext, boolean expD, Node<E> next, boolean d) {
			return firstNext.compareAndSet(expNext, next, expD, d);
		}
		public boolean compareAndSet(int level, Node<E> expNext, Node<E> next) {
			if(level == 0) {
				return firstNext.compareAndSet(expNext, next);
			}
			else {
				return ((AtomicReference<Node<E>>)otherNext[level - 1]).compareAndSet(expNext, next);
			}
		}
		public boolean inserting() {
			return insertingFlag.get();
		}
		public void setInserting(boolean i) {
			insertingFlag.set(i);
		}
		public boolean deleted() {
			return firstNext.isMarked();
		}
	}
	private final int BOUND_OFFSET = 64;
	private final int MAX_LEVEL = 16;
	private int nlevels = MAX_LEVEL;
	private Node<E> head = new Node<E>(MAX_LEVEL);
	public E deleteMin() {
		Node<E> x = head, newhead = null, obshead = head.next(0);
		int offset = 0;
		boolean deleteFlag = true;
		Node<E> nxt;
		while(deleteFlag) {
			nxt = x.next(0);
			if(nxt == null) {
				return null; // already empty
			}
			if(x.inserting() && newhead == null) {
				newhead = x;
			}
			deleteFlag = x.getAndDeleteNext();
			nxt = x.next(0); // shouldn't change after getAndDelete
			offset ++;
			x = nxt;
		}
		E v = x.value;
		if(offset < BOUND_OFFSET) {
			return v;
		}
		if(newhead == null) {
			newhead = x; // is deleted
		}
		if(head.compareAndSet(obshead, true, newhead, true)) {
			restructure();
		}
		return v;
	}
	public void insert(E value) {
		int height = random();
		Node<E> newNode = new Node<E>(value, height);
		List<Node<E>> preds = new ArrayList<Node<E>>(), succs = new ArrayList<Node<E>>();
		Node<E> del;
		do {
			del = locatePreds(value, preds, succs);
			newNode.next(0, succs.get(0));
		}while(preds.get(0).compareAndSet(succs.get(0), false, newNode, false));
		int i = 1;
		while(i < height) {
			newNode.next(i, succs.get(i));
			if(newNode.deleted() || succs.get(i).deleted() || succs.get(i) == del) {
				break;
			}
			if(preds.get(i).compareAndSet(i, succs.get(i), newNode)) {
				i ++;
			}
			else {
				del = locatePreds(value, preds, succs);
				if(succs.get(0) != newNode) break;
			}
		}
		newNode.setInserting(false);
	}
	private void restructure() {
		Node<E> pred = head;
		int i = nlevels - 1;
		Node<E> h, cur;
		while(i > 0) {
			h = head.next(i);
			cur = pred.next(i);
			if(!h.deleted()) {
				i--;
				continue;
			}
			while(cur.deleted()) {
				pred = cur;
				cur = pred.next(i);
			}
			if(head.compareAndSet(i, h, pred.next(i))) {
				i --;
			}
		}
	}
	private Node<E> locatePreds(E value, List<Node<E>> preds, List<Node<E>> succs) {
		preds.clear();
		succs.clear();
		int i = nlevels - 1;
		Node<E> pred = head, del = null;
		Node<E> cur;
		boolean deleteFlag;
		while(i >= 0) {
			cur = pred.next(i);
			if(cur == null) {
				i --;
				continue;
			}
			deleteFlag = pred.deleted();
			while(((Comparable<E>)cur.value).compareTo(value) < 0 || cur.deleted() || (deleteFlag && i == 0)) {
				if(deleteFlag && i == 0) {
					del = cur;
				}
				pred = cur;
				cur = pred.next(i);
				deleteFlag = pred.deleted();
			}
			preds.add(0, pred);
			succs.add(0, cur);
			i--;
		}
		return del;
	}
	private int random() {
		int newLevel = 1;
		while ((int)(Math.random() * 1000) % 2 == 0 && newLevel < MAX_LEVEL) newLevel ++;
		return newLevel;
	}

}