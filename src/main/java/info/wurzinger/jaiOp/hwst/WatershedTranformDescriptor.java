package info.wurzinger.jaiOp.hwst;

import info.wurzinger.jaiOp.wst.FlooderDescriptor;

import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.Range;

@SuppressWarnings("serial")
public class WatershedTranformDescriptor extends OperationDescriptorImpl {
	  // A map-like array of strings with resources information.
	private static final String[][] resources = {
			{ "GlobalName", "HierarchicalWatershedTranform" },
			{ "LocalName", "HierarchicalWatershedTranform" },
			{ "Vendor", "info.wurzinger" },
			{ "Description", "Execute the watershed transform by an iterative calling the watershed flooding algorithm on a magnitude image." },
			{ "DocURL", "http://www.wurzinger.info/jaiOp/hwst" }, // TODO: FINAL WORK: publish javadoc
			{ "Version", "1.0" },
			{ "arg0Desc", "tile size" },
			{ "arg1Desc", "tolerable color gradient" },
			{ "arg2Desc", "minimal segment size" },
			{ "arg3Desc", "smoothing kernel size" },
			{ "arg4Desc", "downsampling option" },
			{ "arg5Desc", "tended pixel count" }
		};

	// An array of strings with the supported modes for this operator.
	private static final String[] supportedModes = { "rendered" };

	// An array of strings with the parameter names for this operator. 
	private static final String[] paramNames = { "tileSize", "minGradient", "minSegmentSize", "smoothingKernelSize", "downsamplingOption", "upsamplingFactor" };

	// An array of Classes with the parameters' classes for this operator.
	private static final Class[] paramClasses = { Integer.class, Double.class, Integer.class, Integer.class, Integer.class, Long.class };

	// An array of Objects with the parameters' default values.
	private static final Object[] paramDefaults = { 500, 20d, 6, 3, 0, 500000L };

	// An array of Ranges with ranges of valid parameter values. 
	private static final Range[] validParamValues = {
		new Range(java.lang.Integer.class, new Integer(50), new Integer(10000)), // tileSize
		new Range(java.lang.Double.class, new Double(0d), new Double(1000d)), // minGradient
		new Range(java.lang.Integer.class, new Integer(1), new Integer(100000)), // minSegmentSize
		new Range(java.lang.Integer.class, new Integer(1), new Integer(17)), // smoothingKernelSize
		new Range(java.lang.Integer.class, new Integer(0), new Integer(1)), // downsamplingOption (0 > OFF, 1 > ON)
		new Range(java.lang.Long.class, new Long(2000), Long.MAX_VALUE) // tended pixel count (width x height)
	};

	// The number of sources required for this operator.
	private static final int numSources = 1;

	// A flag that indicates whether the operator is already registered.
	private static boolean registered = false;

	/**
	 * The constructor for this descriptor, which just calls the constructor
	 * for its ancestral class (OperationDescriptorImpl).
	 */
	public WatershedTranformDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses, paramDefaults, validParamValues);
	}

	/**
	 * A method to register this operator with the OperationRegistry and
	 * RIFRegistry. 
	 */
	public static void register() {
		// Because the watershed transformation depends on the flooder, it has to be
		// ensured, that the Flooder Descriptor is also registered.
		FlooderDescriptor.register();
		
		if (!registered) {
			// Get the OperationRegistry.
			OperationRegistry op = JAI.getDefaultInstance().getOperationRegistry();
			
			// Register the operator's descriptor. 
			WatershedTranformDescriptor desc = new WatershedTranformDescriptor();
			
			op.registerDescriptor(desc);
			
			// Register the operators's RIF.
			WatershedTransformRIF rif = new WatershedTransformRIF();
			
			RIFRegistry.register(op, "HierarchicalWatershedTranform", "info.wurzinger", rif);
			
			registered = true;
		}
	}
	
	/**
	 * Returns an <CODE>Array</CODE> of <CODE>PropertyGenerator</CODE>s
	 * implementing the property inheritance for this descriptor containing an
	 * instance of <CODE>SegmentMapMerger</CODE>.
	 * @param aModeName The registry mode name.
	 * @return An array of <CODE>PropertyGenerator</CODE>s, or null if this
	 * operation does not have any own <CODE>PropertyGenerator</CODE>s.
	 */
	public PropertyGenerator[] getPropertyGenerators(String aModeName) {
		if (aModeName.equalsIgnoreCase("rendered")) {
			return new PropertyGenerator[] {}; //TODO: return a SegmentMapMerger
		}
		return null;
	}
	/**
	 * @deprecated
	 * This is the deprecated version for the <code>getPropertyGenerators(String aModeName)</code>
	 * method. Only for downwards-compatibility.
	 */
	public PropertyGenerator[] getPropertyGenerators() {
		return getPropertyGenerators("rendered");
	}
}
