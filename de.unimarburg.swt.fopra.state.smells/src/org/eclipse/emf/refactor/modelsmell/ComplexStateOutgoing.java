package org.eclipse.emf.refactor.modelsmell;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;


public final class ComplexStateOutgoing implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		
		Model model = (Model) root;
		
		//find all composite states in the model
		ArrayList<State> compositeStates = new ArrayList<State>();
		
		for (NamedElement namedElement : model.getMembers()) {			
			if (namedElement instanceof StateMachine) {				
				for(Region region : ((StateMachine) namedElement).getRegions()) {
					for(Vertex vertex : region.getSubvertices()) {
						if(vertex instanceof State && ((State) vertex).isComposite()) {						
							compositeStates.addAll(getAllCompositeStates((State) vertex));
						}
					}
				}
			}
		}
		
		//check for each composite state if its inner states share the same outgoing transition
		for(State state : compositeStates) {
			if(hasSameOutgoingTransitions(state)) {
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(state);
				results.add(result);
			}
		}		
		
		return results;
	}
	
	public ArrayList<State> getAllCompositeStates(State state) {
		if(state.isComposite()) {
			ArrayList<State> states = new ArrayList<State>();
			states.add(state);
			for(Region region : state.getRegions()) {
				for(Vertex vertex : region.getSubvertices()) {
					if(vertex instanceof State && ((State) vertex).isComposite())
						states.addAll(getAllCompositeStates((State) vertex));
				}
			}			
			return states;
		}
		else 
			return new ArrayList<State>();
	}
	
	
	
	//problem wie man contains macht
	//standardimplementation wohl wie equals mit referenzenvergleich anstatt werten
	//hier jetzt nur vergleich mit transition namen
	
	public boolean hasSameOutgoingTransitions(State compositeState) {
		
		EList<Transition> outgoingTransitions = null;
		EList<String> transitionNames = new BasicEList<String>();
		
		for(Region region : compositeState.getRegions()) {
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex.getOutgoings() == null)
					return false;
						
				outgoingTransitions = vertex.getOutgoings();
				transitionNames = getTransitionNames(outgoingTransitions);
			}
		}
	
		for(Region region : compositeState.getRegions()) {
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex.getOutgoings() == null || outgoingTransitions == null)
					return false;
				
				for(Transition transition : vertex.getOutgoings()) {
					
					if(!transitionNames.contains(transition.getName()))
						return false;
					
					//if(!outgoingTransitions.contains(transition) )
					//	return false;
					
				}
				//else if(! vertex.getOutgoings().equals(outgoingTransitions))
				//	return false;
			}
		}
		return true;
	}
	
	
	public EList<String> getTransitionNames(EList<Transition> transitions) {
		
		EList<String> names = new BasicEList<String>();
		
		for(Transition transition : transitions) {
			names.add(transition.getName());
		}
		
		return names;
	}
	
	
}