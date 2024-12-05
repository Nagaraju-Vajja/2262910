package com.macys.uop.order.ordercollectorchestrator.predicate;

import com.macys.uop.foundation.core.utils.predicate.BaseRecordFailurePredicate;

public class OrdercollectorchestratorFailurePredicate extends BaseRecordFailurePredicate {

    @Override
    protected String getCBName() {
        return "ordercollectorchestrator-cb";
    }

}
