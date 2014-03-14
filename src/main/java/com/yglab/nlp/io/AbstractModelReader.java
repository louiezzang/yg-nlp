package com.yglab.nlp.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * This abstract class reads the training model file.
 * 
 * @author Younggue Bae
 */
public abstract class AbstractModelReader<V> {

	@SuppressWarnings("unchecked")
	public V read(File file) throws IOException, ClassNotFoundException {
		
		ObjectInputStream ois = null;
		InputStream is = AbstractModelReader.class.getResourceAsStream(file.getPath());

		if (is != null) {
			ois = new ObjectInputStream(is);
		} else {
			ois = new ObjectInputStream(new FileInputStream(file));
		}
	
		V model = (V) ois.readObject();
		ois.close();
		
		System.err.println("Read the trained model from object: " + file.getPath());

		return model;
	}

}
