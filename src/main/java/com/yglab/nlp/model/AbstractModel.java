package com.yglab.nlp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a abstract model for maxent and perceptron trainer.
 * 
 * @author Younggue Bae
 */
public abstract class AbstractModel implements Model {

	private static final long serialVersionUID = 1821357479981071284L;
	
	protected String algorithm;
	public Map<String, String> options = new HashMap<String, String>();
	
	public abstract String algorithm();
	
	public abstract Index getLabelIndex();
	
	public abstract Index getFeatureIndex();
	
	public abstract double[][] getWeights();
	
	public void setOption(String key, String value) {
		options.put(key, value);
	}
	
	public String getOption(String key) {
		return options.get(key);
	}
	
	public Map<String, String> getOptions() {
		return this.options;
	}

}
