package com.yglab.nlp.tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.yglab.nlp.io.AbstractModelReader;
import com.yglab.nlp.io.AbstractModelWriter;
import com.yglab.nlp.io.AbstractPlainTextWriter;
import com.yglab.nlp.maxent.MEMM;
import com.yglab.nlp.maxent.MaxentModelPlainTextWriter;
import com.yglab.nlp.maxent.MaxentModelReader;
import com.yglab.nlp.maxent.MaxentModelWriter;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.ner.NamedEntityRecognizer;
import com.yglab.nlp.util.Span;

/**
 * This tokenizer uses maxent to tokenize the input text.
 * 
 */
public class MaxentTokenizer extends AbstractTokenizer {

  public static final String SPLIT ="S";
  public static final String NO_SPLIT ="O";
  public static final Pattern skipPattern = Pattern.compile("^[A-Za-z0-9가-힣]+$");
  
  private AbstractModel model;
	private TokenFeatureGenerator featureGenerator;
	private WhitespaceTokenizer whitespaceTokenizer;
  
	/**
	 * Initializes the tokenizer with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 */
	public MaxentTokenizer(AbstractModel model, TokenFeatureGenerator featureGenerator) {
		this.model = model;
		this.featureGenerator = featureGenerator;
		this.whitespaceTokenizer = new WhitespaceTokenizer();
	}
	
	/**
	 * Trains maximum-entropy markov model or perceptron algorithm with the given train samples.
	 * 
	 * @param trainSamples	The train samples
	 * @param featureGenerator	The context feature generator
	 * @param options	The options
	 * @return
	 */
	public static AbstractModel train(List<TokenSample> trainSamples, TokenFeatureGenerator featureGenerator, Options options) {
	
		if (options.get(Options.ALGORITHM).equals(Options.MAXENT_ALGORITHM)) {
			return trainMaxent(trainSamples, featureGenerator, options);
		}
		else if (options.get(Options.ALGORITHM).equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		return null;
	}
	
	private static AbstractModel trainMaxent(List<TokenSample> trainSamples, TokenFeatureGenerator featureGenerator, Options options) {
		
		AbstractModel model = null;
		
		if (options.getBoolean("useSkipPattern") == true) {
			EventStream<TokenSample, Datum> stream = new TokenSampleEventStream(featureGenerator, trainSamples, skipPattern);
			model = MEMM.trainModel(stream);
			model.setOption("useSkipPattern", "true");
		}
		else {
			EventStream<TokenSample, Datum> stream = new TokenSampleEventStream(featureGenerator, trainSamples);
			model = MEMM.trainModel(stream);
			model.setOption("useSkipPattern", "false");
		}

		return model;
	}

	@Override
	public Span[] tokenizePos(String s) {
		if (this.model.algorithm().equals(Options.MAXENT_ALGORITHM)) {
			return this.tokenizeMaxent(s);
		}
		else if (this.model.algorithm().equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		
		return null;
	}
	
	private Span[] tokenizeMaxent(String s) {
		List<Span> result = new ArrayList<Span>();
		Span[] tokens = whitespaceTokenizer.tokenizePos(s);
		
		boolean useSkipPattern = Boolean.parseBoolean(model.getOption("useSkipPattern"));

		for (int i = 0; i < tokens.length; i++) {
      Span span = tokens[i];
      String token = s.substring(span.getStart(), span.getEnd());
      
      if (token.length() < 2) {
      	result.add(span);
      }
      else if (useSkipPattern && skipPattern.matcher(token).matches()) {
      	result.add(span);
      }
      else {
        int start = span.getStart();
        int end = span.getEnd();
        final int origStart = span.getStart();
        for (int j = origStart + 1; j < end; j++) { 	
        	List<Datum> bestSequence = MEMM.decode(model, this.getCandidates(j - origStart, token));

        	for (int k = 0; k < bestSequence.size(); k++) {
        		Datum datum = bestSequence.get(k);
        		if (datum.getGuessLabel().equals(SPLIT)) {
        			result.add(new Span(start, j));
        			start = j;
        		}
        	}
        }
        result.add(new Span(start, end));
      }
		}
		
		return result.toArray(new Span[result.size()]);
	}
	
	private List<List<Datum>> getCandidates(int position, String token) {
		List<List<Datum>> instanceCandidates = new ArrayList<List<Datum>>();
		
		String[] features = featureGenerator.getFeatures(position, token);

		List<Datum> candidates = new ArrayList<Datum>();
		
		Datum datum1 = new Datum(token.substring(position + 1), SPLIT);
		datum1.setFeatures(Arrays.asList(features));
		datum1.setPreviousLabel(NO_SPLIT);
		candidates.add(datum1);
		
		Datum datum2 = new Datum(token.substring(position + 1), NO_SPLIT);
		datum2.setFeatures(Arrays.asList(features));
		datum2.setPreviousLabel(NO_SPLIT);
		candidates.add(datum2);
		
		Datum datum3 = new Datum(token.substring(position + 1), SPLIT);
		datum3.setFeatures(Arrays.asList(features));
		datum3.setPreviousLabel(SPLIT);
		candidates.add(datum3);
		
		Datum datum4 = new Datum(token.substring(position + 1), NO_SPLIT);
		datum4.setFeatures(Arrays.asList(features));
		datum4.setPreviousLabel(SPLIT);
		candidates.add(datum4);
		
		instanceCandidates.add(candidates);
		
		return instanceCandidates;
	}
	
	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param filename	The train sample file
	 * @return
	 * @throws IOException
	 */
	public static final List<TokenSample> loadSamples(String filename) throws IOException {
		List<TokenSample> samples = new ArrayList<TokenSample>();
		TokenSampleParser parser = new TokenSampleParser();
		BufferedReader in = null;
		InputStream is = NamedEntityRecognizer.class.getResourceAsStream(filename);

		if (is != null) {
			in = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
		}

		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.trim().length() == 0) {
				continue;
			}
			TokenSample sample = parser.parse(line);
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
