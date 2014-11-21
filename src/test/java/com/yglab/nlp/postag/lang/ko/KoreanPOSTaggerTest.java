package com.yglab.nlp.postag.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.postag.POSSample;
import com.yglab.nlp.postag.POSTagger;
import com.yglab.nlp.postag.morph.Token;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSTaggerTest {
	
	private static KoreanPOSFeatureGenerator featureGenerator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		KoreanMorphemeDictionary dic = new KoreanMorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic",
				"/lang/ko/ko-pos-head.dic",
				"/lang/ko/ko-pos-word.dic",
				"/lang/ko/ko-pos-suffix.dic");

		String[] labels = KoreanPOSTagger.getLabels("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		KoreanMorphemeAnalyzer analyzer = new KoreanMorphemeAnalyzer(dic, labels);
		featureGenerator = new KoreanPOSFeatureGenerator(analyzer);
		
		long startTime = System.currentTimeMillis();
//		train();
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("elapsed time to train = " + elapsedTime);
	}

	private static void train() throws Exception {
		List<POSSample> trainSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = KoreanPOSTagger.train(trainSamples, featureGenerator, options);
		
		KoreanPOSTagger.saveModel(model, "./build/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin", "./build/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.txt");
	}
	
	@Test
	public void testTagger() throws Exception {

//		String[] tokens = {
//				"당신은",
//				"학교를",
//				"앞길을", 
//				"열심히",
//				"다닙니다",
//				".",
//		};
		
//		String[] tokens = {
//				"이",
//				"옷은",
//				"2만원",
//				"짜리인가요",
//				"?"
//		};
		
//		String[] tokens = {
//				"운행",
//				"지연",
//				"사고",
//				"기준",
//		};

		String[] tokens = {
			"그는",
			"나를",
			"위해",
			"지내는",			
			"해줄",
			"수",
			"있다",
			"."
		};
		
		AbstractModel trainModel = KoreanPOSTagger.loadModel("./build/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin"); 

		KoreanPOSTagger tagger = new KoreanPOSTagger(trainModel, featureGenerator);
		
		System.out.println("==================================================");
		
		List<Token> analTokens = tagger.analyze(tokens);
		
		System.out.println("");
		
		for (int i = 0; i < analTokens.size(); i++) {
			Token analToken = analTokens.get(i);
			System.out.println(i + ": " + analToken.getToken() + "[" +  analToken.getTag() + "], " + analToken.getAttributes());
		}
	}
	
	@Test
	//@Ignore
	public void testEvaluator() throws Exception {
		AbstractModel trainModel = KoreanPOSTagger.loadModel("./build/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin");
		
		POSTagger tagger = new KoreanPOSTagger(trainModel, featureGenerator);
		
		List<POSSample> testSamples = KoreanPOSTagger.loadSamples(
				"/sample/ko/pos/ko-pos-test-sejong-BGAA0164.txt", 
				"[^\\+/\\(\\)]*/", "");
		
		KoreanPOSTaggerEvaluator evaluator = new KoreanPOSTaggerEvaluator(
				tagger, 
				"./build/test-data/ko/pos/ko-pos-test-result-sejong-BGAA0164.txt", 
				true);

		evaluator.evaluate(testSamples);
	}

}
