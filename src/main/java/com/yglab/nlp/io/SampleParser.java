package com.yglab.nlp.io;

import com.yglab.nlp.util.InvalidFormatException;

/**
 * Interface for parsing train sample data.
 * 
 * @author Younggue Bae
 */
public interface SampleParser<T> {

	public T parse(String sentence) throws InvalidFormatException;
}
