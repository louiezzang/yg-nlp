package com.yglab.nlp.util.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanUnicodeTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testKoreanUnicode() throws Exception {
		System.out.println(String.valueOf(KoreanUnicode.decompose('여').length));
		
		System.out.println(String.valueOf(KoreanUnicode.decompose('영')));
		
		System.out.println(String.valueOf(KoreanUnicode.decompose("ㅂ니다")));
		
		System.out.println(KoreanUnicode.compound(1, 2, 3));
		
		System.out.println(KoreanUnicode.compound(1, 2, 0));
		
		System.out.println(KoreanUnicode.makeChar('영', 1, 2));
		
		System.out.println(KoreanUnicode.replaceJongseong('영', '각'));
	}
}