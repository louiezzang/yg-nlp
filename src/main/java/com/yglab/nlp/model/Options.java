package com.yglab.nlp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines options for NLP tools.
 * 
 * @author Younggue Bae
 */
public class Options {

	public static final String ALGORITHM = "Algorithm";
	public static final String ITERATIONS = "Iterations";
	public static final String CUTOFF = "Cutoff";

	public static final String MAXENT_ALGORITHM = "MAXENT";
	public static final String PERCEPTRON_ALGORITHM = "PERCEPTRON";

	private Map<String, String> options = new HashMap<String, String>();

	public void put(String key, String value) {
		options.put(key, value);
	}

	public String get(String key) {
		return options.get(key);
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}

}
