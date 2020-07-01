package org.isda.cdm.functions.testing;

import com.regnosys.rosetta.common.testing.ExecutableFunction;
import org.isda.cdm.*;
import org.isda.cdm.functions.CashflowSettlementTerms;
import org.isda.cdm.functions.Create_Execution;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.isda.cdm.functions.testing.FunctionUtils.guard;

public class RunExecutionWithSettlementTerms implements ExecutableFunction<Contract, BusinessEvent> {

    @Inject
    Create_Execution execute;

    @Inject
    CashflowSettlementTerms cashflowSettlementTerms;


    @Override
    public BusinessEvent execute(Contract input) {
        List<SettlementTerms> settlementTerm = getSettlementTerm(input);
        if (!settlementTerm.isEmpty()) {
            input = clearCashPayout(input);
        }

        return execute.evaluate(input.getTradableProduct().getProduct(),
                guard(input.getTradableProduct().getQuantityNotation()),
                guard(input.getTradableProduct().getPriceNotation()),
                guard(input.getParty()),
                guard(input.getPartyRole()),
                settlementTerm);
    }

    private Contract clearCashPayout(Contract input) {
        Contract.ContractBuilder contractBuilder = input.toBuilder();
        Optional.of(contractBuilder)
                .map(Contract.ContractBuilder::getTradableProduct)
                .map(TradableProduct.TradableProductBuilder::getProduct)
                .map(Product.ProductBuilder::getContractualProduct)
                .map(ContractualProduct.ContractualProductBuilder::getEconomicTerms)
                .map(EconomicTerms.EconomicTermsBuilder::getPayout)
                .ifPresent(Payout.PayoutBuilder::clearCashflow);
        return contractBuilder.build();
    }

    private List<SettlementTerms> getSettlementTerm(Contract input) {
        return Optional.ofNullable(input)
                .map(Contract::getTradableProduct)
                .map(TradableProduct::getProduct)
                .map(Product::getContractualProduct)
                .map(ContractualProduct::getEconomicTerms)
                .map(EconomicTerms::getPayout)
                .map(Payout::getCashflow)
                .map(cashflows -> cashflows.stream()
                        .map(cashflow -> cashflowSettlementTerms.evaluate(cashflow))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }

    @Override
    public Class<Contract> getInputType() {
        return Contract.class;
    }

    @Override
    public Class<BusinessEvent> getOutputType() {
        return BusinessEvent.class;
    }

}