package com.yglab.nlp.parser.dep.lang.ko;

import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.yglab.nlp.dictionary.RegexFeatureDictionary;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.dep.DefaultDependencyFeatureGenerator;
import com.yglab.nlp.parser.dep.DependencyParser;
import com.yglab.nlp.parser.io.CoNLLReader;
import com.yglab.nlp.postag.lang.ko.KoreanMorphemeAnalyzer;
import com.yglab.nlp.postag.lang.ko.KoreanMorphemeDictionary;
import com.yglab.nlp.postag.lang.ko.KoreanPOSFeatureGenerator;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.morph.Token;
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
@Ignore
public class KoreanDependencyParserTest {
	
	private static DefaultDependencyFeatureGenerator featureGenerator;
	private static Tokenizer tokenizer;
	private static KoreanPOSTagger posTagger;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary(
				"/lang/ko/ko-regex-feature-unit.dic");
		
		featureGenerator = new DefaultDependencyFeatureGenerator(featureDic);
		tokenizer = initTokenizer();
		posTagger = initPOSTagger();
		
		//train();
	}
	
	private static void train() throws Exception {
		CoNLLReader reader = new CoNLLReader();
		reader.startReading("./data/ko/parser/ko-parser-train-sejong-BGAA0164.conll");
		List<ParseSample> trainSamples = DependencyParser.loadSamples(reader);
		String[] labels = reader.getLabels();
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.PERCEPTRON_ALGORITHM);
		options.put(Options.ITERATIONS, "5");
		
		AbstractModel model = DependencyParser.train(trainSamples, labels, featureGenerator, options);
		DependencyParser.saveModel(model, "./build/test-data/ko/parser/ko-parser-model-sejong-BGAA0164.bin", "./build/test-data/ko/parser/ko-parser-model-sejong-BGAA0164.txt");
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

	@Test
	public void testParser() throws Exception {
		System.out.println("====================================================");

		AbstractModel trainedModel = DependencyParser.loadModel("./build/test-data/ko/parser/ko-parser-model-sejong-BGAA0164.bin");
		KoreanDependencyParser parser = new KoreanDependencyParser(trainedModel, featureGenerator, tokenizer, posTagger);
		
		String s = "나는 도서관에 열심히 다닙니다.";
		s = "이 자동차의 디자인은 어떻습니까?";
	
		List<Parse> parses = parser.parse(s);
		Collections.sort(parses);
		
		System.out.println("====================================================");
		System.out.println("Index\tHead\tDeprel\tScore\tWord\tMorpheme");
		System.out.println("====================================================");
		
		for (Parse parse : parses) {
			Token token = (Token) parse.getAttribute("token");
			System.out.println(parse.getIndex() + "\t" + parse.getHead() + "\t" + parse.getLabel() + 
					"\t" + parse.getScore() + "\t" + parse.getWord() + "\t" + token.getTag());
		}
	}

}
