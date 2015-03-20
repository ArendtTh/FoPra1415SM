package org.eclipse.emf.refactor.metrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.State;
public final class NSS implements IMetricCalculator {
		
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	// noch problem bei simple states innerhalb von composite evtl rekurison nötig?
	
	@Override
	public double calculate() {	
		StateMachine statemachine = (StateMachine) context.get(0);
		double ret = 0.0;
		
		for(Region region : statemachine.getRegions()) {
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof State && ((State) vertex).isSimple()) {
					ret++;
				}
				else if(vertex instanceof State && ((State) vertex).isComposite()) {					
					for(Vertex subVertex : getVerticesFromComplexState((State) vertex)) {
						if(subVertex instanceof State && ((State) subVertex).isSimple()) {
							ret++;
						}
					}
				}
			}
		}
		return ret;
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
				

