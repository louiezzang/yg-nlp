package com.yglab.nlp.postag;

import com.yglab.nlp.util.eval.AbstractEvaluator;

/**
 * The evaluator for POSTagger.
 * 
 * @author Younggue Bae
 */
public class POSTaggerEvaluator extends AbstractEvaluator<POSSample> {
	
	private POSTagger tagger;
	
	public POSTaggerEvaluator(POSTagger tagger) {
		this.tagger = tagger;
	}

	@Override
	public void evaluateSample(POSSample sample) {
		String[] actualTags = sample.getLabels();
		String[] predictedTags = tagger.tag(sample);
		
		addPredictedSize(predictedTags.length);
		addActualTrueSize(actualTags.length);
		
		System.out.println("");
		
		for (int i = 0; i < predictedTags.length; i++) {
			System.out.print(i + ": " + sample.getSentence()[i] + " [" + actualTags[i] + "] -> [" + predictedTags[i] + "]");
			if (predictedTags[i].equals(actualTags[i])) {
				addTruePositiveSize(1);
				System.out.println(" = true");
			}
			else {
				System.out.println(" = false");
			}
		}
		
	}

}
