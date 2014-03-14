package com.yglab.nlp.perceptron;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;

/**
 * Perceptron trainer.
 * 
 * @author Younggue Bae
 */
public class PerceptronTrainer<I, O extends Datum> {

	private PerceptronDecoder<I, O> decoder;
	
	public PerceptronTrainer(PerceptronDecoder<I, O> decoder) {
		this.decoder = decoder;
	}
	
	/**
	 * Trains a perceptron model.
	 * 
	 * @param is
	 * @param iterations
	 */
	public AbstractModel trainModel(EventStream<I, O> is, int iterations) {
		PerceptronObjectiveFunction<I, O> perceptron = new PerceptronObjectiveFunction<I, O>(is, decoder);
		MutablePerceptronModel model = (MutablePerceptronModel) decoder.getModel();
		
		for (int i = 1; i <= iterations; i++) {
			System.out.println("========================");
			System.err.println("Iteration: " + i);
			System.out.println("========================");
			long start = System.currentTimeMillis();
			if (i == 1) {
				model = perceptron.calculateInit();
				//model = perceptron.calculate();
			}
			else {
				model = perceptron.calculate();
			}
			long end = System.currentTimeMillis();
			System.out.println("Time:" + (end - start));
		}
		
		double[][] weights = model.to2D();
			
		return new PerceptronModel(model.getLabelIndex(), model.getFeatureIndex(), weights, decoder.isLabeled());
	}
	
}
