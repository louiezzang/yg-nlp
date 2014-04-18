package com.yglab.nlp.util.corpus;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.postag.POSSample;
import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.postag.lang.ko.KoreanStemmer;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;



/**
 * This class generates morpheme dictionary from pos train sample file.
 * 
 * @author Younggue Bae
 * 
 */
public class MorphemeDictionaryGenerator {

	/**
	 * Extracts dictionary from train sample file and appends them into dictionary file by the specified pos tags.
	 * 
	 * @param trainSampleFilename
	 *          The train sample file to extract
	 * @param dicFilename
	 *          The dictionary file to append
	 * @param posRegexPattern
	 *          The pos regex pattern to extract from the train sample file
	 * @throws IOException
	 */
	public static final void generate(String trainSampleFilename, String dicFilename, String posRegexPattern)
			throws IOException {
		Map<String, String> dic = loadDictionary(dicFilename);
		dic = extractFromTrainSamples(trainSampleFilename, posRegexPattern, dic);
		writeToDictionary(dicFilename, dic);
	}

	private static Map<String, String> loadDictionary(String filename) throws IOException {
		Map<String, String> dic = new HashMap<String, String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			//BufferedReader in = new BufferedReader(new InputStreamReader(
			//    new FileInputStream(filename), "UTF-8"));

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				if (line.trim().length() == 0 || line.startsWith("#") || line.startsWith("//")) {
					continue;
				}

				String[] field = line.split("\t");
				String word = field[0];
				String pos = field[1];

				dic.put(word, pos);
			}
			in.close();
		} catch (FileNotFoundException e) {
			return dic;
		}

		return dic;
	}

	private static Map<String, String> extractFromTrainSamples(String filename, String posRegexPattern,
			Map<String, String> dic) throws IOException {
		Pattern posPattern = Pattern.compile(posRegexPattern);
		KoreanStemmer stemmer = new KoreanStemmer();

		POSSampleParser parser = new POSSampleParser();
		BufferedReader in = new BufferedReader(new FileReader(filename));

		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.trim().length() == 0) {
				continue;
			}
			POSSample sample = parser.parse(line);
			String[] tokens = sample.getSentence();
			String[] tags = sample.getLabels();

			for (int ti = 0; ti < tokens.length; ti++) {
				String token = tokens[ti];
				String tag = tags[ti];

				String rootWordPos = null;
				String[] morphTags = tag.split(",");
				for (int mi = morphTags.length - 1; mi >= 0; mi--) {
					String morphTag = morphTags[mi];
					String morphPos = POSSampleParser.parsePos(morphTag);
					// only if tags contains "_" operator
					String morphWord = POSSampleParser.parseMorpheme(morphTag);

					if (morphWord != null) {
						if (!dic.containsKey(morphWord)) {
							Matcher posMatcher = posPattern.matcher(morphPos);
							if (posMatcher.find()) {
								dic.put(morphWord, morphPos);
							}
						}

						if (mi > 0) {
							token = MorphemeUtil.truncateRight(token, morphWord);
						} else {
							token = morphWord;
						}
					} else {
						token = String.valueOf(stemmer.stem(token, morphTag));
					}

					rootWordPos = morphPos;
				}

				Matcher posMatcher = posPattern.matcher(rootWordPos);
				if (posMatcher.find()) {
					dic.put(token, rootWordPos);
				}
			}
		}
		in.close();

		return dic;
	}

	private static void writeToDictionary(String filename, Map<String, String> dic) throws IOException {
		dic = sortByKey(dic);
		fileCopy(filename, filename + ".bak");
		mkdirs(filename);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), "UTF-8"));

		for (Map.Entry<String, String> entry : dic.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
			writer.write(entry.getKey() + "\t" + entry.getValue());
			writer.newLine();
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
	
	private static Map<String, String> sortByKey(Map<String, String> dic) { 
		Map<String, String> treeMap = new TreeMap<String, String>(dic);
		return treeMap;
	}

	private static void fileCopy(String filename1, String filename2) throws IOException {
		if (!new File(filename1).exists()) {
			return;
		}
		FileInputStream fis = new FileInputStream(filename1);
		FileOutputStream fos = new FileOutputStream(filename2);

		int data = 0;
		while ((data = fis.read()) != -1) {
			fos.write(data);
		}
		fis.close();
		fos.close();
	}

}
