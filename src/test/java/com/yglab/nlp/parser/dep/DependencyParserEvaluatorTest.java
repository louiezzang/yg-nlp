package com.yglab.nlp.parser.dep;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.io.CoNLLReader;
import com.yglab.nlp.perceptron.PerceptronModel;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
@Ignore
public class DependencyParserEvaluatorTest {
	
	private static DependencyFeatureGenerator<ParseSample> featureGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureGenerator = new DefaultDependencyFeatureGenerator();
		
		train();
	}
	
	private static void train() throws Exception {
		CoNLLReader reader = new CoNLLReader();
		reader.startReading("./data/en/parser/en-parser-1-train.conll");
		List<ParseSample> trainSamples = DependencyParser.loadSamples(reader);
		String[] labels = reader.getLabels();
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.PERCEPTRON_ALGORITHM);
		options.put(Options.ITERATIONS, "5");
		
		AbstractModel model = DependencyParser.train(trainSamples, labels, featureGenerator, options);
		DependencyParser.saveModel(model, "./target/test-data/en/parser/en-parser-model-1.bin", "./target/test-data/en/parser/en-parser-model-1.txt");	
	}
	
	@Test
	public void testEvaluator() throws Exception {
		CoNLLReader reader = new CoNLLReader();

		AbstractModel trainedModel = DependencyParser.loadModel("./target/test-data/en/parser/en-parser-model-1.bin");
		DependencyParser parser = new DependencyParser(trainedModel, featureGenerator);
		
		reader.startReading("./data/en/parser/en-parser-1-test.conll");
		List<ParseSample> testSamples = DependencyParser.loadSamples(reader);
		
		boolean labeled = ((PerceptronModel) trainedModel).isLabeled();
		//labeled = false;	// force to un-check if the predicted label is correct or not
		DependencyParserEvaluator evaluator = new DependencyParserEvaluator(parser, labeled);
		evaluator.evaluate(testSamples);
	}

}