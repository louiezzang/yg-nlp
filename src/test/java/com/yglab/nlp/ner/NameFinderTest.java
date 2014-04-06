package com.yglab.nlp.ner;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.util.RegexFeatureDictionary;
import com.yglab.nlp.util.Span;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class NameFinderTest {
	
	private static NameFeatureGenerator featureGenerator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary("/lang/ko/ko-regex-feature.dic", "/lang/ko/ko-regex-feature-unit.dic");
		featureGenerator = new DefaultNameFeatureGenerator(featureDic);
		
		train();
	}
	
	private static void train() throws Exception {
		List<NameSample> trainSamples = NameFinder.loadSamples("/sample/ko/ner/ko-ner-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = NameFinder.train(trainSamples, featureGenerator, options);

		NameFinder.saveModel(model, "./target/test-data/ko/ner/ko-ner-default-model.bin", "./target/test-data/ko/ner/ko-ner-default-model.txt");
	}
	
	@Test
	public void testNameFinder() throws Exception {
		System.out.println("==================================================");

		String[] tokens = { 
			"우상복", 
			"은", 
			"포항제철중학교", 
			"교사", 
			",", 
			"오정남", 
			"은", 
			"포철제철중학교", 
			"경북", 
			"상주", 
			"성신여자중학교", 
			"교사는", 
			"각각",
			"중등교육부문에서", 
			"수상하게", 
			"됐다", 
			"." };
		
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(i + ": " + tokens[i]);
		}

		AbstractModel trainModel = NameFinder.loadModel("./target/test-data/ko/ner/ko-ner-default-model.bin");
		NameFinder finder = new NameFinder(trainModel, featureGenerator);
		Span[] result = finder.find(tokens);

		System.out.println("-----------------------------------------------");
		for (Span span : result) {
			System.out.println(span.getStart() + " ~ " + span.getEnd() + " --> " + span.getType());
		}
	}
	
	@Test
	public void testEvaluator() throws Exception {
		//List<NameSample> trainSamples = NameFinder.loadSamples("/sample/ko/ner/ko-ner-train.txt");
		//Options options = new Options();
		//options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		//AbstractModel trainModel = NameFinder.train(trainSamples, featureGenerator, options);
		
		AbstractModel trainModel = NameFinder.loadModel("./target/test-data/ko/ner/ko-ner-default-model.bin");
		NameFinder ner = new NameFinder(trainModel, featureGenerator);
		
		List<NameSample> testSamples = NameFinder.loadSamples("/sample/ko/ner/ko-ner-test.txt");
		NameFinderEvaluator evaluator = new NameFinderEvaluator(ner);
		evaluator.evaluate(testSamples);
		
		evaluator.print();
	}
}