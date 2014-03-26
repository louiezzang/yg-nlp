package com.yglab.nlp.ner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.util.Span;

/**
 * This class handles NER sample events stream.
 * 
 * @author Younggue Bae
 */
public class NameSampleEventStream implements EventStream<NameSample, Datum> {

	protected NameFeatureGenerator featureGenerator;
	protected List<NameSample> samples;
	
	public NameSampleEventStream(NameFeatureGenerator featureGenerator, List<NameSample> samples) {
		this.featureGenerator = featureGenerator;
		this.samples = samples;
	}
	
	@Override
	public List<NameSample> getInputStream() {
		return this.samples;
	}
	
	@Override
	public List<Datum> getOutputStream() {
		List<Datum> trainData = new ArrayList<Datum>();

		String[] previousLabel = new String[2];
		
		for (NameSample sample : samples) {
			previousLabel[0] = "*"; // previous previous label
			previousLabel[1] = "*"; // previous label
			
			String[] tokens = sample.getSentence();
			String[] labels = generateOutcomes(sample.getLabels(), "O", tokens.length);
			for (int i = 0; i < tokens.length; i++) {
				Datum datum = new Datum(tokens[i], labels[i]);
				datum.setFeatures(Arrays.asList(featureGenerator.getFeatures(i, tokens, previousLabel)));
				trainData.add(datum);

				// previousLabel = datum.getLabel();
				previousLabel[0] = previousLabel[1];
				previousLabel[1] = datum.getLabel();
			}
		}
		return trainData;
	}
	
	/**
	 * Generates the name tag outcomes (start, continue, other) for each token in a sentence with the specified length
	 * using the specified name spans.
	 * 
	 * @param names
	 *          Token spans for each of the names.
	 * @param type
	 *          null or overrides the type parameter in the provided samples
	 * @param length
	 *          The length of the sentence.
	 * @return An array of start, continue, other outcomes based on the specified names and sentence length.
	 */
	protected static String[] generateOutcomes(Span[] names, String type, int length) {
		String[] outcomes = new String[length];
		for (int i = 0; i < outcomes.length; i++) {
			outcomes[i] = NameFinder.LABEL_OTHER;
		}
		for (Span name : names) {
			if (name.getType() == null) {
				outcomes[name.getStart()] = type + "-" + NameFinder.LABEL_START;
			} else {
				outcomes[name.getStart()] = name.getType() + "-" + NameFinder.LABEL_START;
			}
			// now iterate from begin + 1 till end
			for (int i = name.getStart() + 1; i < name.getEnd(); i++) {
				if (name.getType() == null) {
					outcomes[i] = type + "-" + NameFinder.LABEL_CONTINUE;
				} else {
					outcomes[i] = name.getType() + "-" + NameFinder.LABEL_CONTINUE;
				}
			}
		}
		return outcomes;
	}
	
}
