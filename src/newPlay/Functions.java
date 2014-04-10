package com.example.newPlay;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class Functions  {
	final static int INITIAL = 0;
	final static int BLACKANDWHITE = 1;
	final static int RGBSWAP = 2;
	final static int INVERTED = 3;
	final static int SAVEEFFECT = 4;
	final static int SATURATION = 5;
	final static int BRIGHTNESS = 6;
	final static int SAVEIMAGE = 7;
	final static int CONTRAST = 8;
	final static int HUE = 9;
	final static int UNDOONE = 10;
	final static int TINT = 11;
	final static int SHARPENING = 12;
	final static int COLORBOOST = 13;
    final static int RELOAD =14;
    final static int NEGATIVE = 15;
	final static int SMOOTH = 16;
	final static int BLUR = 17;
	final static int SEPIA = 18;
	final static int CROPIMAGE = 19;
	final static int SAVELARGE = 20;
	final static int SOFTFOCUS = 21;
	final static int SAVECROP = 22;
	

	public static final double PI = 3.14159d;
	public static final double FULL_CIRCLE_DEGREE = 360d;
	public static final double HALF_CIRCLE_DEGREE = 180d;
	public static final double RANGE = 256d;
	final double GS_RED = 0.3;
	final double GS_GREEN = 0.59;
	final double GS_BLUE = 0.11;
    
	public static double softFocusFlag,softFocusX,softFocusY;
	public static double flag;
	public static int savingFlag,undoFlag;
	public static int width,height;
	public static boolean lock = false;
	public static Vector<Node> vec;
	double value, angle;
	static int funToRun;
	ImageView ivOrig;
	Node node;
	static String selectedImagePath;
	static Bitmap bOrig, newBitmap, tempBitmap;
	int skbaarlevel, x, y,sampleSize;
	
	int invertFlag, eff2Flag, sepiaFlag, negativeFlag, bwFlag;
	int A, R, G, B, red, green, blue, RY, BY, RYY, GYY, BYY, Y, S, C, T;
	int pixel;
	File file, myDir;
    Context context;
	ColorMatrix cm;
	Canvas c;
	ColorMatrixColorFilter cmf;
	Paint p;
	Intent cropIntent;
	ConvolutionMatrix convMatrix;
	Thread thr;
	BitmapFactory.Options options;
	int[] destPixels;
	double[][] GaussianBlurConfig;

	Functions(Context cont) {

		cm = new ColorMatrix();
		p = new Paint();
		invertFlag = 0;
		flag = -1;
		eff2Flag = 0;
		sepiaFlag = 0;
		bwFlag = 0;
		negativeFlag = 0;
		undoFlag=0;
		context = cont;
		vec = new Vector<Node>(10, 10);
		
		
	//	cropIntent = new Intent(getApplicationContext(),CropImage.class);
		convMatrix = new ConvolutionMatrix(3);
		try {
			bOrig = decodeSampledBitmapFromResource();
		} catch (Exception e) {
			Log.e("Exception", "1." + e);
		}
        
		// System.gc();
		// ivOrig.setImageBitmap(bOrig);
		// ivConv.setImageBitmap(bOrig);
		// Bitmap.crea
		width = bOrig.getWidth();
		height = bOrig.getHeight();
		try {
			newBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			destPixels = new int[width * height];
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);

		} catch (Exception e) {
			Log.e("yo", "3." + e);
		}

	}

	
	public void runFunction() {
		
		
	    funToRun = ViewPagerActivity.funToRun;
		Log.e("running", "" + funToRun + " is running with value " + flag);
		if (savingFlag == 1) {
			node = new Node(funToRun);
			newBitmap.getPixels(destPixels, 0, width, 0, 0, width, height);
			bOrig.setPixels(destPixels, 0, width, 0, 0, width, height);
			
		} else if (vec.isEmpty()) {
			node = new Node(funToRun);
			vec.add(node);
		} else {
			node = vec.lastElement();
			if (node.fun != funToRun||funToRun==SOFTFOCUS) {
				if (funToRun!=UNDOONE)
				{
				undoFlag=0;
				node = new Node(funToRun);
				vec.add(node);
				newBitmap.getPixels(destPixels, 0, width, 0, 0, width, height);
				bOrig.setPixels(destPixels, 0, width, 0, 0, width, height);
				}
			}
		}
		switch (funToRun) {
		case 1: // BLACK AND WHITE
			if (savingFlag == 1)
				bwFlag = (int) flag;
			node.val = bwFlag;
			
			if ((bwFlag == 0) ) {
				Canvas c = new Canvas(newBitmap);
				cm.setSaturation(0);
				ColorMatrixColorFilter cmf = new ColorMatrixColorFilter(cm);
				p.setColorFilter(cmf);
				c.drawBitmap(bOrig, 0, 0, p);
				bwFlag = 1 - bwFlag;
			}// ivConv.setImageBitmap(newBitmap);
			else {
				vec.remove(node);
				bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
				newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
				Log.e("asf", "asf");
				bwFlag = 1 - bwFlag;
			}

			break;
		case 2: // EFFECT2
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			if (savingFlag == 1)
				eff2Flag = (int) flag;
			node.val = eff2Flag;
			if ((eff2Flag == 0)) {
				for (int i = 0; i < destPixels.length; i++) {
					destPixels[i] = (destPixels[i] & 0xffff0000)
							| ((destPixels[i] & 0x000000ff) << 8)
							| ((destPixels[i] & 0x0000ff00) >> 8);
				}
				eff2Flag++;
			} else if ((eff2Flag == 1) ) {
				
				for (int i = 0; i < destPixels.length; i++) {
					destPixels[i] = (destPixels[i] & 0xff00ff00)
							| ((destPixels[i] & 0x000000ff) << 16)
							| ((destPixels[i] & 0x00ff0000) >> 16);
				}
				eff2Flag++;
			} else if ((eff2Flag == 2) ) {
				for (int i = 0; i < destPixels.length; i++) {
					destPixels[i] = (destPixels[i] & 0xff0000ff)
							| ((destPixels[i] & 0x0000ff00) << 8)
							| ((destPixels[i] & 0x00ff0000) >> 8);
				}
				eff2Flag++;
			} else {
				vec.remove(node);
				eff2Flag = 0;
			}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			// ivConv.setImageBitmap(newBitmap);
			break;
		case 3: // INVERTED
			Matrix matrix = new Matrix();
			if (savingFlag == 1)
				invertFlag = (int) flag;
			node.val = invertFlag;
			if ((invertFlag == 0) ) {
				matrix.preScale(-1.0f, 1.0f);
				invertFlag++;
			} else if ((invertFlag == 1) ) {
				matrix.preScale(1.0f, -1.0f);
				invertFlag++;
			} else if ((invertFlag == 2)) {
				matrix.preScale(-1.0f, -1.0f);
				invertFlag++;
			} else {
				vec.remove(node);
				matrix.preScale(1.0f, 1.0f);
				invertFlag = 0;
			}
			newBitmap.recycle();
			newBitmap = Bitmap.createBitmap(bOrig, 0, 0, width, height, matrix,
					true);
			System.gc();
			// ivConv.setImageBitmap(newBitmap);
			break;
		case 4: // SAVE EFFECTS
			// bOrig=copyBitmap(newBitmap, bOrig);

			try {
				newBitmap.getPixels(destPixels, 0, width, 0, 0, width, height);
				bOrig.setPixels(destPixels, 0, width, 0, 0, width, height);
			} catch (Exception e) {
				Log.e("panga", "-> " + e);
			}
			Log.e("atleast", "i m coming here");
			break;

		case 5: // SATURATION

			value = ViewPagerActivity.skbaarlevel;
			if (savingFlag == 1)
				value = flag;
			node.val = value;
			
			value = value/50;
			c = new Canvas(newBitmap);
			cm.setSaturation((float) value);
			cmf = new ColorMatrixColorFilter(cm);
			p.setColorFilter(cmf);
			c.drawBitmap(bOrig, 0, 0, p);
			
				break;

		case 6:

			value = ViewPagerActivity.skbaarlevel; // BRIGHTNESS
			if (savingFlag == 1)
				value = flag;
			node.val = value;
           
			value -= 100;
			value = (int)((value * 150) / 100);
			// scan through all pixels
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			for (int i = 0; i < destPixels.length; i++) {
				// get pixel color
				pixel = destPixels[i];
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				//

				// increase/decrease each channel
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// apply new pixel color to output bitmap
				destPixels[i] = Color.argb(A, R, G, B);
				// newBitmap.setPixel(x, y, Color.argb(A, R, G, B));
			}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			// ivConv.setImageBitmap(newBitmap);
			break;
		case 7: // SAVE IMAGE
			try {
				// ivConv.setImageBitmap(bOrig);
				// ivOrig.setImageBitmap(bOrig);
				
				String path = Environment.getExternalStorageDirectory()
						.toString();
				myDir = new File(path + "/MagicInfo_Images");
				file = new File(selectedImagePath);
				if (savingFlag == 1)
				{	
				String fname = "Image-" + System.currentTimeMillis() + ".jpeg";
				file = new File(myDir, fname);
				}
				

				FileOutputStream out = new FileOutputStream(file);
				if (newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) == false) {
					Log.e("saving", "main to na hoti");
				} else
					Log.e("saving",
							"ho gyi save bhai!!" + file.getAbsolutePath());
				out.flush();
				out.close();
			/*
				@SuppressWarnings("unused")
				SingleMediaScanner smscanner = new SingleMediaScanner(
						getApplicationContext(), file);
			*/
			} catch (Exception e) {
				Log.e("exceptin", "saving." + e);
						}
			break;
		case 8: // CONTRAST
			value = ViewPagerActivity.skbaarlevel;
			if (savingFlag == 1)
				value = flag;
			node.val = value;                                                                                                                               

			value = value - 100;
			double contrast = Math.pow((100.0 + value) / 100, 2);
			// scan through all pixels
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			for (int i = 0; i < destPixels.length; i++) {
				// get pixel color
				pixel = destPixels[i];
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}

				G = Color.green(pixel);
				G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}

				B = Color.blue(pixel);
				B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}
				// apply new pixel color to output bitmap
				destPixels[i] = Color.argb(A, R, G, B);
			}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);

			// ivConv.setImageBitmap(newBitmap);
			break;
		case 9: // HUE
			value = ViewPagerActivity.skbaarlevel;
			if (savingFlag == 1)
				value = flag;
			node.val = value;

			value = value - 180;
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);

			angle = (PI * value) / HALF_CIRCLE_DEGREE;
			S = (int) (RANGE * Math.sin(angle));
			C = (int) (RANGE * Math.cos(angle));
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++) {
					int index = y * width + x;
					int r = (destPixels[index] >> 16) & 0xff;
					int g = (destPixels[index] >> 8) & 0xff;
					int b = destPixels[index] & 0xff;
					RY = (70 * r - 59 * g - 11 * b) / 100;
					BY = (-30 * r - 59 * g + 89 * b) / 100;
					Y = (30 * r + 59 * g + 11 * b) / 100;
					RYY = (S * BY + C * RY) / 256;
					BYY = (C * BY - S * RY) / 256;
					GYY = (-51 * RYY - 19 * BYY) / 100;
					R = Y + RYY;
					R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
					G = Y + GYY;
					G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
					B = Y + BYY;
					B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
					destPixels[index] = 0xff000000 | (R << 16) | (G << 8) | B;
				}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;

		case 10: // UNDO EFFECTS
			// newBitmap=copyBitmap(bOrig, newBitmap);
			if ((vec.isEmpty() == false)&&(undoFlag==0)) {
			vec.remove(vec.lastElement());
			undoFlag=1;
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			}
			// ivConv.setImageBitmap(newBitmap);
			break;
		case 11: // TINT
			value = ViewPagerActivity.skbaarlevel;
			if (savingFlag == 1)
				value = flag;
			node.val = value;

			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);

			/*
			 * angle = (PI * (double) value) / HALF_CIRCLE_DEGREE; angle *=64; S
			 * = (int) (90 * Math.sin(angle)); C = (int) (90 *
			 * Math.sin(angle/8)); T = (int) (90 * Math.sin(angle/64));
			 */

			if (value < 60) {
				red = 60;
				green = (int) value;
				blue = 0;
			} else if (value < 120) {
				red = (int) (120 - value);
				green = 60;
				blue = 0;
			} else if (value < 180) {
				red = 0;
				green = 60;
				blue = (int) (value - 120);
			} else if (value < 240) {
				red = 0;
				green = (int) (240 - value);
				blue = 60;
			} else if (value < 300) {
				red = (int) (value - 240);
				green = 0;
				blue = 60;
			} else {
				red = 60;
				green = 0;
				blue = (int) (360 - value);
			}
			red = (red - 30) * 2;
			green = (green - 30) * 2;
			blue = (blue - 30) * 2;
			for (int i = 0; i < destPixels.length; i++) {
				A = (destPixels[i] >> 24) & 0xff;
				R = (destPixels[i] >> 16) & 0xff;
				G = (destPixels[i] >> 8) & 0xff;
				B = destPixels[i] & 0xff;

				RYY = S;
				GYY = C;
				BYY = T;

				Y = B = G = R = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);

				R = Y + red;
				R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
				G = Y + green;
				G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
				B = Y + blue;
				B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
				destPixels[i] = (A << 24) | (R << 16) | (G << 8) | B;
			}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);

			break;
		case 12: // SHARPENING
			value = ViewPagerActivity.skbaarlevel;
			S = 1;
			if (savingFlag == 1)
			
			{
				value = flag;
				S = sampleSize;
				}
			node.val = value;
            value=value*4;
			double[][] SharpConfig = new double[][] { { 0, -value, 0 },
					{  -value, 6*value+2, -value }, { 0, -value, 0 } };
            
			convMatrix.applyConfig(SharpConfig);
			convMatrix.Offset = 0;
			convMatrix.Factor = 2*value + 2;
			destPixels = ConvolutionMatrix.computeConvolution3x3(bOrig,
					convMatrix, destPixels,1);
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;
		case 13: // COLOR BOOST
			red = (int)ViewPagerActivity.skBaarRed;
			green = (int)ViewPagerActivity.skBaarGreen;
			blue = (int)ViewPagerActivity.skBaarBlue;
			 A = 0x00000000 | (red << 16) | (green << 8) | blue;
			value=A;
			 if (savingFlag == 1)
				value = flag;
			node.val = value;
			A=(int)value;
            red= (A&0x00ff0000)>>16;
			green = (A&0x0000ff00)>>8;
			blue =(A&0x000000ff);
			red-=100;
			green-=100;
			blue-=100;
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			Log.e("ColorBoost", red+" "+green+" "+blue);
			for (int i = 0; i < destPixels.length; i++) {
				// get pixel color
				pixel = destPixels[i];
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				R = (int) (R * (1.0 + (double)red / 100.0));
				if (R > 255)
					R = 255;
				if (R < 0)
					R = 0;
				G = (int) (G * (1.0 + (double)green / 100.0));
				if (G > 255)
					G = 255;
				if (G < 0)
					G = 0;

				B = (int) (B * (1.0 + (double)blue / 100.0));
				if (B > 255)
					B = 255;
				if (B < 0)
					B = 0;
				destPixels[i] = (Color.argb(A, R, G, B));
			}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;
		case 14: // RELOAD
			vec.removeAllElements();
			bOrig = decodeSampledBitmapFromResource();
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;
		case 15: 
		    // NEGATIVE
					if (savingFlag == 1)
						negativeFlag = (int) flag;
					node.val = negativeFlag;
					if (negativeFlag == 0) {
						bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
						for (int i = 0; i < destPixels.length; i++) {
							// get pixel color
							pixel = destPixels[i];
							// saving alpha channel
							A = Color.alpha(pixel);
							// inverting byte for each R/G/B channel
							R = 255 - Color.red(pixel);
							G = 255 - Color.green(pixel);
							B = 255 - Color.blue(pixel);
							// set newly-inverted pixel to output image
							destPixels[i] = (Color.argb(A, R, G, B));
						}
						newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
						negativeFlag = 1 - negativeFlag;
					} else {
						vec.remove(node);
						bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
						for (int i = 0; i < destPixels.length; i++) {
							// get pixel color
							pixel = destPixels[i];
							// saving alpha channel
							A = Color.alpha(pixel);
							// inverting byte for each R/G/B channel
							R = Color.red(pixel);
							G = Color.green(pixel);
							B = Color.blue(pixel);
							// set newly-inverted pixel to output image
							destPixels[i] = (Color.argb(A, R, G, B));
						}
						newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
						negativeFlag = 1 - negativeFlag;

					}

					break;
		case 16: // SMOOTHNESS
			value = ViewPagerActivity.skbaarlevel;
			S = 1;
			if (savingFlag == 1)
				{
				value = flag;
				S = sampleSize;
				}
			node.val = value;
				GaussianBlurConfig = new double[][] { { 0, value/2, 0 }, { value/2, 1, value/2 },{ 0, value/2, 0 } };
				convMatrix.Factor = value*2 + 1;
			convMatrix.applyConfig(GaussianBlurConfig);
			convMatrix.Offset = 1;
			destPixels = ConvolutionMatrix.computeConvolution3x3(bOrig,
					convMatrix, destPixels,S);
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;
		case 17:     // BLUR
			S = 1;
			if (savingFlag == 1)
				{
				S = sampleSize;
				}
			GaussianBlurConfig = new double[][] { { 2, 1, 2 }, { 1, -4, 1 },
					{ 2, 1, 2 } };
			convMatrix.applyConfig(GaussianBlurConfig);
			convMatrix.Factor = 8;
			convMatrix.Offset = 2;
			destPixels = ConvolutionMatrix.computeConvolution3x3(bOrig,
					convMatrix, destPixels,S);
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;
		case 18:  // SEPIA

			if (savingFlag == 1)
				sepiaFlag = (int) flag;
			node.val = sepiaFlag;

			if (sepiaFlag == 0) {
				red = 60;
				green = 60;
				blue = 0;
				sepiaFlag++;
			} else if (sepiaFlag == 1) {
				red = 100;
				green = 100;
				blue = 0;
				sepiaFlag++;
			} else if (sepiaFlag == 2) {
				red = 50;
				green = 0;
				blue = 50;
				sepiaFlag++;
			} else if (sepiaFlag == 3) {
				red = 100;
				green = 0;
				blue = 100;
				sepiaFlag++;
			} else if (sepiaFlag == 4) {
				red = 0;
				green = 50;
				blue = 50;
				sepiaFlag++;
			} else if (sepiaFlag == 5) {
				red = 0;
				green = 100;
				blue = 100;
				sepiaFlag++;
			}

			else {
				vec.remove(node);
				sepiaFlag = 0;
				bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
				for (int i = 0; i < destPixels.length; i++) {
					// get pixel color
					pixel = destPixels[i];
					A = Color.alpha(pixel);
					R = Color.red(pixel);
					G = Color.green(pixel);
					B = Color.blue(pixel);
					destPixels[i] = (Color.argb(A, R, G, B));
				}
				newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
				break;
			}
			// scan through all pixels
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			for (int i = 0; i < destPixels.length; i++) {
				// get pixel color
				pixel = destPixels[i];
				// get color on each channel
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// apply grayscale sample
				B = G = R = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);

				// apply intensity level for sepid-toning on each channel
				R += red;
				if (R > 255) {
					R = 255;
				}

				G += green;
				if (G > 255) {
					G = 255;
				}

				B += blue;
				if (B > 255) {
					B = 255;
				}

				// set new pixel color to output image
				destPixels[i] = (Color.argb(A, R, G, B));
			}
			newBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			break;
		case 19:             // SAVE CROP
		case 20:           // SAVE LARGE
			newBitmap.recycle();
			bOrig.recycle();
			invertFlag = 0;
			flag = -1;
			eff2Flag = 0;
			sepiaFlag = 0;
			bwFlag = 0;
			negativeFlag = 0;
			undoFlag=0;
			options = new BitmapFactory.Options();
			options.inMutable = true;
			options.inPurgeable = true;
			options.inInputShareable = true;
			try {
				newBitmap = BitmapFactory
						.decodeFile(selectedImagePath, options);
				bOrig = BitmapFactory.decodeFile(selectedImagePath, options);
				sampleSize = bOrig.getWidth()/width;
				width = bOrig.getWidth();
				height = bOrig.getHeight();
				destPixels = new int[width * height];
				// bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
				savingFlag = 1;
				System.gc();
			} catch (Exception e) {
				Log.e("huya kuch to", "-->" + e);
			}
			if (newBitmap == null || bOrig == null)
				Log.e("panga is here", "decode na ho rhi bhai");
			break;
		case 21:               // SOFT FOCUS

			tempBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            double temp;
			bOrig.getPixels(destPixels, 0, width, 0, 0, width, height);
			
			C = (int)(ViewPagerActivity.softFocusX*width);
			T = (int)(ViewPagerActivity.softFocusY*height);
			
			for (int x = 0; x < width; x++) {
				for (y = 0; y < height; y++) {
					int i = y * width + x;
				    temp = (Math.abs(T - y)/Math.abs(height-softFocusY) + Math.abs(C - x)/Math.abs(width-softFocusX))*700;		
					temp = temp -80;
				    if (temp>250) temp = 255;
					if (temp<0) temp =0;
					
			        destPixels[i] = (destPixels[i] | 0xff000000) & ((((int)temp)<<24)| 0x00ffffff);

				}
			}
			tempBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
			S = 1;
			if (savingFlag == 1)
				{
				S = sampleSize;
				}
			GaussianBlurConfig = new double[][] { { 2, 1, 2 }, { 1, -4, 1 },
					{ 2, 1, 2 } };
			convMatrix.applyConfig(GaussianBlurConfig);
			convMatrix.Factor = 8;
			convMatrix.Offset = 2;
			destPixels = ConvolutionMatrix.computeConvolution3x3(tempBitmap,
					convMatrix, destPixels,S);
			tempBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);

			Canvas c = new Canvas(newBitmap);
			p = new Paint();
			c.drawBitmap(bOrig, 0, 0, p);
			c.drawBitmap(tempBitmap, 0, 0, p);
			tempBitmap.recycle();
			break;
		case 22:
			
			newBitmap.recycle();
			bOrig.recycle();
			
			Log.e("cming here","what now.?"+selectedImagePath);
			try {
				if (savingFlag==0)
				{
				newBitmap = decodeSampledBitmapFromResource();
				bOrig = decodeSampledBitmapFromResource();
				}
				else
				{
					newBitmap = BitmapFactory
							.decodeFile(selectedImagePath, options);
					bOrig = BitmapFactory.decodeFile(selectedImagePath, options);
					 
				}
				width = bOrig.getWidth();
				height = bOrig.getHeight();
				destPixels = new int[width * height];
				
			} catch (Exception e) {
				Log.e("huya kuch to", "-->" + e);
			}
			if (newBitmap == null || bOrig == null)
				Log.e("panga is here", "decode na ho rhi bhai");
			break;
		}
   
	}

	
	@SuppressWarnings("null")
	public Bitmap decodeSampledBitmapFromResource() {

		options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inMutable = true;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap b = null;
		sampleSize = 1;
		selectedImagePath = MainActivity.selectedImagePath;
		options.inSampleSize = sampleSize;
		try {
			while ((b = BitmapFactory.decodeFile(selectedImagePath, options)) == null) {
				options.inSampleSize = ++sampleSize;

				Log.e("sampled", sampleSize + selectedImagePath);
				b.recycle();

			}
		} catch (Exception e) {
			Log.e("yo yo", "2." + e);
		}
		if (b != null)
			b.recycle();

		options.inJustDecodeBounds = false;
       
		b = BitmapFactory.decodeFile(selectedImagePath, options);
		// b=BitmapFactory.decodeFile(selectedImagePath);
		// b=convertToMutable(b);
		Log.e("**", "yahan aaya");
		return (b);
	}

}

class SingleMediaScanner implements MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private File mFile;

	public SingleMediaScanner(Context context, File f) {
		mFile = f;
		mMs = new MediaScannerConnection(context, this);
		mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		mMs.scanFile(mFile.getAbsolutePath(), null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mMs.disconnect();
	}

}