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
import com.yglab.nlp.postag.morph.MorphemeDictionary;

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
		MorphemeDictionary dic = new MorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic");
		
		MorphemeDictionary suffixDic = new MorphemeDictionary(
				"/lang/ko/ko-pos-suffix.dic");

		String[] labels = KoreanPOSTagger.getLabels("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		KoreanMorphemeAnalyzer analyzer = new KoreanMorphemeAnalyzer(dic, suffixDic, labels);
		featureGenerator = new KoreanPOSFeatureGenerator(analyzer);
		
		long startTime = System.currentTimeMillis();
		//train();
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("elapsed time for trainning = " + elapsedTime);
	}

	private static void train() throws Exception {
		List<POSSample> trainSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = KoreanPOSTagger.train(trainSamples, featureGenerator, options);
		
		KoreanPOSTagger.saveModel(model, "./target/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin", "./target/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.txt");
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
				"나를",
				"위해",
				"해줄수",
				"있다",
				"."
			};
		
		AbstractModel trainModel = KoreanPOSTagger.loadModel("./target/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin"); 

		KoreanPOSTagger tagger = new KoreanPOSTagger(trainModel, featureGenerator);
		
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
	//@Ignore
	public void testEvaluator() throws Exception {
		AbstractModel trainModel = KoreanPOSTagger.loadModel("./target/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin");
		
		POSTagger tagger = new KoreanPOSTagger(trainModel, featureGenerator);
		
		List<POSSample> testSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-test-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		POSTaggerEvaluator evaluator = new POSTaggerEvaluator(tagger);
		
		long startTime = System.currentTimeMillis();
		
		evaluator.evaluate(testSamples);
		
		evaluator.print();
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("elapsed time for testing = " + elapsedTime);
	}

}
