package info.wurzinger.jaiOp.wst;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;

import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;

import info.wurzinger.segmenting.elements.segment.SegmentMapTile;
import info.wurzinger.jaiOp.hwst.SegmentMapMatrixGenerator;

@SuppressWarnings("unchecked")
public class FlooderOpImage extends OpImage {
	
	protected SegmentMapTile[][] segmentMapMatrix = null;
	private int startSegmentLabel;
	private int lastStartSegmentLabel;
	private SegmentMapTile lastSegmentMap = null;
	
    public FlooderOpImage(RenderedImage magnitudeImage, RenderedImage colorImage, ImageLayout layout, Map configuration, int startSegmentLabel) {
    	super(OpImage.vectorize(magnitudeImage, colorImage), layout, configuration, true);
    	
    	this.startSegmentLabel = startSegmentLabel;
    	this.lastStartSegmentLabel = startSegmentLabel;
	}

	@Override
	public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
		return destRect;
	}

	@Override
	public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
		int x = sourceRect.x;
		int y = sourceRect.y;
		int width = sourceRect.width;
		int xRemain = width - x;
		int height = sourceRect.height;
		int yRemain = height - y;
		
		return new Rectangle(x, y, Math.min(width, xRemain), Math.min(height, yRemain));
	}
	
	/**
	 * This method (with input arguments Raster[], WritableRaster, and Rectangle) is called when
	 * the OpImage is constructed with the cobbleSources argument set to <code>true</code>.
	 * It calculates an image tile which is masked by the rectangle <code>destRect</code>.
	 */
	@Override
	protected void computeRect(Raster[] sources, WritableRaster destinationRaster, Rectangle destRect) {
		Rectangle srcRect = mapDestRect(destRect, 0);
		
		lastStartSegmentLabel = getNextSegmentLabel();
		
		Flooder flooder = new Flooder(sources[0], sources[1], lastStartSegmentLabel, srcRect);
		lastSegmentMap = flooder.extractSegmentMap(getSourceImage(1));
		
		
		/* set segment map matrix and remember the resulting segment map as the last one */
		if (segmentMapMatrix==null) {
			segmentMapMatrix = new SegmentMapTile[getNumXTiles()][getNumYTiles()];
			setProperty(SegmentMapMatrixGenerator.PROPERTY_SEGMENT_MAP_MATRIX, segmentMapMatrix);
		}
		
		int tileX = getTileIndices(srcRect)[0].x;
		int tileY = getTileIndices(srcRect)[0].y;
		segmentMapMatrix[tileX][tileY] = lastSegmentMap;
	}
	
	private int getNextSegmentLabel() {
		if (lastSegmentMap==null) {
			return startSegmentLabel;
		} else {
			return lastSegmentMap.getHighestLabel() + 1;
		}
	}
	
	@Override
	public Object getProperty(String propertyName) {
		return super.getProperty(propertyName);
	}
	
}
