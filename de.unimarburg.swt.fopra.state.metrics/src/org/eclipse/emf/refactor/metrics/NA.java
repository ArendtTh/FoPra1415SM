package org.eclipse.emf.refactor.metrics;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.metrics.interfaces.IMetricCalculator;
import org.eclipse.emf.refactor.metrics.interfaces.IOperation;
import org.eclipse.emf.refactor.metrics.core.Metric;
import org.eclipse.emf.refactor.metrics.operations.Operations;

public final class NA implements IMetricCalculator {

	private List<EObject> context;
	private String metricID1 = "NEntryA";
	private String metricID2 = "NExitA";
	private String metricID3 = "NDoA";
	private String metricID4 = "NOA";

	IOperation operation = Operations.getOperation("Sum");
	
	@Override
	public void setContext(List<EObject> context) {
		this.context = context;	
	}

	@Override
	public double calculate() {
		Metric metric1 = Metric.getMetricInstanceFromId(metricID1);
		Metric metric2 = Metric.getMetricInstanceFromId(metricID2);
		Metric metric3 = Metric.getMetricInstanceFromId(metricID3);
		Metric metric4 = Metric.getMetricInstanceFromId(metricID4);

		IMetricCalculator calc1 = metric1.getCalculateClass();
		IMetricCalculator calc2 = metric2.getCalculateClass();
		IMetricCalculator calc3 = metric3.getCalculateClass();
		IMetricCalculator calc4 = metric4.getCalculateClass();

		calc1.setContext(this.context);
		calc2.setContext(this.context);
		calc3.setContext(this.context);
		calc4.setContext(this.context);

		double ret = operation.calculate(calc1.calculate(),calc2.calculate());
		ret = operation.calculate(ret, calc3.calculate());
		ret = operation.calculate(ret, calc4.calculate());
		
		return ret;
	}

}