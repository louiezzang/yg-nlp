package com.yglab.nlp.postag;

import java.util.Arrays;
import java.util.List;

/**
 * Sample instance for POS Tagger.
 * 
 * @author Younggue Bae
 */
public class POSSample {

	private List<String> sentence;
	private List<String> labels;

	public POSSample(String[] sentence, String[] labels) {
		if (sentence == null) {
			throw new IllegalArgumentException("sentence must not be null!");
		}

		this.sentence = Arrays.asList(sentence);
		this.labels = Arrays.asList(labels);

		checkArguments();
	}

	private void checkArguments() {
		if (sentence.size() != labels.size()) {
			throw new IllegalArgumentException("There must be exactly one tag for each token. tokens: " + sentence.size()
					+ ", tags: " + labels.size());
		}

		if (sentence.contains(null)) {
			throw new IllegalArgumentException("null elements are not allowed in sentence tokens!");
		}
		if (labels.contains(null)) {
			throw new IllegalArgumentException("null elements are not allowed in tags!");
		}
	}

	public String[] getSentence() {
		return sentence.toArray(new String[sentence.size()]);
	}

	public String[] getLabels() {
		return labels.toArray(new String[labels.size()]);
	}

	public void replaceAllLabels(String regex, String replacement) {
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			label = label.replaceAll(regex, replacement);
			labels.set(i, label);
		}
	}

}
