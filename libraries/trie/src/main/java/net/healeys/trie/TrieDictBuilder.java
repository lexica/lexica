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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * This class is used to read in a list of words and generate a binary
 * trie file.
 */
public class TrieDictBuilder {

	private static void readFileIntoTrie(String dictFile, Trie trie,
		boolean usWord, boolean ukWord) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(dictFile));
			String line;
			while((line = br.readLine()) != null) {
				//System.out.println(line);
				trie.addWord(line,usWord,ukWord);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String [] Args) {

		if(Args.length < 3) {
			System.out.println("Usage: java -jar dicttool usdict ukdict outfile");
			System.exit(1);
		}

		Trie outTrie = new Trie();

		readFileIntoTrie(Args[0],outTrie,true,false);
		readFileIntoTrie(Args[1],outTrie,false,true);
		
		try {
			FileOutputStream of = new FileOutputStream(Args[2],false);
			outTrie.write(new DataOutputStream(of));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

