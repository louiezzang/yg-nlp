package com.yglab.nlp.util.eval;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
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
	
	protected int sampelSize = 0;
	
	protected PrintStream output;
	
	public AbstractEvaluator() {
		this.output = System.out;
	}
	
	public AbstractEvaluator(String outputFile) {
		this(outputFile, false);
	}
	
	public AbstractEvaluator(String outputFile, boolean append) {
		try {
			OutputStream os = new FileOutputStream(outputFile, append);
			this.output = new PrintStream(os);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
	
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
		long startTime = System.currentTimeMillis();
		sampelSize = samples.size();
		for (T sample : samples) {
			evaluateSample(sample);
		}

		printResult(startTime);
		output.close();
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
	
	protected void printResult(long start) {
		long elapsedTime = System.currentTimeMillis() - start;
		
		output.println("");
		output.println("===================================================");
		output.println(" Evaluation");
		output.println("---------------------------------------------------");
		output.println(" Size = " + sampelSize);
		output.println(" Precision = " + getPrecision());
		output.println(" Recall = " + getRecall());
		output.println(" F-Measure = " + getFMeasure());
		printCustomResult();
		output.println(" Elapsed time = " + elapsedTime + " ms");
		output.println(" Execution date = " + new Date());
		output.println("===================================================");
	}
	
	abstract protected void printCustomResult();

}
