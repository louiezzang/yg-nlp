package com.yglab.nlp.util.corpus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.parser.io.TreebankReader;

/**
 * This class converts the Penn Treebank format file to the CoNLL format file for DependencyParser. 
 * 
 * @author Younggue Bae
 */
public class TreebankToCoNLLConverter {
	
	public static void convert(String filePenn, String fileCoNLL) throws IOException {
		mkdirs(fileCoNLL);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileCoNLL, false), "UTF-8"));

		TreebankReader treebankReader = new TreebankReader();
		treebankReader.startReading(filePenn);
		
		int cnt = 0;
		ParseSample instance = null;
		while ((instance = treebankReader.getNext()) != null) {
			for (int index = 1; index < instance.length(); index++) {
				StringBuilder sb = new StringBuilder()
					.append(index).append("\t")
					.append(instance.forms[index]).append("\t")
					.append(instance.lemmas[index]).append("\t")
					.append(instance.cpostags[index]).append("\t")
					.append(instance.postags[index]).append("\t")
					.append("-").append("\t")
					.append(instance.heads[index]).append("\t")
					.append(instance.heads[index] == 0 ? "ROOT" : instance.deprels[index]);
				writer.write(sb.toString());
				writer.newLine();
				cnt++;
			}
			if (cnt > 0) {
				writer.newLine();
			}
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
		TreebankToCoNLLConverter.convert("./data/ko/parser/ko-sejong.tree", "./target/test-data/ko/parser/ko-parser-from-sejong.conll");
	}

}
