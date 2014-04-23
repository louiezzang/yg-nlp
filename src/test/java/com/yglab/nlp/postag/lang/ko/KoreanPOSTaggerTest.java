package com.yglab.nlp.postag.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.postag.POSSample;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.postag.POSTaggerEvaluator;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSTaggerTest {
	
	private static MorphemeDictionary dic;
	private static KoreanPOSFeatureGenerator featureGenerator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// TODO
		dic = new MorphemeDictionary("/lang/ko/ko-pos-josa.dic", "/lang/ko/ko-pos-eomi.dic");
		featureGenerator = new KoreanPOSFeatureGenerator(dic);
		
		//train();
	}

	private static void train() throws Exception {
		List<POSSample> trainSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-train.txt", "[^\\+/\\(\\)]*/", "");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = KoreanPOSTagger.train(trainSamples, featureGenerator, options);
		
		KoreanPOSTagger.saveModel(model, "./target/test-data/ko/pos/ko-pos-model.bin", "./target/test-data/ko/pos/ko-pos-model.txt");
	}
	
	@Test
	public void testTagger() throws Exception {

		String[] tokens = {
				"당신은",
				"학교를",
				"열심히",
				"다닙니다",
				"."
		};
		
//		String[] tokens = {
//				"이",
//				"옷은",
//				"2만원",
//				"짜리인가요",
//				"?"
//		};

		AbstractModel trainModel = KoreanPOSTagger.loadModel("./target/test-data/ko/pos/ko-pos-model.bin"); 

		KoreanPOSTagger tagger = new KoreanPOSTagger(trainModel, featureGenerator, dic);
		
		System.out.println("==================================================");
		
		String[] result = tagger.tag(tokens);
		
		for (int i = 0; i < result.length; i++) {
			String tag = result[i];
			System.out.println(i + ": " + tokens[i] + "\t[" + tag + "]");
		}
		
		System.out.println("==================================================");

		// TODO
//		List<Eojeol> eojeols = tagger.analyze(tokens);
//		
//		for (int i = 0; i < eojeols.size(); i++) {
//			System.out.println(i + ": " + eojeols.get(i).toString());
//		}
	}
	
	@Test
	public void testEvaluator() throws Exception {
		AbstractModel trainModel = KoreanPOSTagger.loadModel("./target/test-data/ko/pos/ko-pos-model.bin");
		
		POSTagger tagger = new KoreanPOSTagger(trainModel, featureGenerator, dic);
		
		List<POSSample> testSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-test.txt", "[^\\+/\\(\\)]*/", "");
		POSTaggerEvaluator evaluator = new POSTaggerEvaluator(tagger);
		evaluator.evaluate(testSamples);
		
		evaluator.print();
	}

}
