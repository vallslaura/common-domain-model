package org.isda.cdm;

import org.isda.cdm.calculation.functions.ResolveRateIndexImpl;
import org.isda.cdm.functions.CalculationPeriod;
import org.isda.cdm.functions.ResolveRateIndex;
import org.isda.cdm.functions.TestableCalculationPeriod;

import com.rosetta.model.lib.validation.ModelObjectValidator;

public class CdmTestsModule extends CdmRuntimeModule {

	@Override
	protected void configure() {
		super.configure();
		bind(ResolveRateIndex.class).to(bindResolveRateIndex());
	}

	@Override
	protected Class<? extends ModelObjectValidator> bindModelObjectValidator() {
		return NoOpValidator.class;
	}

	@Override
	protected Class<? extends CalculationPeriod> bindCalculationPeriod() {
		return TestableCalculationPeriod.class;
	}

	protected Class<? extends ResolveRateIndex> bindResolveRateIndex() {
		return ResolveRateIndexImpl.class;
	}
}
