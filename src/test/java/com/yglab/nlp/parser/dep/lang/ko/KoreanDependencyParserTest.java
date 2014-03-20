package com.yglab.nlp.parser.dep.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.parser.Parse;
import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.dep.DefaultDependencyFeatureGenerator;
import com.yglab.nlp.parser.dep.DependencyParser;
import com.yglab.nlp.parser.io.CONLLReader;
import com.yglab.nlp.postag.POSSample;
import com.yglab.nlp.postag.lang.ko.KoreanPOSFeatureGenerator;
import com.yglab.nlp.postag.lang.ko.KoreanPOSTagger;
import com.yglab.nlp.postag.lang.ko.MorphemeDictionary;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanDependencyParserTest {
	
	private static DefaultDependencyFeatureGenerator featureGenerator;
	private static KoreanPOSTagger posTagger;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureGenerator = new DefaultDependencyFeatureGenerator();
		posTagger = initPOSTagger();
		train();
	}
	
	private static void train() throws Exception {
		CONLLReader reader = new CONLLReader();
		reader.startReading("/sample/ko/parser/ko-parser-train.conll");
		List<ParseSample> trainSamples = DependencyParser.loadSamples(reader);
		String[] labels = reader.getLabels();
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.PERCEPTRON_ALGORITHM);
		options.put(Options.ITERATIONS, "5");
		
		AbstractModel model = DependencyParser.train(trainSamples, labels, featureGenerator, options);
		DependencyParser.saveModel(model, "./target/test-data/ko/parser/ko-parser-model.bin", "./target/test-data/ko/parser/ko-parser-model.txt");
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

	@Test
	public void testParser() throws Exception {
		System.out.println("===============================================");

		AbstractModel trainedModel = DependencyParser.loadModel("./target/test-data/ko/parser/ko-parser-model.bin");
		KoreanDependencyParser parser = new KoreanDependencyParser(trainedModel, featureGenerator, posTagger);
		
		//String[] tokens = { "나는", "도서관에", "열심히", "다닙니다", "." };
		String[] tokens = { "이", "물건의", "품질은", "어떻습니까", "?" };
	
		List<Parse> parses = parser.parse(tokens);
		
		for (Parse parse : parses) {
			System.out.println(parse);
		}
	}

}
