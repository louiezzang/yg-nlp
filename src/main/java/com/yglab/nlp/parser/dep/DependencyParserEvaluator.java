package com.yglab.nlp.parser.dep;

import java.util.List;

import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.util.eval.AbstractEvaluator;

/**
 * The evaluator for DependencyParser.
 * 
 * @author Younggue Bae
 */
public class DependencyParserEvaluator extends AbstractEvaluator<ParseSample> {
	
	private  DependencyParser parser;
	private boolean labeled = false;
	
	public DependencyParserEvaluator(DependencyParser parser, boolean labeled) {
		this.parser = parser;
		this.labeled = labeled;
	}

	@Override
	public void evaluateSample(ParseSample sample) {
		int[] actualHeads = sample.getHeads();
		List<List<Parse>> kBestParses = parser.parse(sample, 1);
		List<Parse> bestParses = kBestParses.get(0);
		
		addPredictedSize(bestParses.size());
		addActualTrueSize(actualHeads.length - 1);
		
		for (int i = 0; i < bestParses.size(); i++) {
			Parse predictedParse = bestParses.get(i);
			if (correctlyPredicted(predictedParse)) {
				addTruePositiveSize(1);
			}
		}
	}
	
	private boolean correctlyPredicted(Parse predictedParse){
		System.out.println(predictedParse);
		
		if (labeled) {
			if (predictedParse.getHead() == predictedParse.getGoldenHead() && 
					predictedParse.getLabel().equals(predictedParse.getGoldenLabel())) {
				return true;
			}
		}
		else {
			if (predictedParse.getHead() == predictedParse.getGoldenHead()) {
				return true;
			}
		}
		return false;
	}

}
