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
public class NamedEntityRecognizerTest {
	
	private static NameFeatureGenerator featureGenerator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary("/lang/ko/ko-regex-feature.dic");
		featureGenerator = new DefaultNameFeatureGenerator(featureDic);
		
		train();
	}
	
	private static void train() throws Exception {
		List<NameSample> trainSamples = NamedEntityRecognizer.loadSamples("/sample/ko/ner/ko-ner-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = NamedEntityRecognizer.train(trainSamples, featureGenerator, options);

		NamedEntityRecognizer.saveModel(model, "./target/test-data/ko/ner/ko-ner-default-model.bin", "./target/test-data/ko/ner/ko-ner-default-model.txt");
	}
	
	@Test
	public void testRecognizer() throws Exception {
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

		AbstractModel trainModel = NamedEntityRecognizer.loadModel("./target/test-data/ko/ner/ko-ner-default-model.bin");
		NamedEntityRecognizer ner = new NamedEntityRecognizer(trainModel, featureGenerator);
		Span[] result = ner.find(tokens);

		System.out.println("-----------------------------------------------");
		for (Span span : result) {
			System.out.println(span.getStart() + " ~ " + span.getEnd() + " --> " + span.getType());
		}
	}
	
	@Test
	public void testEvaluator() throws Exception {
		//List<NameSample> trainSamples = NamedEntityRecognizer.loadSamples("/sample/ko/ner/ko-ner-train.txt");
		//Options options = new Options();
		//options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		//AbstractModel trainModel = NamedEntityRecognizer.train(trainSamples, featureGenerator, options);
		
		AbstractModel trainModel = NamedEntityRecognizer.loadModel("./target/test-data/ko/ner/ko-ner-default-model.bin");
		NamedEntityRecognizer ner = new NamedEntityRecognizer(trainModel, featureGenerator);
		
		List<NameSample> testSamples = NamedEntityRecognizer.loadSamples("/sample/ko/ner/ko-ner-test.txt");
		NamedEntityRecognizerEvaluator evaluator = new NamedEntityRecognizerEvaluator(ner);
		evaluator.evaluate(testSamples);
		
		evaluator.print();
	}
}