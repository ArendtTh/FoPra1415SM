package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;

public final class SameIncomingActivity implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();

		Model model = (Model) root;

		for (NamedElement namedElement : model.getMembers()) {
			if (namedElement instanceof StateMachine) {
				for (Region region : ((StateMachine) namedElement).getRegions()) {
					for (Vertex vertex : region.getSubvertices()) {
						// vertex is a simple state
						if (vertex instanceof State
								&& ((State) vertex).isSimple()
								&& hasSameIncomingActivity(vertex)) {
							LinkedList<EObject> result = new LinkedList<EObject>();
							result.add(vertex);
							results.add(result);
						}
						// vertex is a composite state
						else if (vertex instanceof State
								&& ((State) vertex).isComposite()) {
							for (Vertex subVertex : UnnamedStates
									.getVerticesFromComplexState((State) vertex)) {
								if (subVertex instanceof State
										&& hasSameIncomingActivity(subVertex)) {
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

	public boolean hasSameIncomingActivity(Vertex vertex) {
		EList<Transition> incomings = vertex.getIncomings();

		if (incomings.isEmpty())
			return false;

		Transition first = incomings.get(0);
		if (first.getEffect() == null)
			return false;

		// check if all transition have activities with equal attributes
		Behavior activity = first.getEffect();

		for (Transition transition : incomings) {
			if (transition.getEffect() == null
					|| transition.getEffect().getQualifiedName() == null
					|| !(transition.getEffect().getQualifiedName()
							.equals(activity.getQualifiedName()))) {

				return false;
			}
		}
		return true;
	}
}