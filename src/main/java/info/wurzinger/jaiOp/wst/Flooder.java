package info.wurzinger.jaiOp.wst;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.media.jai.PlanarImage;

import info.wurzinger.segmenting.neighborhood.inspectors.FittingPixelFinder;
import info.wurzinger.segmenting.neighborhood.inspectors.NeighboringLabelFinder;
import info.wurzinger.segmenting.elements.Pixel;
import info.wurzinger.segmenting.elements.segment.SegmentMatrix;
import info.wurzinger.segmenting.elements.segment.LightweightExtractor;
import info.wurzinger.segmenting.elements.segment.Segment;
import info.wurzinger.segmenting.elements.segment.SegmentMap;
import info.wurzinger.segmenting.elements.segment.SegmentMapTile;
import info.wurzinger.segmenting.neighborhood.PixelNeighborhood;

public class Flooder {
	protected static final int MAX_FLOOD_LEVEL = (1<<Byte.SIZE) - 1;
	public static final Segment DAM_SEGMENT = new Segment(SegmentMapTile.SEGMENT_LABEL_SUPREMUM);
	
	private ArrayList<double[]> segmentColors;
	private ArrayList<Integer> segmentAreas;
	protected Raster colorImageRaster = null;
	protected Raster magnitudeRaster = null;
	private int startSegmentLabel = SegmentMap.SEGMENT_LABEL_INFIMUM;
	private Rectangle tileRect = null;
	private SegmentMatrix segmentMatrix = null;
	private static LinkedList<Segment> segmentsToIgnore;
	
	static {
		segmentsToIgnore = new LinkedList<Segment>(); 
	    segmentsToIgnore.add(DAM_SEGMENT);
	}
	
	public Flooder(Raster magnitudeRaster, Raster colorImageRaster, int startSegmentLabel, Rectangle tileRect) {
		segmentColors = new ArrayList<double[]>();
		segmentAreas = new ArrayList<Integer>();
		
		this.colorImageRaster = colorImageRaster;
		this.magnitudeRaster = magnitudeRaster;
		this.startSegmentLabel = startSegmentLabel;
		this.tileRect = tileRect;
	}
	
	protected void floodTile() {
		if (segmentMatrix == null) { // if flooding wasn't done yet
			segmentMatrix = new SegmentMatrix(tileRect.width, tileRect.height, startSegmentLabel, tileRect.x, tileRect.y);
			
			// SORTS all pixels of the image by their gradient magnitude in ascending order.
			// Pixels with the same magnitude are kept in an ArrayList.
		    ArrayList<Pixel>[] sortedPixel = getSortedPixel();
		    
		    NeighboringLabelFinder inspector;
		    PixelNeighborhood pixelNeighborhood;
		    int label = startSegmentLabel;
		    
		    for (int level=0; level<=MAX_FLOOD_LEVEL; level++) {
		    	if (sortedPixel[level]!=null) {
		    		
		    		for (Pixel pixel : sortedPixel[level]) {
		    			inspector = new NeighboringLabelFinder(segmentMatrix, segmentsToIgnore);
		    			pixelNeighborhood = PixelNeighborhood.createInstance(pixel, tileRect);
		    			pixelNeighborhood.addInspector(inspector);
		    			pixelNeighborhood.inspectNeighborhood();
		    			
		    			switch (inspector.getNeighboringSegmentCount()) {
		    				case 0: // new segment found
		    					setSegment(pixel, new Segment(label++));
		    					break;
		    					
		    				case 1: // enlarge neighboring ROI
		    					setSegment(pixel, inspector.getFirstNeigboringSegment());
		    					break;
		    					
		    				default: // set as watershed
		    					setDamPixel(pixel);
		    					break;
		    			}
		    		}
		    		
		    		sortedPixel[level].clear();
		    		
		    	}
		    } // end iterating over gradient magnitude levels
		    
		    sortedPixel = null;
		    
		    // assign dam pixels
			treatDamPixel();
		}
	}
	
	public SegmentMapTile extractSegmentMap(PlanarImage sourceColorImage) {
		if (segmentMatrix==null) {
			floodTile();
		}
		
		LightweightExtractor extractor = new LightweightExtractor(segmentMatrix, segmentColors, segmentAreas);
		return extractor.createSegmentMap(sourceColorImage);
	}
	
//	/**
//	 * Writes the average color (<code>segmentColors</code>) of the segments into the
//	 * destination raster (<code>destinationRaster</code>). The integer matrix
//	 * <code>segmentLabelMatrix</code> holds the information to which segment a pixel
//	 * belongs to. Because the <code>destinationRaster</code> is accessed by absolute
//	 * indices, the rectangle of the current tile is also necessary to know for calculating
//	 * the relative indices. 
//	 * @param destinationRaster the <code>WritableRaster</code> to write the segment color
//	 * @param segmentLabelMatrix the <code>int[][]</code> matrix containing the segment labels per pixel
//	 * @param tileRect the <code>Rectangle</code> to mask the current tile 
//	 * @param segmentColors this <code>ArrayList<double[]></code> contains the average colors indexed
//	 * by the segment label
//	 */
//	protected void writeMosaicImage(WritableRaster destinationRaster, int[][] segmentLabelMatrix, Rectangle tileRect, ArrayList<double[]> segmentColors) {
//		for (int x = tileRect.x; x < (tileRect.x+tileRect.width); x++) { // iterate over all pixel of the rect
//	    	for (int y = tileRect.y; y < (tileRect.y+tileRect.height); y++) {
//	    		destinationRaster.setPixel(x, y, segmentColors.get(segmentLabelMatrix[x - tileRect.x][y - tileRect.y] - lastStartSegmentLabel));
//	    	}
//		}
//	}
	
	/**
	 * Sorts all Pixels of the magnitude image Raster by their gradient magnitude
	 * in ascending order.
	 * Pixels with the same magnitude are kept in an <code>ArrayList</code>.
	 *  
	 * @return an Array of <code>ArrayList<Pixel></code> objects.
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<Pixel>[] getSortedPixel() {
		ArrayList<Pixel>[] sortedPixel = new ArrayList[MAX_FLOOD_LEVEL+1];
		
	    int floodLevel = 0;
	    
	    for (int x = tileRect.x; x < (tileRect.x+tileRect.width); x++) {
	    	for (int y = tileRect.y; y < (tileRect.y+tileRect.height); y++) {
	    		floodLevel = magnitudeRaster.getSample(x, y, 0);
	    		
	    		if (sortedPixel[floodLevel] == null) {
	    			sortedPixel[floodLevel] = new ArrayList<Pixel>();
	    		}
	    		
	    		sortedPixel[floodLevel].add(new Pixel(x, y)); // set 'relative' pixel
	    	}
	    }
	    
	    return sortedPixel;
	}
	
	private void setDamPixel(Pixel pixel) {
		segmentMatrix.setRelativeSegment(pixel, DAM_SEGMENT);
	}
	
	/**
	 * Writes the segment label of the pixel with absolute coordinates <code>x</code> and <code>y</code>
	 * to {@link SegmentMatrix} member.
	 * 
	 * @param pixel stores the absolute <code>x</code> and <code>y</code> coordinate
	 * @param segment the segment to write into the segment label matrix
	 */
	protected void setSegment(Pixel pixel, Segment segment) {
		segmentMatrix.setRelativeSegment(pixel, segment);

		if (segmentColors!=null && segmentAreas!=null) {
			
			double[] pixelColor = new double[colorImageRaster.getNumBands()];
			colorImageRaster.getPixel(pixel.getX(), pixel.getY(), pixelColor);
			int label = segment.getLabel() - segmentMatrix.smallestSegmentLabel;
			
			if (segmentAreas.size()<=label) {
				segmentAreas.add(1);
				segmentColors.add(pixelColor);
			} else {
				int count = segmentAreas.get(label);
				double[] segmentColor = segmentColors.get(label);
				
				for (int band = 0; band < pixelColor.length; band++) {
					segmentColor[band] = (((segmentColor[band] * (double)count) + pixelColor[band]) / ((double)count+1.0d));
				}
				
				segmentColors.set(label, segmentColor);
				segmentAreas.set(label, new Integer(count+1));
			}
		}
	}
	
	/**
	 * This method assigns every pixel which was signed as a watershed pixel during
	 * the flooding process to the best fitting neighboring segment.
	 * The best fitting segment is evaluated by a <code>FittingPixelFinder</code>
	 * neighborhood inspector.
	 */
	protected void treatDamPixel() {
		PixelNeighborhood pixelNeighborhood;
		Pixel pixel;
		FittingPixelFinder fittingPixelFinder;
		Segment bestFittingSegment;
		
		for (int x = tileRect.x; x < (tileRect.x+tileRect.width); x++) { // iterate over all pixel of the rect
	    	for (int y = tileRect.y; y < (tileRect.y+tileRect.height); y++) {
	    		
	    		if (segmentMatrix.getRelativeSegment(x, y) == DAM_SEGMENT) { // only dam pixel
	    			pixel = new Pixel(x, y);
	    			
	    			// finds the neighboring pixel which fits best
	    			fittingPixelFinder = new FittingPixelFinder(segmentMatrix, segmentsToIgnore, pixel, magnitudeRaster, MAX_FLOOD_LEVEL);
	    			
	    			pixelNeighborhood = PixelNeighborhood.createInstance(pixel, tileRect);
	    			pixelNeighborhood.addInspector(fittingPixelFinder);
	    			pixelNeighborhood.inspectNeighborhood();
	    			
					if (fittingPixelFinder.getBestFittingPixel()!=null) {
						bestFittingSegment = fittingPixelFinder.getBestFittingSegment();
						setSegment(pixel, bestFittingSegment);
					}
	    		}
	    		
	    	}
	    }
		
	}
}
