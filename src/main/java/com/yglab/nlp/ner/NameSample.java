package com.yglab.nlp.ner;

import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Span;



/**
 * Sample instance for NER.
 * 
 * @author Younggue Bae
 */
public class NameSample {

	protected List<String> sentence;
	protected List<Span> labels;

	public NameSample(String[] sentence, Span[] labels) {
		if (sentence == null) {
			throw new IllegalArgumentException("sentence must not be null!");
		}

		if (labels == null) {
			labels = new Span[0];
		}

		this.sentence = Arrays.asList(sentence);
		this.labels = Arrays.asList(labels);
	}

	public String[] getSentence() {
		return sentence.toArray(new String[sentence.size()]);
	}
	
	public void setSentence(String[] sentence) {
		this.sentence = Arrays.asList(sentence);
	}

	public Span[] getLabels() {
		return labels.toArray(new Span[labels.size()]);
	}

}
