package info.wurzinger.segmenting.elements.segment;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import info.wurzinger.segmenting.elements.segment.state.SegmentMapState;

/**
 * Merges several {@link SegmentMap}s to one resulting {@link SegmentMap}. 
 * 
 * @author Martin Wurzinger
 */
public class MergedSegmentMap extends SegmentMap {
	
	protected SegmentMatrix segmentMatrix;
	
	/**
	 * Creates a {@link SegmentMap} from several {@link SegmentMap}s organized
	 * as two-dimensional array. The matrix results from the tiling mechanism
	 * which is done to keep memory needs low.
	 * 
	 * @param segmentMapMatrix contains the {@link SegmentMap}s to merge
	 */
	public MergedSegmentMap(SegmentMapTile[][] segmentMapMatrix, int width, int height, int upsamplingFactor) {
		super (segmentMapMatrix[0][0].sourceColorImage);
		state = SegmentMapState.VALID;
		
		imageWidth = width * upsamplingFactor;
		imageHeight = height * upsamplingFactor;
		
		segmentMatrix = new SegmentMatrix(imageWidth, imageHeight);
		
		// merge segments
		for (int tileY=0; tileY < segmentMapMatrix[0].length; tileY++) {
	    	for (int tileX=0; tileX < segmentMapMatrix.length; tileX++) {
	    		if (segmentMapMatrix[tileX][tileY].state != SegmentMapState.VALID) { // TODO: JTS?
	    			state = segmentMapMatrix[tileX][tileY].state;
	    		}
	    		
	    		// merge TreeSet
	    		segments.putAll(segmentMapMatrix[tileX][tileY].segments);
	    		
	    		// set neighborhood relations to segments at the top/bottom border
	    		if (tileY > 0) {
	    			for (int x=0; x<segmentMapMatrix[tileX][tileY-1].bottomNeighborIndex.length; x++) {
	    				segmentMapMatrix[tileX][tileY-1].bottomNeighborIndex[x].getNeighbors().add(segmentMapMatrix[tileX][tileY].topNeighborIndex[x]);
	    				segmentMapMatrix[tileX][tileY].topNeighborIndex[x].getNeighbors().add(segmentMapMatrix[tileX][tileY-1].bottomNeighborIndex[x]);
	    			}
	    		}
	    		
	    		// set neighborhood relations to segments at the left/right border
	    		if (tileX > 0) {
	    			for (int y=0; y<segmentMapMatrix[tileX-1][tileY].leftNeighborIndex.length; y++) {
	    				segmentMapMatrix[tileX-1][tileY].leftNeighborIndex[y].getNeighbors().add(segmentMapMatrix[tileX][tileY].rightNeighborIndex[y]);
	    				segmentMapMatrix[tileX][tileY].rightNeighborIndex[y].getNeighbors().add(segmentMapMatrix[tileX-1][tileY].leftNeighborIndex[y]);
	    			}
	    		}
	    		
	    		
	    		int offsetX = segmentMapMatrix[0][0].getTileWidth() * tileX * upsamplingFactor;
	    		int offsetY = segmentMapMatrix[0][0].getTileHeight() * tileY * upsamplingFactor;
	    		int upsampledTileX, upsampledTileY;
	    		Segment segment;
	    		
	    		for (int x=0; x<segmentMapMatrix[tileX][tileY].getTileWidth(); x++) {
	    			for (int y=0; y<segmentMapMatrix[tileX][tileY].getTileHeight(); y++) {
	    				segment = segmentMapMatrix[tileX][tileY].get(x, y);
	    				upsampledTileX = x * upsamplingFactor;
	    				upsampledTileY = y * upsamplingFactor;
	    				
	    				for (int upsamplingX=0; upsamplingX < upsamplingFactor; upsamplingX++) {
	    					for (int upsamplingY=0; upsamplingY < upsamplingFactor; upsamplingY++) {
	    						segmentMatrix.setSegment(offsetX + upsampledTileX + upsamplingX, offsetY + upsampledTileY + upsamplingY, segment);
	    					}
	    				}
	    				
	    			}
	    		}
	    		
		    }// end for - tileX
	    } // end for - tileY
		
	}
	
	public BufferedImage getMosaicImage() {
		WritableRaster mosaicRaster = sourceColorImage.getColorModel().createCompatibleWritableRaster(segmentMatrix.getWidth(), segmentMatrix.getHeight());
		BufferedImage mosaicImage = new BufferedImage(sourceColorImage.getColorModel(), mosaicRaster, true, null);
		
		for (int x=0; x < segmentMatrix.getWidth(); x++) {
			for (int y=0; y < segmentMatrix.getHeight(); y++) {
				mosaicRaster.setPixel(x, y, segmentMatrix.getSegment(x, y).getColor());
			}
		}
		
		return mosaicImage;
	}
}
