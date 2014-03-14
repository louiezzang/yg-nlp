package com.yglab.nlp.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading the train sample data.
 * 
 * @author Younggue Bae
 */
public class SampleReader<T> {

	protected SampleParser<T> parser;
	
	public SampleReader(SampleParser<T> parser) {
		this.parser = parser;
	}
	
	public List<T> load(String filename) throws IOException {
		List<T> data = new ArrayList<T>();
		BufferedReader in = new BufferedReader(new FileReader(filename));

		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.trim().length() == 0) {
				continue;
			}
			T sentenceSample = parser.parse(line);
			data.add(sentenceSample);
		}
		in.close();

		return data;
	}

}
