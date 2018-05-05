package info.wurzinger.segmenting;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

public class Operations {	
	
	public static RenderedOp smooth(RenderedImage image, int kernelSideLength) {
		ParameterBlock smoothParamBlock = new ParameterBlock();
		
		smoothParamBlock.addSource(image);
		smoothParamBlock.add(kernelSideLength);
		smoothParamBlock.add(kernelSideLength);
		smoothParamBlock.add((int) (kernelSideLength/2));
		smoothParamBlock.add((int) (kernelSideLength/2));
		
		return JAI.create("BoxFilter", smoothParamBlock, getBorderExtenderHint(BorderExtender.BORDER_COPY));
	}
	
	public static RenderedOp scale(RenderedImage image, float scaleFactor) {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(scaleFactor);
		pb.add(scaleFactor);
		
		return JAI.create("Scale", pb);
	}
	
	public static RenderedOp gradientMagnitude(RenderedImage image) {
		ParameterBlockJAI gradientMagnitudeParamBolck = new ParameterBlockJAI("GradientMagnitude");
		gradientMagnitudeParamBolck.addSource(image);
		
		return JAI.create("GradientMagnitude", gradientMagnitudeParamBolck, getBorderExtenderHint(BorderExtender.BORDER_COPY));
	}
	
	public static RenderedOp colorConvertToGray(RenderedImage image) {
//		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//		ColorModel cm = image.getColorModel();
//		boolean hasAlpah = cm.hasAlpha();
//		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
//		int transparency = cm.getTransparency();
//		cm = new ComponentColorModel(cs, hasAlpah, isAlphaPremultiplied, transparency, DataBuffer.TYPE_BYTE);
//		
//	     // Create the ParameterBlock.
//	     ParameterBlock pb = new ParameterBlock();
//	     pb.addSource(image);
//	     pb.add(cm);
//
//	     // Perform the color conversion.
//	     return JAI.create("ColorConvert", pb);
		
//		 Create a gray-level image with the weighted average of the three bands.
		double[][] matrix = {{ 0.114, 0.587, 0.299, 0 }};
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);  
		pb.add(matrix);  
		return JAI.create("bandcombine", pb, null);  
	}
	
	private static RenderingHints getBorderExtenderHint(int borderExtender) {
		BorderExtender extender = BorderExtender.createInstance(borderExtender);
		return new RenderingHints(JAI.KEY_BORDER_EXTENDER, extender);
	}
}
