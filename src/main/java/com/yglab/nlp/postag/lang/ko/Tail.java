package com.yglab.nlp.postag.lang.ko;

import java.util.LinkedList;

import com.yglab.nlp.util.lang.ko.MorphemeUtil;

/**
 * This class defines the tail of token.
 * A tail consists of one or more morphemes.
 * 
 * @author Younggue Bae
 */
public class Tail extends LinkedList<Morpheme> implements Comparable<Tail> {

	private static final long serialVersionUID = 9068900849187143423L;

	private String token;
	private String head;

	/**
	 * Creates a tail.
	 * 
	 */
	public Tail(String token) {
		this.token = token;
	}
	
	/**
	 * Creates a cloned tail.
	 * 
	 * @param tail	The tail to clone
	 */
	public Tail(Tail tail) {
		super(tail);
		this.token = tail.token;
		this.head = tail.getHead();
	}
	
	@Override
	public boolean add(Morpheme morph) {
		if (morph != null) {
			this.addLast(morph);
		  return true;
		}
	  return false;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getHead() {
		return this.head;
	}
	
	public void setHead(String head) {
		this.head = head;
	}

	public String getPos() {
		StringBuilder sb = new StringBuilder();
		for (int i = this.size() - 1; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getPos());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getPos(int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getPos());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getTag() {
		StringBuilder sb = new StringBuilder();
		for (int i = this.size() - 1; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getTag());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getTag(int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i >= 0; i--) {
			Morpheme morph = this.get(i);
			sb.append(morph.getTag());
			if (i > 0) {
				sb.append("+");
			}
		}
		return sb.toString();		
	}
	
	public Tail getSubTail(int index) {
		Tail tail = new Tail(token);
		String head = tail.token;
		for (int i = 0; i <= index; i++) {
			Morpheme morph = this.get(i);
			tail.add(morph);
			head = MorphemeUtil.truncateRight(head, morph.getSurface());
		}
		
		tail.setHead(head);
		
		return tail;
	}
	
	public int getTagSize() {
		return this.getPos().split("\\+").length;
	}
	
	@Override
	public int compareTo(Tail other) {
		int otherSize = ((Tail) other).getTagSize(); 
 
		//descending order
		return this.getTagSize() - otherSize;
	}

}
