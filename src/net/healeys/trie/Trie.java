/*
 * Copyright 2008-2009 Rev. Johnny Healey <rev.null@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.healeys.trie;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

import android.util.Log;

/**
 * This is the base implementation of the Trie.
 * It allows a trie to be created and modified in memory and then written to
 * a file in a compressed format.  It also contains methods that can be used
 * to find all of the words on a board.
 */
public class Trie implements WordFilter {

	protected static final int LEAF_BIT = 0x80000000;
	protected static final int US_WORD_BIT = 0x40000000;
	protected static final int UK_WORD_BIT = 0x20000000;
	protected static final int WORD_MASK = 0x60000000;
	protected static final int LETTER_MASK = 0x03ffffff;

	protected final TrieLeaf EMPTY_LEAF;
	protected final TrieLeaf US_WORD_LEAF;
	protected final TrieLeaf UK_WORD_LEAF;
	protected final TrieLeaf DUAL_WORD_LEAF;

	private int nodeCount;
	private int tailCount;

	protected TrieNode root;
	
	private static String TAG = "Trie";

	public Trie() {
		nodeCount = 0;

		EMPTY_LEAF = new TrieLeaf();
		US_WORD_LEAF = new TrieLeaf(US_WORD_BIT);
		UK_WORD_LEAF = new TrieLeaf(UK_WORD_BIT);
		DUAL_WORD_LEAF = new TrieLeaf(US_WORD_BIT|UK_WORD_BIT);

		root = EMPTY_LEAF;
	}

	public void addWord(String w, boolean usWord, boolean ukWord) {
		int wordBits = 0;
		if(usWord) wordBits |= US_WORD_BIT;
		if(ukWord) wordBits |= UK_WORD_BIT;

		root = root.addSuffix(w,0,wordBits);
	}

	public boolean isWord(String w, boolean usWord, boolean ukWord) {
		int wordMask = 0;
		if(usWord) wordMask |= US_WORD_BIT;
		if(ukWord) wordMask |= UK_WORD_BIT;
		return root.isWord(w,0,wordMask);
	}

	public boolean isWord(String w) {
		return isWord(w,true,true);
	}

	private static void writeInt(OutputStream out, int i) throws IOException {
		out.write(i>>24);
		out.write(i>>16);
		out.write(i>>8);
		out.write(i);
	}

	private static void writeThree(OutputStream out, int i) throws IOException {
		out.write(i>>16);
		out.write(i>>8);
		out.write(i);
	}

	/**
	 * This is the base class for the Nodes that compose a Trie.
	 */
	protected class TrieNode {
		int childBits;

		TrieNode children[];

		TrieNode() {
			childBits = 0;
			children = new TrieNode[26];

			nodeCount++;
		}

		TrieNode(int cBits) {
			this();
			childBits = cBits;
		}

		void writeNode(OutputStream out) throws IOException {

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			int j=0;
			for(int i=childBits&LETTER_MASK;i!=0;i>>=1) {
				if((i&1)!=0) {
					children[j].writeNode(os);
				}
				j++;
			}

			writeInt(out,childBits);
			writeThree(out,os.size());
			out.write(os.toByteArray());
		}

		TrieNode addSuffix(String word, int i, int wordBits) {
			if(i == word.length()) {
				childBits |= wordBits;
			} else {
				int ci = ctoi(word.charAt(i));

				if((childBits & (1 << ci)) == 0) {
					childBits |= 1 << ci;
					children[ci] = EMPTY_LEAF;
					tailCount += 1;
				}
				if(ci == 16) { // Q is always followed by U
					children[ci] = children[ci].addSuffix(word,i+2,wordBits);
				} else {
					children[ci] = children[ci].addSuffix(word,i+1,wordBits);
				}
			}

			return this;
		}

		boolean isWord() {
			return usWord()||ukWord();
		}

		boolean usWord() {
			return (childBits&US_WORD_BIT) != 0;
		}

		boolean ukWord() {
			return (childBits&UK_WORD_BIT) != 0;
		}

		boolean isTail() {
			return (childBits&LEAF_BIT) != 0;
		}

		private boolean checkAgainstMask(int wordMask) {
			return (wordMask & childBits) != 0;
		}

		protected TrieNode childAt(int i) {
			return children[i];
		}

		boolean isWord(String w, int i, int wordMask) {
			if(i == w.length()) {
				return checkAgainstMask(wordMask);
			}
			int ci = ctoi(w.charAt(i));
			if((childBits & (1 << ci)) == 0) {
				return false;
			}
			if(ci == 16) {
				return childAt(ci).isWord(w,i+2,wordMask);
			} else {
				return childAt(ci).isWord(w,i+1,wordMask);
			}
		}

	}

	/**
	 * The TrieLeaf is a TrieNode that has no children.
	 */
	protected class TrieLeaf extends TrieNode {
		TrieLeaf(int cBits) {
			super(cBits|LEAF_BIT);
		}
		
		TrieLeaf() {
			super();
		}

		TrieNode addSuffix(String word, int i, int wordBits) {
			if(i == word.length()) {
				return processWordBits(childBits|wordBits);
			} else {
				TrieNode t = new TrieNode(childBits&(~LEAF_BIT));
				tailCount -= 1;
				return t.addSuffix(word,i,wordBits);
			}
		}

		TrieNode processWordBits(int wordBits) {
			switch(wordBits & WORD_MASK) {
				case US_WORD_BIT:
					return US_WORD_LEAF;
				case UK_WORD_BIT:
					return UK_WORD_LEAF;
				case WORD_MASK:
					return DUAL_WORD_LEAF;
				case 0:
					return EMPTY_LEAF;
			}
			
			return null;
		}

		void writeNode(OutputStream out) throws IOException {
			out.write(childBits>>24);
		}
	}

	public static byte countBits(int b) {
		byte c = 0;
		for(b&=LETTER_MASK;b>0;b>>=1)
			if((b&1)!=0) c++;

		return c;
	}

	public void write(OutputStream out) throws IOException {

		root.writeNode(out);

		out.close();
	}

	public static int ctoi(char c) {
		if(c >= 'a' && c <= 'z') return c - 'a';
		if(c >= 'A' && c <= 'Z') return c - 'A';
		return -1;
	}

	public static char itoc(int i) {
		return (char) (i + 'A');
	}

	private void recursiveSolver(TransitionMap m,WordFilter filter,
		TrieNode node,int pos,int unUsed, StringBuilder prefix, 
		LinkedHashMap<String,Solution> ret) {

		if(node.isWord()) {
			//String w = prefix.toString();
			String w = new String(prefix);
			int mask = ~unUsed | (1<<pos);
			if(filter == null) {
				ret.put(w,new Solution(w,mask));
			} else if (filter.isWord(w)) {
				ret.put(w,new Solution(w,mask));
			}
		}

		if(node.isTail()) {
			return;
		}
		
		unUsed &= ~(1<<pos);
		int available = unUsed & m.transitions(pos);

		if(available == 0) return;

		for(int i=0;i<m.getSize();i++) {
			if((available&(1<<i)) == 0)
				continue;

			int value = m.valueAt(i);
			if((node.childBits&(1<<value)) == 0)
				continue;
					
			prefix.append(itoc(value));
			if(value == 16)
				prefix.append('U');

			recursiveSolver(m,filter,node.childAt(value),i,unUsed,prefix,ret);

			prefix.deleteCharAt(prefix.length()-1);
			if(value == 16)
				prefix.deleteCharAt(prefix.length()-1);
		}

	}

	public LinkedHashMap<String,Solution> solver(TransitionMap m) {
		return solver(m,null);
	}

	public LinkedHashMap<String,Solution> solver(TransitionMap m, 
		WordFilter filter) {
		LinkedHashMap<String,Solution> ret = 
			new LinkedHashMap<String,Solution>();
		StringBuilder prefix = new StringBuilder(m.getSize()+1);

		int unused = 0;
		for(int i=0;i<m.getSize();i++) {
			unused <<= 1;
			unused++;
		}

		for(int i=0;i<m.getSize();i++) {
			int value = m.valueAt(i);
			if((root.childBits&(1<<value)) == 0)
				continue;
					
			prefix.append(itoc(value));
			if(value == 16)
				prefix.append('U');

			recursiveSolver(m,filter,root.childAt(value),i,unused,prefix,ret);

			prefix.deleteCharAt(prefix.length()-1);
			if(value == 16)
				prefix.deleteCharAt(prefix.length()-1);
		}

		return ret;
	}

	public class Solution {
		private String word;
		private int mask;

		private Solution(String word, int mask) {
			Log.d(TAG,"Solution: "+word+" "+mask);
			this.word = word;
			this.mask = mask;
		}

		public String getWord() {
			return word;
		}

		public int getMask() {
			return mask;
		}
	}

}

