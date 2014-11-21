package com.yglab.nlp.ner.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.dictionary.RegexFeatureDictionary;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.model.Span;
import com.yglab.nlp.ner.DefaultNameFeatureGenerator;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameFinder;
import com.yglab.nlp.ner.NameSample;
import com.yglab.nlp.postag.lang.ko.KoreanMorphemeAnalyzer;
import com.yglab.nlp.postag.lang.ko.KoreanMorphemeDictionary;
import com.yglab.nlp.postag.lang.ko.KoreanPOSFeatureGenerator;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.tokenizer.DefaultTokenFeatureGenerator;
import com.yglab.nlp.tokenizer.MaxentTokenizer;
import com.yglab.nlp.tokenizer.TokenFeatureGenerator;
import com.yglab.nlp.tokenizer.TokenSample;
import com.yglab.nlp.tokenizer.Tokenizer;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanNameFinderTest {
	
	private static NameFeatureGenerator featureGenerator;
	private static Tokenizer tokenizer;
	private static KoreanPOSTagger posTagger;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary("/lang/ko/ko-regex-feature.dic", "/lang/ko/ko-regex-feature-unit.dic");
		featureGenerator = new DefaultNameFeatureGenerator(featureDic);
		tokenizer = initTokenizer();
		posTagger = initPOSTagger();
		
		train();
	}
	
	/**
	 * Instantiates Tokenizer.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Tokenizer initTokenizer() throws Exception {
		List<TokenSample> trainSamples = MaxentTokenizer.loadSamples("/sample/ko/tokenizer/ko-tokenizer-train.txt");
		TokenFeatureGenerator featureGenerator = new DefaultTokenFeatureGenerator();
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		options.put("useSkipPattern", "true");
		AbstractModel trainModel = MaxentTokenizer.train(trainSamples, featureGenerator, options);

		Tokenizer tokenizer = new MaxentTokenizer(trainModel, new DefaultTokenFeatureGenerator());

		return tokenizer;
	}
	
	/**
	 * Instantiates POSTagger to tokenize josa from a eojeol.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static KoreanPOSTagger initPOSTagger() throws Exception {
		KoreanMorphemeDictionary dic = new KoreanMorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic",
				"/lang/ko/ko-pos-head.dic",
				"/lang/ko/ko-pos-word.dic",
				"/lang/ko/ko-pos-suffix.dic");

		String[] labels = KoreanPOSTagger.getLabels("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		KoreanMorphemeAnalyzer morphAnalyzer = new KoreanMorphemeAnalyzer(dic, labels);
		KoreanPOSFeatureGenerator posFeatureGenerator = new KoreanPOSFeatureGenerator(morphAnalyzer);
		
//		List<POSSample> posTrainSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
//		
//		Options options = new Options();
//		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
//		AbstractModel trainModel = KoreanPOSTagger.train(posTrainSamples, posFeatureGenerator, options);
		
		AbstractModel trainedPosModel = KoreanPOSTagger.loadModel("./build/test-data/ko/pos/ko-pos-model-sejong-BGAA0164.bin"); 
		
		KoreanPOSTagger posTagger = new KoreanPOSTagger(trainedPosModel, posFeatureGenerator);
		
		return posTagger;
	}
	
	private static void train() throws Exception {
		List<NameSample> trainSamples = NameFinder.loadSamples("/sample/ko/ner/ko-ner-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = NameFinder.train(trainSamples, featureGenerator, options);

		NameFinder.saveModel(model, "./build/test-data/ko/ner/ko-ner-model.bin", "./build/test-data/ko/ner/ko-ner-model.txt");
	}
	
	@Test
	public void testNameFinder() throws Exception {

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
				"교사", 
				"는",
				"각각",
				"중등교육부문", 
				"에서",
				"수상하게", 
				"됐다", 
				"." };
		
		AbstractModel trainModel = NameFinder.loadModel("./build/test-data/ko/ner/ko-ner-model.bin");
		KoreanNameFinder finder = new KoreanNameFinder(trainModel, featureGenerator, tokenizer, posTagger);

		Span[] spans = finder.find(tokens);
		
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(i + " : " + tokens[i]);
		}

		for (Span span : spans) {
			System.out.println(span);
		}
		
		System.out.println("");
		
		String s = "우상복은 포항제철중학교 교사, 오정남은 포철제철중학교 경북 상주 성신여자중학교 교사는 각각 중등교육부문에서 수상하게 됐다.";
		Span[] nameSpans = finder.find(s);
		
		for (Span span : nameSpans) {
			System.out.println(span + " : " + s.substring(span.getStart(), span.getEnd()));
		}
	}
	
}