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

public class FourByFourBoard extends Board {
	private static final int SIZE = 16;
	private static final int WIDTH = 4;

	private static final int transitionBits[] = {
		0x32,0x75,0xea,0xc4,
		0x323,0x757,0xeae,0xc4c,
		0x3230,0x7570,0xeae0,0xc4c0,
		0x2300,0x5700,0xae00,0x4c00
	};

	public FourByFourBoard(String[] b) {
		super(b);
	}

	public int getSize() {
		return SIZE;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int transitions(int position) {
		return transitionBits[position];
	}

}
