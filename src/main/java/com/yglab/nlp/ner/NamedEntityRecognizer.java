package com.yglab.nlp.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.io.AbstractModelReader;
import com.yglab.nlp.io.AbstractModelWriter;
import com.yglab.nlp.io.AbstractPlainTextWriter;
import com.yglab.nlp.maxent.LabelSequenceGEN;
import com.yglab.nlp.maxent.MEMM;
import com.yglab.nlp.maxent.MaxentModelPlainTextWriter;
import com.yglab.nlp.maxent.MaxentModelReader;
import com.yglab.nlp.maxent.MaxentModelWriter;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.model.Index;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.util.Span;

/**
 * Class for recognizing named entity based on maximum-entropy markov model or perceptron algorithm.
 * 
 * @author Younggue Bae
 */
public class NamedEntityRecognizer {

	public static final String LABEL_START = "start";
	public static final String LABEL_CONTINUE = "cont";
	public static final String LABEL_OTHER = "other";
	private static final Pattern typedOutcomePattern = Pattern.compile("(.+)-\\w+");

	protected AbstractModel model;
	protected NameFeatureGenerator featureGenerator;

	/**
	 * Initializes the named entity recognizer with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 */
	public NamedEntityRecognizer(AbstractModel model, NameFeatureGenerator featureGenerator) {
		this.model = model;
		this.featureGenerator = featureGenerator;
	}

	/**
	 * Trains maximum-entropy markov model or perceptron algorithm with the given train samples.
	 * 
	 * @param trainSamples	The train samples
	 * @param featureGenerator	The context feature generator
	 * @param options	The options
	 * @return
	 */
	public static AbstractModel train(List<NameSample> trainSamples, NameFeatureGenerator featureGenerator, Options options) {
	
		if (options.get(Options.ALGORITHM).equals(Options.MAXENT_ALGORITHM)) {
			return trainMaxent(trainSamples, featureGenerator, options);
		}
		else if (options.get(Options.ALGORITHM).equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		return null;
	}
	
	private static AbstractModel trainMaxent(List<NameSample> trainSamples, NameFeatureGenerator featureGenerator, Options options) {
		EventStream<NameSample, Datum> stream = new NameSampleEventStream(featureGenerator, trainSamples);

		return MEMM.trainModel(stream);
	}

	/**
	 * Generates name tags for the given sentence sequence.
	 * This returns the spans which contain the recognized named entity.
	 * 
	 * @param instance The name sample instance
	 * @return
	 */
	public Span[] find(NameSample instance) {
		return this.find(instance.getSentence());
	}
	
	/**
	 * Finds name tags for the given sentence sequence.
	 * This returns the spans which contain the recognized named entity.
	 * 
	 * @param tokens
	 * @return the array of span with start and end position of the named entities which were found in
	 *         the given token array
	 */
	public Span[] find(String[] tokens) {
		if (this.model.algorithm().equals(Options.MAXENT_ALGORITHM)) {
			return this.findMaxent(tokens);
		}
		else if (this.model.algorithm().equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		
		return null;
	}
	
	/**
	 * Finds name tags for the given sentence sequence.
	 * This returns the spans which contain the recognized named entity.
	 * 
	 * @param s	The input string
	 * @param tokenSpans The spans of tokens for the input string
	 * @return the array of span with start and end position of the named entities which were found in
	 *         the given token array
	 */
	public Span[] find(String s, Span[] tokenSpans) {
		List<Span> nameSpans = new ArrayList<Span>();
		
		String[] tokens = Span.spansToStrings(tokenSpans, s);
		Span[] nameTokenSpans = this.find(tokens);
		
		for (Span nameTokenSpan : nameTokenSpans) {
			int start = nameTokenSpan.getStart();
			int end = nameTokenSpan.getEnd();
			String type = nameTokenSpan.getType();
			
			int origNameStart = tokenSpans[start].getStart();
			int origNameEnd = tokenSpans[end - 1].getEnd();
			nameSpans.add(new Span(origNameStart, origNameEnd, type));			
		}
		
		return nameSpans.toArray(new Span[nameSpans.size()]);
	}
	
	public Span[] findMaxent(String[] tokens) {
		Index labelIndex = model.getLabelIndex();
		String[] labels = new String[labelIndex.size()];
		for (int i = 0; i < labelIndex.size(); i++) {
			labels[i] = labelIndex.get(i).toString();
		}
		LabelSequenceGEN gen = new LabelSequenceGEN(featureGenerator, labels);
		List<List<Datum>> candidates = gen.getCandidates(tokens, 2);
		List<Datum> bestSequence = MEMM.decode(model, candidates);

		int start = -1;
		int end = -1;
		List<Span> spans = new ArrayList<Span>(tokens.length);
		for (int li = 0; li < bestSequence.size(); li++) {
			Datum datum = bestSequence.get(li);
			String chunkLabel = datum.getGuessLabel();
			if (chunkLabel.endsWith(LABEL_START)) {
				if (start != -1) {
					spans.add(new Span(start, end, extractNameType(bestSequence.get(li - 1).getGuessLabel())));
				}

				start = li;
				end = li + 1;

			} else if (chunkLabel.endsWith(LABEL_CONTINUE)) {
				end = li + 1;
			} else if (chunkLabel.endsWith(LABEL_OTHER)) {
				if (start != -1) {
					spans.add(new Span(start, end, extractNameType(bestSequence.get(li - 1).getGuessLabel())));
					start = -1;
					end = -1;
				}
			}
		}

		if (start != -1) {
			spans.add(new Span(start, end, extractNameType(bestSequence.get(bestSequence.size() - 1).getGuessLabel())));
		}

		return spans.toArray(new Span[spans.size()]);
	}

	/**
	 * Gets the name type from the outcome.
	 * 
	 * @param outcome
	 *          the outcome
	 * @return the name type, or null if not set
	 */
	protected static String extractNameType(String outcome) {
		Matcher matcher = typedOutcomePattern.matcher(outcome);
		if (matcher.matches()) {
			String nameType = matcher.group(1);
			return nameType;
		}

		return null;
	}

	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param filename	The train sample file
	 * @return
	 * @throws IOException
	 */
	public static final List<NameSample> loadSamples(String filename) throws IOException {
		List<NameSample> samples = new ArrayList<NameSample>();
		NameSampleParser parser = new NameSampleParser();
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
			NameSample sample = parser.parse(line);
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