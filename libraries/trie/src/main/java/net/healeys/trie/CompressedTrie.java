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

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * This class reads a compressed trie file and deserializes it to a Trie.
 * It has a lower memory footprint than the regular Trie, and supports
 * the ability to prune the Trie while it is being read.
 * 
 * This is the class that should be used on mobile devices.
 */
public class CompressedTrie extends Trie {

	public CompressedTrie(InputStream input, int mask, int[] neighborMasks,
		boolean usWords, boolean ukWords) throws IOException {
		super();
		if(usWords) {
			mask |= US_WORD_BIT;
		}
		if(ukWords) {
			mask |= UK_WORD_BIT;
		}
		root = readTrie(new BufferedInputStream(input,8192),mask,
			neighborMasks,-1,true);
	}

	private Trie.TrieNode readTrie(InputStream input, int mask,
		int[] neighborMasks, int value, boolean store) throws IOException {

		int firstByte = input.read()<<24;

		if((firstByte&LEAF_BIT)!=0) {
			//This node is a tail.
			if(store) {
				if((firstByte&mask)!=0) {
					return EMPTY_LEAF.processWordBits(firstByte&mask);
				}
			} 
			return null;
		}

		int cBits = firstByte;
		cBits |= input.read()<<16;
		cBits |= input.read()<<8;
		cBits |= input.read();

		int nextBits = input.read()<<16;
		nextBits |= input.read()<<8;
		nextBits |= input.read();

		if(!store) {
			while(nextBits>0) {
				nextBits -= input.skip(nextBits);
			}
			return null;
		}

		int maskedBits = cBits&mask;
		if(value > -1) {
			maskedBits &= WORD_MASK|neighborMasks[value];
		}

		if(maskedBits != 0) {
			TrieNode ret = null;
			int childNumber = 0;
			for(int i=0;i<26;i++) {
				if((maskedBits&(1<<i))!=0) {
					TrieNode child = readTrie(input,mask,neighborMasks,i,
						true);
					if(child != null) {
						if(ret == null) {
							ret = new CompressedTrieNode(maskedBits);
						}
						ret.children[childNumber] = child;
						childNumber++;
					} else {
						maskedBits ^= (1<<i);
					}
				} else if((cBits&(1<<i))!=0) {
					readTrie(input,mask,null,-1,false);
				}
			}
			if(ret != null) {
				// node has valid children
				ret.childBits = maskedBits;
				return ret;
			} else if ((maskedBits&WORD_MASK) != 0) {
				// node has no valid children but is a word.
				return EMPTY_LEAF.processWordBits(cBits&WORD_MASK);
			}
			return null;
		}
		for(cBits&=LETTER_MASK;cBits!=0;cBits>>=1) {
			if((cBits&1)!=0) {
				readTrie(input,mask,null,-1,false);
			}
		}
		if((cBits&WORD_MASK)!=0) {
			return EMPTY_LEAF.processWordBits(cBits&WORD_MASK);
		}
		return null;
	}

	/**
	 * The CompressedTrieNode is like a normal TrieNode, but only allocates
	 * a large enough Array to store the number of children that it is known
	 * to have.
	 *
	 * Thus, the children cannot be indexed by their position in the alphabet as is
	 * the case in the non-compressed version. Instead,
	 */
	protected class CompressedTrieNode extends Trie.TrieNode {
		public CompressedTrieNode(int cBits) {
			childBits = cBits;
			
			children = new TrieNode[countChildrenFromChildBits()];
		}

		protected TrieNode childAt(int offset) {
			TrieNode child = null;
			int j = 0;
			for(int i = 0; i <= offset; i ++) {
				if((childBits & (1 << i)) != 0) {
					child = children[j];
					j++;
				} else {
					child = null;
				}
			}
			return child;
		}

		/**
		 * Counts the number of children of this node, by looking at the binary representation
		 * of the node.
		 *
		 * A node is represented in part by the first 26 bits of a 32 bit integer. Given not all
		 * of these bits are used, this method will count how many of those bits are equal to 1
		 * (i.e., have children represented by this node).
		 */
		private byte countChildrenFromChildBits() {
			int childBitsToCheck = childBits & LETTER_MASK;
			byte c = 0;
			for(childBitsToCheck &= LETTER_MASK; childBitsToCheck > 0;childBitsToCheck >>= 1)
				if((childBitsToCheck & 1) != 0) c++;

			return c;
		}
	}

}

