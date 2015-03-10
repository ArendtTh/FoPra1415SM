package org.eclipse.emf.refactor.modelsmell;

import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.StateMachine;


public final class UnnamedStateMachine implements IModelSmellFinder {

	@Override
	public LinkedList<LinkedList<EObject>> findSmell(EObject root) {
		LinkedList<LinkedList<EObject>> results = new LinkedList<LinkedList<EObject>>();
		// start custom code
		Model model = (Model) root;
		for (NamedElement ne : model.getMembers()) {
			if (ne instanceof StateMachine) {
				if (ne.getName() == null || ne.getName().equals("")) {
					LinkedList<EObject> result = new LinkedList<EObject>();
					result.add(ne);
					results.add(result);
				}
			}
		}
		// end custom code
		return results;
	}
	
}