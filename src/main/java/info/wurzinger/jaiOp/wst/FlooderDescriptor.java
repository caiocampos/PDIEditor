package info.wurzinger.jaiOp.wst;

import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.Range;

import info.wurzinger.jaiOp.hwst.SegmentMapMatrixGenerator;

@SuppressWarnings("serial")
public class FlooderDescriptor extends OperationDescriptorImpl {
	  // A map-like array of strings with resources information.
	private static final String[][] resources = {
			{ "GlobalName", "WstFlooder" },
			{ "LocalName", "WstFlooder" },
			{ "Vendor", "info.wurzinger" },
			{ "Description", "Execute the watershed flooding algorithm on magnitude images." },
//			{ "DocURL", "http://www.lac.inpe.br/~rafael.santos" }, // TODO: FINAL WORK: publish javadoc
			{ "arg0Desc", "tile size" },
			{ "arg1Desc", "first segment label to start from"},
			{ "Version", "1.0" } };

	// An array of strings with the supported modes for this operator.
	private static final String[] supportedModes = { "rendered" };

	// An array of strings with the parameter names for this operator. 
	private static final String[] paramNames = { "tileSize", "startSegmentLabel"};

	// An array of Classes with the parameters' classes for this operator.
	private static final Class[] paramClasses = { Integer.class, Integer.class };

	// An array of Objects with the parameters' default values.
	private static final Object[] paramDefaults = { 500, 1 };

	// An array of Ranges with ranges of valid parameter values. 
	private static final Range[] validParamValues = {
		new Range(java.lang.Integer.class, new Integer(50), new Integer(10000)),
		new Range(java.lang.Integer.class, new Integer(1), new Integer(Integer.MAX_VALUE))
	};

	// The number of sources required for this operator.
	private static final int numSources = 2;

	// A flag that indicates whether the operator is already registered.
	private static boolean registered = false;

	/**
	 * The constructor for this descriptor, which just calls the constructor
	 * for its ancestral class (OperationDescriptorImpl).
	 */
	public FlooderDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses, paramDefaults, validParamValues);
	}

	/**
	 * A method to register this operator with the OperationRegistry and
	 * RIFRegistry. 
	 */
	public static void register() {
		if (!registered) {
			// Get the OperationRegistry.
			OperationRegistry op = JAI.getDefaultInstance().getOperationRegistry();
			
			// Register the operator's descriptor. 
			FlooderDescriptor desc = new FlooderDescriptor();
			
			op.registerDescriptor(desc);
			
			// Register the operators's RIF.
			FlooderRIF rif = new FlooderRIF();
			
			RIFRegistry.register(op, "WstFlooder", "info.wurzinger", rif);
			
			registered = true;
		}
	}
	
	/**
	 * Returns an <CODE>Array</CODE> of <CODE>PropertyGenerator</CODE>s
	 * implementing the property inheritance for this descriptor containing an
	 * instance of <CODE>SegmentMapMatrixGenerator</CODE>.
	 * @param aModeName The registry mode name.
	 * @return An array of <CODE>PropertyGenerator</CODE>s, or null if this
	 * operation does not have any own <CODE>PropertyGenerator</CODE>s.
	 */
	public PropertyGenerator[] getPropertyGenerators(String aModeName) {
		if (aModeName.equalsIgnoreCase("rendered")) {
			return new PropertyGenerator[] {new SegmentMapMatrixGenerator()};
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
