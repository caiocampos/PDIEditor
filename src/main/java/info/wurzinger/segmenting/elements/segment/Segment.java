package info.wurzinger.segmenting.elements.segment;

import static info.wurzinger.segmenting.elements.segment.state.SegmentState.*;

import info.wurzinger.segmenting.elements.Atom;
import info.wurzinger.segmenting.elements.Pixel;

import java.util.TreeSet;

import info.wurzinger.segmenting.elements.segment.state.*;

/**
 * A segment represents an area in an image with common properties.
 * These properties are the size of the area and the average color.
 * <code>Segment</code> objects can be put into a {@link SegmentMap}
 * wherefore every segment have a <em>label</em> which represents
 * a <code>{@link SegmentMap}</code>-wide unique ID.
 * 
 * The area of the segment is described as {@link Polygon} object.
 * For further information please look at the documentation of the
 * JTS Topology Suite.
 * 
 * @author Martin Wurzinger
 */
public class Segment extends Atom implements Comparable<Segment> {
	
	protected SegmentState state = LIGHTWEIGHT;
	
	protected SegmentStateInfo stateInfo = null;
	
	/** the label of this segment. The label is used as <em>ID</em>
	 * which is <em>unique</em> within the {@link SegmentMap} this segment
	 * belongs to. */
	protected int label = 0;
	
	/** the average color of the {@link Pixel}s this segment covers */
	protected double[] color = null;
	
	/** the number of {@link Pixel}s this segment covers */
	protected int areaSize = 0; // areaSize in pixel
	
	/** the neighboring segments */
	protected TreeSet<Segment> neighbors;
	
	public Segment(int label) {
		this(label, new TreeSet<Segment>(), LIGHTWEIGHT, null);
	}
	
	protected Segment(int label, TreeSet<Segment> neighbors, SegmentState state, SegmentStateInfo stateInfo) {
		this.label = label;
		this.neighbors = neighbors;
		this.state = state;
		this.stateInfo = stateInfo;
	}
	
	/**
	 * @return the average color of the pixel inside the segment.
	 */
	public double[] getColor() {
		switch (state) {
			case FORWARD:
				return getForwardInfo().parent.getColor();
			default:
				return color;
		}
	}
	public void setColor(double[] color) {
		switch (state) {
			case FORWARD:
				getForwardInfo().parent.setColor(color);
				break;
			default:
				this.color = color;
		}
	}
	
	/**
	 * @return the number of pixel this segment covers.
	 */
	public int getAreaSize() {
		switch (state) {
			case FORWARD:
				return getForwardInfo().parent.getAreaSize();
			default:
				return areaSize;
		}
	}

	public void setAreaSize(int areaSizeInPixel) {
		switch (state) {
			case FORWARD:
				getForwardInfo().parent.setAreaSize(areaSizeInPixel);
				break;
			default:
				areaSize = areaSizeInPixel;
		}
	}
	
	/**
	 * @return the label of this segment. The label is used as <em>ID</em>
	 * which is <em>unique</em> within the {@link SegmentMap} this segment
	 * belongs to.
	 */
	public int getLabel() {
		return label;
	}
	
	/**
	 * @return the neighboring segments
	 */
	public TreeSet<Segment> getNeighbors() {
		switch (state) {
			case FORWARD:
				return getForwardInfo().parent.getNeighbors();
			default:
				return neighbors;
		}
	}
	
	/**
	 * Add a {@link Segment} to one's neighbors
	 */
	public void addNeighbor(Segment neighbor) {
		switch (state) {
			case FORWARD:
				getForwardInfo().parent.neighbors.add(neighbor);
				break;
			default:
				neighbors.add(neighbor);
		}
	}
	
	public ForwardInfo getForwardInfo() {
		return ((ForwardInfo) stateInfo);
	}
	
	public Segment getParent() {
		switch (state) {
			case FORWARD:
				return getForwardInfo().parent.getParent();
			default:
				return this;
		}
	}
	
	public SegmentState getState() {
		return state;
	}
	
	public void setState(SegmentState state) {
		
		switch (state) {
			case LIGHTWEIGHT:
				this.stateInfo = null;
				this.state = state;
				break;
			case FORWARD:
				color = null;
				areaSize = 0;
				neighbors = null;
				this.stateInfo = new ForwardInfo();
				this.state = state;
				break;
			default:
				break;
		}
	}
	
	/**
	 * compare the label of <code>this</code> with the label of
	 * <code>s</code>. If the label of <code>s</code> is smaller
	 * a positive value is returned, else if the label of <code>this</code>
	 * is smaller a negative value is returned. If the labels are equal
	 * a 0 is returned.
	 * 
	 * @return an Integer value as described in {@link Comparable} to
	 * get the order between to segments.
	 * 
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Segment s) {
		return label - s.label;
	}
	
	/**
	 * @return <code>true</code> if the segments properties like
	 * <em>label</em>, <em>areaSize</em>, <em>neighbors</em> and
	 * the <em>polygon</em> are equal, else it return <code>false</code>
	 */
	public boolean equals(Object o) {
		if (o==null) return false;
		
		if (!(o instanceof Segment)) return false;
		
		Segment s = (Segment) o;
			
		return s.getLabel()==label
			&& s.areaSize==areaSize
			&& s.getNeighbors().size()==neighbors.size()
			&& ((stateInfo==null && s.stateInfo==null) || stateInfo.equals(s.stateInfo));
	}
	
	public static Segment add(Segment segmA, Segment segmB, int newSegmentLabel) {
		if (segmA.state!=LIGHTWEIGHT && segmA.state!=POLYGON && segmA.state!=VALID) {
			throw new SegmentStateException(segmA.label, segmA.state);
		} else if (segmB.state!=LIGHTWEIGHT && segmB.state!=POLYGON && segmB.state!=VALID) {
			throw new SegmentStateException(segmB.label, segmB.state);
		}
		
		Segment sum = null;
		
		if (segmA!=null && segmB!=null && segmA.label != segmB.label) {
		
			sum = new Segment(newSegmentLabel);
			sum.areaSize = segmA.areaSize;
			sum.color = segmA.color;
			
			segmB.neighbors.remove(segmA);
			segmA.neighbors.remove(segmB);
			
			sum.neighbors.addAll(segmA.neighbors);
			sum.neighbors.addAll(segmB.neighbors);
			
			for (Segment neighborOfA : segmA.neighbors) {
				neighborOfA.neighbors.remove(segmA);
				neighborOfA.neighbors.add(sum);
			}
			for (Segment neighborOfB : segmB.neighbors) {
				neighborOfB.neighbors.remove(segmB);
				neighborOfB.neighbors.add(sum);
			}
			
			segmA.neighbors.clear();
			segmB.neighbors.clear();
			
			sum.state = segmA.state;
			sum.stateInfo = segmA.stateInfo;
			
			sum.add(segmB, false);
		}
		
		return sum;
	}
	
	protected Segment add(Segment segmentToAdd) {
		return add(segmentToAdd, true);
	}
	
	/**
	 * Join <code>segmentToAdd</code> to <code>this</code> segment.
	 * The polygons are unified, area sizes are added, neighbor
	 * references are merged a new average color is calculated.
	 *  
	 * @param segmentToAdd is the segment which has to be added to
	 * <code>this</code> segment.
	 */
	protected Segment add(Segment segmentToAdd, boolean addNeighborhood) {
		if (this!=segmentToAdd && segmentToAdd!=null) {
		
			if (addNeighborhood) {
				for (Segment neighbor : segmentToAdd.neighbors) {
					neighbor.neighbors.remove(segmentToAdd);
				}
				segmentToAdd.neighbors.remove(this);
				
				for (Segment neighbor : segmentToAdd.neighbors) {
					neighbor.neighbors.add(this);
				}
				
				// merge neighborhood
				neighbors.addAll(segmentToAdd.neighbors);
			}
			
			int newAreaSize = areaSize + segmentToAdd.areaSize; 
			
			// set color average
			if (color!=null) {
				for (int band = 0; band < color.length; band++) {
					color[band] = ((color[band] * (double)areaSize + segmentToAdd.color[band] * (double)segmentToAdd.areaSize) / (double)newAreaSize);
				}
			}
			
			areaSize = newAreaSize;
		}
		
		return this;
	}
	
	/**
	 * @return a {@link String} describing <code>this</code> segment. 
	 */
	public String toString() {
		String str = "segment " + label + " (" + state.toString() + ")";
		
		if (state==LIGHTWEIGHT) {
				str += areaSize + ": pixels and "+ neighbors.size() +" neighbors";
		}
		
		return str;
	}

}
