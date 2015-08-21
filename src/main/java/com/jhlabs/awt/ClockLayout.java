/*
     Copied from http://www.jhlabs.com/java/layout/index.html

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this code except in compliance with the License.
     You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
     Unless required by applicable law or agreed to in writing,
     software distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and limitations under the License.
 */

package com.jhlabs.awt;

import java.awt.*;

public class ClockLayout extends ConstraintLayout {

	public ClockLayout() {
	}
	
	public void measureLayout(Container target, Dimension dimension, int type)  {
		int count = target.getComponentCount();
		if (count > 0) {
			Insets insets = target.getInsets();
			Dimension size = target.getSize();
			int x = 0;
			int y = 0;
			int maxWidth = 0;
			int maxHeight = 0;
			double radius = Math.min(size.width-insets.left-insets.right, size.height-insets.top-insets.bottom) / 2;

			Dimension[] sizes = new Dimension[count];

			for (int i = 0; i < count; i++) {
				Component c = target.getComponent(i);
				if (includeComponent(c)) {
					Dimension d = getComponentSize(c, type);
					int w = d.width;
					int h = d.height;
					sizes[i] = d;

					maxWidth = Math.max(maxWidth, w);
					maxHeight = Math.max(maxHeight, h);
				}
			}

			if (dimension != null) {
				dimension.width = (int)(2*radius-maxWidth);
				dimension.height = (int)(2*radius-maxHeight);
			} else {
				int mx = (size.width-insets.left-insets.right-2*hMargin)/2;
				int my = (size.height-insets.top-insets.bottom-2*vMargin)/2;
				x = 0;
				y = 0;
				
				radius -= Math.max(maxWidth, maxHeight)/2;
				for (int i = 0; i < count; i++) {
					Component c = target.getComponent(i);
					if (includeComponent(c)) {
						Dimension d = sizes[i];
						int w = d.width;
						int h = d.height;
				
						double angle = 2 * Math.PI * i / count;
						x = mx + (int)(Math.sin(angle) * radius);
						y = my - (int)(Math.cos(angle) * radius);
						c.setBounds(insets.left+hMargin+x-w/2, insets.top+vMargin+y-h/2, w, h);
					}
				}
			}
		}

	}

}
