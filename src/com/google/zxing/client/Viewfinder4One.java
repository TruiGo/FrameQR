/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class Viewfinder4One extends ViewfinderView {

	public Viewfinder4One(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void drawLaster(Rect frame,Canvas canvas) {
		 // Draw a red "laser scanner" line through the middle to show decoding is active
	      paint.setColor(laserColor);
	      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
	      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
	      int middle = frame.height() / 2 + frame.top;
	      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
	}
}
