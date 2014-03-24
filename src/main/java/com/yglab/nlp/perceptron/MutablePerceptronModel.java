package com.yglab.nlp.perceptron;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.yglab.nlp.model.Index;



/**
 * Model for storing the perceptron training result which can be updated or assigned.
 * 
 * @author Younggue Bae
 */
public class MutablePerceptronModel extends PerceptronModel {

	private static final long serialVersionUID = 1L;
	private Map<WeightIndex, Double> mapWeights = new HashMap<WeightIndex, Double>();

	public MutablePerceptronModel(String[] labels, boolean labeled) {
		super(new Index(), new Index(), null, labeled);
		
		if (labeled) {
			for (String label : labels) {
				labelIndex.add(label);
			}
		}
		else {
			labelIndex.add("<no-type>");
		}
	}
	
	public int domainDimension() {
		return featureIndex.size() * labelIndex.size();
	}
	
	@Override
	public double getWeight(String label, String feature) {
		int l = 0;
		if (labeled) {
			l = labelIndex.indexOf(label);
		}
		int f = featureIndex.indexOf(feature);
		
		WeightIndex index = new WeightIndex(l, f);
		if (mapWeights.containsKey(index)) {
			return mapWeights.get(index);
		}
		else {
			return 0;
		}
	}
	
	public void addWeight(String label, String feature, double value) {
		int l = 0;
		if (labeled) {
			labelIndex.add(label);
			l = labelIndex.indexOf(label);
		}
		
		featureIndex.add(feature);
		int f = featureIndex.indexOf(feature);
		
		WeightIndex index = new WeightIndex(l, f);
		
		if (mapWeights.containsKey(index)) {
			value += mapWeights.get(index);
			mapWeights.put(index, value);
		}
		else {
			mapWeights.put(index, value);
		}
	}
	
	public double[][] to2D() {
		double[][] x2D = new double[labelIndex.size()][featureIndex.size()];
		
		for (int label = 0; label < x2D.length; label++) {
			for (int feature = 0; feature < x2D[label].length; feature++) {
				WeightIndex index = new WeightIndex(label, feature);
				if (mapWeights.containsKey(index)) {
					x2D[label][feature] = mapWeights.get(index);
				}
				else {
					//x2D[label][feature] = 0;
				}
			}
		}

		return x2D;
	}
	
	public double[][] toAveraged2D(int numSample, int numIteration) {
		double[][] x2D = new double[labelIndex.size()][featureIndex.size()];

		for (int label = 0; label < x2D.length; label++) {
			for (int feature = 0; feature < x2D[label].length; feature++) {
				WeightIndex index = new WeightIndex(label, feature);
				if (mapWeights.containsKey(index)) {
					x2D[label][feature] = (double) mapWeights.get(index) / (double) (numSample * numIteration);
				} else {
					// x2D[label][feature] = 0;
				}
			}
		}

		return x2D;
	}

	public double[] to1D() {
		double[] x1D = new double[labelIndex.size() * featureIndex.size()];

		int i = 0;
		for (int label = 0; label < labelIndex.size(); label++) {
			for (int feature = 0; feature < featureIndex.size(); feature++) {
				WeightIndex index = new WeightIndex(label, feature);
				if (mapWeights.containsKey(index)) {
					x1D[i++] = mapWeights.get(index);
				}
				else {
					//x1D[i++] = 0;
				}
			}
		}

		return x1D;
	}
	
	/**
	 * Weight index.
	 */
	static class WeightIndex implements Serializable {

		private static final long serialVersionUID = -8983394344167645192L;
		int label;
		int feature;
		
		public WeightIndex(int label, int feature) {
			this.label = label;
			this.feature = feature;
		}
		
		@Override
		public boolean equals(Object obj) {
			WeightIndex other = (WeightIndex) obj;
			
			if (other.label == this.label && other.feature == this.feature) {
				return true;
			}
			return false;
		}
		
		@Override
	  public int hashCode() {
	    return ("" + label + "").hashCode() + ("" + feature + "").hashCode();
	  }
		
		@Override
		public String toString() {
			return "[" + label + "][" + feature + "]";
		}
	}

}
