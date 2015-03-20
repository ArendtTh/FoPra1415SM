package org.eclipse.emf.refactor.metrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.Vertex;

public final class NE implements IMetricCalculator {
		
	
	//nachfragen ob trigger (deferrable trigger) oder art von event wie ich das in meinem diagramm hatte
	private List<EObject> context; 
		
	@Override
	public void setContext(List<EObject> context) {
		this.context=context;
	}	
		
	@Override
	public double calculate() {	
		StateMachine statemachine = (StateMachine) context.get(0);
		double ret = 0.0;
		
		for(Region region : statemachine.getRegions()) {
			for(Vertex vertex : region.getSubvertices()) {
				if(vertex instanceof State && ((State) vertex).isSimple()) {
					
					//for( Trigger trigger : ((State) vertex).getDeferrableTriggers()) {
					//	ret++;
					//}
				}
				else if(vertex instanceof State && ((State) vertex).isComposite()) {
					for(Vertex subVertex : NSS.getVerticesFromComplexState((State) vertex)) {
						if(subVertex instanceof State && ((State) subVertex).isSimple()) {
						//	for( Trigger trigger : ((State) subVertex).getDeferrableTriggers()) {
						//		ret++;
						//	}
						}
					}
				}
			}
		}
		return ret;
	}
	
}