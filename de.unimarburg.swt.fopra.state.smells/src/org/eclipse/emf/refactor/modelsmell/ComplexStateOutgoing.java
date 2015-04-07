package org.eclipse.emf.refactor.modelsmell;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Pseudostate;
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

		// find all composite states in the model
		ArrayList<State> compositeStates = new ArrayList<State>();

		for (NamedElement namedElement : model.getMembers()) {
			if (namedElement instanceof StateMachine) {
				for (Region region : ((StateMachine) namedElement).getRegions()) {
					for (Vertex vertex : region.getSubvertices()) {
						if (vertex instanceof State
								&& ((State) vertex).isComposite()) {
							compositeStates
									.addAll(getAllCompositeStates((State) vertex));
						}
					}
				}
			}
		}

		// check for each composite state if its inner states share the same
		// outgoing transition
		for (State state : compositeStates) {
			if (hasSameOutgoingTransitions(state)) {
				LinkedList<EObject> result = new LinkedList<EObject>();
				result.add(state);
				results.add(result);
			}
		}
		return results;
	}

	public ArrayList<State> getAllCompositeStates(State state) {
		if (state.isComposite()) {
			ArrayList<State> states = new ArrayList<State>();
			states.add(state);
			for (Region region : state.getRegions()) {
				for (Vertex vertex : region.getSubvertices()) {
					if (vertex instanceof State
							&& ((State) vertex).isComposite())
						states.addAll(getAllCompositeStates((State) vertex));
				}
			}
			return states;
		} else
			return new ArrayList<State>();
	}

	public boolean hasSameOutgoingTransitions(State compositeState) {

		Transition outgoingTransition = null;
		for (Region region : compositeState.getRegions()) {
			for (Vertex subvertex : region.getSubvertices()) {

				outgoingTransition = findOutgoingTransition(subvertex,
						compositeState.getContainer());
				if (outgoingTransition != null)
					break;

			}
			if (outgoingTransition != null)
				break;
		}
		// has no outgoing transition
		if (outgoingTransition == null)
			return false;

		// check for all substates if they have the same outgoing transition and
		// only that
		for (Region region : compositeState.getRegions()) {
			for (Vertex subvertex : region.getSubvertices()) {
				if (!hasSameTransition(subvertex, outgoingTransition)
						|| hasOtherOutgoing(subvertex, outgoingTransition))
					return false;
			}
		}
		return true;
	}

	public Transition findOutgoingTransition(Vertex vertex, Region region) {
		for (Transition transition : vertex.getOutgoings()) {
			if (transition.getTarget().isContainedInRegion(region)
					&& transition.getEffect() != null)
				return transition;
		}
		return null;
	}

	public boolean hasSameTransition(Vertex vertex,
			Transition outgoingTransition) {

		for (Transition transition : vertex.getOutgoings()) {

			if (vertex instanceof Pseudostate)
				return true;

			// transition has no name
			if (transition.getName() == null
					&& outgoingTransition.getName() == null
					&& transition
							.getEffect()
							.getQualifiedName()
							.equals(outgoingTransition.getEffect()
									.getQualifiedName())
					&& ((transition.getTarget().getQualifiedName() != null && transition
							.getTarget()
							.getQualifiedName()
							.equals(outgoingTransition.getTarget()
									.getQualifiedName())) || (transition
							.getTarget().getQualifiedName() == null && outgoingTransition
							.getTarget().getQualifiedName() == null))) {

				return true;

			}
			// transition has name
			else if (transition.getName() != null
					&& transition.getTarget() != null
					&& transition.getName()
							.equals(outgoingTransition.getName())
					&& transition
							.getEffect()
							.getQualifiedName()
							.equals(outgoingTransition.getEffect()
									.getQualifiedName())
					&& ((transition.getTarget().getQualifiedName() != null && transition
							.getTarget()
							.getQualifiedName()
							.equals(outgoingTransition.getTarget()
									.getQualifiedName())) || (transition
							.getTarget().getQualifiedName() == null && outgoingTransition
							.getTarget().getQualifiedName() == null)))

			{
				return true;
			}

		}
		return false;

	}

	public boolean hasOtherOutgoing(Vertex vertex, Transition outgoingTransition) {

		for (Transition transition : vertex.getOutgoings()) {

			// transition has name
			if (transition.getTarget() != null
					&& transition.getName() != null
					&& !transition.getTarget().isContainedInRegion(
							vertex.getContainer())
					&& !transition.getName().equals(
							outgoingTransition.getName())) {

				return true;
			}
			// transition has no name
			else if (transition.getTarget() != null
					&& transition.getName() == null
					&& !transition.getTarget().isContainedInRegion(
							vertex.getContainer())
					&& outgoingTransition.getName() != null) {

				return true;
			}
		}
		return false;
	}

}
