package com.yglab.nlp.sbd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.model.Span;

/**
 * This class handles sentence detector sample events stream.
 * 
 * @author Younggue Bae
 */
public class SentenceSampleEventStream implements EventStream<SentenceSample, Datum> {

	private SentenceFeatureGenerator featureGenerator;
	private List<SentenceSample> samples;
	
	public SentenceSampleEventStream(SentenceFeatureGenerator featureGenerator, List<SentenceSample> samples) {
		this.featureGenerator = featureGenerator;
		this.samples = samples;
	}
	
	@Override
	public List<SentenceSample> getInputStream() {
		return this.samples;
	}
	
	@Override
	public List<Datum> getOutputStream() {
		List<Datum> trainData = new ArrayList<Datum>();

		String[] previousLabel = new String[2];

		for (SentenceSample sample : samples) {
			previousLabel[0] = "*"; // previous previous label
			previousLabel[1] = "*"; // previous label
			
			String[] tokens = sample.getDocument();
			String[] labels = generateOutcomes(sample.getSentences(), tokens.length);
			for (int i = 0; i < tokens.length; i++) {
				Datum datum = new Datum(tokens[i], labels[i]);
				datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(i, tokens, previousLabel)));
				trainData.add(datum);

				previousLabel[0] = previousLabel[1];
				previousLabel[1] = datum.getLabel();
			}
		}
		return trainData;
	}
	
	private static String[] generateOutcomes(Span[] sentenceSpans, int length) {
		String[] outcomes = new String[length];
		for (int i = 0; i < outcomes.length; i++) {
			outcomes[i] = MaxentSentenceDetector.LABEL_OTHER;
		}
		for (Span sentence : sentenceSpans) {
			int eos = sentence.getStart();
			outcomes[eos] = MaxentSentenceDetector.LABEL_EOS;
		}
		
		return outcomes;
	}
}
