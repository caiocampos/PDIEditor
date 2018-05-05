package info.wurzinger.segmenting.neighborhood;

import java.util.ArrayList;
import java.util.LinkedList;

import info.wurzinger.segmenting.neighborhood.inspectors.NeighborhoodInspector;
import info.wurzinger.segmenting.elements.Atom;

public abstract class Neighborhood {
	
	protected LinkedList<NeighborhoodInspector> simpleInspectors;
	protected LinkedList<NeighborhoodInspector> detailedInspectors;
	protected ArrayList<Atom> atoms; // is the list of neighboring atoms (pixels or segments)
	
	public Neighborhood() {
		atoms = new ArrayList<Atom>();
		simpleInspectors = new LinkedList<NeighborhoodInspector>();
		detailedInspectors = new LinkedList<NeighborhoodInspector>();
	}
	
	public void addInspector(NeighborhoodInspector inspector) {
		if (inspector.inspectNullPixel()) {
			detailedInspectors.add(inspector);
		} else {
			simpleInspectors.add(inspector);
		}
	}
	
	public void inspectNeighborhood() {
		int size = atoms.size();
		Atom atom;
		
		resetInspectors();
		
		for (int i=0; i<size; i++) {
			atom = atoms.get(i);
			
			if (atom!=null) {
				for (NeighborhoodInspector<Atom> inspector : simpleInspectors) {
					inspector.inspect(atom);
				}
			}
			
			for (NeighborhoodInspector<Atom> inspector : detailedInspectors) {
				inspector.inspect(atom);
			}
		}
	}
	
	private void resetInspectors() {
		for (NeighborhoodInspector<Atom> inspector : simpleInspectors) {
			inspector.reset();
		}
		for (NeighborhoodInspector<Atom> inspector : detailedInspectors) {
			inspector.reset();
		}
	}
	
	/**
	 * The atoms must be clockwisely added!
	 * @param atom
	 */
	protected void addAtom(Atom atom) {
		atoms.add(atom);
	}
}
