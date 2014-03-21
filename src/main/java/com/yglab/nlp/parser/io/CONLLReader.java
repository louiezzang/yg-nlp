package com.yglab.nlp.parser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.parser.ParseSample;



/**
 * A reader for files in CoNLL format.
 * 
 * @author Younggue Bae
 */
public class CONLLReader {

	protected BufferedReader inputReader;
	protected boolean labeled = true;
	protected List<String> labels = new ArrayList<String>();

	public CONLLReader() {

	}

	public boolean startReading(String file) throws IOException {
		labeled = fileContainsLabels(file);
		InputStream is = getClass().getResourceAsStream(file);

		if (is != null) {
			inputReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
		}
		
		return labeled;
	}

	public boolean isLabeled() {
		return labeled;
	}
	
	public String[] getLabels() {
		if (labeled) {
			return labels.toArray(new String[labels.size()]);
		}
		else {
			return null;
		}
	}

	protected boolean fileContainsLabels(String file) throws IOException {
		BufferedReader in = null;
		InputStream is = getClass().getResourceAsStream(file);

		if (is != null) {
			in = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
		}
		
		String line = in.readLine();
		in.close();

		if (line.split("\t").length == 8) {
			return true;
		}
		else {
			return false;
		}
	}

	public ParseSample getNext() throws IOException {
		ArrayList<String[]> lineList = new ArrayList<String[]>();

		String line = inputReader.readLine();
		while (line != null && !line.trim().equals("") && !line.startsWith("*")) {
			lineList.add(line.split("\t"));
			line = inputReader.readLine();
		}

		int length = lineList.size();

		if (length == 0) {
			inputReader.close();
			return null;
		}

		String[] forms = new String[length + 1];
		String[] lemmas = new String[length + 1];
		String[] cpos = new String[length + 1];
		String[] pos = new String[length + 1];
		String[][] feats = new String[length + 1][];
		String[] deprels = new String[length + 1];
		int[] heads = new int[length + 1];

		forms[0] = "<root>";
		lemmas[0] = "<root-LEMMA>";
		cpos[0] = "<root-CPOS>";
		pos[0] = "<root-POS>";
		deprels[0] = "<no-type>";
		heads[0] = -1;

		for (int i = 0; i < length; i++) {
			String[] field = lineList.get(i);
			forms[i + 1] = field[1];
			lemmas[i + 1] = field[2];
			cpos[i + 1] = field[3];
			pos[i + 1] = field[4];
			feats[i + 1] = field[5].split("\\|");
			heads[i + 1] = Integer.parseInt(field[6]);
			if (labeled) {
				deprels[i + 1] = field[7];
				
				if (!labels.contains(deprels[i + 1])) {
					labels.add(deprels[i + 1]);
				}
			}
		}

		feats[0] = new String[feats[1].length];
		for (int i = 0; i < feats[1].length; i++) {
			feats[0][i] = "<root-feat>" + i;
		}

		return new ParseSample(forms, lemmas, cpos, pos, feats, deprels, heads);
	}

}
