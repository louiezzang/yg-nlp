package com.yglab.nlp.maxent.quasinewton;

/**
 * Interface for unconstrained function minimizers.
 * 
 * @author Younggue Bae
 */
public interface Minimizer<T extends Function> {

	/**
	 * Attempts to find an unconstrained minimum of the objective <code>function</code> starting at <code>initial</code>,
	 * within <code>functionTolerance</code>.
	 * 
	 * @param function
	 *          the objective function
	 * @param functionTolerance
	 *          a <code>double</code> value
	 * @param initial
	 *          a initial feasible point
	 * @return Unconstrained minimum of function
	 */
	public double[] minimize(T function, double functionTolerance, double[] initial);

	public double[] minimize(T function, double functionTolerance, double[] initial, int maxIterations);

}
