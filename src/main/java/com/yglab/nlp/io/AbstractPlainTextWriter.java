package com.yglab.nlp.io;

import java.io.File;
import java.io.IOException;

/**
 * This abstract class writes the training model file into the plain text.
 * 
 * @author Younggue Bae
 */
public abstract class AbstractPlainTextWriter<V> {

	public abstract void write(V model, File file) throws IOException;
}
