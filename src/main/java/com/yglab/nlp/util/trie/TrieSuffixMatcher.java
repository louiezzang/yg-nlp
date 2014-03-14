package com.yglab.nlp.util.trie;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for efficiently matching <code>String</code>s against a set of suffixes. Zero-length <code>Strings</code> are
 * ignored.
 */
public class TrieSuffixMatcher<V> extends Trie<V> {

	/**
	 * Creates a new <code>TrieSuffixMatcher</code> which will match <code>String</code>s with any suffix in the
	 * supplied array.
	 */
	public TrieSuffixMatcher() {
		super();
	}
	
	public void add(String key, V value) {
		addPatternBackward(key, value);
	}

	/**
	 * Returns true if the given <code>String</code> is matched by a suffix in the trie.
	 */
	public boolean matches(String input) {
		TrieNode<V> node = root;
		for (int i = input.length() - 1; i >= 0; i--) {
			node = node.getChild(input.charAt(i));
			if (node == null) {
				return false;
			}
			if (node.isTerminal()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the all substring list of <code>input<code> that is
	 * matched by a pattern in the trie, or <code>null<code> if no match
	 * exists.
	 */
	public List<V> allMatches(String input) {
		List<V> result = new ArrayList<V>();
		TrieNode<V> node = root;
		for (int i = input.length() - 1; i >= 0; i--) {
			node = node.getChild(input.charAt(i));
			if (node == null) {
				//return null;
				break;
			}
			if (node.isTerminal()) {
				result.add(node.value);
			}
		}
		
		return result;
	}

	/**
	 * Returns the shortest suffix of <code>input<code> that is matched,
	 * or <code>null<code> if no match exists.
	 */
	public V shortestMatch(String input) {
		TrieNode<V> node = root;
		for (int i = input.length() - 1; i >= 0; i--) {
			node = node.getChild(input.charAt(i));
			if (node == null) {
				return null;
			}
			if (node.isTerminal()) {
				return node.value;
			}
		}
		return null;
	}

	/**
	 * Returns the longest suffix of <code>input<code> that is matched,
	 * or <code>null<code> if no match exists.
	 */
	public V longestMatch(String input) {
		TrieNode<V> node = root;
		V result = null;
		for (int i = input.length() - 1; i >= 0; i--) {
			node = node.getChild(input.charAt(i));
			if (node == null) {
				break;
			}
			if (node.isTerminal()) {
				result = node.value;
			}
		}
		return result;
	}

}
