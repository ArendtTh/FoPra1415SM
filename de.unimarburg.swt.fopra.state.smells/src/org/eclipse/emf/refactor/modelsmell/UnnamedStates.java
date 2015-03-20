package org.eclipse.emf.refactor.modelsmell;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Vertex;


public final class UnnamedStates implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		
		Model model = (Model) root;
		
		for (NamedElement namedElement : model.getMembers()) {
			if (namedElement instanceof StateMachine) {

				for(Region region : ((StateMachine) namedElement).getRegions()) {
					for(Vertex vertex : region.getSubvertices()) {
						if(vertex instanceof State && ((State) vertex).isSimple() && (
								((State) vertex).getName() == null  ||
								((State) vertex).getName().equals(""))) {
							
								
							LinkedList<EObject> result = new LinkedList<EObject>();
							result.add(vertex);
							results.add(result);
						}
						else if(vertex instanceof State && ((State) vertex).isComposite()) {
							if(((State) vertex).getName() == null ||
								((State) vertex).getName().equals("")) {
									LinkedList<EObject> result = new LinkedList<EObject>();
									result.add(vertex);
									results.add(result);
							}
							for(Vertex subVertex : getVerticesFromComplexState((State) vertex)) {
								if(subVertex instanceof State && ((State) subVertex).isSimple() && (
										((State) subVertex).getName() == null ||
										((State) subVertex).getName().equals(""))) {
									LinkedList<EObject> result = new LinkedList<EObject>();
									result.add(subVertex);
									results.add(result);
								}
							}
						}
					}
				}
				
			}
		}
		
		return results;
	}
	
	
		
	

public static ArrayList<Vertex> getVerticesFromComplexState(State state) {
	if(state.isComposite()) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for(Region region : state.getRegions()) {
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof State)
					vertices.addAll(getVerticesFromComplexState((State) vertex));
			}
		}
		return vertices;
	}						
	else {
		ArrayList<Vertex> singleVertex = new ArrayList<Vertex>();
		singleVertex.add(state);
		
		return singleVertex;
	}
	
}
	
}