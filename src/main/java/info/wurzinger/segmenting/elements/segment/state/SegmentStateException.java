package info.wurzinger.segmenting.elements.segment.state;


public class SegmentStateException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SegmentStateException(int segmentLabel, SegmentState wrongState) {
		super("Segment " + segmentLabel + " is in the wrong state '" + wrongState.toString() + "'.");
	}
	
	public SegmentStateException(int segmentLabel, SegmentState wrongState, SegmentState expectedState) {
		super("Segment " + segmentLabel + " is in state '" + wrongState.toString() + "' but it was expected to be in state '" + expectedState.toString() + "'.");
	}
}
