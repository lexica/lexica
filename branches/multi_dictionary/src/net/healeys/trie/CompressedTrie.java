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

	private int nodesRead;
	private int nodesSaved;

	private static int[] FOLLOW_MASKS = {
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff
	};

	public CompressedTrie(InputStream input) throws IOException {
		this(input,0x3ffffff,true,true);
	}

	public CompressedTrie(InputStream input, int mask, int dictMask)
		throws IOException {
		this(input,mask,FOLLOW_MASKS,dictMask);
	}
	
	@Deprecated
	public CompressedTrie(InputStream input, int mask, boolean usWords,
		boolean ukWords) throws IOException {
		this(input,mask,FOLLOW_MASKS,usWords,ukWords);
	}

	public CompressedTrie(InputStream input, int mask, int[] neighborMasks,
		int dictMask) throws IOException {
		super();
		// TODO: insert this check EVERYWHERE a dictMask is used as parameter
		// Or change the parameter to a type(?)
		if ((dictMask & ~WORD_MASK) > 0) throw new Error("Illegal dictMask");
		nodesRead = 0;
		nodesSaved = 0;
		mask |= dictMask;
		root = readTrie(new BufferedInputStream(input,8192),mask,
			neighborMasks,-1,true,true);
	}
	
	@Deprecated
	public CompressedTrie(InputStream input, int mask, int[] neighborMasks,
		boolean usWords, boolean ukWords) throws IOException {
		super();
		nodesRead = 0;
		nodesSaved = 0;
		if(usWords) {
			mask |= DICTIONARY_BITS[0];
		}
		if(ukWords) {
			mask |= DICTIONARY_BITS[1];
		}
		root = readTrie(new BufferedInputStream(input,8192),mask,
			neighborMasks,-1,true,true);
	}

	private Trie.TrieNode readTrie(InputStream input, int mask, 
		int[] neighborMasks, int value,boolean store, 
		boolean isRoot) throws IOException {
		nodesRead++;

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
						true,false);
					if(child != null) {
						if(ret == null) {
							ret = new CompressedTrieNode(maskedBits);
							nodesSaved++;
						}
						ret.children[childNumber] = child;
						childNumber++;
					} else {
						maskedBits ^= (1<<i);
					}
				} else if((cBits&(1<<i))!=0) {
					readTrie(input,mask,null,-1,false,false);
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
				readTrie(input,mask,null,-1,false,false);
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
	 */
	protected class CompressedTrieNode extends Trie.TrieNode {
		public CompressedTrieNode(int cBits) {
			childBits = cBits;
			
			children = new TrieNode[countBits(cBits&LETTER_MASK)];
		}

		protected TrieNode childAt(int index) {
			TrieNode ret = null;
			int j=0;
			for(int i=0;i<=index;i++) {
				if((childBits&(1<<i)) != 0) {
					ret = children[j];
					j++;
				} else {
					ret = null;
				}
			}
			return ret;
		}
	}

}

