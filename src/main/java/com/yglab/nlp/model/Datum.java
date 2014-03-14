package com.yglab.nlp.model;

import java.util.List;

/**
 * Class for datum that is a basic input model of trainer.
 * 
 * @author Younggue Bae
 */
public class Datum {

	protected int index;
	protected String word;
	protected String label;
	protected List<String> features;
	protected String guessLabel;
	protected String previousLabel;
	protected String[] previousLabelSequence;
	protected boolean gold;

	public Datum() { }
	
	public Datum(String word, String label) {
		this.word = word;
		this.label = label;
	}
	
	public Datum(int index, String word, String label) {
		this.index = index;
		this.word = word;
		this.label = label;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public void setGold(boolean gold) {
		this.gold = gold;
	}

	public boolean isGold() {
		return this.gold;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public String getGuessLabel() {
		return guessLabel;
	}

	public void setGuessLabel(String guessLabel) {
		this.guessLabel = guessLabel;
	}

	public String getPreviousLabel() {
		return previousLabel;
	}

	public void setPreviousLabel(String previousLabel) {
		this.previousLabel = previousLabel;
	}
	
	public String[] getPreviousLabelSequence() {
		return this.previousLabelSequence;
	}
	
	public void setPreviousLabelSequence(String[] previousLabelSequence) {
		this.previousLabelSequence = previousLabelSequence;
	}
	
}