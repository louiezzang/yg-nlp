package com.yglab.nlp.sbd;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Options;
import com.yglab.nlp.tokenizer.WhitespaceTokenizer;
import com.yglab.nlp.util.Span;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class MaxentSentenceDetectorTest {
	
	private static SentenceFeatureGenerator featureGenerator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		featureGenerator = new DefaultSentenceFeatureGenerator(null);
		
		train();
	}
	
	private static void train() throws Exception {
		List<SentenceSample> trainSamples = MaxentSentenceDetector.loadSamples("/sample/ko/sbd/ko-sbd-train.txt");
		
		Options options = new Options();
		options.put(Options.ALGORITHM, Options.MAXENT_ALGORITHM);
		AbstractModel model = MaxentSentenceDetector.train(trainSamples, featureGenerator, options);
		
		MaxentSentenceDetector.saveModel(model, "./target/test-data/ko/sbd/ko-sbd-model.bin", "./target/test-data/ko/sbd/ko-sbd-model.txt");
	}

	@Test
	public void testDetector() throws Exception {
		String text = "나는 학교에... 갑니다. 너는 도서관에 가니? M.C.M 가방이 아주 이쁘네요! 얼마 주고 사셨나요? 좋다 " +
				" 또 정부가 여객을 제외한 물류·차량관리·시설 유지보수 등 나머지 분야를 다수의 자회사에 맡기는 ‘지주회사+자회사’ 형태로 코레일을 운영하겠다는 방침도 논의할 것으로 예상된다." +
				" 여야 동수로 구성된 소위는 출범 하루 만인 31일 오전 첫 회의를 개최해 국토교통부로부터 철도산업 발전 방안에 대한 보고를 듣기로 했다. ";

		AbstractModel trainModel = MaxentSentenceDetector.loadModel("./target/test-data/ko/sbd/ko-sbd-model.bin");
		MaxentSentenceDetector detector = new MaxentSentenceDetector(trainModel, featureGenerator);

		System.out.println(text);
		System.out.println("==================================================");
		System.out.println(" detect sentence by memm");
		System.out.println("--------------------------------------------------");
		
    WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
    String[] tokens = tokenizer.tokenize(text);
    Span[] result = detector.detect(tokens);
    
    for (int i = 0; i < tokens.length; i++) {
    	System.out.println(i + ": " + tokens[i]);
    }

    System.out.println("--------------------------------------------------");
    for (Span span : result) {
    	System.out.println(span.getStart() + " ~ " + span.getEnd() + " --> " + span.getType());
    }
		
    System.out.println("--------------------------------------------------");
		String[] sentences = detector.detect(text);
		
		for (String sentence : sentences) {
			System.out.println(sentence);
		}
	}
	
}