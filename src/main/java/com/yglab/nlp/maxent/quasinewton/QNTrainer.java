package com.yglab.nlp.maxent.quasinewton;

import java.util.List;

import com.yglab.nlp.maxent.MaxentModel;
import com.yglab.nlp.model.Datum;



/**
 * Maxent model trainer by using L-BFGS algorithm.
 * 
 * @author Younggue Bae
 */
public class QNTrainer {

	/**
	 * Trains the train data and returns the trained model.
	 * 
	 * @param trainData
	 * @return the trained model
	 */
	public MaxentModel trainModel(List<? extends Datum> trainData) {
		LogConditionalObjectiveFunction obj = new LogConditionalObjectiveFunction(trainData);
		double[] initial = new double[obj.domainDimension()];

		QNMinimizer minimizer = new QNMinimizer(15);
		double[][] weights = obj.to2D(minimizer.minimize(obj, 1e-4, initial, -1, null));

		return new MaxentModel(obj.labelIndex, obj.featureIndex, weights);
	}

}
