package com.yglab.nlp.parser;

/**
 * Sample instance for dependency parser.
 * 
 * @author Younggue Bae
 */
public class ParseSample {

	// FORM: the forms - usually words, like "thought"
	public String[] forms;

	// LEMMA: the lemmas or stems, e.g. "think"
	public String[] lemmas;

	// COURSE-POS: the course part-of-speech tags, e.g."V"
	public String[] cpostags;

	// FINE-POS: the fine-grained part-of-speech tags, e.g."VBD"
	public String[] postags;

	// HEAD: the positions of the heads for each element
	public int[] heads;

	// DEPREL: the dependency relations, e.g. "SUBJ"
	public String[] deprels;
	
	//FEATURES: some features associated with the elements separated by "|", e.g. "PAST|3P"
	public String[][] feats;

	// Confidence scores per edge
	public double[] confidenceScores;

	public ParseSample() {
	}
	
	public ParseSample(String[] forms, String[] lemmas, String[] cpostags, String[] postags, int[] heads) {
		this(forms, lemmas, cpostags, postags, null, null, heads);
	}
	
	public ParseSample(String[] forms, String[] lemmas, String[] cpostags, String[] postags, String[] deprels, int[] heads) {
		this(forms, lemmas, cpostags, postags, null, deprels, heads);
	}
	
	public ParseSample(String[] forms, String[] lemmas, String[] cpostags, String[] postags, String[][] feats,
			String[] deprels, int[] heads) {
		this.forms = forms;
		this.lemmas = lemmas;
		this.cpostags = cpostags;
		this.postags = postags;
		this.feats = feats;
		this.deprels = deprels;
		this.heads = heads;
	}

	public int length() {
		return forms.length;
	}

	public String[] getForms() {
		return forms;
	}

	public void setForms(String[] forms) {
		this.forms = forms;
	}

	public String[] getLemmas() {
		return lemmas;
	}

	public void setLemmas(String[] lemmas) {
		this.lemmas = lemmas;
	}

	public String[] getCpostags() {
		return cpostags;
	}

	public void setCpostags(String[] cpostags) {
		this.cpostags = cpostags;
	}

	public String[] getPostags() {
		return postags;
	}

	public void setPostags(String[] postags) {
		this.postags = postags;
	}

	public int[] getHeads() {
		return heads;
	}

	public void setHeads(int[] heads) {
		this.heads = heads;
	}

	public String[] getDependencyRelations() {
		return deprels;
	}

	public void setDependencyRelations(String[] deprels) {
		this.deprels = deprels;
	}
	
	public String[][] getFeatures() {
		return feats;
	}

	public void setFeatures(String[][] feats) {
		this.feats = feats;
	}

	public double[] getConfidenceScores() {
		return confidenceScores;
	}

	public void setConfidenceScores(double[] confidenceScores) {
		this.confidenceScores = confidenceScores;
	}

}
