package com.yglab.nlp.postag.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class MorphemeDicGeneratorTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testGenerator() throws Exception {
		// eomi dictionary
		MorphemeDicGenerator.generate("./src/test/resources/sample/ko/pos/ko-pos-train.txt", "./target/test-data/ko/dic/ko-pos-eomi.dic", "^[E]+");
		// josa dictionary
		MorphemeDicGenerator.generate("./src/test/resources/sample/ko/pos/ko-pos-train.txt", "./target/test-data/ko/dic/ko-pos-josa.dic", "^[J]+");
		// noun, adverb, verb, etc. dictionary
		MorphemeDicGenerator.generate("./src/test/resources/sample/ko/pos/ko-pos-train.txt", "./target/test-data/ko/dic/ko-pos-etc.dic", "^[NMVXS]+");
	}
}