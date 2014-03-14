package com.yglab.nlp.opinion.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.ner.NameSample;
import com.yglab.nlp.opinion.DefaultOpinionFeatureGenerator;
import com.yglab.nlp.opinion.OpinionFinder;
import com.yglab.nlp.opinion.TokenPostagPairGenerator;
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
public class KoreanOpinionFinderTest {
	
	private static NameFeatureGenerator featureGenerator;
	private static Tokenizer tokenizer;
	private static TokenPostagPairGenerator tokenPairGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary(
				"/sample/ko/opinion/ko-regex-feature-opinion.dic");

		featureGenerator = new DefaultOpinionFeatureGenerator(featureDic);
		
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
		MorphemeDictionary dicJosa = new MorphemeDictionary("/lang/ko/ko-pos-josa.dic");
		MorphemeDictionary dicEomi = new MorphemeDictionary("/lang/ko/ko-pos-eomi.dic");
		KoreanPOSFeatureGenerator posFeatureGenerator = new KoreanPOSFeatureGenerator(dicJosa, dicEomi);
		
		List<POSSample> posTrainSamples = KoreanPOSTagger.loadSamples("/sample/ko/pos/ko-pos-train.txt", "_[^,]+", "");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel trainModel = KoreanPOSTagger.train(posTrainSamples, posFeatureGenerator, options);

		KoreanPOSTagger posTagger = new KoreanPOSTagger(trainModel, posFeatureGenerator, dicJosa, dicEomi);
		
		return posTagger;
	}
	
	private static void train() throws Exception {
		List<NameSample> trainSamples = KoreanOpinionFinder.loadSamples("/sample/ko/opinion/ko-opinion-train.txt", tokenPairGenerator);
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = OpinionFinder.train(trainSamples, featureGenerator, options);

		OpinionFinder.saveModel(model, "./target/test-data/ko/opinion/ko-opinion-model.bin", "./target/test-data/ko/opinion/ko-opinion-model.txt");
	}
	
	@Test
	public void testFinder() throws Exception {
		AbstractModel trainModel = KoreanOpinionFinder.loadModel("./target/test-data/ko/opinion/ko-opinion-model.bin");
		OpinionFinder opinionFinder = new KoreanOpinionFinder(trainModel, featureGenerator, tokenizer, tokenPairGenerator);

		String s = "안철수가 새정치를 보여준적이 있던가?";

		Span[] nameSpans = opinionFinder.find(s);
		
		for (Span span : nameSpans) {
			System.out.println(span + " : " + s.substring(span.getStart(), span.getEnd()) + " -> " + span.getAttribute("stemWords"));
		}
	}
}