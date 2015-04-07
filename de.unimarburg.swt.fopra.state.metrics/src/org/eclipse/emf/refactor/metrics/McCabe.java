package org.eclipse.emf.refactor.metrics;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.emf.refactor.metrics.interfaces.IOperation;
import org.eclipse.emf.refactor.metrics.core.Metric;
import org.eclipse.emf.refactor.metrics.operations.Operations;

public final class McCabe implements IMetricCalculator {

	private List<EObject> context;
	private String metricID1 = "NSS";
	private String metricID2 = "NT";

	IOperation operation = Operations.getOperation("Subtraction");

	@Override
	public void setContext(List<EObject> context) {
		this.context = context;
	}

	@Override
	public double calculate() {

		double erg = 0.0;

		Metric metric1 = Metric.getMetricInstanceFromId(metricID1);
		Metric metric2 = Metric.getMetricInstanceFromId(metricID2);

		IMetricCalculator calc1 = metric1.getCalculateClass();
		IMetricCalculator calc2 = metric2.getCalculateClass();

		calc1.setContext(this.context);
		calc2.setContext(this.context);
		erg = operation.calculate(calc1.calculate(), calc2.calculate());
		erg = Math.abs(erg) + 2;

		return erg;
	}

}