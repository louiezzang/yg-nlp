package com.yglab.nlp.parser.dep.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.dep.DefaultDependencyFeatureGenerator;
import com.yglab.nlp.parser.dep.DependencyFeatureGenerator;
import com.yglab.nlp.parser.dep.DependencyParser;
import com.yglab.nlp.parser.dep.DependencyParserEvaluator;
import com.yglab.nlp.parser.io.CoNLLReader;
import com.yglab.nlp.perceptron.PerceptronModel;
import com.yglab.nlp.util.RegexFeatureDictionary;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
//@Ignore
public class KoreanDependencyParserEvaluatorTest {
	
	private static RegexFeatureDictionary featureDic;
	private static DependencyFeatureGenerator<ParseSample> featureGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureDic = new RegexFeatureDictionary(
			"/lang/ko/ko-regex-feature-unit.dic");
		
		featureGenerator = new DefaultDependencyFeatureGenerator();
		
		train();
	}
	
	private static void train() throws Exception {
		CoNLLReader reader = new CoNLLReader(featureDic);
		reader.startReading("./data/ko/parser/ko-parser-train-from-sejong.conll");
		List<ParseSample> trainSamples = DependencyParser.loadSamples(reader);
		String[] labels = reader.getLabels();
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.PERCEPTRON_ALGORITHM);
		options.put(Options.ITERATIONS, "5");
		
		AbstractModel model = DependencyParser.train(trainSamples, labels, featureGenerator, options);
		DependencyParser.saveModel(model, "./target/test-data/ko/parser/ko-parser-model-from-sejong.bin", "./target/test-data/ko/parser/ko-parser-model-from-sejong.txt");	
	}
	
	@Test
	public void testEvaluator() throws Exception {
		CoNLLReader reader = new CoNLLReader(featureDic);

		AbstractModel trainedModel = DependencyParser.loadModel("./target/test-data/ko/parser/ko-parser-model-from-sejong.bin");
		DependencyParser parser = new DependencyParser(trainedModel, featureGenerator);
		
		reader.startReading("./data/ko/parser/ko-parser-test-from-sejong.conll");
		List<ParseSample> testSamples = DependencyParser.loadSamples(reader);
		
		boolean labeled = ((PerceptronModel) trainedModel).isLabeled();
		//labeled = false;	// force to un-check if the predicted label is correct or not
		DependencyParserEvaluator evaluator = new DependencyParserEvaluator(parser, labeled);
		evaluator.evaluate(testSamples);
		
		evaluator.print();
		System.out.println("LAS(Labeled Attachment Score) = " + evaluator.getLAS());
		System.out.println("UAS(Unlabeled Attachment Score) = " + evaluator.getUAS());
		System.out.println("LA(Label Accuracy) = " + evaluator.getLA());
	}

}