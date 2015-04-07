package org.eclipse.emf.refactor.metrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.StateMachine;

public final class NT implements IMetricCalculator {
		
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		StateMachine statemachine = (StateMachine) context.get(0);
		double ret = 0.0;
		
		ArrayList<Transition> found = new ArrayList<Transition>();
		
		for(Region region : statemachine.getRegions()) {
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof State && ((State) vertex).isComposite()) {
					
					for(Vertex subVertex : NSS.getVerticesFromComplexState((State) vertex)) {						
						for(Transition transition : subVertex.getIncomings()) {
							if(!found.contains(transition)) {
								found.add(transition);
								ret++;
							}
						}
						
						for(Transition transition : subVertex.getOutgoings()) {							
							if(!found.contains(transition)) {
								found.add(transition);
								ret++;
							}
						}
					}					
				}
				else {				
					for(Transition transition : vertex.getIncomings()) {
						if(!found.contains(transition)) {
							found.add(transition);
							ret++;
						}
					}
					
					for(Transition transition : vertex.getOutgoings()) {
						if(!found.contains(transition)) {
							found.add(transition);
							ret++;
						}
					}						
				} 
			}
		}	
		
	return ret;
	}
		
	
}
