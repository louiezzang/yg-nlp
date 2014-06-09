package com.yglab.nlp.parser.dep;

import java.util.Collections;
import java.util.List;

import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.util.eval.AbstractEvaluator;

/**
 * The evaluator for DependencyParser.
 * 
 * @author Younggue Bae
 */
// TODO
public class DependencyParserEvaluator extends AbstractEvaluator<ParseSample> {
	
	private  DependencyParser parser;
	private boolean labeled = false;
	
	/* LAS(Labeled Attachment Score) */
	private int labeledAttachmentTrueSize = 0;
	
	/* UAS(Unlabeled Attachment Score) */
	private int unlabeledAttachmentTrueSize = 0;
	
	/* LA(Label Accuracy) */
	private int labelAccuracySize = 0;
	
	public DependencyParserEvaluator(DependencyParser parser, boolean labeled) {
		this.parser = parser;
		this.labeled = labeled;
	}

	@Override
	public void evaluateSample(ParseSample sample) {
		int[] actualHeads = sample.getHeads();
		List<List<Parse>> kBestParses = parser.parse(sample, 1);
		List<Parse> bestParses = kBestParses.get(0);
		Collections.sort(bestParses);
		
		addPredictedSize(bestParses.size());
		addActualTrueSize(actualHeads.length - 1);
		
		for (int i = 0; i < bestParses.size(); i++) {
			Parse predictedParse = bestParses.get(i);
			System.out.println(predictedParse);
			
			if (correctlyPredicted(predictedParse, labeled)) {
				addTruePositiveSize(1);
				labeledAttachmentTrueSize++;
			}
			
			if (correctlyPredicted(predictedParse, false)) {
				unlabeledAttachmentTrueSize++;
			}
			
			if (correctlyLabeled(predictedParse)) {
				labelAccuracySize++;
			}
		}
	}
	
	/**
	 * Returns the LAS(Labeled Attachment Score).
	 * 
	 * @return
	 */
	public double getLAS() {
		if (predictedSize == 0) {
			return 0;
		}
		
		double las = (double) labeledAttachmentTrueSize / (double) predictedSize;
		return las;
	}
	
	/**
	 * Returns the UAS(Unlabeled Attachment Score).
	 * 
	 * @return
	 */
	public double getUAS() {
		if (predictedSize == 0) {
			return 0;
		}
		
		double uas = (double) unlabeledAttachmentTrueSize / (double) predictedSize;
		return uas;
	}
	
	/**
	 * Returns the LA(Label Accuracy).
	 * 
	 * @return
	 */
	public double getLA() {
		if (predictedSize == 0) {
			return 0;
		}
		
		double la = (double) labelAccuracySize / (double) predictedSize;
		return la;
	}
	
	private boolean correctlyPredicted(Parse predictedParse, boolean labeled){	
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
	
	private boolean correctlyLabeled(Parse predictedParse){
		if (labeled) {
			if (predictedParse.getLabel().equals(predictedParse.getGoldenLabel())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void printCustomResult() {
		output.println(" LAS(Labeled Attachment Score) = " + getLAS());
		output.println(" UAS(Unlabeled Attachment Score) = " + getUAS());
		output.println(" LA(Label Accuracy) = " + getLA());
	}

}
