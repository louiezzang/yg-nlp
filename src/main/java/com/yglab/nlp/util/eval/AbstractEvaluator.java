package com.yglab.nlp.util.eval;

import java.util.List;

/**
 * The abstract class for evaluators.
 * 
 * @author Younggue Bae
 */
public abstract class AbstractEvaluator<T> implements Evaluator<T> {

	/** predictedSize = true positives + false positives */
	protected int predictedSize = 0;
	
	/** actualTrueSize = true positives + false negatives */
	protected int actualTrueSize = 0;
	
	protected int truePositiveSize = 0;
	
	protected void addPredictedSize(int size) {
		predictedSize += size;
	}
	
	protected void addActualTrueSize(int size) {
		actualTrueSize += size;
	}
	
	protected void addTruePositiveSize(int size) {
		truePositiveSize += size;
	}
	
	abstract public void evaluateSample(T sample);
	
	public void evaluate(List<T> samples) {
		for (T sample : samples) {
			evaluateSample(sample);
		}
	}
	
	public double getPrecision() {
		if (predictedSize == 0) {
			return 0;
		}
		
		double precision = (double) truePositiveSize / (double) predictedSize;
		return precision;
	}
	
	public double getRecall() {
		if (truePositiveSize == 0) {
			return 0;
		}
		
		double recall = (double) truePositiveSize / (double) actualTrueSize;
		return recall;
	}
	
	public double getFMeasure() {	
		double precision = getPrecision();
		double recall = getRecall();
		
		if (precision + recall == 0) {
			return 0;
		}
		
		double fmeasure = (double) (2 * precision * recall) / (precision + recall);
		
		return fmeasure;
	}
	
	public void print() {
		System.out.println("===================================================");
		System.out.println(" Evaluation");
		System.out.println("---------------------------------------------------");
		System.out.println(" Precision = " + getPrecision());
		System.out.println(" Recall = " + getRecall());
		System.out.println(" F-Measure = " + getFMeasure());
		System.out.println("===================================================");
		
	}

}
