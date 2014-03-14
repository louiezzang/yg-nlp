package com.yglab.nlp.parser.dep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.io.AbstractModelReader;
import com.yglab.nlp.io.AbstractModelWriter;
import com.yglab.nlp.io.AbstractPlainTextWriter;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.ParseSampleEventStream;
import com.yglab.nlp.parser.Parser;
import com.yglab.nlp.parser.io.CONLLReader;
import com.yglab.nlp.perceptron.MutablePerceptronModel;
import com.yglab.nlp.perceptron.PerceptronDecoder;
import com.yglab.nlp.perceptron.PerceptronModel;
import com.yglab.nlp.perceptron.PerceptronModelPlainTextWriter;
import com.yglab.nlp.perceptron.PerceptronModelReader;
import com.yglab.nlp.perceptron.PerceptronModelWriter;
import com.yglab.nlp.perceptron.PerceptronTrainer;


/**
 * Dependency parser based on the perceptron algorithm. 
 * The reference papers for this implementation are as belows:
 * "Discriminative training methods for hidden markov models: Theory and experiments with perceptron algorithms."
 * 	Michael Collins, EMNLP 2002 
 * "Non-projective dependency parsing using spanning tree algorithms." 
 * 	Ryan. McDonald & Fernando Pereira, 2005
 * 
 * @author Younggue Bae
 */
public class DependencyParser implements Parser {

	private PerceptronModel model;
	private DependencyFeatureGenerator<ParseSample> featureGenerator;

	/**
	 * Initializes the dependency parser with the specified model.
	 * 
	 * @param model
	 * @param featureGenerator
	 */
	public DependencyParser(AbstractModel model, DependencyFeatureGenerator<ParseSample> featureGenerator) {
		this.model = new PerceptronModel(model.getLabelIndex(), model.getFeatureIndex(), model.getWeights());
		this.featureGenerator = featureGenerator;
	}

	/**
	 * Trains perceptron algorithm with the given train samples.
	 * 
	 * @param trainSamples	The train samples
	 * @param labels	The unique labels
	 * @param featureGenerator	The context feature generator
	 * @param options	The options
	 * @return
	 */
	public static AbstractModel train(List<ParseSample> trainSamples, String[] labels, 
			DependencyFeatureGenerator<ParseSample> featureGenerator, Options options)  {
		
		int iterations = Integer.parseInt(options.get(Options.ITERATIONS));
		
		boolean labeled = false;
		if (labels != null) {
			labeled = true;
		}
		
		System.out.println("labeled == " + labeled);
		
		PerceptronModel model = new MutablePerceptronModel(labels, labeled);
		PerceptronDecoder<ParseSample, Parse> decoder = new EisnerDependencyDecoder(featureGenerator, model, labeled);
		PerceptronTrainer<ParseSample, Parse> trainer = new PerceptronTrainer<ParseSample, Parse>(decoder);
		EventStream<ParseSample, Parse> stream = new ParseSampleEventStream(trainSamples);

		return trainer.trainModel(stream, iterations);
	}
	
	/**
	 * Parses a given sentence into the dependency syntactic tree.
	 * 
	 * @param tokens The tokens sequence of a sentence
	 * @return
	 */
	public List<Parse> parse(String[] tokens) {
		return null;
	}
	
	/**
	 * Parses a given instance into the dependency syntactic tree.
	 * 
	 * @param instance The parse sample instance
	 * @param K	The K-best parses
	 * @return
	 */
	public List<List<Parse>> parse(ParseSample instance, int K) {
		boolean labeled = model.isLabeled();
		//Index labelIndex = model.getLabelIndex();
		//if (labelIndex == null || labelIndex.size() == 0 || (labelIndex.size() == 1 && labelIndex.get(0).equals("<no-type>"))) {
		//	labeled = false;
		//}
		System.out.println("labeled == " + labeled);
		
		PerceptronDecoder<ParseSample, Parse> decoder = new EisnerDependencyDecoder(featureGenerator, model, labeled);
		List<List<Parse>> kBestParses = decoder.decode(instance, K);
		
		return kBestParses;
	}
	
	/**
	 * Loads train sample list from sample file.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public static final List<ParseSample> loadSamples(CONLLReader reader) throws IOException {
		List<ParseSample> samples = new ArrayList<ParseSample>();
		
		ParseSample instance = reader.getNext();

		while (instance != null) {
			samples.add(instance);
			instance = reader.getNext();
		}

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
		AbstractModelWriter<AbstractModel> writer = new PerceptronModelWriter();
		AbstractPlainTextWriter<AbstractModel> plainTextWriter = new PerceptronModelPlainTextWriter();
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
		AbstractModelReader<AbstractModel> reader = new PerceptronModelReader();
		AbstractModel model = reader.read(new File(file));
		
		return model;
	}

}
