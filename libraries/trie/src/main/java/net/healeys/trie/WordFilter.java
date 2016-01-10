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

/**
 * An abstract filter used to identify whether a particular string is a word.
 */
public interface WordFilter {
	/**
	 * Identifies whether or not a string of characters is actually a
	 * valid word.
	 *
	 * @param	word	A string of characters believed to be a word
	 * @return			Whether or not the string is a word
	 */
	boolean isWord(String word);
}
