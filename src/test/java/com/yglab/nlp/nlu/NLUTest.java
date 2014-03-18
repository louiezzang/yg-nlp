package com.yglab.nlp.nlu;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameSample;
import com.yglab.nlp.ner.NamedEntityRecognizer;
import com.yglab.nlp.util.RegexFeatureDictionary;
import com.yglab.nlp.util.Span;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class NLUTest {
	
	private static NameFeatureGenerator featureGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary(
				"/lang/ko/ko-regex-feature.dic",
				"/sample/ko/nlu/ko-regex-feature-nlu.dic");

		featureGenerator = new DefaultNLUFeatureGenerator(featureDic);
		
		train();
	}
	
	private static void train() throws Exception {
		List<NameSample> trainSamples = NamedEntityRecognizer.loadSamples("/sample/ko/nlu/ko-nlu-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = NamedEntityRecognizer.train(trainSamples, featureGenerator, options);

		NamedEntityRecognizer.saveModel(model, "./target/test-data/ko/nlu/ko-nlu-model.bin", "./target/test-data/ko/nlu/ko-nlu-model.txt");
	}
	
	@Test
	public void testRecognizer() throws Exception {
		String[] tokens = { 
				"투애니원",
				"의",
				"내가",
				"제일",
				"잘나가", 
				"노래", 
				"틀어줘"
				};
		
		System.out.println("==================================================");
		
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(i + ": " + tokens[i]);
		}
		
		AbstractModel trainModel = NamedEntityRecognizer.loadModel("./target/test-data/ko/nlu/ko-nlu-model.bin");
		NamedEntityRecognizer ner = new NamedEntityRecognizer(trainModel, featureGenerator);

		Span[] result = ner.find(tokens);

		System.out.println("-----------------------------------------------");
		for (Span span : result) {
			System.out.println(span);
		}
		
		System.out.println("");
		
		String[] tokens1 = { 
				"김철수",
				"한테",
				"전화걸어줘" 
				};
			
		for (int i = 0; i < tokens1.length; i++) {
			System.out.println(i + ": " + tokens1[i]);
		}

		Span[] result1 = ner.find(tokens1);

		System.out.println("-----------------------------------------------");
		for (Span span : result1) {
			System.out.println(span);
		}	
		
		System.out.println("");
		
		String[] tokens2 = { 
				"홍길동",
				"한테",
				"내일보자",
				"라고",
				"문자보내"
				};
			
		for (int i = 0; i < tokens2.length; i++) {
			System.out.println(i + ": " + tokens2[i]);
		}

		Span[] result2 = ner.find(tokens2);

		System.out.println("-----------------------------------------------");
		for (Span span : result2) {
			System.out.println(span);
		}	
	}
}