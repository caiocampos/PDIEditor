package info.wurzinger.segmenting.elements;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImagePyramid {
	
	public enum FetchingMode { off, immediately, lazy }
	
	private FetchingMode fetchingMode = FetchingMode.off;
	private int levels = 0;
	private double scalingFactor = 2d;
	private int[][] resolution = null;
	private BufferedImage[] levelImage = null;
	private BufferedImage original = null;
	
	/**
	 * Instantiates an <code>ImagePyramid</code> object from a given <code>BufferedImage</code>
	 * down to a coarsest level, where the smaller side of the image is <code>smallestSide</code>.
	 * The resolution steps are multiples to the coarsest step.
	 * 
	 * @param image is the image in original size 
	 * @param smallestSide considers the smallest side length of the image in the coarsest level.
	 * @param scalingFactor represents the sharp increase of the pyramid and must be smaller than 1.0.
	 * The closer the <em>scalingFactor</em> lies to 1.0 the sharper the pyramid increases and
	 * the more layers the pyramid will have.
	 * @param fetchingMode Represents the memory and runtime behavior of calculating and storing
	 * the resized images.
	 */
	public ImagePyramid(BufferedImage image, int smallestSide, double scalingFactor, FetchingMode fetchingMode) {
		int smallestX, smallestY;
		
		original = image;
		this.fetchingMode = fetchingMode;
		
		if (scalingFactor>1f) {
			this.scalingFactor = scalingFactor;
		}
		
		if (image!=null) {
			double ratio = (double)image.getWidth() / (double)image.getHeight(); 
			if (ratio >= 1d) {
				smallestX = (int) ((double) smallestSide * ratio);
				smallestY = smallestSide;
			} else {
				smallestX = smallestSide;
				smallestY = (int) ((double) smallestSide / ratio);
			}
			
			// get maximum resolution
			int[] maxResolution = getMaxResolutionAndLevel(image, smallestX, smallestY, this.scalingFactor);
			
			// set levels and the resolutions
			double reciprocalScalingFactor = 1d/scalingFactor;
			levels = maxResolution[2] + 1;
			resolution = new int[levels][2]; // x and y at each resolution level
			
			for (int level=0; level<levels; level++) {
				resolution[level][0] = (int) ((double) smallestX * Math.pow(reciprocalScalingFactor, level));
				resolution[level][1] = (int) ((double) smallestY * Math.pow(reciprocalScalingFactor, level));
			}
			
			levelImage = new BufferedImage[levels];
			
			if (fetchingMode==FetchingMode.immediately) {
				buildUp();
			}
		}
	}

	/**
	 * Uses lazy fetching.
	 * 
	 * @see ImagePyramid#ImagePyramid(BufferedImage, int, double, info.wurzinger.segmenting.elements.ImagePyramid.FetchingMode)
	 */
	public ImagePyramid(BufferedImage image, int smallestSide, double scalingFactor) {
		this(image, smallestSide, scalingFactor, FetchingMode.lazy);
	}
	
	/**
	 * Uses lazy fetching and the scaling factor 0.5.
	 * 
	 * @see ImagePyramid#ImagePyramid(BufferedImage, int, double, info.wurzinger.segmenting.elements.ImagePyramid.FetchingMode)
	 */
	public ImagePyramid(BufferedImage image, int smallestSide) {
		this(image, smallestSide, 0.5d);
	}
	
	/**
	 * Uses the scaling factor 0.5.
	 * 
	 * @see ImagePyramid#ImagePyramid(BufferedImage, int, double, info.wurzinger.segmenting.elements.ImagePyramid.FetchingMode)
	 */
	public ImagePyramid(BufferedImage image, int smallestSide, FetchingMode fetchingMode) {
		this(image, smallestSide, 0.5d, fetchingMode);
	}
	
	/* build up the image pyramid, resizes the image to the appropriate resolutions and stores them in the levelImage-array */
	private void buildUp() {
		if (levels>0) {
			// resize original image first to the maximum resolution supported by the image pyramid
			// last and finest layer
			levelImage[levels-1] = new BufferedImage(resolution[levels-1][0], resolution[levels-1][1], original.getType());
			double initialRescaleFactor = (double) levelImage[levels-1].getWidth() / (double) original.getWidth();

			// create resizing operation to rescale to maximum size
			getResizeOp(initialRescaleFactor).filter(original, levelImage[levels-1]);
			
			// create resizing operation
			AffineTransformOp resizeOp = getResizeOp(1d/scalingFactor);
			
			/* start with second to the last level; the image gets coarser and coarser on each step */
			for (int level=levels-2; level>=0; level--) {
				levelImage[level] = new BufferedImage(resolution[level][0], resolution[level][1], levelImage[level+1].getType());
				levelImage[level] = resizeOp.filter(levelImage[level+1], levelImage[level]);
			}
		}
	}
	
	/**
	 * Get an image for a given level.
	 * 
	 * @param level is the desired resolution level whereas 0 is the most coarsest level.
	 * The higher the level, the higher the resolution.
	 * @return the image at the resolution layer <code>level</code>.
	 * @throws IndexOutOfBoundsException is thrown if <code>level</code> references to a layer
	 * which doesn't exist - this will occur, if <code>level</code> is not within interval [0, getLevels()-1].
	 */
	public BufferedImage getImage(int level) throws IndexOutOfBoundsException {
		BufferedImage img = null;
		
		if (level<0 || level>= levels) {
			throw new IndexOutOfBoundsException("The level " + level + " is not in range [0, " + (level-1) + "].");
		}
		
		if (levelImage[level]!=null) {
			img = levelImage[level]; 
		} else {
			// set resolution
			img = new BufferedImage(resolution[level][0], resolution[level][0], original.getType());
			
			// resize image
			double resizeFactor = (double) resolution[level][0] / (double) original.getWidth();
			getResizeOp(resizeFactor).filter(original, img);
			
			// keep image in storage if fetching mode wasn't turned off
			if (fetchingMode!=FetchingMode.off) {
				levelImage[level] = img;
			}
		}
		
		return img;
	}
	
	/**
	 * @return the number of levels this pyramid has.
	 */
	public int getLevels() {
		return levels;
	}
	
	/**
	 * @return the resolution as two-dimensional array. The width at layer 4 for example,
	 * will be index as getResolutions()[4][0] or the height at the coarsest layer will be
	 * indexed as getResolutions()[0][1].
	 */
	public int[][] getResolutions() {
		return resolution;
	}
	
	/* gets an resizing transformation for a given resizing factor with constant width-to-height ratio */
	private static AffineTransformOp getResizeOp(double resizeFactor) {
		AffineTransform resizeTransform = AffineTransform.getScaleInstance(resizeFactor, resizeFactor);
		return new AffineTransformOp(resizeTransform, AffineTransformOp.TYPE_BILINEAR);
	}
	
	/* calculates the highest possible resolution for the given smallestX, smallestY and scalingFactor */
	private static int[] getMaxResolutionAndLevel(BufferedImage image, int smallestX, int smallestY, double scalingFactor) {
		int[] maxResolution = new int[3];
		int maxLevel = 0;
		int imgX = image.getWidth();
		int imgY = image.getHeight();
		
		maxResolution[0] = smallestX; // x/width
		maxResolution[1] = smallestY; // y/height
		
		// double x and y
		while (maxResolution[0]<=imgX && maxResolution[1]<=imgY) {
			maxResolution[0] *= scalingFactor;
			maxResolution[1] *= scalingFactor;
			maxLevel++;
		}
		
		// halve x and y
		if (maxResolution[0]>smallestX) {
			maxResolution[0] /= scalingFactor;
			maxResolution[1] /= scalingFactor;
			maxLevel--;
		}
		
		maxResolution[2] = maxLevel;
		
		return maxResolution;
	}
	
	/**
	 * @return an <code>ImagePyramid</code> object which have the same resolutions
	 * and level counts, but no images.
	 */
	public ImagePyramid getResolutionClone() {
		ImagePyramid clone = new ImagePyramid(null, 0, this.fetchingMode);
		
		clone.levels = levels;
		clone.levelImage = new BufferedImage[levels];
		clone.resolution = new int[levels][2];
		
		System.arraycopy(resolution, 0, clone.resolution, 0, resolution.length);
		for (int level = 0; level<resolution.length; level++) {
			System.arraycopy(resolution[level], 0, clone.resolution[level], 0, resolution[level].length);
		}
		
		clone.scalingFactor = scalingFactor;
		
		return clone;
	}
	
	/**
	 * Set an image to the specified level.
	 * 
	 * @param level is the desired resolution level whereas 0 is the most coarsest level.
	 * The higher the level, the higher the resolution.
	 * @param image is the {@link BufferedImage} which has to be set to the
	 * resolution layer <code>level</code>.
	 * @throws IndexOutOfBoundsException is thrown if <code>level</code> references to a layer
	 * which doesn't exist - this will occur, if <code>level</code> is not within interval [0, getLevels()-1].
	 */
	public void setImage(int level, BufferedImage image) throws IndexOutOfBoundsException {
		if (level<0 || level>= levels) {
			throw new IndexOutOfBoundsException("The level " + level + " is not in range [0, " + (level-1) + "].");
		}
		
		levelImage[level] = image;
	}
}
