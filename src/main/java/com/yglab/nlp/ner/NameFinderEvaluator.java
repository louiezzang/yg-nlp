package com.yglab.nlp.ner;

import com.yglab.nlp.model.Span;
import com.yglab.nlp.util.eval.AbstractEvaluator;

/**
 * The evaluator for NameFinder.
 * 
 * @author Younggue Bae
 */
public class NameFinderEvaluator extends AbstractEvaluator<NameSample> {
	
	private NameFinder nameFinder;
	
	public NameFinderEvaluator(NameFinder nameFinder) {
		this.nameFinder = nameFinder;
	}

	@Override
	public void evaluateSample(NameSample sample) {
		Span[] actualSpans = sample.getLabels();
		Span[] predictedSpans = nameFinder.find(sample);
		
		addPredictedSize(predictedSpans.length);
		addActualTrueSize(actualSpans.length);
		
		for (Span predictedSpan : predictedSpans) {
			if (correctlyPredicted(predictedSpan, actualSpans)) {
				addTruePositiveSize(1);
			}
		}
	}
	
	private boolean correctlyPredicted(Span predictedSpan, Span[] actualSpans){
		for (Span actualSpan : actualSpans) {
			if (predictedSpan.equals(actualSpan)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void printCustomResult() {
	}

}
