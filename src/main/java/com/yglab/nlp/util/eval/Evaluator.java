package com.yglab.nlp.util.eval;

import java.util.List;

/**
 * The interface for evaluators.
 * 
 * @author Younggue Bae
 */
public interface Evaluator<T> {

	public void evaluate(List<T> samples);
}
