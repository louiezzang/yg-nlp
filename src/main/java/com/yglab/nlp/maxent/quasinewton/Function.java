package com.yglab.nlp.maxent.quasinewton;

/**
 * Interface for a function.
 * 
 * @author Younggue Bae
 */
public interface Function {

	/**
	 * Returns the value of the function at a single point.
	 * 
	 * @param x
	 *          a <code>double[]</code> input
	 * @return the function value at the input
	 */
	public double valueAt(double[] x);

	/**
	 * Returns the number of dimensions in the function's domain
	 * 
	 * @return the number of domain dimensions
	 */
	public int domainDimension();

}
