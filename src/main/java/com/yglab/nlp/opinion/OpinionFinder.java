package com.yglab.nlp.opinion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameSample;
import com.yglab.nlp.ner.NameSampleParser;
import com.yglab.nlp.ner.NamedEntityRecognizer;
import com.yglab.nlp.tokenizer.Tokenizer;
import com.yglab.nlp.util.Span;

/**
 * The class trains the opinion finder and extracts the opinions from the sentence.
 * 
 * @author Younggue Bae
 */
public class OpinionFinder extends NamedEntityRecognizer {

	protected Tokenizer tokenizer;
	protected TokenPostagPairGenerator tokenPairGenerator;

	/**
	 * Initializes the opinion finder with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 * @param tokenizer
	 * @param tokenPairGenerator
	 */
	public OpinionFinder(AbstractModel model, NameFeatureGenerator featureGenerator,
			Tokenizer tokenizer, TokenPostagPairGenerator tokenPairGenerator) {
		super(model, featureGenerator);

		
		this.tokenizer = tokenizer;
		this.tokenPairGenerator = tokenPairGenerator;
	}

	/**
	 * Tokenizes the input string.
	 * 
	 * @param s
	 *          the input string
	 * @return the array of span with start and end position for the input string
	 */
	public Span[] tokenize(String s) {
		return tokenizer.tokenizePos(s);
	}

	/**
	 * Finds the opinion target(entity, aspect) and opinion word for the given input string.
	 * 
	 * @param s
	 *          the input string
	 * @return the array of span with start and end position of the named entities which were found in
	 *         the given input string
	 */
	public Span[] find(String s) {
		List<Span> nameSpans = new ArrayList<Span>();
		
		Span[] tokenSpans = this.tokenize(s);
		String[] tokens = Span.spansToStrings(tokenSpans, s);
		tokens = this.tokenPairGenerator.generate(tokens);
		
		Span[] nameTokenSpans = this.findMaxent(tokens);
		
		for (Span nameTokenSpan : nameTokenSpans) {
			int start = nameTokenSpan.getStart();
			int end = nameTokenSpan.getEnd();
			String type = nameTokenSpan.getType();

			int origNameStart = tokenSpans[start].getStart();
			int origNameEnd = tokenSpans[end - 1].getEnd();
			Span nameSpan = new Span(origNameStart, origNameEnd, type);
			nameSpans.add(nameSpan);			
		}
		
		return nameSpans.toArray(new Span[nameSpans.size()]);
	}
	
	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param filename	The train sample file
	 * @param tokenPairGenerator	The token with postag pair generator
	 * @return
	 * @throws IOException
	 */
	public static final List<NameSample> loadSamples(String filename, TokenPostagPairGenerator tokenPairGenerator) throws IOException {
		List<NameSample> samples = new ArrayList<NameSample>();
		NameSampleParser parser = new NameSampleParser();
		BufferedReader in = null;
		InputStream is = OpinionFinder.class.getResourceAsStream(filename);

		if (is != null) {
			in = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
		}

		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.trim().length() == 0) {
				continue;
			}
			NameSample sample = parser.parse(line);
			String[] tokens = sample.getSentence();
			tokens = tokenPairGenerator.generate(tokens);
			sample.setSentence(tokens);
			
			samples.add(sample);
		}
		in.close();

		return samples;
	}

}
