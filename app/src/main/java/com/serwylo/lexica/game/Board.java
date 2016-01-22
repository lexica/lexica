/*
 *  Copyright (C) 2008-2009 Rev. Johnny Healey <rev.null@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.serwylo.lexica.game;

import net.healeys.trie.TransitionMap;
import net.healeys.trie.Trie;

public abstract class Board implements TransitionMap {
	private String[] board;

	public Board(String[] b) {
		board = b;
	}

	public synchronized String elementAt(int i) {
		return board[i];
	}

	public synchronized String elementAt(int x,int y) {
		return board[x+getWidth()*y];
	}

	public synchronized int valueAt(int i) {
		return Trie.charToOffset(board[i].charAt(0));
	}

	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		int size = getSize();
		for(int i=0;i<size-1;i++) {
			sb.append(board[i]);
			sb.append(",");
		}
		sb.append(board[size-1]);

		return sb.toString();
	}

	public synchronized void rotate() {
		String[] newbrd = new String[getSize()];

		int w = getWidth();

		for(int i=0;i<getSize();i++) {
			newbrd[w*(i%w)+((w-1)-(i/w))] = board[i];
		}

		board = newbrd;
	}

	public abstract int getSize();
	public abstract int getWidth();

	public abstract int transitions(int position);

}
