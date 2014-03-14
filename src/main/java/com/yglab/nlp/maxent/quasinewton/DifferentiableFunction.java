package com.yglab.nlp.maxent.quasinewton;

/**
 * Interface for a function that can be differentiated once.
 * 
 * @author Younggue Bae
 */
public interface DifferentiableFunction extends Function {

	/**
	 * Returns the first-derivative vector at the input location.
	 * 
	 * @param x
	 *          a <code>double[]</code> input vector
	 * @return the vector of first partial derivatives.
	 */
	public double[] derivativeAt(double[] x);
}
