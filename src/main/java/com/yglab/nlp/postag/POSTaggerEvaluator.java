package com.yglab.nlp.postag;

import com.yglab.nlp.util.eval.AbstractEvaluator;

/**
 * The evaluator for POSTagger.
 * 
 * @author Younggue Bae
 */
public class POSTaggerEvaluator extends AbstractEvaluator<POSSample> {
	
	protected POSTagger tagger;
	
	public POSTaggerEvaluator(POSTagger tagger) {
		super();
		this.tagger = tagger;
	}
	
	public POSTaggerEvaluator(POSTagger tagger, String outputFile) {
		super(outputFile);
		this.tagger = tagger;
	}
	
	public POSTaggerEvaluator(POSTagger tagger, String outputFile, boolean append) {
		super(outputFile, append);
		this.tagger = tagger;
	}

	@Override
	public void evaluateSample(POSSample sample) {
		String[] actualTags = sample.getLabels();
		String[] predictedTags = tagger.tag(sample);
		
		addPredictedSize(predictedTags.length);
		addActualTrueSize(actualTags.length);
		
		output.println("");
		
		for (int i = 0; i < predictedTags.length; i++) {
			output.print(i + "\t" + sample.getSentence()[i] + "\t" + actualTags[i] + "\t" + predictedTags[i] + "\t");
			if (predictedTags[i].equals(actualTags[i])) {
				addTruePositiveSize(1);
				output.println("true");
			}
			else {
				output.println("false");
			}
		}
	}

	@Override
	protected void printCustomResult() {
	}
}
