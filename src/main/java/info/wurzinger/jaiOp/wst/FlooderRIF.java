package info.wurzinger.jaiOp.wst;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;

public class FlooderRIF implements RenderedImageFactory {
	/**
	 * Empty constructor
	 */
	public FlooderRIF() {}

	/**
	 * The create method, that will be called to create a RenderedImage (or chain
	 * of operators that represents one).
	 */
	public RenderedImage create(ParameterBlock paramBlock, RenderingHints hints) {
		// Get data from the ParameterBlock.
		RenderedImage magnitudeSource = paramBlock.getRenderedSource(0);
		RenderedImage colorImageSource = paramBlock.getRenderedSource(1);
		int tileSize = paramBlock.getIntParameter(0);
		int startSegmentLabel = paramBlock.getIntParameter(1);
		
		// We will copy the input image layout to the output image.
		ImageLayout layout = new ImageLayout(colorImageSource);
		
		layout.setTileWidth(Math.min(magnitudeSource.getWidth(), tileSize));
		layout.setTileHeight(Math.min(magnitudeSource.getHeight(), tileSize));
		
		// Create a new image (or chain) with this information.
		return new FlooderOpImage(magnitudeSource, colorImageSource, layout, hints, startSegmentLabel);
	}
}
