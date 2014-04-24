package com.yglab.nlp.postag.lang.ko;

import java.util.LinkedList;

/**
 * This class defines the tail of token.
 * A tail consists of one or more morphemes.
 * 
 * @author Younggue Bae
 */
public class Tail extends LinkedList<Morpheme> {

	private static final long serialVersionUID = 9068900849187143423L;

	private String headSurface;
	
	public Tail() {
	}
	
	public Tail(Tail tail) {
		super(tail);
	}
	
	public void setHeadSurface(String headSurface) {
		this.headSurface = headSurface;
	}
	
	public String getHeadSurface() {
		return this.headSurface;
	}
	
	// TODO: 원형 복원하는 로직이 필요함.
	public String getHeadLemma() {
		return this.headSurface;
	}
	
	@Override
	public boolean add(Morpheme morph) {
		if (morph != null) {
			this.addFirst(morph);
		  return true;
		}
	  return false;
	}
	
	public String getPos() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.size(); i++) {
			Morpheme morph = this.get(i);
			sb.append(morph.getPos());
			if (i < this.size() - 1) {
				sb.append("+");
			}
		}
		return sb.toString();
	}
	
	public String getTag() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.size(); i++) {
			Morpheme morph = this.get(i);
			sb.append(morph.getTag());
			if (i < this.size() - 1) {
				sb.append("+");
			}
		}
		return sb.toString();		
	}

}
