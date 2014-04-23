package com.yglab.nlp.postag;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class POSTaggerTest {
	
	private static POSFeatureGenerator featureGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureGenerator = new DefaultPOSFeatureGenerator();
		
		train();
	}

	private static void train() throws Exception {
		List<POSSample> trainSamples = POSTagger.loadSamples("/sample/en/pos/en-pos-train.txt", "[^\\+/\\(\\)]*/", "");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = POSTagger.train(trainSamples, featureGenerator, options);
		
		POSTagger.saveModel(model, "./target/test-data/en/pos/en-pos-default-model.bin", "./target/test-data/en/pos/en-pos-default-model.txt");		
	}
	
	@Test
	public void testTagger() throws Exception {
		String[] tokens = {
				"anxiety",
				"is",
				"rising",
				"."
		};
		
		System.out.println("tokens to test = " + tokens.length);
		
		AbstractModel trainModel = POSTagger.loadModel("./target/test-data/en/pos/en-pos-default-model.bin");
		POSTagger tagger = new POSTagger(trainModel, featureGenerator);
		
		System.out.println("==================================================");
		
		String[] result = tagger.tag(tokens);
		
		for (int i = 0; i < result.length; i++) {
			String tag = result[i];
			System.out.println(i + ": " + tokens[i] + "\t[" + tag + "]");
		}
	}
	
	@Test
	public void testEvaluator() throws Exception {
		AbstractModel trainModel = POSTagger.loadModel("./target/test-data/en/pos/en-pos-default-model.bin");
		
		POSTagger tagger = new POSTagger(trainModel, featureGenerator);
		
		List<POSSample> testSamples = POSTagger.loadSamples("/sample/en/pos/en-pos-test.txt", "[^\\+/\\(\\)]*/", "");
		POSTaggerEvaluator evaluator = new POSTaggerEvaluator(tagger);
		evaluator.evaluate(testSamples);
		
		evaluator.print();
	}
}