package com.yglab.nlp.postag.lang.ko;

import java.util.List;

import com.yglab.nlp.postag.POSSample;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.postag.POSTaggerEvaluator;
import com.yglab.nlp.postag.morph.Token;

/**
 * The evaluator for POSTagger.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSTaggerEvaluator extends POSTaggerEvaluator {

	public KoreanPOSTaggerEvaluator(POSTagger tagger) {
		super(tagger);
	}
	
	public KoreanPOSTaggerEvaluator(POSTagger tagger, String outputFile) {
		super(tagger, outputFile);
	}
	
	public KoreanPOSTaggerEvaluator(POSTagger tagger, String outputFile, boolean append) {
		super(tagger, outputFile, append);
	}

	@Override
	public void evaluateSample(POSSample sample) {
		String[] actualTags = sample.getLabels();

		List<Token> tokens = ((KoreanPOSTagger) tagger).analyze(sample.getSentence());
		
		//addPredictedSize(tokens.size());
		addPredictedSize(actualTags.length);
		addActualTrueSize(actualTags.length);
		
		output.println("");
		
		for (int i = 0; i < tokens.size(); i++) {
			Token predictedToken = tokens.get(i);
			String predictedPos = predictedToken.getPos();
			String predictedTag = predictedToken.getTag();
			output.print(i + "\t" + sample.getSentence()[i] + "\t" + actualTags[i] + "\t" + predictedPos + "\t" + predictedTag + "\t" + predictedToken.getAttributes() + "\t");
			if (predictedPos.equals(actualTags[i])) {
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
