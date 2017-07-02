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
 * The TransitionMap is an abstract representation of a game board.
 * It supports up to 32 positions and provides information about the value
 * at a particular position and the ability for positions to be connected.
 */
public interface TransitionMap {

	boolean canTransition(int fromX, int fromY, int toX, int toY);
	
	/**
	 * Provides the value (letter) stored at a particular position.
	 *
	 * @param	position	the id of a position
	 * @return				the value stored at that position
	 */
	String valueAt(int position);

	/**
	 * Provides the number of positions available on a particular board.
	 *
	 * @return				the number of positions
	 */
	int getSize();

	int getWidth();

	/**
	 * Whether or not the user is allowed to backrack to squares they've already visited.
	 */
	boolean canRevisit();

}
