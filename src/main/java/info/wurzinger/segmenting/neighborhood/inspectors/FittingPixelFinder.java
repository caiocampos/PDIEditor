package info.wurzinger.segmenting.neighborhood.inspectors;

import info.wurzinger.segmenting.elements.segment.Segment;
import info.wurzinger.segmenting.elements.segment.SegmentMatrix;
import info.wurzinger.segmenting.elements.Pixel;

import java.awt.image.Raster;
import java.util.List;


public class FittingPixelFinder extends NeighborhoodInspector<Pixel> {
	
	private int maxFloodLevel;
	private int minFloodLevelDifferece;
	private Segment bestFittingSegment = null;
	private Pixel bestFittingPixel;
	
	private SegmentMatrix segmentMatrix;
	private Pixel centerPixel;
	private Raster imgRaster;
	private List<Segment> segmentsToIgnore;
	
	public FittingPixelFinder(SegmentMatrix segmentMatrix, List<Segment> segmentsToIgnore, Pixel centerPixel, Raster imgRaster, int maxFloodLevel) {
		reset();
		this.segmentMatrix = segmentMatrix;
		this.centerPixel = centerPixel;
		this.imgRaster = imgRaster;
		this.maxFloodLevel = maxFloodLevel;
		this.segmentsToIgnore = segmentsToIgnore;
	}
	
	public void inspect(Pixel p) {
		Segment segment = segmentMatrix.getRelativeSegment(p.getX(), p.getY());
		
		if (segment!=null && !segmentsToIgnore.contains(segment)) {
				
			int floodLevelDifferece = Math.abs(imgRaster.getSample(p.getX(), p.getY(), 0) - imgRaster.getSample(centerPixel.getX(), centerPixel.getY(), 0) );
			if (floodLevelDifferece < minFloodLevelDifferece) {
				bestFittingPixel = p;
				bestFittingSegment = segmentMatrix.getRelativeSegment(p);
				minFloodLevelDifferece = floodLevelDifferece;
			}
		}
	}
	
	public void reset() {
		minFloodLevelDifferece = maxFloodLevel;
		bestFittingSegment = null;
		bestFittingPixel = null;
	}
	
	public Segment getBestFittingSegment() {
		return bestFittingSegment;
	}
	
	public Pixel getBestFittingPixel() {
		return bestFittingPixel;
	}
}
