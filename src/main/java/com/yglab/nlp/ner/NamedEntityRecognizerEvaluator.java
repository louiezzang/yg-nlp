package com.yglab.nlp.ner;

import com.yglab.nlp.util.Span;
import com.yglab.nlp.util.eval.AbstractEvaluator;

/**
 * The evaluator for NamedEntityRecognizer.
 * 
 * @author Younggue Bae
 */
public class NamedEntityRecognizerEvaluator extends AbstractEvaluator<NameSample> {
	
	private NamedEntityRecognizer ner;
	
	public NamedEntityRecognizerEvaluator(NamedEntityRecognizer ner) {
		this.ner = ner;
	}

	@Override
	public void evaluateSample(NameSample sample) {
		Span[] actualSpans = sample.getLabels();
		Span[] predictedSpans = ner.find(sample);
		
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

}
