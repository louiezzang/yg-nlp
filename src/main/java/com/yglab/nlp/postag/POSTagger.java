package com.yglab.nlp.postag;

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



/**
 * POS tagger based on maximum-entropy markov model or perceptron algorithm.
 * 
 * @author Younggue Bae
 */
public class POSTagger {
	
	protected AbstractModel model;
	protected POSFeatureGenerator featureGenerator;

	/**
	 * Initializes the pos tagger with the specified model.
	 * 
	 * @param model	The trained model
	 * @param featureGenerator	The context feature generator
	 */
	public POSTagger(AbstractModel model, POSFeatureGenerator featureGenerator) {
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
	public static AbstractModel train(List<POSSample> trainSamples, POSFeatureGenerator featureGenerator, Options options) {
		if (options.get(Options.ALGORITHM).equals(Options.MAXENT_ALGORITHM)) {
			return trainMaxent(trainSamples, featureGenerator, options);
		}
		else if (options.get(Options.ALGORITHM).equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		return null;
	}
	
	private static AbstractModel trainMaxent(List<POSSample> trainSamples, POSFeatureGenerator featureGenerator, Options options) {
		EventStream<POSSample, Datum> stream = new POSSampleEventStream(featureGenerator, trainSamples);
		
		return MEMM.trainModel(stream);
	}
	
	/**
	 * Puts the pos tags on the given sentence sequence.
	 * 
	 * @param instance The pos sample instance
	 * @return
	 */
	public String[] tag(POSSample instance) {
		return this.tag(instance.getSentence());
	}
	
	/**
	 * Puts the pos tags on the given sentence sequence.
	 * 
	 * @param tokens The tokens sequence of a sentence
	 * @return
	 */
	public String[] tag(String[] tokens) {
		if (this.model.algorithm().equals(Options.MAXENT_ALGORITHM)) {
			return this.tagMaxent(tokens);
		}
		else if (this.model.algorithm().equals(Options.PERCEPTRON_ALGORITHM)) {
			// TODO
			return null;
		}
		
		return null;		
	}


	public String[] tagMaxent(String[] tokens) {
		Index labelIndex = model.getLabelIndex();
		String[] labels = new String[labelIndex.size()];
		for (int i = 0; i < labelIndex.size(); i++) {
			labels[i] = labelIndex.get(i).toString();
		}
		LabelSequenceGEN gen = new LabelSequenceGEN(featureGenerator, labels);
		List<List<Datum>> candidates = gen.getCandidates(tokens, 2);
		List<Datum> bestSequence = MEMM.decode(model, candidates);

		List<String> guessTags = new ArrayList<String>();
		for (Datum datum : bestSequence) {
			guessTags.add(datum.getGuessLabel());
		}
    return guessTags.toArray(new String[guessTags.size()]);
	}
	
	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param filename	The train sample file
	 * @param tagReplaceRegex	The regex pattern to replace on tags
	 * @param tagReplacement	The string to replace on the given regex pattern 
	 * @return
	 * @throws IOException
	 */
	public static final List<POSSample> loadSamples(String filename, String tagReplaceRegex, String tagReplacement) throws IOException {
		List<POSSample> samples = new ArrayList<POSSample>();
		POSSampleParser parser = new POSSampleParser();
		BufferedReader in = null;
		InputStream is = POSTagger.class.getResourceAsStream(filename);

		if (is != null) {
			in = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
		}

		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.trim().length() == 0) {
				continue;
			}
			POSSample sample = parser.parse(line);
			if (tagReplaceRegex != null && tagReplacement != null) {
				sample.replaceAllLabels(tagReplaceRegex, tagReplacement);
			}
			samples.add(sample);
		}
		in.close();

		return samples;
	}

	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param filename	The train sample file
	 * @return
	 * @throws IOException
	 */
	public static final List<POSSample> loadSamples(String filename) throws IOException {
		return loadSamples(filename, null, null);
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