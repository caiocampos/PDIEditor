package info.wurzinger.segmenting.tools;

import java.awt.image.Kernel;

public class MeanSmoothKernel extends Kernel {
	
	public MeanSmoothKernel(int width, int height) {
		super(width, height, getSmoothData(width, height));
	}
	
	public static float[] getSmoothData(int width, int height) {
		float[] data = new float[width*height];
		float meanValue = (float) 1 / (float) (width*height); 
		
		for (int xy = 0; xy<data.length; xy++) {
			data[xy] = meanValue;
		}
		
		return data;
	}
}
