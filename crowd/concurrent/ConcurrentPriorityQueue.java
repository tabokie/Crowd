package crowd.concurrent;

import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.Math;

public class ConcurrentPriorityQueue<E> {

	private class Node<E> {
		public final E value;
		// workaround to avoid use of java.Unsafe 
		// because AtomicMarkableReference do not offer getAndSet method
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
			value = null;
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
			assert next != this;
			if(level == 0) {
				firstNext.set(next, false);
			}
			else if(level < nlevels){
				((AtomicReference<Node<E>>)otherNext[level - 1]).set(next);
			}
		}
		public Node<E> getAndDeleteNext(boolean[] ret) {
			return firstNext.getAndMark(true, ret);
		}
		public Node<E> get(boolean[] ret) {
			return firstNext.get(ret);
		}
		public boolean compareAndSet(Node<E> expNext, boolean expD, Node<E> next, boolean d) {
			assert next != this;
			return firstNext.compareAndSet(expNext, next, expD, d);
		}
		public boolean compareAndSet(int level, Node<E> expNext, Node<E> next) {
			assert next != this;
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
	public E readMin() {
		Node<E> x = head, newhead = null, obshead = head.next(0);
		int offset = 0;
		boolean[] deleteFlag = new boolean[1];
		deleteFlag[0] = true;
		Node<E> nxt;
		while(deleteFlag[0]) {
			nxt = x.next(0);
			if(nxt == null) {
				return null; // already empty
			}
			if(x.inserting() && newhead == null) {
				newhead = x;
			}
			nxt = x.get(deleteFlag);
			offset ++;
			x = nxt;
		}
		return x.value;
	}
	public E deleteMin() {
		Node<E> x = head, newhead = null, obshead = head.next(0);
		int offset = 0;
		boolean[] deleteFlag = new boolean[1];
		deleteFlag[0] = true;
		Node<E> nxt;
		while(deleteFlag[0]) {
			nxt = x.next(0);
			if(nxt == null) {
				return null; // already empty
			}
			if(nxt.inserting() && newhead == null) { // bugs in paper //
				newhead = nxt;
			}
			nxt = x.getAndDeleteNext(deleteFlag);
			// nxt = x.next(0); // shouldn't change after getAndDelete
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
	public String toString() {
		String ret = "";
		Node<E> cur = head.next(0);
		while(cur != null) {
			if(cur.value == null) {
				ret += ">" + "null" + "-";
			}
			else {
				ret += ">" + cur.value.toString() + "-";
			}
			cur = cur.next(0);
		}
		ret += "<";
		return ret;
	}
	public void insert(E value) {
		int height = random();
		Node<E> newNode = new Node<E>(value, height);
		Object[] preds = new Object[height];
		Object[] succs = new Object[height];
		Node<E> del;
		do {
			del = locatePreds(value, preds, succs);
			newNode.next(0, (Node<E>)succs[0]);
		}while(!((Node<E>)preds[0]).compareAndSet((Node<E>)succs[0], false, newNode, false));
		int i = 1;
		while(i < height) {
			newNode.next(i, (Node<E>)succs[i]);
			// no more successors
			if(succs[i] == null) break;
			if(newNode.deleted() || ((Node<E>)succs[i]).deleted() || (Node<E>)succs[i] == del) {
				break;
			}
			if(((Node<E>)preds[i]).compareAndSet(i, (Node<E>)succs[i], newNode)) {
				i ++;
			}
			else {
				del = locatePreds(value, preds, succs);
				if((Node<E>)succs[0] != newNode) break;
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
			if(h == null || !h.deleted()) {
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
	private Node<E> locatePreds(E value, Object[] preds, Object[] succs) {
		int i = preds.length - 1;
		Node<E> pred = head, del = null;
		Node<E> cur;
		boolean deleteFlag;
		while(i >= 0) {
			cur = pred.next(i);
			deleteFlag = pred.deleted();
			while(cur != null && 
				(((Comparable<E>)cur.value).compareTo(value) < 0 || cur.deleted() || (deleteFlag && i == 0))) {
				if(deleteFlag && i == 0) {
					del = cur;
				}
				pred = cur;
				cur = pred.next(i);
				deleteFlag = pred.deleted();
			}
			preds[i] = pred;
			succs[i] = cur;
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