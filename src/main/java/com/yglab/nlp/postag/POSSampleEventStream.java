package com.yglab.nlp.postag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;

/**
 * This class handles POS sample events stream.
 * 
 * @author Younggue Bae
 */
public class POSSampleEventStream implements EventStream<POSSample, Datum> {

	private POSFeatureGenerator featureGenerator;
	private List<POSSample> samples;
	
	public POSSampleEventStream(POSFeatureGenerator featureGenerator, List<POSSample> samples) {
		this.featureGenerator = featureGenerator;
		this.samples = samples;
	}
	
	@Override
	public List<POSSample> getInputStream() {
		return this.samples;
	}
	
	@Override
	public List<Datum> getOutputStream() {
		List<Datum> trainData = new ArrayList<Datum>();
		
		String[] previousLabel = new String[2];
		
		for (POSSample sample: samples) {
			previousLabel[0] = "*";	// previous previous label
			previousLabel[1] = "*";	// previous label
			String[] tokens = sample.getSentence();
			String[] labels = sample.getLabels();
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
	
}
