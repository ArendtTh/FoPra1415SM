package org.eclipse.emf.refactor.metrics;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Vertex;

public final class NFS1 implements IMetricCalculator {

	private List<EObject> context;

	@Override
	public void setContext(List<EObject> context) {
		this.context = context;
	}

	@Override
	public double calculate() {
		StateMachine statemachine = (StateMachine) context.get(0);
		double ret = 0.0;
		// start custom code
		for (Region r : statemachine.getRegions()) {
			for (Vertex v : r.getSubvertices()) {
				if (v instanceof FinalState) {
					ret++;
				}
			}
		}
		// end custom code
		return ret;
	}
}