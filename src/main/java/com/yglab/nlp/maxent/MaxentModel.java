package com.yglab.nlp.maxent;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Index;
import com.yglab.nlp.model.Options;

/**
 * Maxent model for storing the training result.
 * 
 * @author Younggue Bae
 */
public class MaxentModel extends AbstractModel {

	private static final long serialVersionUID = 212985822524689546L;

	protected Index labelIndex;
	protected Index featureIndex;
	protected double[][] weights;

	public MaxentModel(Index labelIndex, Index featureIndex, double[][] weights) {
		this.labelIndex = labelIndex;
		this.featureIndex = featureIndex;
		this.weights = weights;
	}
	
	@Override
	public String algorithm() {
		return Options.MAXENT_ALGORITHM;
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

}
