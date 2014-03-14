package com.yglab.nlp.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * This abstract class writes the training model file.
 * 
 * @author Younggue Bae
 */
public abstract class AbstractModelWriter<V> {

	public void write(V model, File file) throws IOException {
		mkdirs(file.getPath());
		
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));

    os.writeObject(model);
    os.flush();
    os.close();
    
    System.err.println("Wrote the trained model into object: " + file.getPath());
	}
	
	protected static void mkdirs(String filename) {
		String strDir = filename.substring(0, filename.lastIndexOf(File.separator));

		File dir = new File(strDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

}
