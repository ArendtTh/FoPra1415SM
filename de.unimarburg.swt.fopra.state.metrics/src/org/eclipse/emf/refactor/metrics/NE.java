package org.eclipse.emf.refactor.metrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.Vertex;

public final class NE implements IMetricCalculator {

	private List<EObject> context;

	@Override
	public void setContext(List<EObject> context) {
		this.context = context;
	}

	@Override
	public double calculate() {
		StateMachine statemachine = (StateMachine) context.get(0);
		double ret = 0.0;

		for (Region region : statemachine.getRegions()) {
			for (Vertex vertex : region.getSubvertices()) {
				// alle events bzw trigger aus statemachine region
				if (vertex instanceof State && ((State) vertex).isSimple()) {
					ret += getTriggerCountState((State) vertex);
				} else if (vertex instanceof State
						&& ((State) vertex).isComposite()) {

					// alle events in den regionen der composite states
					for (Vertex subVertex : NSS
							.getVerticesFromComplexState((State) vertex)) {
						if (subVertex instanceof State) {
							ret += getTriggerCountState((State) subVertex);
						}
					}
					for (Region subRegion : getRegionsFromComplexState((State) vertex)) {
						ret += getTriggerCountTransition(subRegion);
					}
				}
			}
			// alle events der region selbst
			ret += getTriggerCountTransition(region);
		}
		return ret;
	}

	public static ArrayList<Region> getRegionsFromComplexState(State state) {
		if (state.isComposite()) {
			ArrayList<Region> regions = new ArrayList<Region>();

			for (Region region : state.getRegions()) {
				regions.add(region);

				for (Vertex vertex : region.getSubvertices()) {
					if (vertex instanceof State)
						regions.addAll(getRegionsFromComplexState((State) vertex));
				}
			}
			return regions;
		} else {
			return new ArrayList<Region>();
		}
	}

	public int getTriggerCountTransition(Region region) {
		int res = 0;
		for (Transition transition : region.getTransitions()) {
			for (Trigger trigger : transition.getTriggers()) {
				res++;
			}
		}
		return res;
	}

	public int getTriggerCountState(State state) {
		int res = 0;

		for (Trigger trigger : state.getDeferrableTriggers()) {
			res++;
		}
		return res;
	}
}