package com.yglab.nlp.sbd;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.io.SampleParser;
import com.yglab.nlp.tokenizer.WhitespaceTokenizer;
import com.yglab.nlp.util.InvalidFormatException;
import com.yglab.nlp.util.Span;

/**
 * The parser for sentence detector sample.
 * 
 * @author Younggue Bae
 */
public class SentenceSampleParser implements SampleParser<SentenceSample> {

	private WhitespaceTokenizer tokenizer;

	public SentenceSampleParser() {
		tokenizer = new WhitespaceTokenizer();
	}

	@Override
	public SentenceSample parse(String document) throws InvalidFormatException {
		String[] sentences = document.split("\n");

		List<String> tokens = new ArrayList<String>();
		List<Span> sentenceSpans = new ArrayList<Span>();

		int index = 0;
		for (String sentence : sentences) {
			String[] parts = tokenizer.tokenize(sentence);
			for (int pi = 0; pi < parts.length; pi++) {
				tokens.add(parts[pi]);
				if (pi == parts.length - 1) {
					sentenceSpans.add(new Span(index, index + 1));
				}
				index++;
			}
		}

		return new SentenceSample(tokens.toArray(new String[tokens.size()]),
				sentenceSpans.toArray(new Span[sentenceSpans.size()]));
	}

}
