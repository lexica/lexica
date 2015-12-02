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

public class FiveByFiveBoard extends Board {
	private static final int SIZE = 25;
	private static final int WIDTH = 5;

	private static final int transitionBits[] = {
		0x62,0xe5,0x1ca,0x394,0x308,
		0xc43,0x1ca7,0x394e,0x729c,0x6118,
		0x18860,0x394e0,0x729c0,0xe5380,0xc2300,
		0x310c00,0x729c00,0xe53800,0x1ca7000,0x1846000,
		0x218000,0x538000,0xa70000,0x14e0000,0x8c0000
	};

	public FiveByFiveBoard(String[] b) {
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
