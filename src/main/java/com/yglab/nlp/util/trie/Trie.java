package com.yglab.nlp.util.trie;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Trie is a simple tree-based string matching.
 * 
 * @author Younggue Bae
 */
public abstract class Trie<V> {
	protected TrieNode<V> root;

	protected Trie() {
		this.root = new TrieNode<V>('\000', null, false);
	}

	/**
	 * Returns the next {@link TrieNode} visited, given that you are at <code>node</code>, and the the next character in
	 * the input is the <code>idx</code>'th character of <code>s</code>.
	 */
	protected final TrieNode<V> matchChar(TrieNode<V> node, String s, int idx) {
		return node.getChild(s.charAt(idx));
	}

	/**
	 * Adds any necessary nodes to the trie so that the given <code>String</code> can be decoded and the last character is
	 * represented by a terminal node. Zero-length <code>Strings</code> are ignored.
	 */
	protected final void addPatternForward(String key, V value) {
		TrieNode<V> node = root;
		int stop = key.length() - 1;
		int i;
		if (key.length() > 0) {
			for (i = 0; i < stop; i++) {
				node = node.getChildAddIfNotPresent(key.charAt(i), value, false);
			}
			node = node.getChildAddIfNotPresent(key.charAt(i), value, true);
		}
	}

	/**
	 * Adds any necessary nodes to the trie so that the given <code>String</code> can be decoded <em>in reverse</em> and
	 * the first character is represented by a terminal node. Zero-length <code>Strings</code> are ignored.
	 */
	protected final void addPatternBackward(String key, V value) {
		TrieNode<V> node = root;
		if (key.length() > 0) {
			for (int i = key.length() - 1; i > 0; i--) {
				node = node.getChildAddIfNotPresent(key.charAt(i), value, false);
			}
			node = node.getChildAddIfNotPresent(key.charAt(0), value, true);
		}
	}

	/**
	 * Returns true if the given <code>String</code> is matched by a pattern in the trie
	 */
	public abstract boolean matches(String input);

	/**
	 * Returns the all substring list of <code>input<code> that is
	 * matched by a pattern in the trie, or <code>null<code> if no match
	 * exists.
	 */
	public abstract List<V> allMatches(String input);
	
	/**
	 * Returns the shortest substring of <code>input<code> that is
	 * matched by a pattern in the trie, or <code>null<code> if no match
	 * exists.
	 */
	public abstract V shortestMatch(String input);

	/**
	 * Returns the longest substring of <code>input<code> that is
	 * matched by a pattern in the trie, or <code>null<code> if no match
	 * exists.
	 */
	public abstract V longestMatch(String input);


	/**
	 * Node class for the character tree.
	 */
	@SuppressWarnings({ "rawtypes", "hiding" })
	protected class TrieNode<V> implements Comparable {
		protected TrieNode<V>[] children;
		protected LinkedList<TrieNode<V>> childrenList;
		protected char nodeChar;
		protected V value;
		protected boolean terminal;

		/**
		 * Creates a new TrieNode, which contains the given <code>nodeChar</code>. If <code>isTerminal</code> is
		 * <code>true</code>, the new node is a <em>terminal</em> node in the trie.
		 */
		public TrieNode(char nodeChar, V value, boolean isTerminal) {
			this.nodeChar = nodeChar;
			this.value = value;
			this.terminal = isTerminal;
			this.childrenList = new LinkedList<TrieNode<V>>();
		}

		/**
		 * Returns <code>true</code> if this node is a <em>terminal</em> node in the trie.
		 */
		public boolean isTerminal() {
			return terminal;
		}

		/**
		 * Returns the child node of this node whose node-character is <code>nextChar</code>. If no such node exists, one
		 * will be is added. If <em>isTerminal</em> is <code>true</code>, the node will be a terminal node in the trie.
		 */
		public TrieNode<V> getChildAddIfNotPresent(char nextChar, V value, boolean isTerminal) {
			if (childrenList == null) {
				childrenList = new LinkedList<TrieNode<V>>();
				childrenList.addAll(Arrays.asList(children));
				children = null;
			}
			
			if (childrenList.size() == 0) {
				TrieNode<V> newNode = new TrieNode<V>(nextChar, value, isTerminal);
				childrenList.add(newNode);
				return newNode;
			}

			ListIterator<TrieNode<V>> iter = childrenList.listIterator();
			TrieNode<V> node = (TrieNode<V>) iter.next();
			while ((node.nodeChar < nextChar) && iter.hasNext()) {
				node = (TrieNode<V>) iter.next();
			}

			if (node.nodeChar == nextChar) {
				node.terminal = node.terminal | isTerminal;
				
				if (isTerminal) {	// This is very important! This updates the exist node value.
					node.value = value;
				}
				
				return node;
			}

			if (node.nodeChar > nextChar) {
				iter.previous();
			}

			TrieNode<V> newNode = new TrieNode<V>(nextChar, value, isTerminal);
			iter.add(newNode);
			return newNode;
		}

		/**
		 * Returns the child node of this node whose node-character is <code>nextChar</code>. If no such node exists,
		 * <code>null</code> is returned.
		 */
		@SuppressWarnings("unchecked")
		public TrieNode<V> getChild(char nextChar) {
			if (children == null) {
				children = (TrieNode<V>[]) childrenList.toArray(new TrieNode[childrenList.size()]);
				childrenList = null;
				Arrays.sort(children);
			}

			int min = 0;
			int max = children.length - 1;
			int mid = 0;
			while (min < max) {
				mid = (min + max) / 2;
				if (children[mid].nodeChar == nextChar) {
					return children[mid];
				}
				if (children[mid].nodeChar < nextChar) {
					min = mid + 1;
				}
				else {
					// if (children[mid].nodeChar > nextChar)
					max = mid - 1;
				}
			}

			if (min == max) {
				if (children[min].nodeChar == nextChar) {
					return children[min];
				}
			}

			return null;
		}

		public int compareTo(Object o) {
			TrieNode other = (TrieNode) o;
			if (this.nodeChar < other.nodeChar) {
				return -1;
			}
			if (this.nodeChar == other.nodeChar) {
				return 0;
			}
			// if (this.nodeChar > other.nodeChar)
			return 1;
		}
	}
	
}
