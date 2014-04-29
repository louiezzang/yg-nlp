package com.yglab.nlp.postag.morph;

import java.util.LinkedList;

import com.yglab.nlp.util.lang.ko.MorphemeUtil;

/**
 * This class defines the token.
 * A token consists of one or more morphemes which are head and tail.
 * 
 * @author Younggue Bae
 */
public class Token extends LinkedList<Morpheme> implements Comparable<Token> {

	private static final long serialVersionUID = 1L;
	
	private String token;
	private String head;
	private int numValidTag;

	/**
	 * Creates a token.
	 * 
	 */
	public Token(String token) {
		this.token = token;
	}
	
	/**
	 * Creates a cloned token.
	 * 
	 * @param token	The token to clone
	 */
	public Token(Token token) {
		super(token);
		this.token = token.getToken();
		this.head = token.getHead();
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
	
	public Token getSubTail(int index) {
		Token tail = new Token(token);
		String head = tail.token;
		for (int i = 0; i <= index; i++) {
			Morpheme morph = this.get(i);
			tail.add(morph);
			head = MorphemeUtil.truncateRight(head, morph.getSurface());
		}
		
		tail.setHead(head);
		
		return tail;
	}
	
	public int getNumTag() {
		return this.getPos().split("\\+").length;
	}
	
	public int getNumValidTag() {
		return this.numValidTag;
	}
	
	public void setNumValidTag(int numValidTag) {
		this.numValidTag = numValidTag;
	}
	
	@Override
	public int compareTo(Token other) {
		int otherNumValidTag = ((Token) other).getNumValidTag();
		if (numValidTag > 0 || otherNumValidTag > 0) {
			//System.out.println(this.getTag() + ":" + numValidTag + ", " + other.getTag() + ":" + otherNumValidTag);
			//descending order
			return otherNumValidTag - this.numValidTag;
		}

		int otherNumTag = ((Token) other).getNumTag(); 
		//descending order
		return otherNumTag - this.getNumTag();
	}

}
