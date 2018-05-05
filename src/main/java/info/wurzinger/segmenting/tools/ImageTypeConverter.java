package info.wurzinger.segmenting.tools;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * This class contain static methods for checking and converting
 * the type of an <code>BufferedImage</code>.
 * 
 * @author Martin Wurzinger
 */
public class ImageTypeConverter {
	
	/**
	 * Converts the type of an image.
	 * 
	 * @param img is the <code>BufferedImage</code> which should be converted.
	 * @param destinationImageType is the targeted image type (<code>BufferedImage.TYPE_*</code>)
	 * as an <code>int</code> value
	 *  
	 * @return the new created <code>BufferedImage</code> with the type <code>destinationImageType</code>
	 */
	public static BufferedImage convertType(BufferedImage img, int destinationImageType) {
		java.awt.image.ColorConvertOp cco = new java.awt.image.ColorConvertOp(null);
		BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(), destinationImageType);
		return cco.filter(img, dest);
	}
	
	/**
	 * Converts the type of an image to the standard RGB format.
	 * 
	 * @param img is the <code>BufferedImage</code> which should be converted.
	 * as an <code>int</code> value
	 *  
	 * @return the new created <code>BufferedImage</code> with the type <code>TYPE_INT_RGB</code>
	 */
	public static BufferedImage convertToStandardRGB(BufferedImage img) {
		return convertType(img, BufferedImage.TYPE_INT_RGB);
	}
	
	/**
	 * Converts the type of an image to the standard RGB format.
	 * 
	 * @param img is the <code>BufferedImage</code> which should be converted.
	 * as an <code>int</code> value
	 *  
	 * @return the new created <code>BufferedImage</code> with the type <code>TYPE_INT_RGB</code>
	 */
	public static BufferedImage checkRgbType(BufferedImage img) {
		if (img!=null && img.getType()!=BufferedImage.TYPE_INT_RGB) {
			return convertType(img, BufferedImage.TYPE_INT_RGB);
		} else {
			return img;
		}
	}
	
	/**
	 * Converts the type of an image to the ARGB format.
	 * 
	 * @param img is the <code>BufferedImage</code> which should be converted.
	 * as an <code>int</code> value
	 *  
	 * @return the new created <code>BufferedImage</code> with the type <code>TYPE_INT_ARGB</code>
	 */
	public static BufferedImage convertToStandardARGB(BufferedImage img) {
		return convertType(img, BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Converts the type of an image to the gray format with one byte per pixel.
	 * 
	 * @param img is the <code>BufferedImage</code> which should be converted.
	 * as an <code>int</code> value
	 *  
	 * @return the new created <code>BufferedImage</code> with the type <code>TYPE_BYTE_GRAY</code>
	 */
	public static BufferedImage convertToGray(BufferedImage img) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    ColorConvertOp colorOp = new ColorConvertOp(cs, hints);
	    return colorOp.filter(img, null);
	}
	
	/**
	 * Converts the type of an image to the gray format with one byte per pixel.
	 * 
	 * @param img is the <code>BufferedImage</code> which should be converted.
	 * as an <code>int</code> value
	 *  
	 * @return the new created <code>BufferedImage</code> with the type <code>TYPE_BYTE_GRAY</code>
	 */
	public static BufferedImage checkGrayType(BufferedImage img) {
		if (img!=null && ColorSpace.getInstance(ColorSpace.CS_GRAY).equals(img.getColorModel().getColorSpace())) {
			return convertToGray(img);
		} else {
			return img;
		}
	}
}
