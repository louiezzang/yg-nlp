package com.yglab.nlp.sbd;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.util.LangUtil;


/**
 * It finds the end of sentence positions with the given EOS characters.
 * 
 * @author Younggue Bae
 */
public class EOSFinder {

	private char eosCharacters[];

	public EOSFinder(char eosCharacters[]) {
		this.eosCharacters = eosCharacters;
	}

	public List<Integer> getPositions(String s) {
		return getPositions(s.toCharArray());
	}

	public List<Integer> getPositions(StringBuffer buf) {
		return getPositions(buf.toString().toCharArray());
	}

	public List<Integer> getPositions(char[] cbuf) {
		List<Integer> positions = new ArrayList<Integer>();
		char[] eosCharacters = getEndOfSentenceCharacters();

		for (int i = 0; i < cbuf.length; i++) {
			for (char eosCharacter : eosCharacters) {
				if (cbuf[i] == eosCharacter && !isExceptionalCase(cbuf, i)) {
					positions.add(i);
					break;
				}
			}
		}
		return positions;
	}

	private boolean isExceptionalCase(char[] cbuf, int position) {
		if (position >= 1) {
			char ch1 = cbuf[position - 1];
			if (LangUtil.isAlphabet(ch1) && Character.isUpperCase(ch1)) {
				return true;
			}
		}

		return false;
	}

	public char[] getEndOfSentenceCharacters() {
		return eosCharacters;
	}

}
