package com.yglab.nlp.parser;

import java.util.List;

import com.yglab.nlp.model.EventStream;



/**
 * This class handles parse sample events stream.
 * 
 * @author Younggue Bae
 */
public class ParseSampleEventStream implements EventStream<ParseSample, Parse> {

	private List<ParseSample> samples;
	
	public ParseSampleEventStream(List<ParseSample> samples) {
		this.samples = samples;
	}
	
	@Override
	public List<ParseSample> getInputStream() {
		return this.samples;
	}
	
	@Override
	public List<Parse> getOutputStream() {

		return null;
	}
	
}
