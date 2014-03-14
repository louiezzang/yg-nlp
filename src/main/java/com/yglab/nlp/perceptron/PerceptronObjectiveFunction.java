package com.yglab.nlp.perceptron;

import java.util.List;

import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;



/**
 * Perceptron objective function.
 * The reference papers for this implementation are as belows:
 * "Discriminative training methods for hidden markov models: Theory and experiments with perceptron algorithms."
 * 	Michael Collins, EMNLP 2002 
 * 
 * @author Younggue Bae
 */
public class PerceptronObjectiveFunction<I, O extends Datum> {

	private EventStream<I, O> is;
	private PerceptronDecoder<I, O> decoder;
	private MutablePerceptronModel model;
	
	public PerceptronObjectiveFunction(EventStream<I, O> is, PerceptronDecoder<I, O> decoder) {
		this.is = is;
		this.decoder = decoder;
		this.model = (MutablePerceptronModel) decoder.getModel();
	}
	
	public MutablePerceptronModel calculateInit() {
		for (I instance : is.getInputStream()) {
			List<O> goldenStructures = decoder.getGoldenStructures(instance);
			
			for (O structure : goldenStructures) {
				System.out.println("  " + structure);
				String label = null;
				if (decoder.isLabeled()) {
					label = structure.getLabel();
				}

				List<String> features = structure.getFeatures();
				for (String feature : features) {
					model.addWeight(label, feature, 1);
				}
			}
		}
		
		return model;
	}
	
	public MutablePerceptronModel calculate() {
		for (I instance : is.getInputStream()) {
			List<O> bestStructures = decoder.decode(instance);
			
			for (O structure : bestStructures) {
				System.out.println("  " + structure);
				O goldStructure = decoder.getGoldenStructure(structure, instance);
				String label = null;
				if (decoder.isLabeled()) {
					label = structure.getLabel();
				}

				List<String> features = structure.getFeatures();
				// If parse item is gold, do nothing.
				if (structure.isGold()) {
					//for (String feature : features) {
					//	model.addWeight(label, feature, 1);
					//}
				}
				// If parse item is not gold.
				else {
					// Give a negative penalty to the bad features of this bad parse item.
					for (String feature : features) {
						model.addWeight(label, feature, -1);
					}
					// Give a positive weight to the actual golden features about this bad parse item
					for (String feature : goldStructure.getFeatures()) {
						model.addWeight(goldStructure.getLabel(), feature, 1);
					}
				}
			}
		}
		
		return model;
	}
	
	public MutablePerceptronModel calculate_typeA() {
		for (I instance : is.getInputStream()) {
			List<O> bestStructures = decoder.decode(instance);
			
			for (O structure : bestStructures) {
				System.out.println("  " + structure);
				//O goldStructure = decoder.getGoldenStructure(structure, instance);
				String label = null;
				if (decoder.isLabeled()) {
					label = structure.getLabel();
				}

				List<String> features = structure.getFeatures();
				if (structure.isGold()) {
					for (String feature : features) {
						model.addWeight(label, feature, 1);
					}
				}
				else {
					for (String feature : features) {
						model.addWeight(label, feature, -1);
					}
					//for (String feature : goldStructure.getFeatures()) {
					//	model.addWeight(goldStructure.getLabel(), feature, 1);
					//}
				}
			}
		}
		
		return model;
	}
	
	public MutablePerceptronModel calculate_typeB() {
		for (I instance : is.getInputStream()) {
			List<O> bestStructures = decoder.decode(instance);
			
			for (O structure : bestStructures) {
				System.out.println("  " + structure);
				O goldStructure = decoder.getGoldenStructure(structure, instance);
				String label = null;
				if (decoder.isLabeled()) {
					label = structure.getLabel();
				}

				List<String> features = structure.getFeatures();
				if (structure.isGold()) {
					for (String feature : features) {
						model.addWeight(label, feature, 1);
					}
				}
				else {
					for (String feature : features) {
						model.addWeight(label, feature, -1);
					}
					for (String feature : goldStructure.getFeatures()) {
						model.addWeight(goldStructure.getLabel(), feature, 1);
					}
				}
			}
		}
		
		return model;
	}
	
	public MutablePerceptronModel calculate_typeC() {
		for (I instance : is.getInputStream()) {
			List<O> bestStructures = decoder.decode(instance);
			
			for (O structure : bestStructures) {
				System.out.println("  " + structure);
				O goldStructure = decoder.getGoldenStructure(structure, instance);
				String label = null;
				if (decoder.isLabeled()) {
					label = structure.getLabel();
				}

				List<String> features = structure.getFeatures();
				if (structure.isGold()) {
					//for (String feature : features) {
					//	model.addWeight(label, feature, 1);
					//}
				}
				else {
					for (String feature : features) {
						model.addWeight(label, feature, -1);
					}
					for (String feature : goldStructure.getFeatures()) {
						model.addWeight(goldStructure.getLabel(), feature, 1);
					}
				}
			}
		}
		
		return model;
	}

}
