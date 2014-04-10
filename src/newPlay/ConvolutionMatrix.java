package com.example.newPlay;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class ConvolutionMatrix {
	public static final int SIZE = 3;

	public double[][] Matrix;
	public double Factor = 1;
	public double Offset = 1;

	public ConvolutionMatrix(int size) {
		Matrix = new double[size][size];
	}

	public void setAll(double value) {
		for (int x = 0; x < SIZE; ++x) {
			for (int y = 0; y < SIZE; ++y) {
				Matrix[x][y] = value;
			}
		}
	}

	public void applyConfig(double[][] config) {
		for (int x = 0; x < SIZE; ++x) {
			for (int y = 0; y < SIZE; ++y) {
				Matrix[x][y] = config[x][y];
			}
		}
	}

	public static int index(int x, int y, int width) {
		return (y*width + x);
	}

	public static int[] computeConvolution3x3(Bitmap src,
			ConvolutionMatrix matrix, int destPixels[], int S) {
		int width = src.getWidth();
		int height = src.getHeight();
		src.getPixels(destPixels, 0, width, 0, 0, width, height);
		int A, R, G, B;
		int sumR, sumG, sumB;
		int[][] pixels = new int[SIZE][SIZE];
		
		{
			Log.e("ConvolutionMatrix", "applying effect");
			for (int y = 0; y < height - 2; ++y) {
			for (int x = 0; x < width - 2; ++x) {

				// get pixel matrix
				for (int i = 0; i < SIZE; ++i) {
					for (int j = 0; j < SIZE; ++j) {
						pixels[i][j] = destPixels[index(x + i, y + j, width)];
					}
				}

				// get alpha of center pixel
				A = Color.alpha(pixels[1][1]);

				// init color sum
				sumR = sumG = sumB = 0;

				// get sum of RGB on matrix
				for (int i = 0; i < SIZE; ++i) {
					for (int j = 0; j < SIZE; ++j) {
						sumR += (Color.red(pixels[i][j]) * matrix.Matrix[i][j]);
						sumG += (Color.green(pixels[i][j]) * matrix.Matrix[i][j]);
						sumB += (Color.blue(pixels[i][j]) * matrix.Matrix[i][j]);
					}
				}

				// get final Red
				R = (int) (sumR / matrix.Factor + matrix.Offset);
				G = (int) (sumG / matrix.Factor + matrix.Offset);
				B = (int) (sumB / matrix.Factor + matrix.Offset);

				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}
				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}
				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}
				// apply new pixel
				destPixels[index(x + 1, y + 1, width)] = Color.argb(A, R, G, B);
			}
		}
	}
		// final image
		return destPixels;
	}
}