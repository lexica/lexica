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

package net.healeys.lexic.view;

import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.Iterator;

public class VisibilityToggle implements View.OnClickListener {
	private boolean toggle;
	private LinkedList<View> uniqueWordViews;
	
	private int hideText;
	private int showText;

	public VisibilityToggle(int hideText, int showText) {
		toggle = false;
		uniqueWordViews = new LinkedList<View>();

		this.hideText = hideText;
		this.showText = showText;
	}

	public void add(View v) {
		uniqueWordViews.add(v);
		if(toggle) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.GONE);
		}
	}

	public void onClick(View v) {
		toggle = !toggle;
		Iterator<View> vi = uniqueWordViews.iterator();
		while(vi.hasNext()) {
			if(toggle) {
				vi.next().setVisibility(View.VISIBLE);
			} else {
				vi.next().setVisibility(View.GONE);
			}
		}

		Button b = (Button) v;
		if(toggle) {
			b.setText(hideText);
		} else {
			b.setText(showText);
		}
	}

}

