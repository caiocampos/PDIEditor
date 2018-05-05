package info.wurzinger.segmenting.tools;

import java.awt.image.BufferedImage;

/**
 * This is an adapter class to access more comfortable to the
 * color channels of an RGB <code>BufferedImage</code>.
 * 
 * @author Martin Wurzinger
 */
public class RgbImage {

	public static enum Channel {
		Red (16), Green (8), Blue (0);
		
		public final int shift;
		public final int bitMask;
		
		Channel(int shift) {
			this.shift = shift;
			this.bitMask = 255 << shift;
		}
	}
	
	private BufferedImage image;
	
	public RgbImage(BufferedImage image) {
		this.image = null;
		setImage(image);
	}
	
	public void setImage(BufferedImage image) {
		this.image = ImageTypeConverter.checkRgbType(image);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setColor(Channel channel, int x, int y, int value) {
		image.setRGB(x, y, (image.getRGB(x, y) & (~channel.bitMask)) | (value << channel.shift));
	}
	
	public int getColor(Channel channel, int x, int y) {
		return getColor(channel, image.getRGB(x, y));
	}
	
	public static int getColor(Channel channel, int data) {
		return (data >> channel.shift) & 255;
	}
	
	// red
	public void setRed(int x, int y, int value) {
		setColor(Channel.Red, x, y, value);
	}
	public int getRed(int x, int y) {
		return getColor(Channel.Red, x, y);
	}
	
	// green
	public void setGreen(int x, int y, int value) {
		setColor(Channel.Green, x, y, value);
	}
	public int getGreen(int x, int y) {
		return getColor(Channel.Green, x, y);
	}
	
	// blue
	public void setBlue(int x, int y, int value) {
		setColor(Channel.Blue, x, y, value);
	}
	public int getBlue(int x, int y) {
		return getColor(Channel.Blue, x, y);
	}
	
	// all three colors
	public void setRGB(int x, int y, int value) {
		image.setRGB(x, y, value);
	}
	public int getRGB(int x, int y) {
		return image.getRGB(x, y);
	}
	
	/**
	 * Sets the alpha value of the parameter <code>value</code> to 1.
	 * This is automatically done by the <code>BufferedImage</code> class.
	 * 
	 * @param value the value which should be altered.
	 * @return the altered value with a leading 1 like the binary
	 * pattern '0000.0001 rrrr.rrrr gggg.gggg bbbb.bbbb'.   
	 */
	public static int convertAlphaRGB(int value) {
		return value -16777216; // prepend a leading 1; this is done for buffered images
	}
}
