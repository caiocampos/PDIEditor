package info.wurzinger.segmenting.elements.segment.state;

import info.wurzinger.segmenting.elements.segment.Segment;
import info.wurzinger.segmenting.elements.segment.SegmentMap;

/**
 * The <code>SegmentStateInfo</code> interface is used to earmark
 * classes which can be used in {@link Segment} to hold further
 * information for processing {@link Segment}s and {@link SegmentMap}s.
 * 
 * @see Segment#stateInfo
 * 
 * @author Martin Wurzinger
 */
public interface SegmentStateInfo {
	public boolean equals(Object o);
}
