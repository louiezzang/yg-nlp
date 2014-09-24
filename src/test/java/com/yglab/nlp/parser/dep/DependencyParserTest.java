package com.yglab.nlp.parser.dep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.io.CoNLLReader;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
@Ignore
public class DependencyParserTest {
	
	private static DefaultDependencyFeatureGenerator featureGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureGenerator = new DefaultDependencyFeatureGenerator();
		
		train();
	}
	
	@SuppressWarnings("unused")
	private static void train() throws Exception {
		List<ParseSample> trainSamples1 = new ArrayList<ParseSample>();
		String[] labels1 = { "DEP", "NP-SBJ", "VP", "NP-OBJ" };
		
		String[] forms1 = { "<root>", "the", "dog", "barks" };
		String[] cpostags1 = { "<root-CPOS>", "DT", "NN", "VBD" };
		String[] postags1 = { "<root-POS>", "DT", "NN", "VBD" };
		String[] deprels1 = { "<no-type>", "DEP", "NP-SBJ", "ROOT" };
		int[] heads1 = { -1, 2, 3, 0 };
		
		ParseSample sample1 = new ParseSample(forms1, forms1, cpostags1, postags1, deprels1, heads1);
		trainSamples1.add(sample1);
		
		String[] forms2 = { "<root>", "the", "dog", "eats", "food" };
		String[] lemmas2 = { "<root-LEMMA>", "the", "dog", "eat", "food" };
		String[] cpostags2 = { "<root-CPOS>", "DT", "NN", "VBP", "NN" };
		String[] postags2 = { "<root-POS>", "DT", "NN", "VBP", "NN" };
		String[] deprels2 = { "<no-type>", "DEP", "SBJ", "ROOT", "NP-OBJ" };
		int[] heads2 = { -1, 2, 3, 0, 3, 3 };
		ParseSample sample2 = new ParseSample(forms2, lemmas2, cpostags2, postags2, deprels2, heads2);
		trainSamples1.add(sample2);
		
		CoNLLReader reader = new CoNLLReader();
		reader.startReading("/sample/en/parser/en-parser-train.conll");
		List<ParseSample> trainSamples2 = DependencyParser.loadSamples(reader);
		String[] labels2 = reader.getLabels();
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.PERCEPTRON_ALGORITHM);
		options.put(Options.ITERATIONS, "5");
		
		//AbstractModel model = DependencyParser.train(trainSamples1, labels1, featureGenerator, options);
		AbstractModel model = DependencyParser.train(trainSamples2, labels2, featureGenerator, options);
		DependencyParser.saveModel(model, "./target/test-data/en/parser/en-parser-test-model.bin", "./target/test-data/en/parser/en-parser-test-model.txt");
	}
	
	@Test
	public void testParser() throws Exception {
		System.out.println("===============================================");

		AbstractModel trainedModel = DependencyParser.loadModel("./target/test-data/en/parser/en-parser-test-model.bin");
		DependencyParser parser = new DependencyParser(trainedModel, featureGenerator);
		
		CoNLLReader reader = new CoNLLReader();
		reader.startReading("/sample/en/parser/en-parser-test.conll");
		List<ParseSample> testSamples = DependencyParser.loadSamples(reader);

		for (ParseSample testSample : testSamples) {
			List<List<Parse>> kBestParses = parser.parse(testSample, 1);
			List<Parse> bestParse = kBestParses.get(0);
			Collections.sort(bestParse);
			
			for (Parse parse : bestParse) {
				System.out.println(parse);
			}
		}
	}
}