package com.yglab.nlp.ner.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.ner.DefaultNameFeatureGenerator;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameSample;
import com.yglab.nlp.ner.NameFinder;
import com.yglab.nlp.ner.lang.ko.KoreanNameFinder;
import com.yglab.nlp.postag.POSSample;
import com.yglab.nlp.postag.lang.ko.KoreanPOSFeatureGenerator;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.lang.ko.MorphemeDictionary;
import com.yglab.nlp.tokenizer.DefaultTokenFeatureGenerator;
import com.yglab.nlp.tokenizer.MaxentTokenizer;
import com.yglab.nlp.tokenizer.TokenFeatureGenerator;
import com.yglab.nlp.tokenizer.TokenSample;
import com.yglab.nlp.tokenizer.Tokenizer;
import com.yglab.nlp.util.RegexFeatureDictionary;
import com.yglab.nlp.util.Span;

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
		// TODO
		MorphemeDictionary dic = new MorphemeDictionary("/lang/ko/ko-pos-josa.dic", "/lang/ko/ko-pos-eomi.dic");
		KoreanPOSFeatureGenerator posFeatureGenerator = new KoreanPOSFeatureGenerator(dic);
		
		List<POSSample> posTrainSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-train.txt", "_[^,]+", "");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel trainModel = KoreanPOSTagger.train(posTrainSamples, posFeatureGenerator, options);

		// TODO
		KoreanPOSTagger posTagger = new KoreanPOSTagger(trainModel, posFeatureGenerator, dic);
		
		return posTagger;
	}
	
	private static void train() throws Exception {
		List<NameSample> trainSamples = NameFinder.loadSamples("/sample/ko/ner/ko-ner-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = NameFinder.train(trainSamples, featureGenerator, options);

		NameFinder.saveModel(model, "./target/test-data/ko/ner/ko-ner-model.bin", "./target/test-data/ko/ner/ko-ner-model.txt");
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
		
		AbstractModel trainModel = NameFinder.loadModel("./target/test-data/ko/ner/ko-ner-model.bin");
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