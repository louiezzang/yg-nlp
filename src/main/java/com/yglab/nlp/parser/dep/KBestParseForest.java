package com.yglab.nlp.parser.dep;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.parser.Parse;



/**
 * This class stores parse forest items and find the K-best parse trees.
 * 
 * @author Younggue Bae
 */
public class KBestParseForest {

	private ParseForestItem[][][][][] chart;
	private int K;
	private int length;

	/**
	 * Initializes KBestParseForest.
	 * 
	 * @param K
	 * @param length
	 */
	public KBestParseForest(int K, int length) {
		this.chart = new ParseForestItem[length][length][2][2][K];
		this.K = K;
		this.length = length;
	}
	
	/**
	 * Adds parse forest chart items.
	 * 
	 * @param s
	 * @param t
	 * @param direction
	 * @param score
	 * @return
	 */
	public boolean add(int s, int t, int direction, double score) {

		boolean added = false;

		if (chart[s][t][direction][1][0] == null) {
			for (int i = 0; i < K; i++) {
				chart[s][t][direction][1][i] = new ParseForestItem(s, t, direction, 1, Double.NEGATIVE_INFINITY, null);
			}
		}

		if (chart[s][t][direction][1][K - 1].getScore() > score) {
			return false;
		}

		for (int i = 0; i < K; i++) {
			if (chart[s][t][direction][1][i].getScore() < score) {
				ParseForestItem tmp = chart[s][t][direction][1][i];
				chart[s][t][direction][1][i] = new ParseForestItem(s, t, direction, 1, score, null);
				for (int j = i + 1; j < K && tmp.getScore() != Double.NEGATIVE_INFINITY; j++) {
					ParseForestItem tmp1 = chart[s][t][direction][1][j];
					chart[s][t][direction][1][j] = tmp;
					tmp = tmp1;
				}
				added = true;
				break;
			}
		}

		return added;
	}

	/**
	 * Adds parse forest chart items.
	 * 
	 * @param s
	 * @param t
	 * @param direction
	 * @param completeness
	 * @param score
	 * @param parse
	 * @param left
	 * @param right
	 * @return
	 */
	public boolean add(int s, int t, int direction, int completeness, double score, Parse parse,
			ParseForestItem left, ParseForestItem right) {

		boolean added = false;

		if (chart[s][t][direction][completeness][0] == null) {
			for (int i = 0; i < K; i++) {
				chart[s][t][direction][completeness][i] = new ParseForestItem(s, t, direction,
						completeness, parse);
			}
		}

		if (chart[s][t][direction][completeness][K - 1].getScore() > score) {
			return false;
		}

		for (int i = 0; i < K; i++) {
			if (chart[s][t][direction][completeness][i].getScore() < score) {
				ParseForestItem tmp = chart[s][t][direction][completeness][i];
				chart[s][t][direction][completeness][i] = new ParseForestItem(s, t, direction,
						completeness, score, parse, left, right);
				for (int j = i + 1; j < K && tmp.getScore() != Double.NEGATIVE_INFINITY; j++) {
					ParseForestItem tmp1 = chart[s][t][direction][completeness][j];
					chart[s][t][direction][completeness][j] = tmp;
					tmp = tmp1;
				}
				added = true;
				break;
			}

		}

		return added;
	}

	/**
	 * Returns a parse forest chart item.
	 * 
	 * @param s
	 * @param t
	 * @param direction
	 * @param completeness
	 * @return
	 */
	public ParseForestItem getItem(int s, int t, int direction, int completeness) {
		return getItem(s, t, direction, completeness, 0);
	}

	/**
	 * Returns a parse forest chart item.
	 * 
	 * @param s
	 * @param t
	 * @param direction
	 * @param completeness
	 * @param k
	 * @return
	 */
	public ParseForestItem getItem(int s, int t, int direction, int completeness, int k) {
		if (chart[s][t][direction][completeness][k] != null) {
			return chart[s][t][direction][completeness][k];
		}
		return null;
	}

	/**
	 * Returns the parse forest chart items.
	 * 
	 * @param s
	 * @param t
	 * @param direction
	 * @param completeness
	 * @return
	 */
	public ParseForestItem[] getItems(int s, int t, int direction, int completeness) {
		if (chart[s][t][direction][completeness][0] != null) {
			return chart[s][t][direction][completeness];
		}
		return null;
	}

	/**
	 * Returns the best parse.
	 * 
	 * @return
	 */
	public List<Parse> getBestParse() {
		List<Parse> bestParse = new ArrayList<Parse>();
		ParseForestItem pfi = chart[0][length-1][1][1][0];

		String result = traverse(bestParse, pfi);
		System.out.println("*best parse == " + result);
	
		for (Parse parse : bestParse) {
			System.out.println(parse);
		}
		
		return bestParse;
	}
	
	/**
	 * Returns the K-best parses.
	 * 
	 * @return
	 */
	public List<List<Parse>> getBestParses() {
		List<List<Parse>> kBestParses = new ArrayList<List<Parse>>();
		for (int k = 0; k < K; k++) {
			ParseForestItem pfi = chart[0][length-1][1][1][k];
			if (pfi.getScore() != Double.NEGATIVE_INFINITY) {
				List<Parse> bestParse = new ArrayList<Parse>();
				String result = traverse(bestParse, pfi);
				System.out.println("*[" + k + "]best parse == " + result);
				kBestParses.add(bestParse);
			}
		}
		return kBestParses;
	}

	
	/**
	 * Traverses the graph to find the best parse tree structure.
	 * 
	 * @param bestParse
	 * @param pfi
	 * @return
	 */
	private String traverse(List<Parse> bestParse, ParseForestItem pfi) {
		if (pfi.getLeft() == null) {
			return "";
		}
		//System.out.println(pfi + "\t=> " + pfi.getLeft() + "\t|\t" + pfi.getRight() );
		
		if (pfi.getCompleteness() == 1) {
			return (traverse(bestParse, pfi.getLeft()) + " " + traverse(bestParse, pfi.getRight())).trim();
		}
		else if (pfi.getDirection() == 1) {
			bestParse.add(pfi.getParse());
			return ((traverse(bestParse, pfi.getLeft()) + " " + traverse(bestParse, pfi.getRight())).trim() + " " + pfi.s + "|" + pfi.t)
					.trim();
		} 
		else {
			bestParse.add(pfi.getParse());
			return (pfi.t + "|" + pfi.s + " " + (traverse(bestParse, pfi.getLeft()) + " " + traverse(bestParse, pfi.getRight()))
					.trim()).trim();
		}
	}

	/**
	 * Returns pairs of indexes and -1,-1 if < K pairs.
	 * 
	 * @param items1
	 * @param items2
	 * @return
	 */
	public int[][] getKBestPairs(ParseForestItem[] items1, ParseForestItem[] items2) {
		// in this case K = items1.length

		boolean[][] beenPushed = new boolean[K][K];

		int[][] result = new int[K][2];
		for (int i = 0; i < K; i++) {
			result[i][0] = -1;
			result[i][1] = -1;
		}

		if (items1 == null || items2 == null || items1[0] == null || items2[0] == null)
			return result;

		BinaryHeap heap = new BinaryHeap(K + 1);
		int n = 0;
		ValueIndexPair vip = new ValueIndexPair(items1[0].getScore() + items2[0].getScore(), 0, 0);

		heap.add(vip);
		beenPushed[0][0] = true;

		while (n < K) {
			vip = heap.removeMax();

			if (vip.val == Double.NEGATIVE_INFINITY)
				break;

			result[n][0] = vip.i1;
			result[n][1] = vip.i2;

			n++;
			if (n >= K)
				break;

			if (!beenPushed[vip.i1 + 1][vip.i2]) {
				heap.add(new ValueIndexPair(items1[vip.i1 + 1].getScore() + items2[vip.i2].getScore(),
						vip.i1 + 1, vip.i2));
				beenPushed[vip.i1 + 1][vip.i2] = true;
			}
			if (!beenPushed[vip.i1][vip.i2 + 1]) {
				heap.add(new ValueIndexPair(items1[vip.i1].getScore() + items2[vip.i2 + 1].getScore(),
						vip.i1, vip.i2 + 1));
				beenPushed[vip.i1][vip.i2 + 1] = true;
			}

		}

		return result;
	}
}

/**
 * ValueIndexPair
 */
class ValueIndexPair {
	public double val;
	public int i1, i2;

	public ValueIndexPair(double val, int i1, int i2) {
		this.val = val;
		this.i1 = i1;
		this.i2 = i2;
	}

	public int compareTo(ValueIndexPair other) {
		if (val < other.val)
			return -1;
		if (val > other.val)
			return 1;
		return 0;
	}

}

/**
 * Max Heap. 
 * We know that never more than K elements on Heap.
 */
class BinaryHeap {
	private int DEFAULT_CAPACITY;
	private int currentSize;
	private ValueIndexPair[] theArray;

	public BinaryHeap(int def_cap) {
		DEFAULT_CAPACITY = def_cap;
		theArray = new ValueIndexPair[DEFAULT_CAPACITY + 1];
		// theArray[0] serves as dummy parent for root (who is at 1)
		// "largest" is guaranteed to be larger than all keys in heap
		theArray[0] = new ValueIndexPair(Double.POSITIVE_INFINITY, -1, -1);
		currentSize = 0;
	}

	public ValueIndexPair getMax() {
		return theArray[1];
	}

	private int parent(int i) {
		return i / 2;
	}

	private int leftChild(int i) {
		return 2 * i;
	}

	private int rightChild(int i) {
		return 2 * i + 1;
	}

	public void add(ValueIndexPair e) {

		// bubble up:
		int where = currentSize + 1; // new last place
		while (e.compareTo(theArray[parent(where)]) > 0) {
			theArray[where] = theArray[parent(where)];
			where = parent(where);
		}
		theArray[where] = e;
		currentSize++;
	}

	public ValueIndexPair removeMax() {
		ValueIndexPair min = theArray[1];
		theArray[1] = theArray[currentSize];
		currentSize--;
		boolean switched = true;
		// bubble down
		for (int parent = 1; switched && parent < currentSize;) {
			switched = false;
			int leftChild = leftChild(parent);
			int rightChild = rightChild(parent);

			if (leftChild <= currentSize) {
				// if there is a right child, see if we should bubble down there
				int largerChild = leftChild;
				if ((rightChild <= currentSize)
						&& (theArray[rightChild].compareTo(theArray[leftChild])) > 0) {
					largerChild = rightChild;
				}
				if (theArray[largerChild].compareTo(theArray[parent]) > 0) {
					ValueIndexPair temp = theArray[largerChild];
					theArray[largerChild] = theArray[parent];
					theArray[parent] = temp;
					parent = largerChild;
					switched = true;
				}
			}
		}
		return min;
	}

}
