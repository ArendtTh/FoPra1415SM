package org.eclipse.emf.refactor.modelsmell;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.Transition;


public final class IsolatedStates implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		
		Model model = (Model) root;
		
		for (NamedElement namedElement : model.getMembers()) {			
			
			if (namedElement instanceof StateMachine) {
				
				for(Region region : ((StateMachine) namedElement).getRegions()) {
					for(Vertex vertex : region.getSubvertices()) {
						//vertex is a simple state
						if(vertex instanceof State && ((State) vertex).isSimple() && 
								isIsolated(vertex)) {
							
								
							LinkedList<EObject> result = new LinkedList<EObject>();
							result.add(vertex);
							results.add(result);
						}
						//vertex is a composite state
						else if(vertex instanceof State && ((State) vertex).isComposite()) {
							//check the composite state
							if( isIsolated(vertex)) {
									LinkedList<EObject> result = new LinkedList<EObject>();
									result.add(vertex);
									results.add(result);
							}
							//check the substates of the composite state
							for(Vertex subVertex : UnnamedStates.getVerticesFromComplexState((State) vertex)) {
								if(subVertex instanceof State && isIsolated(subVertex)) {							
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
	
	
	public boolean isIsolated(Vertex vertex) {		
		EList<Transition> incomings = vertex.getIncomings();
		return incomings.isEmpty();
	}
}