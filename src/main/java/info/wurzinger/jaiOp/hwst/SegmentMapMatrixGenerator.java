package info.wurzinger.jaiOp.hwst;

import info.wurzinger.jaiOp.wst.FlooderDescriptor;

import java.awt.Image;

import javax.media.jai.OperationDescriptor;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

import info.wurzinger.segmenting.elements.segment.SegmentMap;

@SuppressWarnings("serial")
public class SegmentMapMatrixGenerator implements PropertyGenerator {
	
	public static final String PROPERTY_SEGMENT_MAP_MATRIX = "segmentMapTileMatrix";
	private static final String[] PROPERTY_NAMES = {PROPERTY_SEGMENT_MAP_MATRIX };
	
	/**
	 * empty constructor
	 */
	public SegmentMapMatrixGenerator() {}

	/** {@inheritDoc} */
	public boolean canGenerateProperties(Object opNode) {
		if (opNode instanceof RenderedOp) {
			RenderedOp renOp = (RenderedOp) opNode;
			OperationDescriptor flooderDescriptor = new FlooderDescriptor();
			
			if (flooderDescriptor.getName().equalsIgnoreCase(renOp.getOperationName())) {
				return true;
			}
		}
		
		return false;
	}

	/** {@inheritDoc} */
	public Class getClass(String propertyName) {
		if (propertyName.equalsIgnoreCase(PROPERTY_SEGMENT_MAP_MATRIX)) {
			return SegmentMap[][].class;
		} else {
			return null;
		}
	}

	/** {@inheritDoc} */
	public Object getProperty(String propertyName, Object opNode) {
		if (opNode instanceof RenderedOp) {
			RenderedOp renderedOp = (RenderedOp) opNode;
			
			if (propertyName.equalsIgnoreCase(PROPERTY_SEGMENT_MAP_MATRIX)) {
				// The segment maps are set by the FlooderOpImage when the OpImage
				// is rendered.
				return renderedOp.getRendering().getProperty(propertyName);
			}
		}
		
		return Image.UndefinedProperty;
	}

	/** {@inheritDoc} */
	public Object getProperty(String propertyName, RenderedOp renderedOp) {
		return getProperty(propertyName, (Object) renderedOp);
	}
	
	/** {@inheritDoc} */
	public Object getProperty(String propertyName, RenderableOp renderableOp) {
		return getProperty(propertyName, (Object) renderableOp);
	}
	
	/** {@inheritDoc} */
	public String[] getPropertyNames() {
		return PROPERTY_NAMES;
	}
}
