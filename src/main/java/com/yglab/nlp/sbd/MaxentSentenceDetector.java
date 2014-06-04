package com.yglab.nlp.sbd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.io.AbstractModelReader;
import com.yglab.nlp.io.AbstractModelWriter;
import com.yglab.nlp.io.AbstractPlainTextWriter;
import com.yglab.nlp.maxent.DefaultTagSequenceGenerator;
import com.yglab.nlp.maxent.MEMM;
import com.yglab.nlp.maxent.MaxentModelPlainTextWriter;
import com.yglab.nlp.maxent.MaxentModelReader;
import com.yglab.nlp.maxent.MaxentModelWriter;
import com.yglab.nlp.maxent.TagSequenceGenerator;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.model.Span;
import com.yglab.nlp.tokenizer.WhitespaceTokenizer;

/**
 * This class detects the sentence boundaries for a document.
 * 
 * @author Younggue Bae
 */
public class MaxentSentenceDetector implements SentenceDetector {

	public static final String LABEL_EOS = "EOS";
	public static final String LABEL_OTHER = "O";
	
	private AbstractModel model;
	//private SentenceFeatureGenerator featureGenerator;
	private WhitespaceTokenizer whitespaceTokenizer;
	private TagSequenceGenerator gen;
  
	/**
	 * Initializes the sentence detector with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 */
	public MaxentSentenceDetector(AbstractModel model, SentenceFeatureGenerator featureGenerator) {
		this.model = model;
		//this.featureGenerator = featureGenerator;
		this.whitespaceTokenizer = new WhitespaceTokenizer();
		
		this.gen = new DefaultTagSequenceGenerator(featureGenerator, model.getLabels(), 2);
	}
	
	/**
	 * Trains maximum-entropy markov model or perceptron algorithm with the given train samples.
	 * 
	 * @param trainSamples	The train samples
	 * @param featureGenerator	The context feature generator
	 * @param options	The options
	 * @return
	 */
	public static AbstractModel train(List<SentenceSample> trainSamples, SentenceFeatureGenerator featureGenerator, Options options) {
	
		if (options.get(Options.ALGORITHM).equals(Options.MAXENT_ALGORITHM)) {
			return trainMaxent(trainSamples, featureGenerator, options);
		}
		else if (options.get(Options.ALGORITHM).equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		return null;
	}
	
	private static AbstractModel trainMaxent(List<SentenceSample> trainSamples, SentenceFeatureGenerator featureGenerator, Options options) {
		EventStream<SentenceSample, Datum> stream = new SentenceSampleEventStream(featureGenerator, trainSamples);

		return MEMM.trainModel(stream);
	}
	
	@Override
	public String[] detect(String s) {
		List<String> sentences = new ArrayList<String>();
		
		String[] tokens = whitespaceTokenizer.tokenize(s);
		Span[] spans = this.detect(tokens);
		
		List<Integer> eosPositions = new ArrayList<Integer>();
		for (Span span : spans) {
			eosPositions.add(span.getStart());
		}
		
		if (eosPositions.size() == 0) {
			sentences.add(s);
			return sentences.toArray(new String[sentences.size()]);
		}
		
		StringBuilder sbSentence = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			if (eosPositions.contains(i)) {
				sbSentence.append(tokens[i]);
				sentences.add(sbSentence.toString());
				sbSentence = new StringBuilder();
			}
			else {
				sbSentence.append(tokens[i]).append(" ");
			}
		}
		
		if (!sbSentence.toString().trim().equals("")) {
			sentences.add(sbSentence.toString());
		}
		
		return sentences.toArray(new String[sentences.size()]);
	}
	
	public Span[] detect(String[] tokens) {
		if (this.model.algorithm().equals(Options.MAXENT_ALGORITHM)) {
			return this.detectMaxent(tokens);
		}
		else if (this.model.algorithm().equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		
		return null;
	}
	
	private Span[] detectMaxent(String[] tokens) {
		List<List<Datum>> candidates = gen.getCandidates(tokens);
		List<Datum> bestSequence = MEMM.decode(model, candidates);

		List<Span> spans = new ArrayList<Span>(tokens.length);
		for (int li = 0; li < bestSequence.size(); li++) {
			Datum datum = bestSequence.get(li);
			String label = datum.getGuessLabel();
			if (label.equals(LABEL_EOS)) {
				spans.add(new Span(li, li + 1, LABEL_EOS));
			}
		}

		return spans.toArray(new Span[spans.size()]);
	}
  
	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param filename	The train sample file
	 * @return
	 * @throws IOException
	 */
	public static final List<SentenceSample> loadSamples(String filename) throws IOException {
		List<SentenceSample> samples = new ArrayList<SentenceSample>();
		SentenceSampleParser parser = new SentenceSampleParser();
		BufferedReader in = null;
		InputStream is = MaxentSentenceDetector.class.getResourceAsStream(filename);

		if (is != null) {
			in = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
		}

		StringBuilder sb = new StringBuilder();
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.trim().length() == 0) {
				SentenceSample sample = parser.parse(sb.toString());
				samples.add(sample);
				sb = new StringBuilder();
			}
			else {
				sb.append(line.trim()).append("\n");
			}
		}
		
		if (!sb.toString().trim().equals("")) {
			SentenceSample sample = parser.parse(sb.toString());
			samples.add(sample);
		}
		
		in.close();

		return samples;
	}
	
	/**
	 * Saves the trained model.
	 * 
	 * @param model
	 * @param binaryFile
	 * @param plainTextFile
	 * @throws IOException
	 */
	public static final void saveModel(AbstractModel model, String binaryFile, String plainTextFile) throws IOException {
		AbstractModelWriter<AbstractModel> writer = new MaxentModelWriter();
		AbstractPlainTextWriter<AbstractModel> plainTextWriter = new MaxentModelPlainTextWriter();
		writer.write(model, new File(binaryFile));
		
		if (plainTextFile != null) {
			plainTextWriter.write(model, new File(plainTextFile));
		}
	}
	
	/**
	 * Loads the trained model.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final AbstractModel loadModel(String file) throws IOException, ClassNotFoundException {
		AbstractModelReader<AbstractModel> reader = new MaxentModelReader();
		AbstractModel model = reader.read(new File(file));
		
		return model;
	}

}
