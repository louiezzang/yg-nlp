package com.yglab.nlp.util.corpus;

import java.io.IOException;

import com.yglab.nlp.parser.ParseSample;

/**
 * The interface for reading the treebank file format.
 * 
 * @author Younggue Bae
 */
public interface TreebankReader {

	public boolean startReading(String file) throws IOException;
	
	public ParseSample getNext() throws IOException;
}
