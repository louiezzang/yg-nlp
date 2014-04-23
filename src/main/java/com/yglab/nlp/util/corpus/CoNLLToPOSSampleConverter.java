package com.yglab.nlp.util.corpus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.dep.DependencyParser;
import com.yglab.nlp.parser.io.CoNLLReader;

/**
 * This class converts the CoNLL parser train sample file to the train sample file for POS tagger. 
 * 
 * @author Younggue Bae
 */
public class CoNLLToPOSSampleConverter {
	
	public static void convert(String fileCoNLL, String filePOS) throws IOException {
		mkdirs(filePOS);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePOS, false), "UTF-8"));
		
		CoNLLReader reader = new CoNLLReader();
		reader.startReading(fileCoNLL);
		List<ParseSample> samples = DependencyParser.loadSamples(reader);

		int cnt = 1;
		for (ParseSample sample : samples) {
			StringBuilder sb = new StringBuilder();
			
			String[] tokens = sample.getForms();
			String[] postags = sample.getPostags();
			// ignore ROOT node in index "0"
			for (int i = 1; i < tokens.length; i++) {
				String token = tokens[i];
				String postag = postags[i];
				sb.append(token).append("<").append(postag).append(">");
				if (i < tokens.length - 1) {
					sb.append(" ");
				}
			}
			System.out.println(cnt + "\t" + sb.toString());
			writer.write(sb.toString());
			writer.newLine();
			cnt++;
		}
		
		writer.close();
	}
	
	private static void mkdirs(String filename) {
		String strDir = filename.substring(0, filename.lastIndexOf(File.separator));

		File dir = new File(strDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void main(String[] args) throws Exception {
		CoNLLToPOSSampleConverter.convert("./data/ko/parser/ko-parser-train-from-sejong.conll", "./target/test-data/ko/pos/ko-pos-train-from-conll.txt");
		CoNLLToPOSSampleConverter.convert("./data/ko/parser/ko-parser-test-from-sejong.conll", "./target/test-data/ko/pos/ko-pos-test-from-conll.txt");
		
		System.out.println();
		
		CoNLLToPOSSampleConverter.convert("./data/en/parser/en-parser-train.conll", "./target/test-data/en/pos/en-pos-train-from-conll.txt");
	}

}
