package com.yglab.nlp.ner.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.dictionary.RegexFeatureDictionary;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.model.Span;
import com.yglab.nlp.ner.NameSample;
import com.yglab.nlp.ner.PatternBasedNameFeatureGenerator;
import com.yglab.nlp.ner.TokenPostagPairGenerator;
import com.yglab.nlp.postag.POSSample;
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
public class KoreanPatternBasedNameFinderTest {
	
	private static PatternBasedNameFeatureGenerator featureGenerator;
	private static Tokenizer tokenizer;
	private static TokenPostagPairGenerator tokenPairGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary(
				"/sample/ko/ner/ko-regex-feature-opinion.dic");

		featureGenerator = new PatternBasedNameFeatureGenerator(featureDic);
		
		tokenizer = initTokenizer();
		
		KoreanPOSTagger posTagger = initPOSTagger();
		tokenPairGenerator = new KoreanTokenPostagPairGenerator(posTagger, "\t");
		
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
		KoreanMorphemeDictionary dic = new KoreanMorphemeDictionary("/lang/ko/ko-pos-josa.dic", "/lang/ko/ko-pos-eomi.dic");
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
		List<NameSample> trainSamples = KoreanPatternBasedNameFinder.loadSamples("/sample/ko/ner/ko-ner-opinion-train.txt", tokenPairGenerator);
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = KoreanPatternBasedNameFinder.train(trainSamples, featureGenerator, options);

		KoreanPatternBasedNameFinder.saveModel(model, "./target/test-data/ko/ner/ko-ner-opinion-model.bin", "./target/test-data/ko/ner/ko-ner-opinion-model.txt");
	}
	
	@Test
	public void testFinder() throws Exception {
		AbstractModel trainModel = KoreanPatternBasedNameFinder.loadModel("./target/test-data/ko/ner/ko-ner-opinion-model.bin");
		KoreanPatternBasedNameFinder opinionFinder = new KoreanPatternBasedNameFinder(trainModel, featureGenerator, tokenizer, tokenPairGenerator);

		String s = "안철수가 새정치를 보여준적이 있던가?";

		Span[] nameSpans = opinionFinder.find(s);
		
		for (Span span : nameSpans) {
			System.out.println(span + " : " + s.substring(span.getStart(), span.getEnd()) + " -> " + span.getAttribute("stemWords"));
		}
	}
}