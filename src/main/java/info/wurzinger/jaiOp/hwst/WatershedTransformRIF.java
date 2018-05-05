package info.wurzinger.jaiOp.hwst;

import info.wurzinger.segmenting.Operations;
import info.wurzinger.segmenting.elements.segment.MergedSegmentMap;
import info.wurzinger.segmenting.elements.segment.SegmentMap;
import info.wurzinger.segmenting.elements.segment.SegmentMapTile;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public class WatershedTransformRIF implements RenderedImageFactory {

	public WatershedTransformRIF() {}
	
	/**
	 * The create method, that will be called to create a RenderedImage (or chain
	 * of operators that represents one).
	 */
	public RenderedImage create(ParameterBlock paramBlock, RenderingHints hints) {
		
		// Get data from the ParameterBlock.
		RenderedImage colorImageSource = paramBlock.getRenderedSource(0);
		int tileSize = paramBlock.getIntParameter(0);
		double minWstGradient = paramBlock.getDoubleParameter(1);
		int minSegmentSize = paramBlock.getIntParameter(2);
		int smoothingBoxFilterKernelSize = paramBlock.getIntParameter(3);
		boolean downsamplingOption = (paramBlock.getIntParameter(4)==1);
		long tendedPixelCount = paramBlock.getLongParameter(5);
		
		// *** Create a transformation chain ***
		
		RenderedOp gradientMagnitudeImageOp;
		RenderedImage colorImageOp = colorImageSource;
		RenderedOp floodedImageOp;
		ParameterBlock floodingParamBlock;
		
		// downsample image if necessary
		int downsamplingFactor = 1;
		if (downsamplingOption) {
			downsamplingFactor = getDownsamplingFactor(tendedPixelCount, colorImageOp.getWidth(), colorImageOp.getHeight());
			float scalingFactor = 1f / (float) downsamplingFactor;
			colorImageOp = Operations.scale(colorImageOp, scalingFactor);
		}
		
		// smooth image
		if (smoothingBoxFilterKernelSize%2==0) smoothingBoxFilterKernelSize++; // only odd filter size possible
		RenderedOp smoothedImageOp = Operations.smooth(colorImageOp, smoothingBoxFilterKernelSize);
		
		// get the gradient image (sobel gradient)
		gradientMagnitudeImageOp = Operations.gradientMagnitude(smoothedImageOp);
		
		// linear band reduction to a gray scaled image
		gradientMagnitudeImageOp = Operations.colorConvertToGray(gradientMagnitudeImageOp);
		
	    // Create a ParameterBlock with that image and parameters.
		floodingParamBlock = new ParameterBlock();
		floodingParamBlock.addSource(gradientMagnitudeImageOp); // magnitude raster
		floodingParamBlock.addSource(colorImageOp); // color image raster
	    
		// set the tile size parameter
		floodingParamBlock.add(tileSize);
		// set the first segment label to start labeling from
		floodingParamBlock.add(1);
		
	    // Create the output image.
		floodedImageOp = JAI.create("WstFlooder", floodingParamBlock, null);
		
		// render the image tile by tile and reduce the SegmentMapTile by the
		// hierarchical watershed transformation
		renderFloodedImageOp(floodedImageOp, minWstGradient, minSegmentSize, downsamplingFactor);
		
		return floodedImageOp;
	}
	
	/**
	 * Renders the tiles of the image, extract their segment maps, exectue the HWST,
	 * and eliminate small segments.
	 * 
	 * @param floodedImageOp
	 * @param minWstGradient
	 * @param minSegmentSize
	 * @param upsamplingFactor
	 */
	protected void renderFloodedImageOp(RenderedOp floodedImageOp, double minWstGradient, int minSegmentSize, int upsamplingFactor) {
		//	floodedImageOp.getTiles();
		//	segmentCount = ((Integer) floodedImageOp.getProperty(SegmentMapMatrixGenerator.PROPERTY_SEGMENT_COUNT_MATRIX)).intValue();
		int countTilesX = (int) Math.ceil((double) floodedImageOp.getWidth() / (double) floodedImageOp.getTileWidth());
		int countTilesY = (int) Math.ceil((double) floodedImageOp.getHeight() / (double) floodedImageOp.getTileHeight());
		
		SegmentMapTile[][] segmentMapMatrix = null;
		
		for (int tileY=0; tileY < countTilesY; tileY++) {
			for (int tileX=0; tileX < countTilesX; tileX++) {
				// render all tiles immediately
				floodedImageOp.getTile(tileX, tileY);
				
				segmentMapMatrix = (SegmentMapTile[][]) floodedImageOp.getProperty(SegmentMapMatrixGenerator.PROPERTY_SEGMENT_MAP_MATRIX);
				
				// execute HWST
				new HierarchicalWatershedTransformer(segmentMapMatrix[tileX][tileY]).execute(minWstGradient);
				
				// eliminate small segments
				segmentMapMatrix[tileX][tileY].joinSmallSegments(minSegmentSize);
			}
		}
		
		SegmentMap mergedMap = new MergedSegmentMap(segmentMapMatrix, floodedImageOp.getWidth(), floodedImageOp.getHeight(), upsamplingFactor);
		
		floodedImageOp.setProperty(SegmentMapGenerator.PROPERTY_SEGMENT_MAP, mergedMap);
		floodedImageOp.removeProperty(SegmentMapMatrixGenerator.PROPERTY_SEGMENT_MAP_MATRIX);
	}
	
	private static int getDownsamplingFactor(long tendedPixelCount, int srcWidth, int srcHeight) {
		int resizeFactor = 2; // is at least 2
		int nextResizeFactor = 2;
		long pixelCount = (srcWidth * srcHeight) / nextResizeFactor;
		
		do {
			resizeFactor = nextResizeFactor;
			nextResizeFactor *= nextResizeFactor;
			pixelCount = (srcWidth * srcHeight) / nextResizeFactor;
			
		} while (tendedPixelCount < pixelCount);
		
		if (resizeFactor > 2) {
			long lastPixelCount = (srcWidth * srcHeight) / (resizeFactor/2);
			
			if (Math.abs(tendedPixelCount - lastPixelCount) < Math.abs(tendedPixelCount - pixelCount)) {
				resizeFactor /= 2;
			}
		}
		
		return resizeFactor;
	}
}
