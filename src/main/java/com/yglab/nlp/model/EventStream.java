package com.yglab.nlp.model;

import java.util.List;

/**
 * Interface for reading the input sample instances.
 * 
 * @author Younggue Bae
 */
public interface EventStream<I, O> {

	public List<I> getInputStream();
	
	public List<O> getOutputStream();
}
