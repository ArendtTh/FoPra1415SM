package org.eclipse.emf.refactor.metrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;

public final class NOA implements IMetricCalculator {
		
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		org.eclipse.uml2.uml.StateMachine statemachine = (org.eclipse.uml2.uml.StateMachine) context.get(0);
		double ret = 0.0;
		
		ArrayList<Transition> found = new ArrayList<Transition>();
		
		for(Region region : statemachine.getRegions()) {
			
			//transitionen in regionen der statemachine
			for(Transition transition : region.getTransitions()) {
				if(transition.getEffect() != null) {
					ret++;
				}
			}
			
			//transitionen in regionen von composite states
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof State && ((State) vertex).isComposite()) {
							
					
					for(Region subRegion : getRegionsFromComplexState((State) vertex)) {
						for(Transition subTransition : subRegion.getTransitions()) {
							if(subTransition.getEffect() != null) 
								ret++;
						}
					}
				}
			}
		}
		return ret;
	}
					


public static ArrayList<Region> getRegionsFromComplexState(State state) {
	if(state.isComposite()) {
		ArrayList<Region> regions = new ArrayList<Region>();
		
		for(Region region : state.getRegions()) {				
			regions.add(region);			
			
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof State)
					regions.addAll(getRegionsFromComplexState((State) vertex));
			}
		}
		return regions;
	}						
	else {			
		return new ArrayList<Region>();
	}
}

}