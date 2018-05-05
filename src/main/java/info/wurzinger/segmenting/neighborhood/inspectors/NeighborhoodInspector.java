package info.wurzinger.segmenting.neighborhood.inspectors;

import info.wurzinger.segmenting.elements.Atom;

public abstract class NeighborhoodInspector<AtomInstance extends Atom> {
	
	public abstract void inspect(AtomInstance atomInstance);
	public abstract void reset();
	
	public boolean inspectNullPixel() {
		return false;
	}
}
