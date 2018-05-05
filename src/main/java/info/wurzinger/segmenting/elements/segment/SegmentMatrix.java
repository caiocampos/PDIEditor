package info.wurzinger.segmenting.elements.segment;

import info.wurzinger.segmenting.elements.Pixel;

public class SegmentMatrix {
	
	protected Segment[][] segmentMatrix; // TODO: set private
	public int smallestSegmentLabel;
	public int tileXOffset;
	public int tileYOffset;
	
	protected int width = 0;
	protected int height = 0;
	
	public SegmentMatrix(int width, int height) {
		this(new Segment[width][height], width, height, SegmentMap.SEGMENT_LABEL_INFIMUM+1, 0, 0);
	}
	
	public SegmentMatrix(int width, int height, int smallestSegmentLabel, int tileXOffset, int tileYOffset) {
		this(new Segment[width][height], width, height, smallestSegmentLabel, tileXOffset, tileYOffset);
	}
	
	protected SegmentMatrix(Segment[][] segmentMatrix, int width, int height, int smallestSegmentLabel, int tileXOffset, int tileYOffset) {
		this.segmentMatrix = segmentMatrix;
		
		this.width = width;
		this.height = height;
		
		this.smallestSegmentLabel = smallestSegmentLabel;
		this.tileXOffset = tileXOffset;
		this.tileYOffset = tileYOffset;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Segment getSegment(int relativeX, int relativeY) {
		return segmentMatrix[relativeX][relativeY];
	}
	public Segment getSegment(Pixel relativePixel) {
		return segmentMatrix[relativePixel.getX()][relativePixel.getY()];
	}
	
	public void setSegment(int relativeX, int relativeY, Segment segment) {
		segmentMatrix[relativeX][relativeY] = segment;
	}
	public void setSegment(Pixel relativePixel, Segment segment) {
		segmentMatrix[relativePixel.getX()][relativePixel.getY()] = segment;
	}
	
	public Segment getRelativeSegment(int absoluteX, int absoluteY) {
		return segmentMatrix[absoluteX - tileXOffset][absoluteY - tileYOffset];
	}
	
	public Segment getRelativeSegment(double absoluteX, double absoluteY) {
		return segmentMatrix[(int) absoluteX - tileXOffset][(int) absoluteY - tileYOffset];
	}
	
	public Segment getRelativeSegment(Pixel absolutePixel) {
		return segmentMatrix[absolutePixel.getX() - tileXOffset][absolutePixel.getY() - tileYOffset];
	}
	
	
	public void setRelativeSegment(int absoluteX, int absoluteY, Segment segment) {
		segmentMatrix[absoluteX - tileXOffset][absoluteY - tileYOffset] = segment;
	}
	
	public void setRelativeSegment(double absoluteX, double absoluteY, Segment segment) {
		segmentMatrix[(int) absoluteX - tileXOffset][(int) absoluteY - tileYOffset] = segment;
	}
	
	public void setRelativeSegment(Pixel absolutePixel, Segment segment) {
		segmentMatrix[absolutePixel.getX() - tileXOffset][absolutePixel.getY() - tileYOffset] = segment;
	}
}
