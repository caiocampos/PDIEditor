package info.wurzinger.segmenting.elements.segment.state;

import info.wurzinger.segmenting.elements.segment.SegmentMap;

public class SegmentMapStateException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SegmentMapStateException(SegmentMap map) {
		super("SegmentMap " + map.toString() + " is in the wrong state '" + map.getState().toString() + "'.");
	}
	
	public SegmentMapStateException(SegmentMap map, SegmentMapState expectedState) {
		super("SegmentMap " + map.toString() + " is in state '" +  map.getState().toString() + "' but it was expected to be in state '" + expectedState.toString() + "'.");
	}
}
