package com.yglab.nlp.tokenizer;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.util.Span;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class MaxentTokenizerTest {
	
	private static DefaultTokenFeatureGenerator featureGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureGenerator = new DefaultTokenFeatureGenerator();
		
		train_en();
		train_ko();
	}
	
	@Test
	public void testTrainSamples() throws Exception {
		List<TokenSample> trainSamples = MaxentTokenizer.loadSamples("/sample/en/tokenizer/en-tokenizer-train.txt");
		
		for (TokenSample sample : trainSamples) {
			System.out.println("text: " + sample.getText());
			Span[] tokenSpans = sample.getTokenSpans();
			
			System.out.println("spans: ");
			for (Span span : tokenSpans) {
				System.out.println("  " + span);
			}
		}
	}
	
	@Test
	public void testFeatureGenerator() throws Exception {
		String token = "I've";
		String[] features = featureGenerator.getFeatures(1, token);
		
		System.out.println("features: ");
		for (String feature : features) {
			System.out.println("  " + feature);
		}
	}
	
	@Test
	public void testSampleEventStream() throws Exception {
		List<TokenSample> trainSamples = MaxentTokenizer.loadSamples("/sample/en/tokenizer/en-tokenizer-train.txt");
		
		TokenSampleEventStream stream = new TokenSampleEventStream(featureGenerator, trainSamples, MaxentTokenizer.skipPattern);
		List<Datum> trainData = stream.getOutputStream();
		
		for (Datum datum : trainData) {
			System.out.println(datum.getLabel() + ": " + datum.getFeatures());
		}
	}
	
	private static void train_en() throws Exception {
		List<TokenSample> trainSamples = MaxentTokenizer.loadSamples("/sample/en/tokenizer/en-tokenizer-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		options.put("useSkipPattern", "true");
		AbstractModel model = MaxentTokenizer.train(trainSamples, featureGenerator, options);

		MaxentTokenizer.saveModel(model, "./target/test-data/en/tokenizer/en-tokenizer-model.bin", "./target/test-data/en/tokenizer/en-tokenizer-model.txt");
	}
	
	private static void train_ko() throws Exception {
		List<TokenSample> trainSamples = MaxentTokenizer.loadSamples("/sample/ko/tokenizer/ko-tokenizer-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		options.put("useSkipPattern", "true");
		AbstractModel model = MaxentTokenizer.train(trainSamples, featureGenerator, options);

		MaxentTokenizer.saveModel(model, "./target/test-data/ko/tokenizer/ko-tokenizer-model.bin", "./target/test-data/ko/tokenizer/ko-tokenizer-model.txt");
	}
	
	@Test
	public void testTokenizer_en() throws Exception {
		AbstractModel trainModel = MaxentTokenizer.loadModel("./target/test-data/en/tokenizer/en-tokenizer-model.bin");
		MaxentTokenizer tokenizer = new MaxentTokenizer(trainModel, featureGenerator);
		
		String s = "I've five minutes away from the underground station \"Westbad\".";
		s = "So I proposed to meet at a quite local point: the cafe \"Daily's\" in Unter-den-Linden 18, 30291s Berlin.";
		String[] tokens = tokenizer.tokenize(s);
		
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(i + ": " + tokens[i]);
		}
	}
	
	@Test
	public void testTokenizer_ko() throws Exception {
		AbstractModel trainModel = MaxentTokenizer.loadModel("./target/test-data/ko/tokenizer/ko-tokenizer-model.bin");
		MaxentTokenizer tokenizer = new MaxentTokenizer(trainModel, featureGenerator);
		
		String s = "행사 관계자는 \"이민호의 인기가 상상을 초월할 정도다\"라며 이민호의 싱가포르 인기를 밝혔다.";
		s = "국민주택기금이 공공기숙사 사업주체(S.P.C)로 직접 나설 수 있도록 해 사립대학의 기숙사 투자도 촉진하기로 했다.";
		String[] tokens = tokenizer.tokenize(s);
		
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(i + ": " + tokens[i]);
		}
	}
}