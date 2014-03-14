package com.yglab.nlp.perceptron;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Index;
import com.yglab.nlp.model.Options;

/**
 * Model for storing the training result.
 * 
 * @author Younggue Bae
 */
public class PerceptronModel extends AbstractModel {

	private static final long serialVersionUID = 1L;

	protected Index labelIndex;
	protected Index featureIndex;
	protected double[][] weights;
	protected boolean labeled;

	public PerceptronModel(Index labelIndex, Index featureIndex, double[][] weights) {
		this(labelIndex, featureIndex, weights, true);
	}
	
	public PerceptronModel(Index labelIndex, Index featureIndex, double[][] weights, boolean labeled) {
		this.labelIndex = labelIndex;
		this.featureIndex = featureIndex;
		this.weights = weights;
		this.labeled = labeled;
	}
	
	@Override
	public String algorithm() {
		return Options.PERCEPTRON_ALGORITHM;
	}
	
	@Override
	public Index getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(Index labelIndex) {
		this.labelIndex = labelIndex;
	}

	@Override
	public Index getFeatureIndex() {
		return featureIndex;
	}

	public void setFeatureIndex(Index featureIndex) {
		this.featureIndex = featureIndex;
	}

	@Override
	public double[][] getWeights() {
		return weights;
	}

	public void setWeights(double[][] weights) {
		this.weights = weights;
	}
	
	public double getWeight(String label, String feature) {
		int l = 0;
		if (label != null) {
			l = labelIndex.indexOf(label);
		}
		int f = featureIndex.indexOf(feature);
		
		if (l >= 0 && f >= 0) {
			return weights[l][f];
		}
		else {
			return 0;
		}
	}
	
	public boolean isLabeled() {
		return this.labeled;
	}

}
