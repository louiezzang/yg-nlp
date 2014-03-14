package com.yglab.nlp.parser.dep;

import com.yglab.nlp.parser.Parse;

/**
 * The McDonald et al writeup of the Eisner algorithm describes it in terms of a big 4-dimensional table C, such
 * that C[i,j,d,c] is the score of the best subtree from i to j in direction d and completeness c. 
 * The indices i and j are in the range [1,n], the direction d = {'<-:0', '->:1'}, and the completeness c = {0, 1}.
 * If d = '<-' then the head of the tree is j, the right-most element; otherwise the head is the left-most element, i. 
 * If completeness c = 1, then the subtree is not taking any more dependents; otherwise it needs to be completed.
 * 
 * @author Younggue Bae
 */
public class ParseForestItem {

	public int s;
	public int t;
	private int direction;
	private int completeness;
	private double score;
	private Parse parse;
	private ParseForestItem left;
	private ParseForestItem right;
	
	public ParseForestItem(int s, int t, int direction, int completeness, Parse parse) {
		this(s, t, direction, completeness, Double.NEGATIVE_INFINITY, parse, null, null);
	}
	
	public ParseForestItem(int s, int t, int direction, int completeness, double score, Parse parse) {
		this(s, t, direction, completeness, score, parse, null, null);
	}
	
	public ParseForestItem(int s, int t, int direction, int completeness, 
			double score, Parse parse, ParseForestItem left, ParseForestItem right) {
		this.s = s;
		this.t = t;
		this.direction = direction;
		this.completeness = completeness;
		this.score = score;
		this.parse = parse;
		this.left = left;
		this.right = right;
	}

	public int getDirection() {
		return this.direction;
	}
	
	public int getCompleteness() {
		return this.completeness;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public double getScore() {
		return this.score;
	}
	
	public Parse getParse() {
		return this.parse;
	}
	
	public ParseForestItem getLeft() {
		return this.left;
	}
	
	public ParseForestItem getRight() {
		return this.right;
	}
	
	@Override
	public boolean equals(Object obj) {
		ParseForestItem other = (ParseForestItem) obj;

		if (other.s == this.s && other.t == this.t &&
				other.getDirection() == this.direction && other.getCompleteness() == this.completeness) {
			return true;
		}

		return false;
	}
	
	@Override 
	public String toString() {
		String dir = direction == 1 ? "->" : "<-"; 
		String str = "C[" + s + "," + t + "," + dir + "," + completeness + "]=" + score;
		return str;
	}
}

