package com.yglab.nlp.parser;

import com.yglab.nlp.model.Datum;

/**
 * Data structure for holding parse structure.
 * 
 * @author Younggue Bae
 */
public class Parse extends Datum {

	private int head;
	private String cpostag;
	private String postag;
	private double score;
	
	private int goldenHead;
	private String goldenLabel;
	
	public Parse() {
		super();
	}
	
	public Parse(int index, int head) {
		this(index, head, null, null, null);
	}
	
	public Parse(int index, int head, String label) {
		this(index, head, label, null, null);
	}
	
	public Parse(int index, int head, String label, String cpostag, String postag) {
		this.index = index;
		this.head = head;
		this.label = label;
		this.cpostag = cpostag;
		this.postag = postag;
	}

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}

	public String getCpostag() {
		return cpostag;
	}

	public void setCpostag(String cpostag) {
		this.cpostag = cpostag;
	}

	public String getPostag() {
		return postag;
	}

	public void setPostag(String postag) {
		this.postag = postag;
	}
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public int getGoldenHead() {
		return goldenHead;
	}

	public void setGoldenHead(int goldenHead) {
		this.goldenHead = goldenHead;
	}

	public String getGoldenLabel() {
		return goldenLabel;
	}

	public void setGoldenLabel(String goldenLabel) {
		this.goldenLabel = goldenLabel;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append(index).append("\t")
			.append(head).append("\t")
			.append("*"+goldenHead).append("\t")
			.append(label).append("\t")
			.append("*"+goldenLabel).append("\t")
			.append(word).append("\t")
			.append(cpostag).append("\t")
			.append(postag).append("\t")
			.append(score).append("\t")
			.append(gold).append("\t");
		
		return sb.toString();
	}

}
